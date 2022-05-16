package com.example.quizboxapp

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf

class QuestionActivity : AppCompatActivity(), View.OnClickListener {
    private val cateName: String = SetsActivity.cateName
    private lateinit var question : TextView
    private lateinit var qCount : TextView
    private lateinit var timer : TextView
    private lateinit var option1 : Button
    private lateinit var option2 : Button
    private lateinit var option3 : Button
    private lateinit var option4 : Button
    private lateinit var questionList : ArrayList<Question>
    private lateinit var countDown : CountDownTimer
    lateinit var db : FirebaseFirestore
    lateinit var setNo : String
    var quesNum : Int = 0
    var score = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_questions_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        question = findViewById(R.id.question_view)
        qCount = findViewById(R.id.no_of_questions_view)
        timer = findViewById(R.id.countdown)

        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)

        option1.setOnClickListener(this)
        option2.setOnClickListener(this)
        option3.setOnClickListener(this)
        option4.setOnClickListener(this)

        db = FirebaseFirestore.getInstance()
        setNo = intent.getStringExtra("Set").toString()

        getQuestionList()

    }

    private fun getQuestionList() {
        questionList = ArrayList()


        db.collection("QuizBox").document(cateName)
            .collection(setNo)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val questions : QuerySnapshot = task.result
                    for (doc : QueryDocumentSnapshot in questions) {
                        val correctAns : Long = doc.data["Answer"] as Long
                        questionList.add(Question(
                            doc.data["Question"].toString(),
                            doc.data["A"].toString(),
                            doc.data["B"].toString(),
                            doc.data["C"].toString(),
                            doc.data["D"].toString(),
                            correctAns.toInt()
                        ))
                    }
                    setQuestion()

                } else {
                    Log.d("success", "ket noi k thanh cong")
                }
            }
    }

    private fun setQuestion() {
        timer.text = "15"
        question.text = questionList[0].question
        option1.text = questionList[0].optionA
        option2.text = questionList[0].optionB
        option3.text = questionList[0].optionC
        option4.text = questionList[0].optionD
        qCount.text = "1/${questionList.size}"

        startTimer()
    }

    private fun startTimer() {
        countDown = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(millisUntilFinished < 15000)
                    timer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                changeQuestion()
            }

        }
        countDown.start()

    }

    @SuppressLint("SetTextI18n")
    private fun changeQuestion() {
        quesNum++
        if(quesNum < questionList.size) {
            playAnim(question, 0F, 0)
            playAnim(option1, 0F, 1)
            playAnim(option2, 0F, 2)
            playAnim(option3, 0F, 3)
            playAnim(option4, 0F, 4)

            qCount.text = (quesNum + 1).toString() + "/" + questionList.size
            timer.text = 15.toString()
            startTimer()
        } else {
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("CorrectNum", "$score / ${questionList.size}")
            intent.putExtra("Score", score)
            startActivity(intent)
        }
    }



    private fun playAnim(view : View, value : Float, viewNum : Int) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
            .setInterpolator(
                DecelerateInterpolator()
            ).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    if(value == 0F) {
                        when(viewNum) {
                            0 -> (view as TextView).text = questionList[quesNum].question
                            1 -> (view as Button).text = questionList[quesNum].optionA
                            2 -> (view as Button).text = questionList[quesNum].optionB
                            3 -> (view as Button).text = questionList[quesNum].optionC
                            4 -> (view as Button).text = questionList[quesNum].optionD
                        }

                        if(viewNum != 0) {
                            (view as Button).backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2133A0"))
                        }

                        playAnim(view, 1F, viewNum )
                    }


                }

                override fun onAnimationCancel(p0: Animator?) {
                    TODO("Not yet implemented")
                }

                override fun onAnimationRepeat(p0: Animator?) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onClick(view: View?) {
        var selectedOption = 0
        when(view?.id) {
            R.id.option1 -> selectedOption = 1
            R.id.option2 -> selectedOption = 2
            R.id.option3 -> selectedOption = 3
            R.id.option4 -> selectedOption = 4
        }
        countDown.cancel()
        checkAnswer(selectedOption, view!!)
    }

    private fun checkAnswer(selectedOption : Int, view : View) {
        if(selectedOption == questionList[quesNum].correctAns) {
            view.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            val mp = MediaPlayer.create(this, R.raw.ding)
            score++

            mp.start()
        } else {
            view.backgroundTintList = ColorStateList.valueOf(Color.RED)

            when(questionList[quesNum].correctAns) {
                1 -> option1.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                2 -> option2.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                3 -> option3.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                4 -> option4.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            }
            val mp = MediaPlayer.create(this, R.raw.wrong_buzzer)

            mp.start()
        }

//        Timer().schedule(2000) {
//            Log.d("Tag", "TEst")
//        }
        changeQuestion()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        countDown.cancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}