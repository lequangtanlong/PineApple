package com.ZellyCookies.PineApple.Login

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ZellyCookies.PineApple.R
import com.ZellyCookies.PineApple.Utils.FirebaseMethods
import com.ZellyCookies.PineApple.Utils.GPS
import com.ZellyCookies.PineApple.Utils.User

class RegisterBasicInfo : AppCompatActivity() {
    private var mContext: Context? = null
    private var email: String? = null
    private var username: String? = null
    private var password: String? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mUsername: EditText? = null
    private val loadingPleaseWait: TextView? = null
    private var btnRegister: Button? = null
    var gps: GPS? = null

    //firebase
    private val mAuth: FirebaseAuth? = null
    private val mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val firebaseMethods: FirebaseMethods? = null
    private val mFirebaseDatabase: FirebaseDatabase? = null
    private val myRef: DatabaseReference? = null
    private val append = ""
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerbasic_info)
        mContext = this@RegisterBasicInfo
        Log.d(TAG, "onCreate: started")
        gps = GPS(this)
        initWidgets()
        init()
    }

    private fun init() {
        btnRegister!!.setOnClickListener {
            email = mEmail?.getText().toString()
            username = mUsername?.getText().toString()
            password = mPassword?.getText().toString()
            if (checkInputs(email, username, password)) {
                //find geo location
                val location: Location? = gps?.location
                var latitude = 37.349642
                var longtitude = -121.938987
                if (location != null) {
                    latitude = location.latitude
                    longtitude = location.longitude
                }
                val intent = Intent(this@RegisterBasicInfo, RegisterGender::class.java)
                val user = User(
                    "",
                    "",
                    "",
                    "",
                    email,
                    username,
                    false,
                    false,
                    false,
                    false,
                    "",
                    "",
                    "",
                    latitude,
                    longtitude
                )
                intent.putExtra("password", password)
                intent.putExtra("classUser", user)
                startActivity(intent)
            }
        }
    }

    private fun checkInputs(email: String?, username: String?, password: String?): Boolean {
        Log.d(TAG, "checkInputs: checking inputs for null values.")
        if (email == "" || username == "" || password == "") {
            Toast.makeText(mContext, "All fields must be filed out.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Checks if the email id is valid or not.
        if (!email!!.matches(Regex(emailPattern))) {
            Toast.makeText(
                getApplicationContext(),
                "Invalid email address, enter valid email id and click on Continue",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun initWidgets() {
        Log.d(TAG, "initWidgets: initializing widgets")
        mEmail = findViewById<View>(R.id.input_email) as EditText?
        mUsername = findViewById<View>(R.id.input_username) as EditText?
        btnRegister = findViewById<View>(R.id.btn_register) as Button?
        mPassword = findViewById<View>(R.id.input_password) as EditText?
        mContext = this@RegisterBasicInfo
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}