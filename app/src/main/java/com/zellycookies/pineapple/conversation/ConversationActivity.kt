package com.zellycookies.pineapple.conversation

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zellycookies.pineapple.conversation.Adapter.ConversationAdapter
import com.zellycookies.pineapple.conversation.Object.GroupObject
import com.zellycookies.pineapple.conversation.Object.MessageObject
import com.zellycookies.pineapple.login.Login
import com.zellycookies.pineapple.matched.ProfileCheckinMatched
import com.zellycookies.pineapple.utils.GPS
import com.zellycookies.pineapple.utils.User
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.*
import java.util.*
import com.zellycookies.pineapple.R
//
class ConversationActivity : AppCompatActivity() {
    var action = "INIT"
    private var mGroupObject: GroupObject? = null
    private var userMatched: User? = null
    private var userId: String? = null
    private var gps: GPS? = null
    var messageList: ArrayList<MessageObject>? = null
    private var mChat: RecyclerView? = null
    private var mChatAdapter: RecyclerView.Adapter<*>? = null
    private var mChatLayoutManager: RecyclerView.LayoutManager? = null
    private var mFirestore: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private var mGroupMessageDb: DocumentReference? = null
    private var mMessageId: CollectionReference? = null
    private var imagePerson: ImageView? = null
    private var tvNamePerson: TextView? = null
    private var mMessage: EditText? = null
    private var btnSend: ImageButton? = null
    private var btnBack: ImageButton? = null
    private var btnInfo: ImageButton? = null
    private val latitude = 37.349642
    private val longtitude = -121.938987
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.conversation_activity)
        gps = GPS(this)
        setupFirebaseAuth()
        mFirestore = FirebaseFirestore.getInstance()


        //load user data
        mGroupObject = intent.getSerializableExtra("groupObject") as GroupObject?
        userMatched = mGroupObject!!.userMatch
        userId = mAuth!!.currentUser!!.uid
        mGroupMessageDb = mFirestore!!.collection("message").document(mGroupObject!!.chatId!!)


        //setup display info
        btnInfo = findViewById(R.id.checkInfoUserMatched)
        btnBack = findViewById(R.id.back_matched_activity)
        imagePerson = findViewById(R.id.image_user_group)
        tvNamePerson = findViewById(R.id.name_user_group)
        btnSend = findViewById(R.id.send)
        mMessage = findViewById(R.id.messageInput)
        btnSend?.setOnClickListener(View.OnClickListener { sendMessage() })
        btnBack?.setOnClickListener(View.OnClickListener { onBackPressed() })
        btnInfo?.setOnClickListener(View.OnClickListener {
            val distance = gps!!.calculateDistance(
                latitude,
                longtitude,
                userMatched!!.latitude,
                userMatched!!.longtitude
            )
            val intent = Intent(this@ConversationActivity, ProfileCheckinMatched::class.java)
            intent.putExtra("classUser", userMatched)
            intent.putExtra("distance", distance)
            startActivity(intent)
        })
        initializeMessage()
        chatMessage
        initTopBar()
    }

    var messageIdList: MutableList<DocumentSnapshot>? = null
    private val chatMessage: Unit
        private get() {
            mMessageId = mGroupMessageDb!!.collection("messages")
            mMessageId!!.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        messageIdList = value.documents
                        Log.d(
                            TAG,
                            "getChatMessage: messageIdList " + messageIdList!!.size + "-" + messageIdList.toString()
                        )
                        if (action == "INIT" || action == SEND_MESSAGE) {
                            for (i in messageIdList!!) {
                                var messageText: String? = ""
                                var sendBy: String? = ""
                                var datetime: String? = ""
                                if (i.getString("messageText") != null) {
                                    messageText = i.getString("messageText")
                                }
                                if (i.getString("sendBy") != null) {
                                    sendBy = i.getString("sendBy")
                                }
                                if (i.getString("sendAt") != null) {
                                    datetime = i.getString("sendAt")
                                }
                                val mMessage = MessageObject(i.id, sendBy, messageText, datetime)
                                messageList!!.add(mMessage)
                                mChatLayoutManager!!.scrollToPosition(messageList!!.size - 1)
                                mChatAdapter!!.notifyDataSetChanged()
                                action = UPDATED_MESSAGE
                            }
                            messageIdList!!.clear()
                        }
                    }
                }
        }

    private fun initTopBar() {
        tvNamePerson!!.text = userMatched!!.username
        val profileImageUrl = userMatched!!.profileImageUrl
        if (Util.isOnMainThread()) {
            when (profileImageUrl) {
                "defaultFemale" -> imagePerson?.let {
                    Glide.with(applicationContext).load(R.drawable.img_ava_female)
                        .into(it)
                }
                "defaultMale" -> imagePerson?.let {
                    Glide.with(applicationContext).load(R.drawable.img_ava_male)
                        .into(it)
                }
                else -> Glide.with(applicationContext).load(profileImageUrl).into(imagePerson!!)
            }
        }
    }

    private fun initializeMessage() {
        messageList = ArrayList()
        mChat = findViewById(R.id.messageList)
        mChat?.isNestedScrollingEnabled = false
        mChat?.setHasFixedSize(false)
        mChatLayoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        mChat?.layoutManager = mChatLayoutManager
        mChatAdapter = ConversationAdapter(messageList!!)
        mChat?.adapter = mChatAdapter
    }

    private fun sendMessage() {
        val messageId = mGroupMessageDb!!.collection("messages").document().id
        val mMessageDb = mGroupMessageDb!!.collection("messages").document(messageId)
        val cal = Calendar.getInstance(Locale.ENGLISH)
        val dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString()
        var newMessageMap: MutableMap<Any, Any> = mutableMapOf()
        userId?.let { newMessageMap.put("sendBy", it) }
        newMessageMap.put("sendAt", dateTime)
        newMessageMap.put("timestamp", FieldValue.serverTimestamp())

        if (!mMessage!!.text.toString().isEmpty()) {
            newMessageMap["messageText"] = mMessage!!.text.toString()
            updateDatabaseWithNewMessage(mMessageDb, newMessageMap)
            action = SEND_MESSAGE
            messageList!!.clear()
            Log.d(TAG, " sendMessage over")
        }
    }

    private fun updateDatabaseWithNewMessage(
        mMessageDb: DocumentReference,
        newMessageMap: Map<*, *>
    ) {
        mMessageDb.set(newMessageMap)
        mMessage!!.setText(null)
        //Need to make notification
    }

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
                val intent = Intent(this@ConversationActivity, Login::class.java)

                //clear the activity stack， in case when sign out, the back button will bring the user back to the previous activity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    companion object {
        //initialize variables and constants
        private const val TAG = "Conversation_Activity"
        private const val SEND_MESSAGE = "SEND_MESSAGE"
        private const val UPDATED_MESSAGE = "UPDATED_MESSAGE"
    }
}