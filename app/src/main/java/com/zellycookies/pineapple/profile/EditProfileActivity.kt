package com.zellycookies.pineapple.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zellycookies.pineapple.login.Login
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Boolean
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.toString
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.utility.UtilityHistoryActivity

class EditProfileActivity : AppCompatActivity() {
    private val mContext: Context = this@EditProfileActivity

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private var mPhotoDB: DatabaseReference? = null
    private var mProfileImage: ImageView? = null
    private var userId: String? = null
    private var profileImageUri: String? = null
    private var resultUri: Uri? = null
    private var userSex: String? = null
    private var phoneNumber: EditText? = null
    private var aboutMe: EditText? = null
    private var username: TextView? = null
    private var moviesCheckBox: CheckBox? = null
    private var foodCheckBox: CheckBox? = null
    private var artCheckBox: CheckBox? = null
    private var musicCheckBox: CheckBox? = null
    private var DoBCheckBox: CheckBox? = null
    private var distanceCheckBox: CheckBox? = null
    private var isHobby_moviesClicked = false
    private var isHobby_foodClicked = false
    private var isHobby_musicClicked = false
    private var isHobby_artClicked = false
    private var isClicked = false
    private var isShowDoB = true
    private var isShowDistance = true
    private var userSexSelection: RadioGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val toolbar = findViewById<View>(R.id.toolbartag) as TextView
        toolbar.text = "Profile"
        setupFirebaseAuth()
        val back = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener { onBackPressed() }
        mProfileImage = findViewById<View>(R.id.profileImage) as ImageView
        phoneNumber = findViewById<View>(R.id.edit_phone) as EditText
        aboutMe = findViewById<View>(R.id.edit_aboutme) as EditText
        username = findViewById<View>(R.id.tvUserName) as TextView
        userSexSelection = findViewById<View>(R.id.radioGroupUserSex) as RadioGroup
        moviesCheckBox = findViewById<View>(R.id.checkbox_movies) as CheckBox
        foodCheckBox = findViewById<View>(R.id.checkbox_food) as CheckBox
        artCheckBox = findViewById<View>(R.id.checkbox_art) as CheckBox
        musicCheckBox = findViewById<View>(R.id.checkbox_music) as CheckBox
        DoBCheckBox = findViewById<View>(R.id.settings_showDoB) as CheckBox
        distanceCheckBox = findViewById<View>(R.id.settings_showDistance) as CheckBox
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d(TAG, "onCreate: user id is$userId")
        checkUserSex()
        mProfileImage!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    private val userData: Unit
        private get() {
            mPhotoDB!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        val map = dataSnapshot.value as Map<String, Any?>?
                        if (map!!["profileImageUrl"] != null) {
                            profileImageUri = map["profileImageUrl"].toString()
                            Log.d(
                                TAG,
                                "onDataChange: the profileImageUri is$profileImageUri"
                            )
                            when (profileImageUri) {
                                "defaultFemale" -> mProfileImage?.let {
                                    Glide.with(application)
                                        .load(R.drawable.img_ava_female).into(it)
                                }
                                "defaultMale" -> mProfileImage?.let {
                                    Glide.with(application)
                                        .load(R.drawable.img_ava_male).into(it)
                                }
                                else -> Glide.with(application).load(profileImageUri).into(
                                    mProfileImage!!
                                )
                            }
                        }
                        if (map["username"] != null) {
                            username!!.text = map["username"].toString()
                        }
                        if (map["phone_number"] != null) {
                            phoneNumber!!.setText(map["phone_number"].toString())
                        }
                        if (map["description"] != null) {
                            aboutMe!!.setText(map["description"].toString())
                        }
                        if (map["sex"] != null) {
                            if (map["sex"].toString().equals("male", ignoreCase = true)) {
                                userSexSelection!!.check(R.id.maleSelction)
                            } else {
                                userSexSelection!!.check(R.id.femaleSelection)
                            }
                        }
                        if (Boolean.valueOf(map["hobby_movies"].toString()) == true) {
                            isHobby_moviesClicked = true
                            moviesCheckBox!!.isChecked = true
                        } else {
                            isHobby_moviesClicked = false
                            moviesCheckBox!!.isChecked = false
                        }
                        if (Boolean.valueOf(map["hobby_food"].toString()) == true) {
                            isHobby_foodClicked = true
                            foodCheckBox!!.isChecked = true
                        } else {
                            isHobby_foodClicked = false
                            foodCheckBox!!.isChecked = false
                        }
                        if (Boolean.valueOf(map["hobby_music"].toString()) == true) {
                            isHobby_musicClicked = true
                            musicCheckBox!!.isChecked = true
                        } else {
                            isHobby_musicClicked = false
                            musicCheckBox!!.isChecked = false
                        }
                        if (Boolean.valueOf(map["hobby_art"].toString()) == true) {
                            isHobby_artClicked = true
                            artCheckBox!!.isChecked = true
                        } else {
                            isHobby_artClicked = false
                            artCheckBox!!.isChecked = false
                        }
                        if (Boolean.valueOf(map["showDoB"].toString()) == true) {
                            isShowDoB = true
                            DoBCheckBox!!.isChecked = true
                        } else {
                            isShowDoB = false
                            DoBCheckBox!!.isChecked = false
                        }
                        if (Boolean.valueOf(map["showDistance"].toString()) == true) {
                            isShowDistance = true
                            distanceCheckBox!!.isChecked = true
                        } else {
                            isShowDistance = false
                            distanceCheckBox!!.isChecked = false
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun saveUserPhoto() {
        if (resultUri != null) {
            val filepath = FirebaseStorage.getInstance().reference.child("profileImages").child(
                userId!!
            )
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, resultUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            val uploadTask = filepath.putBytes(data)
            //            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    finish();
//                }
//            });
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                filepath.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    //                        FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUri.toString());
                    //                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                    var userInfo = mutableMapOf<String, Any>()
                    userInfo.put("profileImageUrl", downloadUri.toString())
                    mPhotoDB!!.updateChildren(userInfo)
                }
            }

//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                    Map userInfo = new HashMap<>();
//                    userInfo.put("profileImageUrl", downloadUrl.toString());
//                    mPhotoDB.updateChildren(userInfo);
//
//                    return;
//                }
//            });
        }
    }

