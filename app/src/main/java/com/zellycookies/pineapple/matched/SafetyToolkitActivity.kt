package com.zellycookies.pineapple.matched

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.login.Login

class SafetyToolkitActivity : AppCompatActivity() {
    private val mContext: Context = this@SafetyToolkitActivity
    private var user : FirebaseUser? = null
    private var userId : String? = null
    private var userSex : String? = null
    private var otherName : String? = null
    private var otherId : String? = null
    private var otherSex : String? = null
    private var userRef : DatabaseReference? = null
    private var otherRef : DatabaseReference? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_toolkit)
        setupFirebaseAuth()
        init()

        addButtonListener()
    }

    private fun init() {
        // Initialize user's info & database reference
        val intent = intent
        otherName = intent.getStringExtra("otherName")
        otherId = intent.getStringExtra("otherId")
        otherSex = intent.getStringExtra("otherSex")
        Log.d(TAG, "Other: $otherName | $otherId | $otherSex")
        user = FirebaseAuth.getInstance().currentUser
        userId = user!!.uid
        checkUserSex()
        otherRef = FirebaseDatabase.getInstance().reference.child(otherSex!!).child(otherId!!)

        val toolbar = findViewById<View>(R.id.toolbartag) as TextView
        toolbar.text = getString(R.string.safety_toolkit)
    }

    private fun blockUser() {
        userRef!!.child("block")
            .child("blocked-users").child(otherId!!).setValue(true)
        otherRef!!.child("block")
            .child("blocked-by").child(userId!!).setValue(true)
    }

    private fun unmatchUser() {
        userRef!!.child("group").child(otherId!!).setValue(null)
        otherRef!!.child("group").child(userId!!).setValue(null)
        userRef!!.child("connections").child("match_result").child(otherId!!).setValue(null)
        otherRef!!.child("connections").child("match_result").child(userId!!).setValue(null)
        otherRef!!.child("connections").child("likeme").child(userId!!).setValue(null)
    }

    private fun dialog(isBlock : Boolean) : AlertDialog {
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val view : View = inflater.inflate(R.layout.dialog_block, null)

        val tvHeader = view.findViewById(R.id.dialog_header) as TextView
        val tvContent = view.findViewById(R.id.dialog_content) as TextView
        val tvUsername = view.findViewById(R.id.dialog_username) as TextView
        tvHeader.text = getString(R.string.block_user)
        tvContent.text = getString(
            if (isBlock) R.string.are_you_sure_you_want_to_block
            else R.string.are_you_sure_you_want_to_unmatch_with
        )
        tvUsername.text = otherName
        return AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(R.string.yes
            ) { dialog, _ ->
                if (isBlock) blockUser()
                else unmatchUser()
                dialog.dismiss()
                returnToMatched()
            }
            .setNegativeButton(R.string.no
            ) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    private fun returnToMatched() {
        val intent = Intent(mContext, Matched_Activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initUserRef() {
        userRef = FirebaseDatabase.getInstance().reference.child(userSex!!).child(userId!!)
    }

    private fun addButtonListener() {
        val btnBlock : Button = findViewById(R.id.btn_block)
        val btnUnmatch : Button = findViewById(R.id.btn_unmatch)

        btnBlock.setOnClickListener {
            dialog(true).show()
        }

        btnUnmatch.setOnClickListener {
            dialog(false).show()
        }
    }

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

    private fun checkUserSex() {
        if (user != null) {
            val maleDb = FirebaseDatabase.getInstance().reference.child("male")
            maleDb.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.key == userId) {
                        userSex = "male"
                        Log.d(TAG,"onChildAdded: the sex is $userSex")
                        initUserRef()
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
            val femaleDb = FirebaseDatabase.getInstance().reference.child("female")
            femaleDb.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.key == userId) {
                        userSex = "female"
                        Log.d(TAG, "onChildAdded: the sex is $userSex")
                        initUserRef()
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
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