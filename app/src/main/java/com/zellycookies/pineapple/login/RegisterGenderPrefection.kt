package com.zellycookies.pineapple.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utils.User

class RegisterGenderPrefection : AppCompatActivity() {
    var password: String? = null
    var user: User? = null
    private var preferenceContinueButton: Button? = null
    private var maleSelectionButton: Button? = null
    private var femaleSelectionButton: Button? = null
    var preferMale = true
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gender_prefection)
        val intent: Intent = getIntent()
        user = intent.getSerializableExtra("classUser") as User
        password = intent.getStringExtra("password")
        maleSelectionButton = findViewById<View>(R.id.maleSelectionButton) as Button?
        femaleSelectionButton = findViewById<View>(R.id.femaleSelectionButton) as Button?
        preferenceContinueButton = findViewById<View>(R.id.preferenceContinueButton) as Button?

        //By default male has to be selected so below code is added
        femaleSelectionButton!!.alpha = .5f
        femaleSelectionButton!!.setBackgroundColor(Color.GRAY)
        maleSelectionButton!!.setOnClickListener { maleButtonSelected() }
        femaleSelectionButton!!.setOnClickListener { femaleButtonSelected() }
        preferenceContinueButton!!.setOnClickListener { openAgeEntryPage() }
    }

    fun maleButtonSelected() {
        preferMale = true
        maleSelectionButton!!.setBackgroundColor(Color.parseColor("#FF4081"))
        maleSelectionButton!!.alpha = 1.0f
        femaleSelectionButton!!.alpha = .5f
        femaleSelectionButton!!.setBackgroundColor(Color.GRAY)
    }

    fun femaleButtonSelected() {
        preferMale = false
        femaleSelectionButton!!.setBackgroundColor(Color.parseColor("#FF4081"))
        femaleSelectionButton!!.alpha = 1.0f
        maleSelectionButton!!.alpha = .5f
        maleSelectionButton!!.setBackgroundColor(Color.GRAY)
    }

    fun openAgeEntryPage() {
        val preferSex = if (preferMale) "male" else "female"
        user?.preferSex = preferSex
        val intent = Intent(this, RegisterAge::class.java)
        intent.putExtra("password", password)
        intent.putExtra("classUser", user)
        startActivity(intent)
    }
}