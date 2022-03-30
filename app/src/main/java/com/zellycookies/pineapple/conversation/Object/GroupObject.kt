package com.zellycookies.pineapple.conversation.Object

import com.zellycookies.pineapple.utils.User
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