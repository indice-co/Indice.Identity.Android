package gr.indice.identity.models

data class ResultSet<T>(
    val count: Int?,
    val items: List<T>?
)
