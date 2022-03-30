package com.zellycookies.pineapple.conversation.Object

class MessageObject {
    var messageId: String? = null
    var senderId: String? = null
    var message: String? = null
    var datetime: String? = null

    //ArrayList<String> mediaUrlList;
    constructor() {}
    constructor(messageId: String?, senderId: String?, message: String?, datetime: String?) {
        this.messageId = messageId
        this.senderId = senderId
        this.message = message
        this.datetime = datetime
    }
}