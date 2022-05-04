package com.zellycookies.pineapple.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utils.User

import java.text.SimpleDateFormat
import java.util.*

class RegisterAge : AppCompatActivity() {
    var password: String? = null
    var user: User? = null
    private var ageSelectionPicker: DatePicker? = null
    private var ageContinueButton: Button? = null

    // age limit
    private val ageLimit = 13
    var dateFormatter = SimpleDateFormat("MM-dd-yyyy")
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_age)
        val intent: Intent = getIntent()
        user = intent.getSerializableExtra("classUser") as User
        password = intent.getStringExtra("password")
        ageSelectionPicker = findViewById<View>(R.id.ageSelectionPicker) as DatePicker?
        ageContinueButton = findViewById<View>(R.id.ageContinueButton) as Button?
        ageContinueButton!!.setOnClickListener { openHobbiesEntryPage() }
    }

    fun openHobbiesEntryPage() {
        val age = getAge(
            ageSelectionPicker?.getYear()!!,
            ageSelectionPicker?.getMonth()!!,
            ageSelectionPicker?.getDayOfMonth()!!
        )

        // if user is above 13 years old then only he/she will be allowed to register to the system.
        if (age > ageLimit) {
            // code for converting date to string
            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = ageSelectionPicker?.getYear()!!
            cal[Calendar.MONTH] = ageSelectionPicker?.getMonth()!!
            cal[Calendar.DAY_OF_MONTH] = ageSelectionPicker?.getDayOfMonth()!!
            val dateOfBirth = cal.time
            val strDateOfBirth = dateFormatter.format(dateOfBirth)

            // code to set the dateOfBirthAttribute.
            user?.dateOfBirth = strDateOfBirth
            val intent = Intent(this, RegisterHobby::class.java)
            intent.putExtra("password", password)
            intent.putExtra("classUser", user)
            startActivity(intent)
        } else {
            Toast.makeText(
                getApplicationContext(),
                "Age of the user should be greater than $ageLimit !!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // get the current age of the user.
    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dateOfBirth = Calendar.getInstance()
        val today = Calendar.getInstance()
        dateOfBirth[year, month] = day
        var age = today[Calendar.YEAR] - dateOfBirth[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dateOfBirth[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }
}