package com.example.project_praticum

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast

class ScanActivity : Activity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnFlash: LinearLayout
    private lateinit var btnImage: LinearLayout

    private var flashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        btnBack = findViewById(R.id.btnBack)
        btnFlash = findViewById(R.id.btnFlash)
        btnImage = findViewById(R.id.btnImage)

        btnBack.setOnClickListener {
            finish()
        }

        btnFlash.setOnClickListener {
            flashOn = !flashOn
            Toast.makeText(
                this,
                if (flashOn) "Flash on" else "Flash off",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnImage.setOnClickListener {
            Toast.makeText(this, "Choose image will be added next", Toast.LENGTH_SHORT).show()
        }
    }
}