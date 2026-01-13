package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        lifecycleScope.launch {
            delay(1500)
            startActivity(Intent(this@LoadingActivity, AccountActivity::class.java))
            finish()
        }
    }
}
