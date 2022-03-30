package com.zellycookies.pineapple.utils

import java.util.*

//Aging methods
class CalculateAge(dob: String) {
    var age = 0


    fun setAge(year: Int, month: Int, day: Int) {
        val dateOfBirth = Calendar.getInstance()
        val today = Calendar.getInstance()
        dateOfBirth[year, month] = day
        var age = today[Calendar.YEAR] - dateOfBirth[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dateOfBirth[Calendar.DAY_OF_YEAR]) {
            age--
        }
        this.age = age
    }

    init {
        val splitDOB = dob.split("-").toTypedArray()
        setAge(splitDOB[2].toInt(), splitDOB[0].toInt(), splitDOB[1].toInt())
    }
}