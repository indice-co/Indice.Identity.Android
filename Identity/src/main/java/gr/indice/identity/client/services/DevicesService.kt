package gr.indice.identity.client.services

import android.util.Base64
import gr.indice.identity.BuildConfig
import gr.indice.identity.apis.DevicesRepository
import gr.indice.identity.apis.ThisDeviceRepository
import gr.indice.identity.client.IdentityClientOptions
import gr.indice.identity.models.CreateDeviceRequest
import gr.indice.identity.models.DeviceAuthentications
import gr.indice.identity.models.DeviceInfo
import gr.indice.identity.models.UpdateDeviceRequest
import gr.indice.identity.models.extensions.biometric
import gr.indice.identity.models.extensions.biometricInit
import gr.indice.identity.models.extensions.from
import gr.indice.identity.models.extensions.pin
import gr.indice.identity.models.extensions.pinInit
import gr.indice.identity.protocols.Client
import gr.indice.identity.protocols.IdentityEncryptedStorage
import gr.indice.identity.protocols.StorageKey
import gr.indice.identity.utils.CallbackType
import gr.indice.identity.utils.CryptoUtils
import gr.indice.identity.utils.DeviceSelection
import gr.indice.identity.utils.ServiceErrorException
import gr.indice.identity.utils.SharedIdentityKeys.devicePinKey
import gr.indice.identity.utils.SharedIdentityKeys.hasFingerPrint
import gr.indice.identity.utils.getThisDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.security.Signature
import java.util.concurrent.CancellationException

data class DeviceData(
    val userDevices: StateFlow<List<DeviceInfo>?>,
    var deviceId: StateFlow<String>
)

data class QuickLoginStatus(
    val hasDevicePin: StateFlow<Boolean>,
    val hasFingerPrint: StateFlow<Boolean>,
    val hasQuickLogin: Flow<Boolean>
)
/**
 * Manages the users devices. Provides info as bindable objects that the relevant UI can use.
 */
interface DevicesService {

    val devicesInfo: DeviceData
    val quickLoginStatus: QuickLoginStatus
    @Throws(ServiceErrorException::class)
    suspend fun refreshDevices()
    /** Register or update an existing registration of the current device. */
    @Throws(ServiceErrorException::class)
    suspend fun updateThisDeviceRegistration(pnsHandle: String?, tags:List<String>?)
    /** Delete a devices from the user's registered devices list. */
    @Throws(ServiceErrorException::class)
    suspend fun delete(deviceId: String)
    /**
     * Register or update a device, to be able to perform a **device_authentication** grant with **pin** mode
     * This method returns a **lambda** that, provided with on otp [CallbackType.OtpResult], completes the device pin registration.
     * ```
     *
     *
     * viewModelScope.launch {
     *     val pin: String = makePin()
     *     val continuation = identityClient.devicesService.registerDevicePin(pin)
     *     // If success show otp input
     *     // After OTP input call continuation with otpValue
     *     continuation(CallbackType.OtpResult.Submit(otpValue))
     * }
     *
     * ```
     * It is not necessary to call the continuation with an OtpResult.aborted result but it is recommended.
     * @throws ServiceErrorException
     */
    @Throws(ServiceErrorException::class)
    suspend fun registerDevicePin(pin: String) : suspend (CallbackType.OtpResult) -> Unit
    /**
     * Register or update a device, to be able to perform a **device_authentication** grant with **fingerprint** mode
     * This method returns a **lambda** that, provided with on otp, completes the device fingerprint registration.
     * ```
     *
     * viewModelScope.launch {
     *
     *     val continuation = identityClient.devicesService.registerDeviceFingerprint()
     *     // If success show otp input
     *     // After OTP input call continuation with otpValue
     *     continuation(CallbackType.OtpResult.Submit(otpValue))
     * }
     *
     * ```
     * It is not necessary to call the continuation with an OtpResult.aborted result but it is recommended.
     *
     * @param signatureUnlock Prompt for fingerprint
     */
    @Throws(ServiceErrorException::class)
    suspend fun registerDeviceFingerprint(signatureUnlock: suspend (Signature) -> Signature) : suspend (CallbackType.OtpResult) -> Unit
    /** Remove a device pin registration */
    suspend fun removeRegistrationDevicePin()
    /** Remove a fingerprint registration */
    suspend fun removeRegistrationFingerprint()
    /** Trigger enable current device's trust status */
    @Throws(ServiceErrorException::class)
    suspend fun enableDeviceTrust(deviceSelection: DeviceSelection)
    /** Remove current device's trust status */
    @Throws(ServiceErrorException::class)
    suspend fun removeDeviceTrust()
}

