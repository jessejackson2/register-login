package com.project.registerlogin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    // Declare views
    private lateinit var passwordInput: EditText
    private var isPasswordVisible = false
    private lateinit var usernameInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupLink: TextView
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupLink = findViewById(R.id.signupLink)
        passwordInput = findViewById(R.id.passwordInput)

        dbHelper = DatabaseHelper(this)
        // Set the onTouchListener to toggle password visibility
        passwordInput.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = passwordInput.compoundDrawablesRelative[2]
                // Check if the drawableEnd (eye icon) is pressed
                if (drawableEnd != null && event.x >= passwordInput.width - passwordInput.paddingRight - drawableEnd.intrinsicWidth) {
                    isPasswordVisible = !isPasswordVisible
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Set up click listener for the login button
        loginButton.setOnClickListener {
            loginUser()
        }

        // Set up click listener for the sign-up link
        signupLink.setOnClickListener {
            goToSignup()
        }
    }

    // Function to toggle password visibility
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                android.R.drawable.ic_menu_view,
                0
            ) // Set custom eye-slash drawable
        } else {
            passwordInput.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                android.R.drawable.ic_menu_view,
                0
            ) // Reset to system eye drawable
        }
        passwordInput.setSelection(passwordInput.text.length) // Move cursor to end
    }

    // Function to login the user
    private fun loginUser() {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            val db = dbHelper.readableDatabase
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM users WHERE name = ? AND password = ?",
                arrayOf(username, password)
            )
            if (cursor.moveToFirst()) {
                val nameColumnIndex = cursor.getColumnIndex("name")
                val genderColumnIndex = cursor.getColumnIndex("gender")
                val ageColumnIndex = cursor.getColumnIndex("year_of_birth")
                if (nameColumnIndex != -1 && genderColumnIndex != -1) {
                    val name = cursor.getString(nameColumnIndex)
                    val gender = cursor.getString(genderColumnIndex)
                    val age = cursor.getInt(ageColumnIndex)

                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_NAME", name)
                        putExtra("USER_GENDER", gender)
                        putExtra("USER_YEAR_OF_BIRTH", age)
                    }
                    startActivity(intent)
                    finish()
                    Toast.makeText(
                        this,
                        "Username and password match successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Handle the case where the "name" or "gender" column is not found
                    Toast.makeText(this, "User data retrieval error", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Show error message
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
            db.close()
        } else {
            // Show error message for empty fields
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
        }
    }


    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    // Database helper class for SQLite database operations
    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT, password TEXT, gender TEXT,year_of_birth INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS users")
            onCreate(db)
        }
    }
}
