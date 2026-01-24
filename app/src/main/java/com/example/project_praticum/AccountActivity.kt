package com.example.project_praticum

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val signInBtn = findViewById<Button>(R.id.btnSignIn)

        signInBtn.setOnClickListener {
            LoginSheet().show(
                supportFragmentManager,
                "LoginActivity"
            )
        }

        val signUpBtn = findViewById<TextView>(R.id.TxtSignUp)

        signUpBtn.setOnClickListener {
            SignupSheet().show(
                supportFragmentManager,
                "SignUpActivity"
            )
        }

    }

}
