package com.animation.demo.widget.heartview

import android.animation.*
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import com.animation.demo.R
import java.util.*

/**
 * Created by my on 2018/07/02 0002.
 * 道具向上或向下飘荡动画控件
 */
class PeriscopeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private val line = LinearInterpolator()//线性
    private val acc = AccelerateInterpolator()//加速
    private val dce = DecelerateInterpolator()//减速
    private val accdec = AccelerateDecelerateInterpolator()//先加速后减速
    // 初始化插补器
    private val interpolators: Array<Interpolator> = arrayOf(line, acc, dce, accdec)
    val period = 1000L

    private var mRule: Boolean = true//运行规则

    private val lp: RelativeLayout.LayoutParams by lazy {
        RelativeLayout.LayoutParams(dWidth, dHeight).apply {
//            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)//这里的TRUE 要注意 不是true
        }
    }

    init {
        var a = context.theme.obtainStyledAttributes(attrs, R.styleable.PeriscopeLayout, defStyleAttr, 0)
        mRule = a.getBoolean(R.styleable.PeriscopeLayout_is_fly_up_type, true)
    }

    //初始化显示的图片
    private val drawables: List<Drawable> = listOf(
            resources.getDrawable(R.mipmap.donut)
            , resources.getDrawable(R.mipmap.guava)
            , resources.getDrawable(R.mipmap.kiwi)
            , resources.getDrawable(R.mipmap.lightning)
            , resources.getDrawable(R.mipmap.orange)
            , resources.getDrawable(R.mipmap.pear)
            , resources.getDrawable(R.mipmap.pitaya)
            , resources.getDrawable(R.mipmap.star)
            , resources.getDrawable(R.mipmap.tangerine)
            , resources.getDrawable(R.mipmap.watermelon)
    )

    private val random = Random()


    private var mHeight: Int = 0
    private var mWidth: Int = 0

    private val dHeight: Int by lazy { drawables[0].intrinsicHeight }
    private val dWidth: Int by lazy { drawables[0].intrinsicWidth }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
    }


    fun addHeart() {
        val imageView = ImageView(context)
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(drawables.size)])
        when (mRule) {
            false -> {
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            }
            else -> {
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            }
        }
        imageView.layoutParams = lp

        addView(imageView)

        val set = getAnimator(imageView)
        set.addListener(AnimEndListener(imageView))
        set.start()

    }

    private fun getAnimator(target: View): Animator {
        val set = getEnterAnimtor(target)

        val bezierValueAnimator = getBezierValueAnimator(target)

        val finalSet = AnimatorSet()
//        finalSet.playSequentially(set)
        finalSet.playSequentially(set, bezierValueAnimator)
        bezierValueAnimator.interpolator = interpolators[random.nextInt(4)]
        finalSet.setTarget(target)
        return finalSet
    }

    private fun getEnterAnimtor(target: View): AnimatorSet {

        val alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f)
        val scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f)//缩放动画
        val scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f)
        val enter = AnimatorSet()
        enter.duration = 600
        enter.interpolator = LinearInterpolator()
        enter.playTogether(alpha, scaleX, scaleY)
        enter.setTarget(target)
        return enter
    }

    private fun getBezierValueAnimator(target: View): ValueAnimator {

        //初始化一个贝塞尔计算器- - 传入
        val evaluator = BezierEvaluator(getPointF(1), getPointF(2), mRule)

        //这里最好画个图 理解一下 传入了起点 和 终点
        var wid = if (mWidth == 0) 50 else mWidth
        wid = if (wid == 0) 50 else wid
        val animator: ValueAnimator
        when (mRule) {
            false -> {
                //改成从右下角开始
                animator = ValueAnimator.ofObject(evaluator,
//                                               PointF(((mWidth - dWidth)).toFloat(), (mHeight - dHeight).toFloat()),//原来的样子,如果多加上这一行,会有比较有趣的现象
                        //再减bottomMargin 是因为上面设置layout的时候,bottomMargin 设置为 bottomMargin
                        PointF(((mWidth - dWidth) / 2).toFloat(), (mHeight - dHeight).toFloat()),
                        PointF(random.nextInt(wid).toFloat(), 0f))

            }
            else -> {
                animator = ValueAnimator.ofObject(evaluator,
                        PointF(((mWidth - dWidth) / 2).toFloat(), 0f),//设置最初时控件所在位置x,y轴
                        PointF(random.nextInt((mWidth - dWidth)).toFloat(), (mHeight - dHeight).toFloat()),//设置掉落中弹起位置
                        PointF(random.nextInt((mWidth - dWidth)).toFloat(), mHeight.toFloat()))//设置最终掉落位置
            }
        }
        animator.addUpdateListener(BezierListenr(target))
        animator.setTarget(target)
        animator.duration = 3000
        return animator
    }

    /**
     * 获取中间的两个 点

     * @param scale
     */
    private fun getPointF(scale: Int): PointF {
        val pointF = PointF()
        pointF.x = random.nextInt(Math.abs(mWidth - dWidth) + 1).toFloat()//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        pointF.y = (random.nextInt(Math.abs(mHeight - dHeight) + 1) / scale).toFloat()
        return pointF
    }

    private inner class BezierListenr(private val target: View) : ValueAnimator.AnimatorUpdateListener {

        override fun onAnimationUpdate(animation: ValueAnimator) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            val pointF = animation.animatedValue as PointF
            target.x = pointF.x
            target.y = pointF.y
            // 这里顺便做一个alpha动画
            target.alpha = 1 - animation.animatedFraction
        }
    }


    private inner class AnimEndListener(private val target: View) : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView(target)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playContinuousAnim()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isPlay = false
    }

    /**
     * 自动连续播放
     */
    var isPlay = false

    fun playContinuousAnim() {
        isPlay = true
        val runnable: Runnable = object : Runnable {
            override fun run() {
                addHeart()
                if (isPlay)
                    postDelayed(this, period)
            }
        }
        postDelayed(runnable, period * 2L)
    }

    //开关是否播放
    fun switchPlay(open: Boolean) {
        isPlay = open
    }
}