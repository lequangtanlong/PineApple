package com.zellycookies.pineapple.conversation.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zellycookies.pineapple.conversation.Object.MessageObject
import com.zellycookies.pineapple.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ConversationAdapter(messageList: ArrayList<MessageObject>) :
    RecyclerView.Adapter<ConversationAdapter.MessageViewHolder>() {
    var messageList: ArrayList<MessageObject>
    var mFirebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val layoutView: View
        if (viewType == MSG_TYPE_RIGHT) {
            layoutView = LayoutInflater.from(parent.context)
                .inflate(R.layout.conversation_message_right, null, false)
        } else {
            layoutView = LayoutInflater.from(parent.context)
                .inflate(R.layout.conversation_message_left, null, false)
        }
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutView.layoutParams = lp
        return MessageViewHolder(layoutView)
    }

    override fun getItemViewType(position: Int): Int {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        return if (messageList[position].senderId.equals(mFirebaseUser!!.uid)) {
            Log.d(TAG, "getItemViewType: sender")
            MSG_TYPE_RIGHT
        } else {
            Log.d(TAG, "getItemViewType: recieved")
            MSG_TYPE_LEFT
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.mTimeSend.setText(messageList[position].datetime)
        holder.mMessage.setText(messageList[position].senderId)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    //message holder subclass
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mMessage: TextView
        var mTimeSend: TextView
        var mLayout: LinearLayout

        init {
            Log.d(TAG, "MessageViewHolder: is actived")
            mLayout = itemView.findViewById(R.id.sendLayout)
            mTimeSend = itemView.findViewById(R.id.time_send)
            mMessage = itemView.findViewById(R.id.message)
        }
    }

    companion object {
        private const val TAG = "MessageAdapter"
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPE_RIGHT = 1
    }

    init {
        this.messageList = messageList
    }
}