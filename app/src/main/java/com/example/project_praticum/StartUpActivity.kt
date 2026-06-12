package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StartUpActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            goToMain()
            return
        }

        setContentView(R.layout.activity_start_up)

        val signInBtn = findViewById<Button>(R.id.btnSignIn)
        val signUpBtn = findViewById<TextView>(R.id.TxtSignUp)

        signInBtn.setOnClickListener {
            LoginSheet().show(
                supportFragmentManager,
                "LoginActivity"
            )
        }

        signUpBtn.setOnClickListener {
            SignupSheet().show(
                supportFragmentManager,
                "SignUpActivity"
            )
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}