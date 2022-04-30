package com.zellycookies.pineapple.utility.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.zellycookies.pineapple.R

class ActivityAdapter(
    mContext: Context,
    private val resourceId: Int,
    objects: List<ActivityObject?>
) :
    ArrayAdapter<ActivityObject?>(mContext, resourceId, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val activityObject: ActivityObject? = getItem(position)

        //improve the efficiency
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
            viewHolder = ViewHolder()
            viewHolder.actTime = view.findViewById<View>(R.id.tvTime) as TextView
            viewHolder.actContent = view.findViewById<View>(R.id.tvActivity) as TextView
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.actTime!!.text = activityObject!!.time
        viewHolder.actContent!!.text = activityObject.content
        return view
    }

    internal inner class ViewHolder {
        var actTime: TextView? = null
        var actContent: TextView? = null
    }
}