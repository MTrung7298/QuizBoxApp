package com.example.quizboxapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.quizboxapp.MainActivity
import com.example.quizboxapp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInputLayout : TextInputLayout
    private lateinit var passwordInputLayout : TextInputLayout
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInputLayout = findViewById(R.id.email)
        passwordInputLayout = findViewById(R.id.password)
        val goBtn = findViewById<Button>(R.id.go_btn)

        firebaseAuth = Firebase.auth
        
        goBtn.setOnClickListener {
            login()
        }
    }

    private fun login() {
        if (!validateEmail() or !validatePassword()) {
            return
        }

        val email = emailInputLayout.editText?.text.toString()
        val password = passwordInputLayout.editText?.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (task.result.additionalUserInfo?.isNewUser!!) {
                    val user = firebaseAuth.currentUser
                    val email = user!!.email
                    val uid = user.uid
                    val hashMap = HashMap<Any, String?>()
                    hashMap["fullName"] = ""
                    hashMap["email"] = email
                    hashMap["password"] = password
                    hashMap["userName"] = ""
                    hashMap["image"] = ""
                    hashMap["uid"] = uid
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val reference: DatabaseReference =
                        database.reference
                    reference.child(uid).setValue(hashMap)
                }
                if (firebaseAuth.currentUser!!.isEmailVerified) {
                    Toast.makeText(
                        this,
                        "I wish you LUCK!",
                        Toast.LENGTH_LONG
                    ).show()

                    //luu session
                    val user = firebaseAuth.currentUser
                    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor = preferences.edit()

                    if (user != null) {
                        editor.putString("Email", user.email)
                        editor.putString("Id", user.uid)
                        editor.apply()
                    }

                    val mainIntent =
                        Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                } else if (!firebaseAuth.currentUser!!.isEmailVerified) {
                    Toast.makeText(
                        this,
                        "Error: Email is not Verified!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }



    private fun validateEmail(): Boolean {
        val value: String = emailInputLayout.editText?.text.toString()
        return if (value.isEmpty()) {
            emailInputLayout.error = "Field cannot be empty"
            false
        } else {
            emailInputLayout.error = null
            emailInputLayout.isErrorEnabled = false
            true
        }
    }

    private fun validatePassword(): Boolean {
        val value: String = passwordInputLayout.editText?.text.toString()
        return if (value.isEmpty()) {
            passwordInputLayout.error = "Field cannot be empty"
            false
        } else {
            passwordInputLayout.error = null
            passwordInputLayout.isErrorEnabled = false
            true
        }
    }

    fun forgotPassword() {

    }
    fun signUp(view : View) {
        val signUpActivity = Intent(applicationContext, SignUpActivity::class.java)
        startActivity(signUpActivity)
    }
}