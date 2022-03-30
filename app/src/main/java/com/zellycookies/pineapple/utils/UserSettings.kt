package com.zellycookies.pineapple.utils


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