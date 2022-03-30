package com.zellycookies.pineapple.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SettingsActivity : AppCompatActivity() {
    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //setup firebase
        setupFirebaseAuth()

        //setup display content
        buttonSetup()
        val toolbar = findViewById<View>(R.id.toolbartag) as TextView
        toolbar.text = "Profile"

        //setup buttons
        val back = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener { onBackPressed() }
    }

    private fun buttonSetup() {
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            funLogout()
        }
        findViewById<Button>(R.id.btn_changePassword).setOnClickListener {
            funChangePassword()
        }
    }

    //Logout/Signout
    private fun funLogout() {
        mAuth!!.signOut()
        finish()
    }

    // Change password
    private fun funChangePassword() {
        val intent = Intent(this@SettingsActivity, ChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    //----------------------------------------Firebase----------------------------------------
    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // user is signed in
                Log.d(TAG, "onAuthStateChanged: signed_in:" + user.uid)
            } else {
                //user is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
                Log.d(TAG, "onAuthStateChanged: navigating back to login screen.")
                val intent = Intent(this@SettingsActivity, Login::class.java)

                //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
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
        private const val TAG = "SettingsActivity"
    }
}