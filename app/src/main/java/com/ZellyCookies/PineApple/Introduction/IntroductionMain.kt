package com.ZellyCookies.PineApple.Introduction

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ZellyCookies.PineApple.Login.Login
import com.ZellyCookies.PineApple.Login.RegisterBasicInfo
import com.ZellyCookies.PineApple.R


//import android.support.v7.app.AppCompatActivity;
class IntroductionMain : AppCompatActivity() {
    private var signupButton: Button? = null
    private var loginButton: Button? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction_main)
        signupButton = findViewById<View>(R.id.signup_button) as Button?
        signupButton!!.setOnClickListener { openEmailAddressEntryPage() }
        loginButton = findViewById<View>(R.id.login_button) as Button?
        loginButton!!.setOnClickListener { openLoginPage() }
    }

    fun openLoginPage() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun openEmailAddressEntryPage() {
        val intent = Intent(this, RegisterBasicInfo::class.java)
        startActivity(intent)
    }
}