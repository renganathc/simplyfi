package com.factory.simplyfi

class ChatMessage(var text : String, var senderAccountType : String, var id : String, var from_id : String, var name : String, var xp : Long){
    constructor() : this("","", "","", "", -1)
}
