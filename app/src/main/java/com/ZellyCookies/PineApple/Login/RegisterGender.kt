package com.ZellyCookies.PineApple.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ZellyCookies.PineApple.R
import com.ZellyCookies.PineApple.Utils.User

class RegisterGender : AppCompatActivity() {
    var password: String? = null
    var user: User? = null
    private var genderContinueButton: Button? = null
    private var maleSelectionButton: Button? = null
    private var femaleSelectionButton: Button? = null
    var male = true
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gender)
        val intent: Intent = getIntent()
        user = intent.getSerializableExtra("classUser") as User
        password = intent.getStringExtra("password")
        maleSelectionButton = findViewById<View>(R.id.maleSelectionButton) as Button?
        femaleSelectionButton = findViewById<View>(R.id.femaleSelectionButton) as Button?
        genderContinueButton = findViewById<View>(R.id.genderContinueButton) as Button?

        //By default male has to be selected
        femaleSelectionButton!!.alpha = .5f
        maleSelectionButton!!.setOnClickListener { maleButtonSelected() }
        femaleSelectionButton!!.setOnClickListener { femaleButtonSelected() }
        genderContinueButton!!.setOnClickListener { openPreferenceEntryPage() }
    }

    fun maleButtonSelected() {
        male = true
        maleSelectionButton!!.setBackgroundResource(R.drawable.btn_main_default)
        maleSelectionButton!!.alpha = 1f
        femaleSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
        femaleSelectionButton!!.alpha = .5f
    }

    fun femaleButtonSelected() {
        male = false
        maleSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
        maleSelectionButton!!.alpha = .5f
        femaleSelectionButton!!.setBackgroundResource(R.drawable.btn_main_default)
        femaleSelectionButton!!.alpha = 1f
    }

    fun openPreferenceEntryPage() {
        val ownSex = if (male) "male" else "female"
        user?.sex = ownSex
        //set default prefer sex
        user?.preferSex = ownSex
        //set default photo
        val defaultPhoto = if (male) "defaultMale" else "defaultFemale"
        user?.profileImageUrl = defaultPhoto
        val intent = Intent(this, RegisterAge::class.java)
        intent.putExtra("password", password)
        intent.putExtra("classUser", user)
        startActivity(intent)
    }
}