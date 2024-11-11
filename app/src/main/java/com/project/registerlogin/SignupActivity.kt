package com.project.registerlogin

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class SignupActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var genderGroup: RadioGroup
    private lateinit var registerButton: Button
    private lateinit var yearSpinner: Spinner
    private lateinit var clearButton: Button
    private lateinit var backButton: Button
    private var isPasswordVisible = false

    // SQLite Database variables
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        nameInput = findViewById(R.id.nameInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        genderGroup = findViewById(R.id.genderGroup)
        yearSpinner = findViewById(R.id.yearSpinner)
        registerButton = findViewById(R.id.registerButton)
        clearButton = findViewById(R.id.clearButton)
        backButton = findViewById(R.id.backButton)

        dbHelper = DatabaseHelper(this)

        // Prepare the list of years (e.g., 1900 to current year)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (1900..currentYear).toList()

        // Set up the Spinner adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = adapter

        // Set listeners
        registerButton.setOnClickListener { registerUser() }
        clearButton.setOnClickListener { clearForm() }
        backButton.setOnClickListener { goToLogin() }

        // Set onTouchListener for password visibility toggle
        passwordInput.setOnTouchListener { v, event ->
            togglePasswordVisibility(
                event,
                passwordInput
            )
        }
        confirmPasswordInput.setOnTouchListener { v, event ->
            togglePasswordVisibility(
                event,
                confirmPasswordInput
            )
        }
    }

    // Toggle password visibility
    private fun togglePasswordVisibility(event: MotionEvent, editText: EditText): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = editText.compoundDrawablesRelative[2]
            if (drawableEnd != null && event.x >= editText.width - editText.paddingRight - drawableEnd.intrinsicWidth) {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        android.R.drawable.ic_menu_view,
                        0
                    )
                } else {
                    editText.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        android.R.drawable.ic_menu_view,
                        0
                    )
                }
                editText.setSelection(editText.text.length)
                return true
            }
        }
        return false
    }

    // function to register a new user
    private fun registerUser() {
        val fullName = nameInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        val selectedGenderId = genderGroup.checkedRadioButtonId
        val gender = findViewById<RadioButton>(selectedGenderId)?.text.toString()
        val yearOfBirth = yearSpinner.selectedItem?.toString()?.toInt() ?: 0

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }
        if (yearOfBirth == 0) {
            Toast.makeText(this, "Please select a year of birth", Toast.LENGTH_SHORT).show()
            return
        }

        if (fullName.isEmpty() || gender.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val cursor =
            db.query("users", arrayOf("name"), "name=?", arrayOf(fullName), null, null, null)
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
            return
        }
        cursor.close()

        val values = ContentValues().apply {
            put("name", fullName)
            put("password", password)
            put("gender", gender)
            put("year_of_birth", yearOfBirth) // Save year of birth
        }
        db.insert("users", null, values)
        db.close()
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
        clearForm()
        goToLogin()
    }

// Function to clear the form fields
    private fun clearForm() {
        nameInput.text.clear()
        passwordInput.text.clear()
        confirmPasswordInput.text.clear()
        genderGroup.clearCheck()
    }

    // Function to go to the login activity
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // SQLite Database helper class
    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT, password TEXT, gender TEXT, year_of_birth INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS users")
            onCreate(db)
        }
    }

}
