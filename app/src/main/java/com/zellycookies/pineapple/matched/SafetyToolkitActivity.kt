package com.zellycookies.pineapple.matched

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.profile.AccountActivity
import com.zellycookies.pineapple.profile.EditProfileActivity

class SafetyToolkitActivity : AppCompatActivity() {
    private val mContext: Context = this@SafetyToolkitActivity
    private var username : String? = null
    private var userId : String? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_toolkit)
        init()
        setupFirebaseAuth()

        addButtonListener()
    }

    private fun init() {
        val intent = intent
        username = intent.getStringExtra("username")
        userId = intent.getStringExtra("userId")
        Log.d(TAG, "User: $username | $userId")

        val toolbar = findViewById<View>(R.id.toolbartag) as TextView
        toolbar.text = R.string.safety_toolkit.toString()
    }

    private fun addButtonListener() {
        val btnBlock : Button = findViewById(R.id.btn_block)
        val btnUnmatch : Button = findViewById(R.id.btn_unmatch)

        btnBlock.setOnClickListener {

        }

        btnUnmatch.setOnClickListener {

        }
    }

    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // user is signed in
                Log.d(TAG, "onAuthStateChanged: signed_in:" + user.uid)
            } else {
                //user is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
                Log.d(TAG, "onAuthStateChanged: navigating back to login screen.")
                val intent = Intent(mContext, Login::class.java)

                //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    companion object {
        private const val TAG = "SafetyToolkitActivity"
    }
}