package com.example.quizboxapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.example.quizboxapp.MainActivity
import com.example.quizboxapp.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        //Menu Hooks

        val textInputLayoutEmail = findViewById<TextInputLayout>(R.id.EMail)
        val textInputLayoutPassword = findViewById<TextInputLayout>(R.id.PassWOrd)
        val textInputLayoutUserName = findViewById<TextInputLayout>(R.id.USername)
        val textInputLayoutFullName = findViewById<TextInputLayout>(R.id.fullName)
        val backBtn = findViewById<Button>(R.id.prof_back_btn)


        //Init Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.reference
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val uid = preferences.getString("Id", "Not Id Exist")

        if (uid != null) {
            databaseReference.child(uid).get().addOnCompleteListener {
                Log.i("firebase", "Got value ${it.result}")
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }



        assert(user != null)
        val query = databaseReference.orderByChild("email").equalTo(user!!.email)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val fullname = "" + ds.child("fullName").value
                    val username = "" + ds.child("userName").value
                    val password = "" + ds.child("password").value
                    val email = "" + ds.child("email").value

                    //set data
                    textInputLayoutFullName.editText!!.setText(fullname)
                    textInputLayoutUserName.editText!!.setText(username)
                    textInputLayoutPassword.editText!!.setText(password)
                    textInputLayoutEmail.editText!!.setText(email)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}