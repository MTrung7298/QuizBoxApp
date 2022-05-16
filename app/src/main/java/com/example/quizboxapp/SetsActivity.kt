package com.example.quizboxapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SetsActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navToggleBtn: Button
    private lateinit var linearLayout: LinearLayout
    private lateinit var setRecyclerView : RecyclerView
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sets)

        db = FirebaseFirestore.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cateName = intent.getStringExtra("Category")!!

        setRecyclerView = findViewById(R.id.rv_set)
        drawerLayout = findViewById(R.id.drawer_layout)
        linearLayout = findViewById(R.id.main_content)

        loadSet()

    }

    private fun loadSet() {
        db.collection("QuizBox").document(cateName)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document : DocumentSnapshot = task.result
                    if(document.exists()) {
                        val sets : Long = document.get("Sets") as Long

                        setRecyclerView.adapter = RecyclerSetAdapter(this, sets.toInt())
                    } else {
                        Toast.makeText(this, "No Set Document Exists", Toast.LENGTH_LONG).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        lateinit var cateName : String
    }
}
