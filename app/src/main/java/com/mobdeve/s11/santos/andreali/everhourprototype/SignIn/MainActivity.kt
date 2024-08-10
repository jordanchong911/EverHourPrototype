package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splashscreen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cloSplashscreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(R.layout.splashscreen_onboarding)

            findViewById<Button>(R.id.btnSignUp).setOnClickListener {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            findViewById<Button>(R.id.btnSignIn).setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000)
    }

}