    private fun saveUserData() {
        var userInfo: MutableMap<String, Any> = mutableMapOf()
//        userInfo.put("phone_number", phoneNumber!!.text.toString())
//        userInfo.put("description", aboutMe!!.text.toString())
//        userInfo.put("se", isSEClicked)
//        userInfo.put("database", isDatabaseClicked)
//        userInfo.put("design", isDesignClicked)
//        userInfo.put("oop", isOopClicked)

        userInfo["phone_number"] = phoneNumber!!.text.toString()
        userInfo["description"] = aboutMe!!.text.toString()
        userInfo["hobby_movies"] = isHobby_moviesClicked
        userInfo["hobby_food"] = isHobby_foodClicked
        userInfo["hobby_art"] = isHobby_artClicked
        userInfo["hobby_music"] = isHobby_musicClicked
        userInfo["showDoB"] = isShowDoB
        userInfo["showDistance"] = isShowDistance

//Updation of sex is not allowed once profile is created.
//        if (((RadioButton)findViewById(userSexSelection.getCheckedRadioButtonId())).getText().toString().equalsIgnoreCase("male"))
//        {
//            userInfo.put("sex","male");
//        }
//        else
//        {
//            userInfo.put("sex","female");
//        }
        mPhotoDB!!.updateChildren(userInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageUri = data!!.data
            resultUri = imageUri
            mProfileImage!!.setImageURI(resultUri)
        }
    }

    fun checkUserSex() {
        val user = FirebaseAuth.getInstance().currentUser
        val maleDb = FirebaseDatabase.getInstance().reference.child("male")
        maleDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.key == userId) {
                    Log.d(TAG, "onChildAdded: the sex is male")
                    userSex = "male"
                    mPhotoDB = FirebaseDatabase.getInstance().reference.child(userSex!!).child(
                        userId!!
                    )
                    userData
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
                if (dataSnapshot.key == user!!.uid) {
                    Log.d(TAG, "onChildAdded: the sex is female")
                    userSex = "female"
                    mPhotoDB = FirebaseDatabase.getInstance().reference.child(userSex!!).child(
                        userId!!
                    )
                    userData
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun saveAndBack(view: View?) {
        saveUserPhoto()
        saveUserData()
        UtilityHistoryActivity.uploadActivity(userSex!!, userId!!, "You edited your profile")
        val intent = Intent(mContext, NewSettingsActivity::class.java)
        startActivity(intent)
    }
    //----------------------------------------Firebase----------------------------------------
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
                val intent = Intent(this@EditProfileActivity, Login::class.java)

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

    fun onHobbyCheckBoxClicked(view: View) {
        val checked = (view as CheckBox).isChecked
        when (view.getId()) {
            R.id.checkbox_food -> isHobby_foodClicked = checked
            R.id.checkbox_art -> isHobby_artClicked = checked
            R.id.checkbox_movies -> isHobby_moviesClicked = checked
            R.id.checkbox_music -> isHobby_musicClicked = checked
            R.id.settings_showDoB -> isShowDoB = checked
            R.id.settings_showDistance -> isShowDistance = checked
        }
    }

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}