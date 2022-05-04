package com.zellycookies.pineapple.login

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
import com.zellycookies.pineapple.utils.FirebaseMethods
import com.zellycookies.pineapple.utils.User
import com.zellycookies.pineapple.R

class RegisterHobby : AppCompatActivity() {
    //User Info
    var userInfo: User? = null
    var password: String? = null
    private var mContext: Context? = null
    private var hobbiesContinueButton: Button? = null
    private var moviesSelectionButton: Button? = null
    private var foodSelectionButton: Button? = null
    private var artSelectionButton: Button? = null
    private var musicSelectionButton: Button? = null

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
        moviesSelectionButton = findViewById<View>(R.id.moviesSelectionButton) as Button?
        foodSelectionButton = findViewById<View>(R.id.foodSelectionButton) as Button?
        artSelectionButton = findViewById<View>(R.id.artSelectionButton) as Button?
        musicSelectionButton = findViewById<View>(R.id.musicSelectionButton) as Button?
        hobbiesContinueButton = findViewById<View>(R.id.hobbiesContinueButton) as Button?

        // Initially all the buttons needs to be grayed out so this code is added, on selection we will enable it later
        moviesSelectionButton!!.alpha = .5f
        foodSelectionButton!!.alpha = .5f
        artSelectionButton!!.alpha = .5f
        musicSelectionButton!!.alpha = .5f
        moviesSelectionButton!!.setOnClickListener { moviesButtonClicked() }
        foodSelectionButton!!.setOnClickListener { foodButtonClicked() }
        artSelectionButton!!.setOnClickListener { artButtonClicked() }
        musicSelectionButton!!.setOnClickListener { musicButtonClicked() }
    }

    fun moviesButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (moviesSelectionButton!!.alpha == 1.0f) {
            moviesSelectionButton!!.alpha = .5f
            moviesSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isHobby_movies=(false)
        } else {
            moviesSelectionButton!!.alpha = 1.0f
            moviesSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isHobby_movies=(true)
        }
    }

    fun foodButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (foodSelectionButton!!.alpha == 1.0f) {
            foodSelectionButton!!.alpha = .5f
            foodSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isHobby_food=(false)
        } else {
            foodSelectionButton!!.alpha = 1.0f
            foodSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isHobby_food=(true)
        }
    }

    fun artButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (artSelectionButton!!.alpha == 1.0f) {
            artSelectionButton!!.alpha = .5f
            artSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isHobby_art=(false)
        } else {
            artSelectionButton!!.alpha = 1.0f
            artSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isHobby_art=(true)
        }
    }

    fun musicButtonClicked() {
        // this is to toggle between selection and non selection of button
        if (musicSelectionButton!!.alpha == 1.0f) {
            musicSelectionButton!!.alpha = .5f
            musicSelectionButton!!.setBackgroundResource(R.drawable.btn_alt)
            userInfo?.isHobby_music=(false)
        } else {
            musicSelectionButton!!.alpha = 1.0f
            musicSelectionButton!!.setBackgroundResource(R.drawable.btn_main)
            userInfo?.isHobby_music=(true)
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
                            userInfo?.user_id = user.uid
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