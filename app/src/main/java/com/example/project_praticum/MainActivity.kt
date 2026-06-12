package com.example.project_praticum

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var iconHome: ImageView
    private lateinit var iconCart: ImageView
    private lateinit var iconPlant: ImageView
    private lateinit var iconAccount: ImageView

    private lateinit var textHome: TextView
    private lateinit var textCart: TextView
    private lateinit var textPlant: TextView
    private lateinit var textAccount: TextView

    private lateinit var btnScan: View

    private val homeFragment = HomeFragment()
    private val cartFragment = CartFragment()
    private val plantFragment = MyPlantFragment()
    private val accountFragment = AccountFragment()

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupBottomNav()

        if (savedInstanceState == null) {
            loadFragment(homeFragment)
            setActiveNav(iconHome, textHome)
        }
    }

    private fun initViews() {
        iconHome = findViewById(R.id.icon_home)
        textHome = findViewById(R.id.text_home)

        iconCart = findViewById(R.id.icon_cart)
        textCart = findViewById(R.id.text_cart)

        iconPlant = findViewById(R.id.icon_my_plant)
        textPlant = findViewById(R.id.text_my_plant)

        iconAccount = findViewById(R.id.icon_account)
        textAccount = findViewById(R.id.text_account)

        btnScan = findViewById(R.id.nav_scan)
    }

    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            loadFragment(homeFragment)
            setActiveNav(iconHome, textHome)
        }

        findViewById<LinearLayout>(R.id.nav_cart).setOnClickListener {
            loadFragment(cartFragment)
            setActiveNav(iconCart, textCart)
        }

        btnScan.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.nav_my_plant).setOnClickListener {
            loadFragment(plantFragment)
            setActiveNav(iconPlant, textPlant)
        }

        findViewById<LinearLayout>(R.id.nav_account).setOnClickListener {
            loadFragment(accountFragment)
            setActiveNav(iconAccount, textAccount)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        if (currentFragment == fragment) return

        currentFragment = fragment

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setActiveNav(activeIcon: ImageView?, activeText: TextView?) {
        val activeColor = ContextCompat.getColor(this, R.color.active_navigation)
        val inactiveColor = ContextCompat.getColor(this, R.color.inactive_navigation)

        resetNavColors(inactiveColor)

        activeIcon?.setColorFilter(activeColor)
        activeText?.setTextColor(activeColor)
    }

    private fun resetNavColors(color: Int) {
        iconHome.setColorFilter(color)
        textHome.setTextColor(color)

        iconCart.setColorFilter(color)
        textCart.setTextColor(color)

        iconPlant.setColorFilter(color)
        textPlant.setTextColor(color)

        iconAccount.setColorFilter(color)
        textAccount.setTextColor(color)
    }
}