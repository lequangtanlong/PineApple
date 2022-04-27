package com.zellycookies.pineapple.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.protobuf.Value
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.conversation.Object.GroupObject
import com.zellycookies.pineapple.introduction.IntroductionMain
import com.zellycookies.pineapple.matched.Matched_Activity
import com.zellycookies.pineapple.utils.FirebaseMethods
import com.zellycookies.pineapple.utils.User
import java.security.acl.Group
import java.util.ArrayList

class DeleteAccountActivity : AppCompatActivity() {
    //firebase
    private val mContext: Context = this@DeleteAccountActivity
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var firebaseMethods: FirebaseMethods? = null
    private var user : FirebaseUser? = null
    private var userSex : String? = null
    private var userId : String? = null
    private var dbRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        setupFirebaseAuth()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().reference

        Log.d(TAG, "Path: ${dbRef!!.child("male")
            .child("yl9nQvsZsyMS44KGFBFzttjW0re2")
            .child("connection")
            .child("likeme").key}")

        checkUserSex()
        firebaseMethods = FirebaseMethods(mContext)
        findViewById<Button>(R.id.btn_deleteAccount).setOnClickListener {
            deleteAccount()
        }
    }

    // Delete account
    private fun deleteAccount() {
        val inputPassword : EditText = findViewById(R.id.input_password)

        Log.d(TAG, "Getting users Credentials")
        val credential : AuthCredential = EmailAuthProvider.getCredential(
            user!!.email!!, inputPassword.text.toString())
        Log.d(TAG, user!!.toString())
        Log.d(TAG, "Deleting user's account")
        user!!.reauthenticate(credential).addOnCompleteListener { taskAuth ->
            if (taskAuth.isSuccessful) {
                Log.d(TAG, "User re-authenticated")
                removeAllRelations()
                user!!.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, IntroductionMain::class.java)
                        startActivity(intent)
                        mAuth!!.signOut()
                        finish()
                    }
                }
            } else {
                Toast.makeText(
                    this@DeleteAccountActivity,
                    "Failed to re-authenticate",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "Failed to re-authenticate user")
            }
        }
    }

    // remove all relations (such as match,...) after deletion
    private fun removeAllRelations() {
        Log.d(TAG, "Removing all relations.")

        val dbRef : DatabaseReference = FirebaseDatabase.getInstance().reference
        removeRelationsInGender(dbRef, false)
        removeRelationsInGender(dbRef, true)
        removeUser()
    }

    private fun removeRelationsInGender(dbRef : DatabaseReference?, isMale : Boolean) {
        val sex = if (isMale) "male" else "female"
        Log.d(TAG, "Removing relations from '$sex'.")
        val ref = dbRef!!.child(sex)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val uid = ds.key
                    Log.d(TAG, "onDataChange: uid: $uid")
                    val connRef = ref.child(uid!!).child("connections")
                    removeRelationsInConn(connRef, "dislikeme")
                    removeRelationsInConn(connRef, "likeme")
                    removeRelationsInConn(connRef, "match_result")
                    removeRelationsInGroup(ref.child(uid).child("group"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun removeUser() {
        Log.d(TAG, "Removing the user.")
        val userRef = dbRef!!.child(userSex!!).child(userId!!)
        userRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.ref.setValue(null)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                mAuth!!.signOut()
                finish()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun removeRelationsInGroup(groupRef : DatabaseReference) {
        Log.d(TAG, "Removing all relations in group.")
        val ref = groupRef.child(userId!!)
        ref.setValue(null)
    }

    private fun removeRelationsInConn(connRef : DatabaseReference, childName : String) {
        Log.d(TAG, "Removing all relations in 'connection : $childName'.")
        val ref = connRef.child(childName).child(userId!!)
        ref.setValue(null)
    }

    private fun checkUserSex() {
        Log.d(TAG, "Checking user's sex")
        val maleDb = dbRef!!.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is male")
                    userSex = "male"
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val femaleDb = dbRef!!.child("female")
        femaleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is female")
                    userSex = "female"
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

/*

    private fun checkUserSex() {
        val maleDb = dbRef!!.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is male")
                    userSex = "male"
                    lookForSex = dataSnapshot.getValue(User::class.java)!!.preferSex
                    findMatchUID()
                    findLikeUID()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val femaleDb = dbRef!!.child("female")
        femaleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is female")
                    userSex = "female"
                    lookForSex = dataSnapshot.getValue(User::class.java)!!.preferSex
                    findMatchUID()
                    findLikeUID()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findMatchUID() {
        Log.d(TAG, "findMatchUID: start to find match")
        val groupRef = dbRef!!.child(userSex!!).child(userId!!).child("group")
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val uid = ds.key
                    val idGroup = ds.value as String?
                    Log.d(
                        TAG,
                        "onDataChange: uid :$uid - idGroup :$idGroup"
                    )
                    dbRef!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = firebaseMethods!!.getUser(dataSnapshot, lookForSex, uid)
                            if (!checkDup(user)) {
                                Log.d(TAG, "Day la idGroup :")
                                val groupObject = GroupObject(idGroup, user)
                                matchList.add(groupObject)
                                Log.d(TAG, "onDataChange: match list size is " + matchList.size)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d(TAG, "onCancelled: test cancel")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun findLikeUID() {
        Log.d(TAG, "findLikeUID: start to find likes")
        val groupRef = dbRef!!.child(userSex!!).child(userId!!).child("connections").child("likeme")
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val uid = ds.key
                    val idGroup = ds.value as String?
                    Log.d(
                        TAG,
                        "onDataChange: uid :$uid - idGroup :$idGroup"
                    )
                    dbRef!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = firebaseMethods!!.getUser(dataSnapshot, lookForSex, uid)
                            if (!checkDup(user)) {
                                Log.d(TAG, "Day la idGroup :")
                                val groupObject = GroupObject(idGroup, user)
                                likeList.add(groupObject)
                                Log.d(TAG, "onDataChange: match list size is " + matchList.size)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d(TAG, "onCancelled: test cancel")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
*/

    private fun checkCurrentUser(user: FirebaseUser?) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in")
        if (user == null) {
            val intent = Intent(this, IntroductionMain::class.java)
            startActivity(intent)
        }
    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: check user")
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
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
        user = FirebaseAuth.getInstance().currentUser
    }

    companion object {
        private const val TAG = "DeleteAccountActivity"
    }
}