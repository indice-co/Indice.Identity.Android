package gr.indice.identity.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.RSAKeyGenParameterSpec

object CryptoUtils {

    private const val STORE_NAME = "AndroidKeyStore"
    private const val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
    private const val KEY_SIZE = 4096
    private const val KEY_BIO_NAME = "gr.indice.keys.bio-sec-keys"
    private const val KEY_PIN_NAME = "gr.indice.keys.pin-sec-keys"
    private const val SIGN_ALGORITHM = "SHA256withRSA"
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
    private const val KEY_PADDING = KeyProperties.SIGNATURE_PADDING_RSA_PKCS1
    private val String.Companion.RSA_KEY_START get() = "-----BEGIN PUBLIC KEY-----"
    private val String.Companion.RSA_KEY_END get() = "-----END PUBLIC KEY-----"


    enum class Algorithm(val value: String) {
        SHA256("SHA-256")
    }

    enum class KeyType {
        BIOMETRIC, PIN
    }

    private fun KeyType.keyName() : String {
        return if (this == KeyType.BIOMETRIC) KEY_BIO_NAME else KEY_PIN_NAME
    }

    private fun KeyStore.public(keyType: KeyType) = this.getCertificate(keyType.keyName()).publicKey


    private fun KeyStore.private(keyType: KeyType) = this.getKey(keyType.keyName(), null) as PrivateKey

    private fun KeyStore.delete(keyType: KeyType) {
        if (this.containsAlias(keyType.keyName())) {
            this.deleteEntry(keyType.keyName())
        }
    }

    fun sha256(text: String) = hashString(text, Algorithm.SHA256)

    private fun base64Encode(bytes: ByteArray) : String {
        val encodingFlags =
                Base64.NO_PADDING or
                Base64.URL_SAFE or
                Base64.NO_WRAP

        return Base64.encodeToString(bytes, encodingFlags)
    }

    fun createCodeVerifier(): String = String.random(32)

    @Suppress("SameParameterValue") // Future proofing for more.
    private fun hashString(input: String, algorithm: Algorithm): String {
        return MessageDigest
            .getInstance(algorithm.value)
            .digest(input.toByteArray())
            .let { base64Encode(it) }
            .trim()
    }

    private fun createRandomKey(length: Int) : ByteArray {
        val rnd = SecureRandom.getInstanceStrong()
        val bytes = ByteArray(length)

        rnd.nextBytes(bytes)

        return bytes
    }

    private fun keyStore() = KeyStore.getInstance(STORE_NAME).also { it.load(null) }

    fun createKeyPair(keyType: KeyType) {
        val rsaSpec = RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4)
        val keyPurpose = KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        val keySpec = KeyGenParameterSpec.Builder(keyType.keyName(), keyPurpose)
            .let { builder ->
                return@let when(keyType) {
                    KeyType.PIN -> builder
                        .setUserAuthenticationRequired(false)
                        .setInvalidatedByBiometricEnrollment(false)
                        .setSignaturePaddings(KEY_PADDING)
                        .setDigests(KeyProperties.DIGEST_SHA256)

                    KeyType.BIOMETRIC -> builder
                        .setUserAuthenticationRequired(true)
                        .setInvalidatedByBiometricEnrollment(true)
                        .setAlgorithmParameterSpec(rsaSpec)
                        .setSignaturePaddings(KEY_PADDING)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setBlockModes(BLOCK_MODE)
                        .setKeySize(KEY_SIZE)
                }
            }.build()

        val keyGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, STORE_NAME)
        keyGenerator.initialize(keySpec)
        keyGenerator.generateKeyPair()
    }

    fun createPinHash(pin: String, deviceId: String): String {
        return sha256(sign("$pin-$deviceId", KeyType.PIN))
    }

    fun sign(text: String, keyType: KeyType): String =
        Signature.getInstance(SIGN_ALGORITHM).run {
            initSign(keyStore().private(keyType))
            update(text.toByteArray())
            sign().let { Base64.encodeToString(it, Base64.NO_WRAP) }
        }

    fun deleteKeyPair(keyType: KeyType) {
        keyStore().delete(keyType)
    }

    fun createUniqueId(length: Int = 32) : String {
        return base64Encode(createRandomKey(length))
    }

    fun createRandomKeyString(length: Int = 64) : String {
        return base64Encode(createRandomKey(length))
    }

    fun getPrivateKey(keyType: KeyType) : PrivateKey {
        return keyStore().private(keyType)
    }


    fun getSignature(): Signature {
        return Signature.getInstance(SIGN_ALGORITHM)
    }

    fun getPemFromKey(keyType: KeyType): String {
        return keyStore().public(keyType).encoded.let {
            val keyString = Base64.encodeToString(it, Base64.NO_WRAP)
                .chunked(65)
                .joinToString("\n")

            val keyPrefix = String.RSA_KEY_START
            val keySuffix = String.RSA_KEY_END

            return@let "$keyPrefix\n$keyString\n$keySuffix"
        }
    }
}