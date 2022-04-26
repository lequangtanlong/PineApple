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
    var isSE = false
    var isDatabase = false
    var isDesign = false
    var isOop = false
    var isShowDoB = true
    var isShowDistance = true
    var description: String? = null
    var sex: String? = null
    var preferSex: String? = null

    // Added new attribute date of birth.
    var dateOfBirth: String? = null
    var profileImageUrl: String? = null

    //define behaviors
    var latitude = 0.0
    var longtitude = 0.0

    constructor() {}

    //define attributes
    constructor(
        sex: String?,
        preferSex: String?,
        user_id: String?,
        phone_number: String?,
        email: String?,
        username: String?,
        SE: Boolean,
        db: Boolean,
        ui: Boolean,
        oop: Boolean,
        description: String?,
        dateOfBirth: String?,
        profileImageUrl: String?,
        latitude: Double,
        longtitude: Double
    ) {
        this.sex = sex
        this.user_id = user_id
        this.phone_number = phone_number
        this.email = email
        this.username = username
        isSE = SE
        isDatabase = db
        isDesign = ui
        isOop = oop
        isShowDoB = true
        isShowDistance = true
        this.description = description
        this.preferSex = preferSex
        this.dateOfBirth = dateOfBirth
        this.profileImageUrl = profileImageUrl
        this.latitude = latitude
        this.longtitude = longtitude
    }

    override fun toString(): String {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", SE=" + isSE +
                ", Database=" + isDatabase +
                ", UI=" + isDesign +
                ", OOP=" + isOop +
                ", showDoB=" + isShowDoB +
                ", showDistance=" + isShowDistance +
                ", description='" + description + '\'' +
                ", sex='" + sex + '\'' +  //", preferSex='" + preferSex + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}'
    }
}