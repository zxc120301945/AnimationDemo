package com.animation.demo.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.animation.demo.R
import kotlinx.android.synthetic.main.activity_periscope.*

class PeriscopeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_periscope)
        click.setOnClickListener {
            heart_view.addHeart()
        }
    }
}