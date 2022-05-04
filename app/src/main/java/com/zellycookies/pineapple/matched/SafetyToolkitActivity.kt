package com.zellycookies.pineapple.matched

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.utility.UtilityHistoryActivity
import com.zellycookies.pineapple.utility.ViewWhoYouLikeActivity
import com.zellycookies.pineapple.utils.User

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
        UtilityHistoryActivity.uploadActivity(userSex!!, userId!!, "You blocked $otherName")
    }

    private fun unmatchUser() {
        userRef!!.child("group").child(otherId!!).setValue(null)
        otherRef!!.child("group").child(userId!!).setValue(null)
        userRef!!.child("connections").child("match_result").child(otherId!!).setValue(null)
        otherRef!!.child("connections").child("match_result").child(userId!!).setValue(null)
        otherRef!!.child("connections").child("likeme").child(userId!!).setValue(null)
        UtilityHistoryActivity.uploadActivity(userSex!!, userId!!, "You unmatched with $otherName")
    }

    private fun reportUser(view : View) {
        val rbHarass = view.findViewById<RadioButton>(R.id.rb_harass)
        val rbPretend = view.findViewById<RadioButton>(R.id.rb_pretend)
        val rbSwear = view.findViewById<RadioButton>(R.id.rb_swearing)
        val rbHate = view.findViewById<RadioButton>(R.id.rb_hate_speech)
        val rbOther = view.findViewById<RadioButton>(R.id.rb_other)
        var check = -1

        if (rbHarass.isChecked) check = 0
        if (rbPretend.isChecked) check = 1
        if (rbSwear.isChecked) check = 2
        if (rbHate.isChecked) check = 3
        if (rbOther.isChecked) check = 4

        val content = when (check) {
            0 -> rbHarass.text
            1 -> rbPretend.text
            2 -> rbSwear.text
            3 -> rbHate.text
            4 -> rbOther.text
            else -> null
        }

        when (content) {
            null -> Toast.makeText(this, "Specify the issue before submitting the report", Toast.LENGTH_LONG).show()
            rbOther.text -> dialogReportOther().show()
            else -> uploadReport(content.toString())
        }
    }

    private fun dialog(type : Int) : AlertDialog {
        // type: 0 = Block; 1 = Unmatch; 2 = Report
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val view : View = inflater.inflate(
            if (type != 2) R.layout.dialog_block else R.layout.dialog_report,
            null)

        val tvHeader = view.findViewById(R.id.dialog_header) as TextView
        val tvContent = view.findViewById(R.id.dialog_content) as TextView
        val tvUsername = view.findViewById(R.id.dialog_username) as TextView
        tvHeader.text = getString(
            when (type) {
                0 -> R.string.block_user
                1 -> R.string.unmatch_user
                else -> R.string.report_user
            }
        )
        tvContent.text = getString(
            when (type) {
                0 -> R.string.are_you_sure_you_want_to_block
                1 -> R.string.are_you_sure_you_want_to_unmatch_with
                else -> R.string.report_an_issue_your_have_with_this_user
            }
        )
        tvUsername.text = otherName

        return AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(if (type != 2) R.string.yes else R.string.submit
            ) { dialog, _ ->
                when (type) {
                    0 -> blockUser()
                    1 -> unmatchUser()
                    else -> {
                        reportUser(view)
                    }
                }
                dialog.dismiss()
                if (type != 2) {
                    returnToMatched()
                }
            }
            .setNegativeButton(if (type != 2) R.string.no else R.string.cancel
            ) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    private fun dialogReportOther() : AlertDialog {
        val inflater : LayoutInflater = LayoutInflater.from(this)
        val view : View = inflater.inflate(R.layout.dialog_report_other, null)
        val tvUsername = view.findViewById(R.id.dialog_username) as TextView
        tvUsername.text = otherName
        val tfOther = view.findViewById<EditText>(R.id.tf_other)

        return AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(R.string.submit
            ) { dialog, _ ->
                val content = tfOther.text.toString()
                if (content == "")
                    Toast.makeText(this, "Specify the issue before submitting the report", Toast.LENGTH_LONG).show()
                else uploadReport(content)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel
            ) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    private fun uploadReport(content : String) {
        otherRef!!.child("reports").child(userId!!).setValue(content)
        checkOtherDeactivate()
        Log.d(TAG, content)
        Toast.makeText(this, "Report sent", Toast.LENGTH_SHORT).show()
    }

    // >= 3 reports --> deactivate
    private fun checkOtherDeactivate() {
        val reportList : MutableList<String> = ArrayList()
        otherRef!!.child("reports").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                reportList.add(snapshot.key.toString())
                if (reportList.size >= 3) {
                    Log.d(TAG, "$otherId has received ${reportList.size} reports")
                    otherRef!!.child("deactivated").setValue(true)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
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
        val btnReport : Button = findViewById(R.id.btn_report)

        btnBlock.setOnClickListener {
            dialog(0).show()
        }

        btnUnmatch.setOnClickListener {
            dialog(1).show()
        }

        btnReport.setOnClickListener {
            dialog(2).show()
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