package com.animation.demo

import android.app.Application
import com.animation.demo.widget.svgaView.SVGAHelper

/**
 * Created by my on 2018/06/28 0028.
 */
class AnimationApp :Application(){

    override fun onCreate() {
        super.onCreate()
        //初始化svga播放管理器
        SVGAHelper.init(this)
    }
}