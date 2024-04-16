package gr.indice.identity.pixie

import gr.indice.identity.utils.CryptoUtils
import java.util.UUID

data class PKCE (
    val challenge: String,
    val nonce: String,
    val challengeMethod: ChallengeMethod
){

    enum class ChallengeMethod(val value: String) {
        SHA_256("S256")
    }

    data class Data(
        val verifier: String,
        val pkce: PKCE
    )

    companion object {
        fun generateData(challengeMethod: ChallengeMethod = ChallengeMethod.SHA_256): Data {
            val codeVerifier = CryptoUtils.createRandomKeyString(32)
            val nonce = CryptoUtils.createRandomKeyString()
            val challenge = CryptoUtils.sha256(codeVerifier)

            val pkce = PKCE(challenge = challenge, nonce = nonce, challengeMethod = challengeMethod)
            return Data(verifier = codeVerifier, pkce = pkce)
        }
    }
}