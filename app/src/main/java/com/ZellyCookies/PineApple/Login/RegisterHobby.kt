package com.ZellyCookies.PineApple.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ZellyCookies.PineApple.R
import com.ZellyCookies.PineApple.Utils.FirebaseMethods
import com.ZellyCookies.PineApple.Utils.User

class RegisterHobby : AppCompatActivity() {
    //User Info
    var userInfo: User? = null
    var password: String? = null
    private var mContext: Context? = null
    private var hobbiesContinueButton: Button? = null
    private var seSelectionButton: Button? = null
    private var dbSelectionButton: Button? = null
    private var uiSelectionButton: Button? = null
    private var opSelectionButton: Button? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var firebaseMethods: FirebaseMethods? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null
    private var append = ""
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_hobby)
        mContext = this@RegisterHobby
        firebaseMethods = FirebaseMethods(mContext!!)
        Log.d(TAG, "onCreate: started")
        val intent: Intent = getIntent()
        userInfo = intent.getSerializableExtra("classUser") as User
        password = intent.getStringExtra("password")
        initWidgets()
        setupFirebaseAuth()
        init()
    }

    private fun initWidgets() {
        seSelectionButton = findViewById<View>(R.id.seSelectionButton) as Button?
        dbSelectionButton = findViewById<View>(R.id.dbSelectionButton) as Button?
        uiSelectionButton = findViewById<View>(R.id.uiSelectionButton) as Button?
        opSelectionButton = findViewById<View>(R.id.opSelectionButton) as Button?
        hobbiesContinueButton = findViewById<View>(R.id.hobbiesContinueButton) as Button?

        // Initially all the buttons needs to be grayed out so this code is added, on selection we will enable it later
        seSelectionButton!!.alpha = .5f
        dbSelectionButton!!.alpha = .5f
        uiSelectionButton!!.alpha = .5f
        opSelectionButton!!.alpha = .5f
        seSelectionButton!!.setOnClickListener { seButtonClicked() }
        dbSelectionButton!!.setOnClickListener { dbButtonClicked() }
        uiSelectionButton!!.setOnClickListener { uiButtonClicked() }
        opSelectionButton!!.setOnClickListener { opButtonClicked() }
    }

    fun seButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (seSelectionButton!!.alpha == 1.0f) {
            seSelectionButton!!.alpha = .5f
            seSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isSE=(false)
        } else {
            seSelectionButton!!.alpha = 1.0f
            seSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isSE=(true)
        }
    }

    fun dbButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (dbSelectionButton!!.alpha == 1.0f) {
            dbSelectionButton!!.alpha = .5f
            dbSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isDatabase=(false)
        } else {
            dbSelectionButton!!.alpha = 1.0f
            dbSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isDatabase=(true)
        }
    }

    fun uiButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (uiSelectionButton!!.alpha == 1.0f) {
            uiSelectionButton!!.alpha = .5f
            uiSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isDesign=(false)
        } else {
            uiSelectionButton!!.alpha = 1.0f
            uiSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isDesign=(true)
        }
    }

    fun opButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (opSelectionButton!!.alpha == 1.0f) {
            opSelectionButton!!.alpha = .5f
            opSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isOop=(false)
        } else {
            opSelectionButton!!.alpha = 1.0f
            opSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isOop=(true)
        }
    }

    fun init() {
        hobbiesContinueButton!!.setOnClickListener {
            firebaseMethods?.registerNewEmail(
                userInfo?.email!!,
                password!!,
                userInfo?.username
            )
        }
    }
    //----------------------------------------Firebase----------------------------------------
    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance() // get database instance
        myRef = mFirebaseDatabase!!.getReference()
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user: FirebaseUser? = firebaseAuth.getCurrentUser()
                if (user != null) {
                    // user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid())
                    myRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) { //show when the data changes
                            //1st check: make sure the username is not already in use
                            if (userInfo?.username?.let {
                                    firebaseMethods?.checkIfUsernameExists(
                                        it,
                                        snapshot
                                    )
                                } == true
                            ) {
                                append = myRef?.push()?.getKey()?.substring(3, 10).toString()
                                Log.d(
                                    TAG,
                                    "onDataChange: username already exists. Appending random string to name: $append"
                                )
                            }
                            userInfo?.username = userInfo?.username.toString() + append

                            //add new user to the database
                            //add new_user_account setting to the database
                            userInfo?.let { firebaseMethods?.addNewUser(it) }
                            Toast.makeText(
                                mContext,
                                "Signup successful. Sending verification email.",
                                Toast.LENGTH_SHORT
                            ).show()
                            mAuth!!.signOut()
                            val intent = Intent(this@RegisterHobby, Login::class.java)
                            startActivity(intent)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


//                        fun onCancelled(databaseError: DatabaseError?) {}
                    })
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuthListener?.let { mAuth?.addAuthStateListener(it) }
    }

    protected override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth?.removeAuthStateListener(mAuthListener!!)
        }
    }

    companion object {
        private const val TAG = "RegisterHobby"
    }
}