package com.zellycookies.pineapple.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.zellycookies.pineapple.conversation.Object.GroupObject
import com.bumptech.glide.Glide
import com.zellycookies.pineapple.R

class LikerAdapter(
    private val mContext: Context,
    private val resourceId: Int,
    objects: List<GroupObject?>
) :
    ArrayAdapter<GroupObject?>(mContext, resourceId, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val groupObject: GroupObject? = getItem(position)

        //improve the efficiency
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
            viewHolder = ViewHolder()
            viewHolder.personPic = view.findViewById<View>(R.id.person_image) as ImageView
            viewHolder.personName = view.findViewById<View>(R.id.person_name) as TextView
            viewHolder.imageButton = view.findViewById<View>(R.id.videoCalBtn) as ImageButton
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val profileImageUrl: String = groupObject?.userMatch?.profileImageUrl!!
        when (profileImageUrl) {
            "defaultFemale" -> viewHolder.personPic?.let {
                Glide.with(mContext).load(R.drawable.img_ava_female)
                    .into(it)
            }
            "defaultMale" -> viewHolder.personPic?.let {
                Glide.with(mContext).load(R.drawable.img_ava_male)
                    .into(it)
            }
            else -> Glide.with(mContext).load(profileImageUrl).into(viewHolder.personPic!!)
        }
        viewHolder.personName!!.setText(groupObject.userMatch.username!!)
        viewHolder.imageButton!!.isFocusable = false
        return view
    }

    internal inner class ViewHolder {
        var personPic: ImageView? = null
        var personName: TextView? = null
        var imageButton: ImageButton? = null
    }
}