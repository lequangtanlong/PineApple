package com.zellycookies.pineapple.conversation.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.conversation.Object.MessageObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
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
        return if (messageList[position].senderId.equals(mFirebaseUser!!.uid) && (messageList[position].imagePath == null || messageList[position].imagePath == "") ) {
            Log.d(TAG, "getItemViewType: sender")
            MSG_TYPE_RIGHT
        } else if(messageList[position].imagePath == null || messageList[position].imagePath == "") {
            Log.d(TAG, "getItemViewType: recieved")
            MSG_TYPE_LEFT
        }
        else if(messageList[position].senderId.equals(mFirebaseUser!!.uid) && (messageList[position].imagePath != null && messageList[position].imagePath != "")){
            MSG_IMG_RIGHT
        }
        else
            MSG_IMG_LEFT
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.mTimeSend.text = messageList[position].datetime

        if(messageList[position].message == null || messageList[position].message == ""){
            val newurl = URL(messageList[position].imagePath)
        //    holder.mImage?.setImageBitmap(BitmapFactory.decodeStream(newurl.openConnection().getInputStream()))
            holder.mMessage?.visibility = View.INVISIBLE

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var mIcon_val: Bitmap? = null
            try {
                val conn: HttpURLConnection = newurl.openConnection() as HttpURLConnection
                conn.setDoInput(true)
                conn.connect()
                val length: Int = conn.getContentLength()
                val bitmapData = IntArray(length)
                val bitmapData2 = ByteArray(length)
                val `is`: InputStream = conn.getInputStream()
                val options = BitmapFactory.Options()
                mIcon_val = BitmapFactory.decodeStream(`is`, null, options)
                if(holder.mImage != null){
                    holder.mImage!!.setImageBitmap(mIcon_val)
                }
                holder.mImage?.visibility = View.VISIBLE

                var bitmap: Bitmap? = null
                bitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 120, 120, false) };
                holder.mImage?.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            holder.mMessage?.text =  messageList[position].message }
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

            mMessage = itemView.findViewById(R.id.message)

            mImage = itemView.findViewById(R.id.imageMessage)

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