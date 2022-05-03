package com.zellycookies.pineapple.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.home.HomeSwipeActivity
import com.zellycookies.pineapple.profile.Profile_Activity


//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
class Login : AppCompatActivity() {
    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mContext: Context? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null

    private lateinit var googleSignInButton: Button
    var mGoogleSignInClient: GoogleSignInClient? = null
    val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_login)
        mEmail = findViewById<View>(R.id.input_email) as EditText?
        mPassword = findViewById<View>(R.id.input_password) as EditText?
        mContext = this@Login
        setupFirebaseAuth()

        init()

        googleSignInButton = findViewById(R.id.btn_login_google)
        googleSignInButton.setOnClickListener {
            signIn();
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.d(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this@Login, HomeSwipeActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this@Login,
                        "Authentication Failed.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }


    private fun isStringNull(string: String): Boolean {
        Log.d(TAG, "isStringNull: checking string if null.")
        return string == ""
    }

    //----------------------------------------Firebase----------------------------------------
    private fun init() {
        //initialize the button for logging in
        val btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnLogin.setOnClickListener {
            Log.d(TAG, "onClick: attempting to log in")
            val email: String = mEmail?.getText().toString()
            val password: String = mPassword?.getText().toString()
            if (isStringNull(email) || isStringNull(password)) {
                Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this@Login, object : OnCompleteListener<AuthResult?> {
                        override fun onComplete(task: Task<AuthResult?>) {
                            val user: FirebaseUser? = mAuth?.getCurrentUser()
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                try {
                                    if (user != null) {
                                        if (user.isEmailVerified()) {
                                            Log.d(TAG, "onComplete: success, email is verified.")
                                            val intent = Intent(this@Login, HomeSwipeActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                mContext,
                                                "Email is not verified \n check your email inbox.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            mAuth?.signOut()
                                        }
                                    }
                                } catch (e: NullPointerException) {
                                    Log.e(
                                        TAG,
                                        "signInWithEmail: onComplete: " + task.isSuccessful()
                                    )
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException())
                                Toast.makeText(
                                    this@Login, R.string.auth_failed,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }
        }
        val linkSignUp: TextView = findViewById<View>(R.id.link_signup) as TextView
        linkSignUp.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onClick: navigating to register screen")
            val intent = Intent(this@Login, RegisterBasicInfo::class.java)
            startActivity(intent)
        })
        /**
         * If the user is logged in then navigate to HomeActivity and call 'finish()'
         */

        val linkForgotPassword = findViewById<View>(R.id.link_forgotPassword)
        linkForgotPassword.setOnClickListener {
            Log.d(TAG, "onClick: navigating to forgot password screen")
            val intent = Intent(this@Login, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        if (mAuth != null && mAuth?.currentUser != null) {
            val intent = Intent(this@Login, Profile_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        //mAuth!!.signOut()
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.getCurrentUser()
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid())
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out")
                }
            }
        }
    }

    override fun onBackPressed() {}

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuthListener?.let { mAuth?.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth?.removeAuthStateListener(mAuthListener!!)
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}