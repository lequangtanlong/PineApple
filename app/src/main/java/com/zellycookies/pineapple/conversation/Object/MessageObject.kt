package com.zellycookies.pineapple.conversation.Object

object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

class MessageObject {
    var messageId: String? = null
    var senderId: String? = null
    var message: String? = null
    var datetime: String? = null
    var imagePath: String? = null
    var type: String? = null

    //ArrayList<String> mediaUrlList;
    constructor() {}
    constructor(messageId: String?, senderId: String?, message: String?, datetime: String?, type: String? = MessageType.TEXT) {
        this.messageId = messageId
        this.senderId = senderId
        if(type == MessageType.IMAGE){
            this.imagePath = message
        }
        else{
            this.message = message
        }
        this.datetime = datetime
    }
}