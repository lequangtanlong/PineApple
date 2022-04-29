package com.zellycookies.pineapple.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zellycookies.pineapple.R


class FirebaseMethods(context: Context) {
    //firebase
    private val mAuth: FirebaseAuth
    private val mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val mFirebaseDatabase: FirebaseDatabase
    private val myRef: DatabaseReference
    private var userID: String? = null
    private val mContext: Context
    fun checkIfUsernameExists(username: String, datasnapshot: DataSnapshot): Boolean {
        Log.d(TAG, "checkIfUsernameExists: checking if $username already exists.")
        val user = User()
        for (ds in userID?.let {
            datasnapshot.child(it)
                .getChildren()
        }!!) { //datasnapshot contains every node in the setting
            Log.d(TAG, "checkIfUsernameExists: datasnapshot: $ds")
            user.username = ds.getValue(User::class.java)?.username
            Log.d(TAG, "checkIfUsernameExists: username: " + user.username)
            if (StringManipulation.expandUsername(user.username) == username) {
                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.username)
                return true
            }
        }
        return false
    }

    /**
     * RegisterBasicInfo a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    fun registerNewEmail(email: String, password: String, username: String?) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    Log.d(TAG, "createUserWithEmail: " + task.isSuccessful())
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //send verification email
                        sendVerificationEmail()
                        userID = mAuth.getCurrentUser()?.getUid()
                        Log.d(TAG, "onComplete: Authstate changed: $userID")
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            mContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // ...
                }
            })
    }

    private fun sendVerificationEmail() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
            } else {
                Toast.makeText(
                    mContext,
                    "couldn't send verification email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addNewUser(user: User) {
        if (user.sex == "female") {
            userID?.let {
                myRef.child(mContext.getString(R.string.dbfemale))
                    .child(it)
                    .setValue(user)
            }
        } else {
            userID?.let {
                myRef.child(mContext.getString(R.string.dbmale))
                    .child(it)
                    .setValue(user)
            }
        }
    }

    fun getUser(dataSnapshot: DataSnapshot, sex: String?, uid: String?): User {
        val user = User()
        for (ds in dataSnapshot.getChildren()) {
            if (ds.key.equals(sex)) {
                val temp: User? = uid?.let { ds.child(it).getValue(User::class.java) }
                if (temp != null) {
                    user.user_id = uid
                    user.username = temp.username
                    user.profileImageUrl = temp.profileImageUrl
                    user.dateOfBirth = temp.dateOfBirth
                    user.description = temp.description
                    user.isSE = temp.isSE
                    user.isOop = temp.isOop
                    user.isDatabase = temp.isDatabase
                    user.isDesign = temp.isDesign
                    user.email = temp.email
                    user.phone_number = temp.phone_number
                    user.latitude = temp.latitude
                    user.longtitude = temp.longtitude
                }

            }
        }
        return user
    }

    fun getUserFromId(dataSnapshot: DataSnapshot, uid: String?): User {
        val user = User()
        for (ds in dataSnapshot.children) {
            val temp: User? = uid?.let { ds.child(it).getValue(User::class.java) }
            if (temp != null) {
                user.username = temp.username
                user.profileImageUrl = temp.profileImageUrl
                user.dateOfBirth = temp.dateOfBirth
                user.description = temp.description
                user.isSE = temp.isSE
                user.isOop = temp.isOop
                user.isDatabase = temp.isDatabase
                user.isDesign = temp.isDesign
                user.email = temp.email
                user.phone_number = temp.phone_number
                user.latitude = temp.latitude
                user.longtitude = temp.longtitude
            } else continue
        }
        return user
    }

    companion object {
        private const val TAG = "FirebaseMethods"
    }

    init {
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.getReference()
        mContext = context
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser()?.getUid()
        }
    }
}