internal class DevicesServiceImpl(
    private val identityOptions: IdentityClientOptions,
    private val thisDeviceRepository: ThisDeviceRepository,
    private val devicesRepository: DevicesRepository,
    private val encryptedStorage: IdentityEncryptedStorage,
    private val client: Client
) : BaseService(), DevicesService {

    private val _userDevices = MutableStateFlow<List<DeviceInfo>?>(emptyList())
    private val _deviceId = MutableStateFlow(thisDeviceRepository.ids.device)

    private val _hasDevicePin by lazy { MutableStateFlow(encryptedStorage.readBoolean(StorageKey.devicePinKey) ?: false) }
    private val _hasFingerPrint by lazy { MutableStateFlow(encryptedStorage.readBoolean(StorageKey.hasFingerPrint) ?: false) }

    //region DeviceData
    override val devicesInfo = DeviceData(
        userDevices = _userDevices.asStateFlow(),
        deviceId = _deviceId.asStateFlow()
    )

    override val quickLoginStatus: QuickLoginStatus = QuickLoginStatus(
        hasDevicePin = _hasDevicePin.asStateFlow(),
        hasFingerPrint = _hasFingerPrint.asStateFlow(),
        hasQuickLogin = _hasFingerPrint.combine(_hasDevicePin) { fingerPrint, pin ->
            return@combine fingerPrint || pin
        }
    )

    override suspend fun refreshDevices() {
        updateFetchDeviceList()
    }

    override suspend fun updateThisDeviceRegistration(pnsHandle: String?, tags: List<String>?) {
        val deviceId = thisDeviceRepository.ids.device
        val isRegistered = if (_userDevices.value?.getThisDevice(deviceId) == null) {
            updateFetchDeviceList()
            _userDevices.value?.getThisDevice(deviceId) != null
        } else {
            true
        }

        if (isRegistered) {
            load {
                devicesRepository.update(
                    thisDeviceRepository.ids.device,
                    UpdateDeviceRequest.from(thisDeviceRepository, pnsHandle, tags)
                )
            }.also { updateFetchDeviceList() }
        } else {
            thisDeviceRepository.resetIds()
            _deviceId.value = thisDeviceRepository.ids.device
            load {
                devicesRepository.create(
                    CreateDeviceRequest.from(thisDeviceRepository, pnsHandle, tags))
            }.also { updateFetchDeviceList() }
        }
    }

    override suspend fun delete(deviceId: String) {
       load { devicesRepository.delete(deviceId) }
        _userDevices.value = _userDevices.value?.filter { it.deviceId == deviceId }
    }
    //endregion

    //region Authorize Device
    override suspend fun registerDevicePin(pin: String): suspend (CallbackType.OtpResult) -> Unit {
        try {
            CryptoUtils.deleteKeyPair(CryptoUtils.KeyType.PIN)
            _hasDevicePin.value = false
            encryptedStorage.storeBoolean(StorageKey.devicePinKey, false)

            CryptoUtils.createKeyPair(CryptoUtils.KeyType.PIN)

            val codeVerifier = CryptoUtils.createCodeVerifier()
            val verifierHash = CryptoUtils.sha256(codeVerifier)

            val authRequest = DeviceAuthentications.AuthorizationRequest.pinInit(
                codeChallenge = verifierHash,
                deviceIds = thisDeviceRepository.ids,
                client = client
            )

            val challenge = load { devicesRepository.initialize(authRequest) }.challenge!!

            return { otpResult ->
                val otp = otpResult.otpValue

                val signedChallenge = CryptoUtils.getSignature().run {
                    initSign(CryptoUtils.getPrivateKey(CryptoUtils.KeyType.PIN))
                    update(challenge.toByteArray())
                    sign().let { Base64.encodeToString(it, Base64.NO_WRAP) }
                }

                val devicePin = CryptoUtils.createPinHash(pin, thisDeviceRepository.ids.device)

                val regRequest = DeviceAuthentications.RegistrationRequest.pin(
                    challenge, codeVerifier, signedChallenge, thisDeviceRepository.ids, thisDeviceRepository.info, devicePin, otp)

                load { devicesRepository.complete(regRequest) }.run {
                    thisDeviceRepository.update(registrationId?.toString())
                    _hasDevicePin.value = true
                    encryptedStorage.storeBoolean(StorageKey.devicePinKey, true)
                    updateDeviceWith(thisDeviceRepository.ids.device)
                }

            }

        } catch (e: Exception) {
            _hasDevicePin.value = false
            encryptedStorage.storeBoolean(StorageKey.devicePinKey, false)
            throw e
        }
    }

    override suspend fun registerDeviceFingerprint(signatureUnlock: suspend (Signature) -> Signature): suspend (CallbackType.OtpResult) -> Unit {
        try {
            CryptoUtils.deleteKeyPair(CryptoUtils.KeyType.BIOMETRIC)
            encryptedStorage.storeBoolean(StorageKey.hasFingerPrint, false)
            _hasFingerPrint.value = false
            CryptoUtils.createKeyPair(CryptoUtils.KeyType.BIOMETRIC)

            val codeVerifier = CryptoUtils.createCodeVerifier()
            val verifierHash = CryptoUtils.sha256(codeVerifier)

            val authRequest = DeviceAuthentications.AuthorizationRequest.biometricInit(
                codeChallenge = verifierHash,
                deviceIds = thisDeviceRepository.ids,
                client = client
            )

            val signature = CryptoUtils.getSignature()
            val key = CryptoUtils.getPrivateKey(CryptoUtils.KeyType.BIOMETRIC)
            signature.initSign(key)

            val unlockSignature = signatureUnlock(signature)

            val challenge = load { devicesRepository.initialize(authRequest) }.challenge!!

            val signedChallenge = with(unlockSignature) {
                update(challenge.toByteArray())
                sign().let { Base64.encodeToString(it, Base64.NO_WRAP) }
            }

            val pemKey = CryptoUtils.getPemFromKey(CryptoUtils.KeyType.BIOMETRIC)

            return { otpResult ->

                val otp = otpResult.otpValue

                val request = DeviceAuthentications.RegistrationRequest.biometric(
                    challenge,
                    codeVerifier,
                    signedChallenge,
                    thisDeviceRepository.ids,
                    thisDeviceRepository.info,
                    pemKey,
                    otp
                )

                load { devicesRepository.complete(request) }
                    .apply {
                        thisDeviceRepository.update(registrationId?.toString())
                        _hasFingerPrint.value = true
                        encryptedStorage.storeBoolean(StorageKey.hasFingerPrint, true)
                        updateDeviceWith(thisDeviceRepository.ids.device)
                    }

            }
        } catch (e: Exception) {
            //TODO Handle Error?
            if (BuildConfig.DEBUG)
                e.printStackTrace()
            if (e is CancellationException)
                throw e
            _hasFingerPrint.value = false
            encryptedStorage.storeBoolean(StorageKey.hasFingerPrint, false)
            throw e
        }
    }

    override suspend fun removeRegistrationDevicePin() {
        CryptoUtils.deleteKeyPair(CryptoUtils.KeyType.PIN)
        encryptedStorage.storeBoolean(StorageKey.devicePinKey, false)
        _hasDevicePin.value = false
    }

    override suspend fun removeRegistrationFingerprint() {
        CryptoUtils.deleteKeyPair(CryptoUtils.KeyType.BIOMETRIC)
        encryptedStorage.storeBoolean(StorageKey.hasFingerPrint, false)
        _hasFingerPrint.value = false
    }

    override suspend fun enableDeviceTrust(deviceSelection: DeviceSelection) {
        val ids = thisDeviceRepository.ids

        if (devicesInfo.userDevices.value == null) {
            refreshDevices()
        }

        val devices = (devicesInfo.userDevices.value ?: emptyList()).filter { it.deviceId != ids.device }

        val currentTrustedCount = devices.count { it.isTrusted == true }

        val swapDeviceId = if (currentTrustedCount >= identityOptions.maxTrustedDevicesCount) {
            when(val selection = deviceSelection(devices)) {
                is CallbackType.DeviceSwapResult.Swap -> {
                    selection.deviceInfo.deviceId
                }
                CallbackType.DeviceSwapResult.Aborted -> {
                    //throw Exception("Another device has the trusted status")
                    null
                }
            }
        } else { null }

        load { devicesRepository.trust(deviceId = ids.device, swapDeviceId = swapDeviceId) }
        updateDeviceWith(ids.device)
        swapDeviceId?.let { updateDeviceWith(it) }

    }

    override suspend fun removeDeviceTrust() {
        val deviceId = thisDeviceRepository.ids.device

        load { devicesRepository.unTrust(deviceId) }
        updateDeviceWith(deviceId)
    }
    //endregion

    //region Private helpers
    @Throws(ServiceErrorException::class)
    private suspend fun updateFetchDeviceList() {
        _userDevices.value = load { devicesRepository.devices() }.items
    }
    @Throws(ServiceErrorException::class)
    private suspend fun updateDeviceWith(deviceId: String) {
        val newDevice = load { devicesRepository.device(deviceId) }
        val deviceList = (_userDevices.value ?: emptyList()).toMutableList()
        val devIndex = _userDevices.value?.indexOfFirst { it.deviceId == newDevice.deviceId }

        devIndex?.let {
            deviceList[it] = newDevice
        } ?: deviceList.add(0, newDevice)

        _userDevices.value = deviceList
    }
    //endregion

}
