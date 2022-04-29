package com.zellycookies.pineapple.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.conversation.Object.GroupObject
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.utils.FirebaseMethods
import com.zellycookies.pineapple.utils.GPS
import com.zellycookies.pineapple.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.main.ProfileCheckinMain
import com.zellycookies.pineapple.matched.ProfileAdapter

class BlockedList : AppCompatActivity() {
    private val mContext: Context = this@BlockedList
    private var userId: String? = null
    private var userSex: String? = null
    private var lookforSex: String? = null
    private var latitude = 37.349642
    private var longtitude = -121.938987
    private var idGroupList: ArrayList<String>? = null
    var mAdapter: ProfileAdapter? = null
    var blockedList: MutableList<GroupObject> = ArrayList<GroupObject>()
    var copyList: MutableList<GroupObject> = ArrayList<GroupObject>()
    var gps: GPS? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private var dbRef: DatabaseReference? = null
    private var firebaseMethods: FirebaseMethods? = null
    private var mFirebaseFirestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_list)
        firebaseMethods = FirebaseMethods(mContext)

        setupFirebaseAuth()

        val back = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener { onBackPressed() }
        val toolbar = findViewById<View>(R.id.toolbartag) as TextView
        toolbar.text = getString(R.string.blocked_users)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        gps = GPS(this)
        dbRef = FirebaseDatabase.getInstance().reference
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        checkUserSex()
        mAdapter = ProfileAdapter(mContext, R.layout.blocked_item, blockedList)
        val listView = findViewById<View>(R.id.blockedList) as ListView
        listView.adapter = mAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                Log.d(TAG, "onItemClick: The list has been clicked")
                checkClickedItem(position)
            }
    }

    private fun checkUserSex() {
        val maleDb = dbRef!!.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is male")
                    userSex = "male"
                    //update location
                    latitude = dataSnapshot.getValue(User::class.java)!!.latitude
                    longtitude = dataSnapshot.getValue(User::class.java)!!.longtitude
                    lookforSex = dataSnapshot.getValue(User::class.java)!!.preferSex
                    findBlockedUID()
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

                    //update location
                    latitude = dataSnapshot.getValue(User::class.java)!!.latitude
                    longtitude = dataSnapshot.getValue(User::class.java)!!.longtitude
                    lookforSex = dataSnapshot.getValue(User::class.java)!!.preferSex
                    findBlockedUID()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findBlockedUID() {
        Log.d(TAG, "findLikeUID: start to find liker")
        idGroupList = ArrayList()
        val groupRef = dbRef!!.child(userSex!!).child(userId!!).child("block").child("blocked-users")
        groupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val uid = ds.key
                    Log.d(
                        TAG,
                        "onDataChange: uid :$uid"
                    )
                    dbRef!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = firebaseMethods!!.getUser(dataSnapshot, lookforSex, uid)
                            if (!checkDup(user)) {
                                Log.d(TAG, "Day la idGroup :")
                                val groupObject = GroupObject(user)
                                blockedList.add(groupObject)
                                copyList.add(groupObject)
                                mAdapter?.notifyDataSetChanged()
                                Log.d(TAG, "onDataChange: like list size is " + blockedList.size)
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

    private fun checkDup(user: User): Boolean {
        if (blockedList.size != 0) {
            for (groupObject in blockedList) {
                if (groupObject.userMatch.username === user.username) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkClickedItem(position: Int) {
        val user: User = blockedList[position].userMatch
        val userRef = FirebaseDatabase.getInstance().reference.child(userSex!!).child(userId!!)
        val otherRef = FirebaseDatabase.getInstance().reference.child(user.sex!!).child(user.user_id!!)
        userRef.child("block")
            .child("blocked-users").child(user.user_id!!).setValue(null)
        otherRef.child("block")
            .child("blocked-by").child(userId!!).setValue(null)
        Toast.makeText(mContext, "Unblocked ${user.username}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, BlockedList::class.java)
        startActivity(intent)
        finish()
    }

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
        private const val TAG = "BlockedListActivity"
    }
}