package gr.indice.identity.protocols

interface OAuth2Grant {
    val params: Map<String, String>
    val grantType: String
}

fun OAuth2Grant.with(authorizationDetails: Any): OAuth2Grant {
    val extras = "authorization_details" to authorizationDetails.toString()

    return OAuthParamsWrapper(parent = this, extras = extras)
}

private class OAuthParamsWrapper(private val parent: OAuth2Grant, private val extras: Pair<String, String>): OAuth2Grant {
    override val params: Map<String, String>
        get() = parent.params + mapOf(extras)
    override val grantType get() = parent.grantType

}