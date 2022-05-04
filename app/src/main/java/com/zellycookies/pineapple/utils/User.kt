package com.zellycookies.pineapple.utils


import java.io.Serializable

//User class
class User : Serializable {
    var user_id: String? = null
        get() = field
        set(value) {
            field = value
        }
    var phone_number: String? = null
    var email: String? = null
    var username: String? = null
    var isHobby_movies = false
    var isHobby_food = false
    var isHobby_art = false
    var isHobby_music = false
    var isShowDoB = true
    var isShowDistance = true
    var description: String? = null
    var sex: String? = null
    var preferSex: String? = null
    var preferDistance: Int? = null
    var preferMinAge: Int? = null
    var preferMaxAge: Int? = null

    // Added new attribute date of birth.
    var dateOfBirth: String? = null
    var profileImageUrl: String? = null
    var imageUrl_1 : String? = null
    var imageUrl_2 : String? = null
    var imageUrl_3 : String? = null
    var imageUrl_4 : String? = null

    //define behaviors
    var latitude = 0.0
    var longtitude = 0.0

    constructor() {}

    //define attributes
    constructor(
        sex: String?,
        preferSex: String?,
        preferDistance: Int?,
        preferMinAge: Int?,
        preferMaxAge: Int?,
        user_id: String?,
        phone_number: String?,
        email: String?,
        username: String?,
        hobby_movies: Boolean,
        hobby_food: Boolean,
        hobby_art: Boolean,
        hobby_music: Boolean,
        description: String?,
        dateOfBirth: String?,
        profileImageUrl: String?,
        latitude: Double,
        longtitude: Double,
        imageUrl_1: String?,
        imageUrl_2: String?,
        imageUrl_3: String?,
        imageUrl_4: String?
    ) {
        this.sex = sex
        this.user_id = user_id
        this.phone_number = phone_number
        this.email = email
        this.username = username
        isHobby_movies = hobby_movies
        isHobby_food = hobby_food
        isHobby_art = hobby_art
        isHobby_music = hobby_music
        isShowDoB = true
        isShowDistance = true
        this.description = description
        this.preferSex = preferSex
        this.preferDistance = preferDistance
        this.preferMinAge = preferMinAge
        this.preferMaxAge = preferMaxAge
        this.dateOfBirth = dateOfBirth
        this.profileImageUrl = profileImageUrl
        this.latitude = latitude
        this.longtitude = longtitude
        this.imageUrl_1 = imageUrl_1
        this.imageUrl_2 = imageUrl_2
        this.imageUrl_3 = imageUrl_3
        this.imageUrl_4 = imageUrl_4
    }

    override fun toString(): String {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", Movies=" + isHobby_movies +
                ", Food=" + isHobby_food +
                ", Art=" + isHobby_art +
                ", Music=" + isHobby_music +
                ", showDoB=" + isShowDoB +
                ", showDistance=" + isShowDistance +
                ", description='" + description + '\'' +
                ", sex='" + sex + '\'' +  //", preferSex='" + preferSex + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}'
    }
}