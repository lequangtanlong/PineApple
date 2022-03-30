package com.zellycookies.pineapple.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.zellycookies.pineapple.R

class ForgotPasswordActivity : AppCompatActivity() {
    //firebase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        val toolbar = findViewById<View>(R.id.toolbartag) as TextView
        toolbar.text = "Forgot Password"

        // button action listener
        val btnChangePassword = findViewById<Button>(R.id.btn_submit_forgotPassword)
        btnChangePassword.setOnClickListener {
            val email = findViewById<EditText>(R.id.input_email_forgotpw).text.toString()

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Password reset Email sent.")
                        Toast.makeText(this@ForgotPasswordActivity,
                            "Password reset Email sent", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        val back = findViewById<View>(R.id.back) as ImageButton
        back.setOnClickListener { onBackPressed() }
    }

    companion object {
        private const val TAG = "ForgotPasswordActivity"
    }
}