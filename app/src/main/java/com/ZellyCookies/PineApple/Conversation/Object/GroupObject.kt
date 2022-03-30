package com.ZellyCookies.PineApple.Conversation.Object

import com.ZellyCookies.PineApple.Utils.User
import java.io.Serializable

class GroupObject : Serializable {
    var chatId: String? = null
    var userMatch: User

    constructor(chatId: String?, userMatch: User) {
        this.chatId = chatId
        this.userMatch = userMatch
    }

    constructor(userMatch: User) {
        this.userMatch = userMatch
    }
}