package com.project.registerlogin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        welcomeTextView = findViewById(R.id.welcomeTextView)
        ageTextView = findViewById(R.id.ageTextView)
        genderTextView = findViewById(R.id.genderTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Get user data from intent
        val name = intent.getStringExtra("USER_NAME")
        val gender = intent.getStringExtra("USER_GENDER")
        val yearOfBirth = intent.getIntExtra("USER_YEAR_OF_BIRTH", 0)

        // Calculate current year and age
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = if (yearOfBirth > 0) currentYear - yearOfBirth else 0

        // Set data to views
        welcomeTextView.text = "Welcome, $name!"
        ageTextView.text = "Age: $age"
        genderTextView.text = "Gender: $gender"

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
