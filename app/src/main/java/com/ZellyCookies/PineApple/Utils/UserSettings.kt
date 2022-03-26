package com.ZellyCookies.PineApple.Utils


class UserSettings {
    var user: User? = null

    constructor(user: User?) {
        this.user = user
    }

    constructor() {}

    override fun toString(): String {
        return "UserSettings{" +
                "user=" + user +
                '}'
    }
}