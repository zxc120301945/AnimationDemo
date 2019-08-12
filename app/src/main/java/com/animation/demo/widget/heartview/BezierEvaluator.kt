package com.animation.demo.widget.heartview

import android.animation.TypeEvaluator
import android.graphics.PointF

/**
 * Created by my on 2018/07/02 0002.
 */
class BezierEvaluator(private val pointF1: PointF, private val pointF2: PointF, private var rlue: Boolean = true) : TypeEvaluator<PointF> {

    override fun evaluate(time: Float, startValue: PointF,
                          endValue: PointF): PointF {

        val timeLeft = 1.0f - time
        val point = PointF()//结果

        when (rlue) {
            false -> {
                point.x = (timeLeft * timeLeft * timeLeft * startValue.x
                        + 3f * timeLeft * timeLeft * time * pointF1.x
                        + 3f * timeLeft * time * time * pointF2.x
                        + time * time * time * endValue.x)

                point.y = (timeLeft * timeLeft * timeLeft * startValue.y
                        + 3f * timeLeft * timeLeft * time * pointF1.y
                        + 3f * timeLeft * time * time * pointF2.y
                        + time * time * time * endValue.y)
            }
            else -> {
                //按照时间点变化x轴的位置
                point.x = (timeLeft * timeLeft * timeLeft * timeLeft * startValue.x + 6f * timeLeft * timeLeft * time * time * pointF1.x + 6f * timeLeft * time * time * time * pointF2.x + time * time * time * time * endValue.x)
                point.y = (timeLeft * timeLeft * timeLeft * startValue.y + timeLeft * timeLeft * time * pointF1.y + timeLeft * time * time * pointF2.y + time * time * time * endValue.y)
            }
        }
        return point
    }
}