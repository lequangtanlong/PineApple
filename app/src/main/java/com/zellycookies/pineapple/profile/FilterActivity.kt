package com.zellycookies.pineapple.profile

import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener
import com.zellycookies.pineapple.R

class FilterActivity : AppCompatActivity() {
    private val TAG = "SettingsActivity"
    private lateinit var distance: SeekBar
    private lateinit var rangeSeekBar: RangeSeekBar<*>
    private lateinit var gender: TextView
    private lateinit var distanceText:TextView
    private lateinit var ageRange:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        val toolbar = findViewById<TextView>(R.id.toolbartag)
        toolbar.text = "Filter"
        val back = findViewById<ImageButton>(R.id.back)
        distance = findViewById(R.id.distance)

        distanceText = findViewById<TextView>(R.id.distance_text)
        ageRange = findViewById<TextView>(R.id.age_range)
        rangeSeekBar = findViewById(R.id.rangeSeekbar)
        distance.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                distanceText.text = "$progress Km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        rangeSeekBar.setOnRangeSeekBarChangeListener { bar, number, number2 ->
            ageRange.setText("$number-$number2")
        }

        back.setOnClickListener { onBackPressed() }
    }
}