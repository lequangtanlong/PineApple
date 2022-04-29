package com.zellycookies.pineapple.utility

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.conversation.Object.GroupObject
import com.zellycookies.pineapple.home.HomeSwipeActivity
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.main.Cards
import com.zellycookies.pineapple.main.ProfileCheckinMain
import com.zellycookies.pineapple.matched.ProfileAdapter
import com.zellycookies.pineapple.utils.CalculateAge
import com.zellycookies.pineapple.utils.FirebaseMethods
import com.zellycookies.pineapple.utils.GPS
import com.zellycookies.pineapple.utils.User
import java.util.*

class ViewWhoYouLikeActivity : AppCompatActivity() {
    private val mContext: Context = this@ViewWhoYouLikeActivity
    private var userId: String? = null
    private var userSex: String? = null
    private var lookforSex: String? = null
    private var latitude = 37.349642
    private var longtitude = -121.938987
    private var search: EditText? = null
    private var idGroupList: ArrayList<String>? = null
    var mAdapter: ProfileAdapter? = null
    var likeList: MutableList<GroupObject> = ArrayList<GroupObject>()
    var copyList: MutableList<GroupObject> = ArrayList<GroupObject>()
    var gps: GPS? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var dbRef: DatabaseReference? = null
    private var firebaseMethods: FirebaseMethods? = null
    private var mFirebaseFirestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_who_you_like)
        firebaseMethods = FirebaseMethods(mContext)

        setupFirebaseAuth()

        val back = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener { onBackPressed() }
        searchFunc()
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        gps = GPS(this)
        dbRef = FirebaseDatabase.getInstance().reference
        mFirebaseFirestore = FirebaseFirestore.getInstance()
        checkUserSex()
        mAdapter = ProfileAdapter(this@ViewWhoYouLikeActivity, R.layout.vwyl_item, likeList)
        val listView = findViewById<View>(R.id.likeList) as ListView
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
            if (likeList.size != 0) {
                likeList.clear()
                for (groupObject in copyList) {
                    if (groupObject.userMatch.username!!.toLowerCase(Locale.getDefault())
                            .contains(text)
                    ) {
                        likeList.add(groupObject)
                    }
                }
            }
        } else {
            likeList.clear()
            likeList.addAll(copyList)
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

                    //update location
                    latitude = dataSnapshot.getValue(User::class.java)!!.latitude
                    longtitude = dataSnapshot.getValue(User::class.java)!!.longtitude
                    lookforSex = dataSnapshot.getValue(User::class.java)!!.preferSex
                    findLikeUID()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findLikeUID() {
        Log.d(TAG, "findLikeUID: start to find liker")
        idGroupList = ArrayList()

        val potentialMatch = FirebaseDatabase.getInstance().reference.child(lookforSex!!)
        potentialMatch.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists() && dataSnapshot.key != userId && dataSnapshot.child("connections").child("likeme").hasChild(userId!!)) {
                    val curUser = dataSnapshot.getValue(User::class.java)
                    val groupObject = GroupObject(curUser!!)
                    likeList.add(groupObject)
                    copyList.add(groupObject)
                    mAdapter?.notifyDataSetChanged()
                    Log.d(TAG, "onDataChange: like list size is " + likeList.size)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkDup(user: User): Boolean {
        if (likeList.size != 0) {
            for (groupObject in likeList) {
                if (groupObject.userMatch.username === user.username) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkClickedItem(position: Int) {
        val groupObject: GroupObject = likeList[position]
        val user: User = groupObject.userMatch
        //calculate distance
        val distance = gps!!.calculateDistance(latitude, longtitude, user.latitude, user.longtitude)
        val intent = Intent(this@ViewWhoYouLikeActivity, ProfileCheckinMain::class.java)
        //        intent.putExtra("classUser", user);
//        intent.putExtra("distance", distance);

        val name: String? = user.username
        val dob: String? = user.dateOfBirth
        val bio: String? = user.description
        val isSE: String = if (user.isSE) "SE\t" else " "
        val isOOP: String = if (user.isOop) "OOP\t" else " "
        val isUI: String = if (user.isDesign) "UI/UX\t" else " "
        val isDB: String = if (user.isDatabase) "DB" else " "
        val interest: String = "$isSE$isOOP$isUI$isDB."
        val profileImageURL : String? = user.profileImageUrl

        intent.putExtra("name", name)
        intent.putExtra("dob", dob)
        intent.putExtra("bio", bio)
        intent.putExtra("interest", interest)
        intent.putExtra("distance", distance)
        intent.putExtra("photo", profileImageURL)

        startActivity(intent)
    }

    /**
     * Setup the firebase auth object
     */
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
                val intent = Intent(this@ViewWhoYouLikeActivity, Login::class.java)

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
        private const val TAG = "Matched_Activity"
        private const val ACTIVITY_NUM = 0
    }
}