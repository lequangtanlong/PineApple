package com.zellycookies.pineapple.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar
import com.zellycookies.pineapple.R
import com.zellycookies.pineapple.home.HomeSwipeActivity
import com.zellycookies.pineapple.main.ProfileCheckinMain


class FilterActivity : AppCompatActivity() {
    private val TAG = "SettingsActivity"
    private lateinit var distance: SeekBar
    private lateinit var rangeSeekBar: RangeSeekBar<*>
    private lateinit var gender : TextView
    private lateinit var distanceText :TextView
    private lateinit var ageRange :TextView
    private lateinit var genderSpinner : Spinner
    private lateinit var submitButton : Button
    private var maxDistance : Int = 100
    private var minDistance : Int = 16

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        val toolbar = findViewById<TextView>(R.id.toolbartag)
        toolbar.text = "Filter"
        val back = findViewById<ImageButton>(R.id.back)
        gender = findViewById(R.id.gender_text)
        distance = findViewById(R.id.distance)
        distanceText = findViewById<TextView>(R.id.distance_text)
        ageRange = findViewById<TextView>(R.id.age_range)
        rangeSeekBar = findViewById(R.id.rangeSeekbar)
        genderSpinner = findViewById(R.id.gender_spinner)
        submitButton = findViewById(R.id.submitButton)

        val genderPreferences = resources.getStringArray(R.array.gender_preferences)
        if (genderSpinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, genderPreferences)
            genderSpinner.adapter = adapter

            genderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    gender.text = genderPreferences[position].toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        distance.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                distanceText.text = "$progress Km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        rangeSeekBar.setOnRangeSeekBarChangeListener { bar, number, number2 ->
            ageRange.text = "$number - $number2"
            maxDistance = number2.toInt()
            minDistance = number.toInt()
        }

        submitButton.setOnClickListener {
            val intent = Intent(this@FilterActivity, HomeSwipeActivity::class.java)
            intent.putExtra("genderPreference", gender.text)
            intent.putExtra("distance", distance.progress)
            intent.putExtra("minAge", minDistance)
            intent.putExtra("maxAge", maxDistance)

            startActivity(intent)

            onBackPressed()
        }

        back.setOnClickListener { onBackPressed() }
    }
}