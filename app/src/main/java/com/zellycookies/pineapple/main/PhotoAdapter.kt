package com.zellycookies.pineapple.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.zellycookies.pineapple.R
import com.bumptech.glide.Glide

class PhotoAdapter(var mContext: Context, resource: Int, objects: List<Cards>) :
    ArrayAdapter<Cards?>(
        mContext, resource, objects
    ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val card_item: Cards? = getItem(position)
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false)
        }
        var userSex = "male"
        val name: TextView = convertView?.findViewById<View>(R.id.name) as TextView
        val image = convertView?.findViewById<View>(R.id.image) as ImageView
        val btnInfo: ImageButton =
            convertView?.findViewById<View>(R.id.checkInfoBeforeMatched) as ImageButton
        btnInfo.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, ProfileCheckinMain::class.java)
            intent.putExtra("name", card_item?.name + ", " + card_item?.age)
            intent.putExtra("photo", card_item?.profileImageUrl)
            intent.putExtra("dob", card_item?.dob)
            intent.putExtra("bio", card_item?.bio)
            intent.putExtra("interest", card_item?.interest)
            intent.putExtra("distance", card_item?.distance)
            intent.putExtra("showDoB", card_item?.showDoB)
            intent.putExtra("showDistance", card_item?.showDistance)
            intent.putExtra("userId", card_item?.userId)
            mContext.startActivity(intent)
        })
        name.setText(card_item?.name + ", " + card_item?.age)
        when (card_item?.profileImageUrl) {
            "defaultFemale" -> {
                Glide.with(getContext()).load(R.drawable.img_ava_female).into(image)
                }
            "defaultMale" -> {
                Glide.with(getContext()).load(R.drawable.img_ava_male).into(image)
                }
            else -> Glide.with(getContext()).load(card_item?.profileImageUrl).into(image)
        }
        return convertView
    }
}