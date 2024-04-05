package gr.indice.identity.utils

import gr.indice.identity.models.DeviceInfo

typealias OtpProvider = suspend (needsOtp: Boolean) -> CallbackType.OtpResult
typealias DeviceSelection = suspend (List<DeviceInfo>) -> CallbackType.DeviceSwapResult
class CallbackType {

    sealed interface OtpResult {
        data object Aborted: OtpResult
        class Submit(val value: String): OtpResult
        val isAborted
            get() =  when(this) {
                Aborted -> true
                is Submit -> false
            }

        val otpValue get() = when(this) {
            Aborted -> null
            is Submit -> value
        }

    }

    sealed interface DeviceSwapResult {
        class Swap(val deviceInfo: DeviceInfo): DeviceSwapResult
        data object Aborted: DeviceSwapResult
    }

}

