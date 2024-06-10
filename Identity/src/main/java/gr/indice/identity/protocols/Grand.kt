package gr.indice.identity.protocols

interface OAuth2Grant {
    val params: Map<String, Any>
    val grantType: String
}

fun OAuth2Grant.with(authorizationDetails: Any): OAuth2Grant {
    val extras = "authorization_details" to authorizationDetails.toString()

    return OAuthParamsWrapper(parent = this, extras = extras)
}

private class OAuthParamsWrapper(private val parent: OAuth2Grant, private val extras: Pair<String, Any>): OAuth2Grant {
    override val params: Map<String, Any>
        get() = parent.params + mapOf(extras)
    override val grantType get() = parent.grantType

}