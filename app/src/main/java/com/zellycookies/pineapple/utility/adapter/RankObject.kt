package com.zellycookies.pineapple.utility.adapter

import com.zellycookies.pineapple.utils.User

class RankObject(user: User, private var likeCount: Int) {
    private var user : User? = user

    fun getUser(): User? { return user }
    fun getLikeCount(): Int { return likeCount }
    fun upLikeCount() { likeCount++ }

}