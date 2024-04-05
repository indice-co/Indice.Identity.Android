package gr.indice.identity.models

import com.squareup.moshi.Json

/**
* 
* Values: Sms,Email,Telephone,Viber,EToken,PushNotification,None
*/
enum class TotpDeliveryChannel {

    @Json(name = "Sms")
    SMS,
    
    @Json(name = "Email")
    EMAIL,
    
    @Json(name = "Telephone")
    TELEPHONE,
    
    @Json(name = "Viber")
    VIBER,
    
    @Json(name = "EToken")
    ETOKEN,
    
    @Json(name = "PushNotification")
    PUSHNOTIFICATION,
    
    @Json(name = "None")
    NONE;
}


