package com.example.quizboxapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.quizboxapp.R

import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var fullNameLayout : TextInputLayout
    private lateinit var userNameLayout : TextInputLayout
    private lateinit var emailLayout : TextInputLayout
    private lateinit var passwordLayout : TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_up)

        //Hooks

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Hooks
        fullNameLayout = findViewById(R.id.reg_fullName)
        userNameLayout = findViewById(R.id.reg_userName)
        emailLayout = findViewById(R.id.reg_email)
        passwordLayout = findViewById(R.id.reg_password)
        val loginTv = findViewById<TextView>(R.id.login_tv)
        val signupBtn = findViewById<Button>(R.id.signup_btn)
        firebaseAuth = FirebaseAuth.getInstance()


        loginTv.setOnClickListener {
            val loginActivity = Intent(applicationContext, LoginActivity::class.java)
            startActivity(loginActivity)
        }

        signupBtn.setOnClickListener {
            signUp()
        }
    }


    private fun validateName(): Boolean {
        val value: String = fullNameLayout.editText?.text.toString()
        return if (value.isEmpty()) {
            fullNameLayout.error = "Field cannot be empty"
            false
        } else {
            fullNameLayout.error = null
            fullNameLayout.isErrorEnabled = false
            true
        }
    }

    private fun validateUserName(): Boolean {
        val value : String = userNameLayout.editText?.text.toString()
        val noWhiteSpace = Regex("\\A\\w{4,20}\\z")
        return if (value.isEmpty()) {
            userNameLayout.error = "Field cannot be empty"
            false
        } else if (value.length >= 15) {
            userNameLayout.error = "Username too long"
            false
        } else if (!value.matches(noWhiteSpace)) {
            userNameLayout.error = "White Spaces are not allowed"
            false
        } else {
            userNameLayout.error = null
            userNameLayout.isErrorEnabled = false
            true
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun validatePassword(): Boolean {
        val value : String = passwordLayout.editText?.text.toString()
        val passwordVal = Regex("^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}" +  //at least 4 characters
                "$")
        return if (value.isEmpty()) {
            passwordLayout.error = "Field cannot be empty"
            false
        } else if (!value.matches(passwordVal)) {
            passwordLayout.error = "Password is too weak"
            false
        } else {
            passwordLayout.error = null
            passwordLayout.isErrorEnabled = false
            true
        }
    }


    private fun validateEmail(): Boolean {
        val value : String = emailLayout.editText?.text.toString()
        val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return if (value.isEmpty()) {
            emailLayout.error = "Field cannot be empty"
            false
        } else if (!value.matches(emailPattern)) {
            emailLayout.error = "Invalid email address"
            false
        } else {
            emailLayout.error = null
            emailLayout.isErrorEnabled = false
            true
        }
    }

    private fun signUp() {
        if (!validateName() or !validatePassword() or !validateEmail() or !validateUserName()) //validate when sign up button is clicked
        {
            return
        }


        //Get all the values
        val fullName: String = fullNameLayout.editText?.text.toString()
        val userName: String = userNameLayout.editText?.text.toString()
        val email: String = emailLayout.editText?.text.toString()
        val password : String = passwordLayout.editText?.text.toString()


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser!!
                val Email = user.email
                val uid = user.uid
                val hashMap = HashMap<Any, String?>()
                hashMap["fullName"] = fullName
                hashMap["email"] = Email
                hashMap["password"] = password
                hashMap["userName"] = userName
                hashMap["image"] = ""
                hashMap["uid"] = uid
                val database = FirebaseDatabase.getInstance()
                val reference = database.reference
                reference.child(uid).setValue(hashMap)

                firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "verification email sent to $email", Toast.LENGTH_LONG
                        ).show()
                        val emailVerify = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(emailVerify)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Check Internet Connection" +
                                task.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Check Internet Connection" + task.exception?.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}



