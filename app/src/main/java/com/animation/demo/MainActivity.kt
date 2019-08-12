package com.animation.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.animation.demo.activity.FlipCardActivity
import com.animation.demo.activity.LightActivity
import com.animation.demo.activity.PeriscopeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickFlipCard(view: View) {
        this.startActivity(Intent(this, FlipCardActivity::class.java))
    }

    fun onClickLight(view: View) {
        this.startActivity(Intent(this, LightActivity::class.java))
    }

    fun onClickPeriscope(view: View) {
        this.startActivity(Intent(this, PeriscopeActivity::class.java))
    }
}
