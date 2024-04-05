package gr.indice.identity.protocols

data class StorageKey(val name: String) {
    companion object
}
interface IdentityEncryptedStorage {

    fun store(key: StorageKey, value: String)
    fun storeBoolean(key: StorageKey, value: Boolean)
    fun read(key: StorageKey) : String?
    fun readBoolean(key: StorageKey) : Boolean?
    fun remove(key: StorageKey)
    fun remove(keys: List<StorageKey>)
    fun contains(key: StorageKey): Boolean

}