package com.zellycookies.pineapple.matched

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zellycookies.pineapple.conversation.ConversationActivity
import com.zellycookies.pineapple.conversation.Object.GroupObject
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.utils.FirebaseMethods
import com.zellycookies.pineapple.utils.GPS
import com.zellycookies.pineapple.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import java.util.*
import com.zellycookies.pineapple.utils.TopNavigationViewHelper
import com.zellycookies.pineapple.R

class Matched_Activity : AppCompatActivity() {
    private val mContext: Context = this@Matched_Activity
    private var userId: String? = null
    private var userSex: String? = null
    private var lookforSex: String? = null
    private var latitude = 37.349642
    private var longtitude = -121.938987
    private var search: EditText? = null
    private var idGroupList: ArrayList<String>? = null
    var mAdapter: ProfileAdapter? = null
    var matchList: MutableList<GroupObject> = ArrayList<GroupObject>()
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
        setContentView(R.layout.activity_matched)
        firebaseMethods = FirebaseMethods(mContext)
        setupFirebaseAuth()
        setupTopNavigationView()
        searchFunc()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        gps = GPS(this)
        dbRef = FirebaseDatabase.getInstance().reference
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        checkUserSex()
        mAdapter = ProfileAdapter(this@Matched_Activity, R.layout.matched_item, matchList)
        val listView = findViewById<View>(R.id.matchList) as ListView
        listView.adapter = mAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Log.d(TAG, "onItemClick: The list has been clicked")
                checkClickedItem(position)
            }
    }

    private fun searchFunc() {
        search = findViewById<View>(R.id.searchBar) as EditText
        search!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchText()
            }

            override fun afterTextChanged(s: Editable) {
                searchText()
            }
        })
    }

    private fun searchText() {
        val text = search!!.text.toString().toLowerCase(Locale.getDefault())
        if (text.length != 0) {
            if (matchList.size != 0) {
                matchList.clear()
                for (groupObject in copyList) {
                    if (groupObject.userMatch.username!!.toLowerCase(Locale.getDefault())
                            .contains(text)
                    ) {
                        matchList.add(groupObject)
                    }
                }
            }
        } else {
            matchList.clear()
            matchList.addAll(copyList)
        }
        mAdapter?.notifyDataSetChanged()
    }

    fun checkUserSex() {
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
                    findMatchUID()
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
                    findMatchUID()
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
        idGroupList = ArrayList()
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
                            val user = firebaseMethods!!.getUser(dataSnapshot, lookforSex, uid)
                            if (!checkDup(user)) {
                                Log.d(TAG, "Day la idGroup :")
                                val groupObject = GroupObject(idGroup, user)
                                matchList.add(groupObject)
                                copyList.add(groupObject)
                                mAdapter?.notifyDataSetChanged()
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

    private fun checkDup(user: User): Boolean {
        if (matchList.size != 0) {
            for (groupObject in matchList) {
                if (groupObject.userMatch.username === user.username) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkClickedItem(position: Int) {
        val groupObject: GroupObject = matchList[position]
        val user: User = groupObject.userMatch
        //calculate distance
        val distance = gps!!.calculateDistance(latitude, longtitude, user.latitude, user.longtitude)
        val intent = Intent(this, ConversationActivity::class.java)
        //        intent.putExtra("classUser", user);
//        intent.putExtra("distance", distance);
        intent.putExtra("groupObject", groupObject)
        startActivity(intent)
    }

    private fun setupTopNavigationView() {
        Log.d(TAG, "setupTopNavigationView: setting up TopNavigationView")
        val tvEx = findViewById<View>(R.id.topNavViewBar) as BottomNavigationView
        TopNavigationViewHelper.setupTopNavigationView(tvEx)
        TopNavigationViewHelper.enableNavigation(mContext, tvEx)
        val menu = tvEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
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
                val intent = Intent(this@Matched_Activity, Login::class.java)

                //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {}
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
        private const val TAG = "Matched_Activity"
        private const val ACTIVITY_NUM = 0
    }
}