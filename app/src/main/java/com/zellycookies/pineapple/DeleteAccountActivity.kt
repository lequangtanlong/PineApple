package com.zellycookies.pineapple

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zellycookies.pineapple.introduction.IntroductionMain

class DeleteAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        setupFirebaseAuth()
        findViewById<Button>(R.id.btn_deleteAccount).setOnClickListener {
            deleteAccount()
        }
    }

    // Delete account
    fun deleteAccount() {
        val inputEmail : EditText = findViewById(R.id.input_email)
        val inputPassword : EditText = findViewById(R.id.input_password)

        Log.d(TAG, "Getting users Credentials")
        var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val credential : AuthCredential = EmailAuthProvider.getCredential(
            inputEmail.text.toString(), inputPassword.text.toString()
        )
        Log.d(TAG, "Deleting user's account")
        user!!.reauthenticate(credential).addOnCompleteListener {
            Log.d(TAG, "User re-authenticated")
            user!!.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    user = null
                    checkCurrentUser(user)
                    Log.d(TAG, "User account deleted")
                }
            }
        }
    }

    private fun checkCurrentUser(user: FirebaseUser?) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in")
        if (user == null) {
            val intent = Intent(this, IntroductionMain::class.java)
            startActivity(intent)
        }
    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: check user")
        var mAuth = FirebaseAuth.getInstance()
        val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            //check if the user is logged in
            checkCurrentUser(user)
            if (user != null) {
                // user is signed in
                Log.d(TAG, "onAuthStateChanged: signed_in:" + user.uid)
            } else {
                //user is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
            }
        }
    }

    companion object {
        private const val TAG = "DeleteAccountActivity"
    }
}