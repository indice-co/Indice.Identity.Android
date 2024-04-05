package gr.indice.identity.protocols

interface CurrentDeviceInfoProvider {
    val name        : String
    val model       : String
    val osVersion   : String
}