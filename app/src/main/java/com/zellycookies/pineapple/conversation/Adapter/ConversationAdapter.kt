package com.zellycookies.pineapple.conversation.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.conversation.Object.MessageObject
import com.zellycookies.pineapple.conversation.Object.MessageType
import java.io.IOException
import java.net.URL


class ConversationAdapter(messageList: ArrayList<MessageObject>) :
    RecyclerView.Adapter<ConversationAdapter.MessageViewHolder>() {
    var messageList: ArrayList<MessageObject> = messageList
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mContext: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        var layoutView: View = if (viewType == MSG_TYPE_RIGHT) {
            LayoutInflater.from(parent.context).inflate(R.layout.conversation_message_right, null, false)
        } else if (viewType == MSG_TYPE_LEFT) {
            LayoutInflater.from(parent.context).inflate(R.layout.conversation_message_left, null, false)
        } else if (viewType == MSG_IMG_RIGHT) {
            LayoutInflater.from(parent.context).inflate(R.layout.conversation_image_right, null, false)
        }
        else{
            LayoutInflater.from(parent.context).inflate(R.layout.conversation_image_left, null, false)

        }
        var lp = RecyclerView.LayoutParams(
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
        holder.mTimeSend.text = messageList[position].datetime

        if(messageList[position].message == ""){
            holder.mImage?.let { DownloadImageFromInternet(it).execute() }
        } else {
            holder.mMessage?.text =  messageList[position].message

        }

    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
         //   Toast.makeText(applicationContext, "Please wait, it may take a few minute...",     Toast.LENGTH_SHORT).show()
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    //message holder subclass
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mMessage: TextView? = null
        var mTimeSend: TextView
        var mImage: ImageView? = null
        //var mLayout: LinearLayout

        init {
            Log.d(TAG, "MessageViewHolder: is activated")
            //mLayout = itemView.findViewById(R.id.sendLayout)
            mTimeSend = itemView.findViewById(R.id.time_send)

            try {
                mMessage = itemView.findViewById(R.id.message)
            }catch (e: IOException){
                mImage = itemView.findViewById(R.id.imageMessage)
            }
        }

    }

    companion object {
        private const val TAG = "MessageAdapter"
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPE_RIGHT = 1
        private const val MSG_IMG_LEFT = 2
        private const val MSG_IMG_RIGHT  = 3

    }

}