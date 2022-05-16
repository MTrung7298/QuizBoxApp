package com.example.quizboxapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.preference.PreferenceManager
import com.example.quizboxapp.user.LoginActivity
import com.example.quizboxapp.R
import com.example.quizboxapp.user.UserProfileActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navToggleBtn: Button
    private lateinit var linearLayout: LinearLayout
    val END_SCALE = 0.7f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navToggleBtn = findViewById(R.id.action_menu_presenter)
        linearLayout = findViewById(R.id.main_content)

        val startMarBtn = findViewById<Button>(R.id.start_mar_btn)
        val startProBtn = findViewById<Button>(R.id.start_pro_btn)
        val startHisBtn = findViewById<Button>(R.id.start_his_btn)
        val startAllBtn = findViewById<Button>(R.id.start_all_btn)
        val startMulBtn = findViewById<Button>(R.id.start_mul_btn)

        startMarBtn.setOnClickListener {
            val intent = Intent(this, SetsActivity::class.java)
            intent.putExtra("Category", "Cat1")
            startActivity(intent)
        }

        startProBtn.setOnClickListener {
            val intent = Intent(this, SetsActivity::class.java)
            intent.putExtra("Category", "Cat2")
            startActivity(intent)
        }

        startHisBtn.setOnClickListener {
            val intent = Intent(this, SetsActivity::class.java)
            intent.putExtra("Category", "Cat3")
            startActivity(intent)
        }

        startAllBtn.setOnClickListener {
            val intent = Intent(this, SetsActivity::class.java)
            intent.putExtra("Category", "Cat4")
            startActivity(intent)
        }

        startMulBtn.setOnClickListener {
            val intent = Intent(this, SetsActivity::class.java)
            intent.putExtra("Category", "Cat5")
            startActivity(intent)
        }

        navigationDrawer()
    }


    private fun navigationDrawer() {
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.home)
        navToggleBtn.setOnClickListener {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) drawerLayout.closeDrawer(
                GravityCompat.START
            ) else drawerLayout.openDrawer(GravityCompat.START)
        }
        animateNavigationDrawer()
    }

    private fun animateNavigationDrawer() {
        drawerLayout.setScrimColor(ContextCompat.getColor(applicationContext, R.color.cat_heading))
        drawerLayout.addDrawerListener(object : SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                linearLayout.scaleX = offsetScale
                linearLayout.scaleY = offsetScale
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff = linearLayout.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                linearLayout.translationX = xTranslation
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.user_profile -> {
                val intent = Intent(applicationContext, UserProfileActivity::class.java)
                startActivity(intent)
                super@MainActivity.onBackPressed()
                Log.d("TAG", "Profile clicked")
            }

            R.id.logout -> {
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = preferences.edit()
                editor.putString("Email", "")
                editor.apply()

                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        }
        return true
    }

}