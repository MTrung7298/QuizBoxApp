package com.example.quizboxapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ScoreActivity : AppCompatActivity() {
    lateinit var score : TextView
    lateinit var nextBtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        score = findViewById(R.id.score)
        nextBtn = findViewById(R.id.nl_btn)

        //custom lời chúc, nhận xét, nhạc

        score.text = intent.getStringExtra("CorrectNum")


        nextBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}