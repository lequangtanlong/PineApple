package com.zellycookies.pineapple.utils



object StringManipulation {
    fun expandUsername(username: String?): String {
        return username!!.replace(".", " ")
    }

    fun condenseUsername(username: String): String {
        return username.replace(" ", ".")
    }
}