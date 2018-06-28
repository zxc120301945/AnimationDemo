package com.animation.demo.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.support.constraint.ConstraintLayout
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animation.demo.R
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.level_up_layout.view.*
import org.jetbrains.anko.dip
import org.reactivestreams.Subscriber
import java.util.concurrent.TimeUnit

/**
 * 升级进度条
 */
class LevelProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        //初始化布局
        LayoutInflater.from(context).inflate(R.layout.level_up_layout, this)
        tv_level_num.typeface = Typeface.createFromAsset(context.assets, "fonts/DINCondensedC-2.ttf")
    }


    var currentProgress = 20.1f
    var maxProgress = 100
    var isPlayOver = false
    private val startRatio = 0.15f
    private val totalHeight = 101f
    var nextHeight = 0f

    fun setProgressData() {
        var current = Math.abs(currentProgress.toFloat() / maxProgress)
        current_value?.text = "20"
        svga_line.startAnimation()
        val nextLevel = "${2}"
        val ss = SpannableString("$nextLevel\nLevel")
        ss.setSpan(AbsoluteSizeSpan(dip(13)), 0, nextLevel.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(AbsoluteSizeSpan(dip(8)), nextLevel.length + 1, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_level_num.text = ss
        nextHeight = (totalHeight * (1 - startRatio) * current) + totalHeight * startRatio
        startEnterAnim({
            startUpgradeLevelView()
        })
    }

    private var showBtnAnimatorSet: AnimatorSet? = null
    private var bgShowDisposable: Disposable? = null
    private var processShowDisposable: Disposable? = null

    private fun startEnterAnim(callback: () -> Unit = {}) {
        if (isPlayOver) {
            callback()
            return
        }
        author_level_up_container.visibility = View.VISIBLE
        if (showBtnAnimatorSet != null) {
            return
        }
        //等级按钮缩放动画（1秒）
        //0f,1.04f,0.99f,1.02f,1f,1f,1f  加速-减速运动
        showBtnAnimatorSet = AnimatorSet()//组合动画
        val xAnimator = ObjectAnimator.ofFloat(iv_btn, "scaleX", 0f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)
        val yAnimator = ObjectAnimator.ofFloat(iv_btn, "scaleY", 0f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)
        val tvXAnimator = ObjectAnimator.ofFloat(tv_level_num, "scaleX", 0f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)
        val tvYAnimator = ObjectAnimator.ofFloat(tv_level_num, "scaleY", 0f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)

        showBtnAnimatorSet?.duration = 1000
        showBtnAnimatorSet?.play(xAnimator)?.with(yAnimator)?.with(tvXAnimator)?.with(tvYAnimator)//两个动画同时开始

        //经验条出现（持续0.41秒）
        //从0.33秒开始，至0.74
        bgShowDisposable = Flowable.timer(330, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bgShowDisposable?.dispose()
                    showBg()
                }

        //经验条上涨（0.5秒）
        //0.75秒开始至1.25秒结束
        processShowDisposable = Flowable.timer(750, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bgShowDisposable?.dispose()
                    callback()
                    isPlayOver = true
                }

        showBtnAnimatorSet?.start()

    }

    private var bgAnimator: ValueAnimator? = null

    /**
     * 显示经验条的背景
     */
    private fun showBg() {
        //控件总高度
        var x = 101

        //动画用处，在持续时间内按照一定速率产生一个从0~101的渐变数值，会根据动画持续的总时间产生一个0~1时间因子，
        //有了这样一个时间因子。通过相应的变幻，就可以根据你的startValue和endValue来生成相应的值
        bgAnimator = ValueAnimator.ofInt(0, x)
        bgAnimator?.duration = 410

        bgAnimator?.addUpdateListener { valueAnimate ->
            var alpLp = iv_bg_progress.layoutParams as ViewGroup.LayoutParams

            //根据动画产生的数值提供一个可变的临时数值
            val temp = if (valueAnimate.animatedValue as Int > 0) {
                valueAnimate.animatedValue as Int
            } else {
                1
            }
            //高度在动画持续时间内变化
            alpLp.height = dip(temp)
            iv_bg_progress.requestLayout()
            if (iv_bg_progress.visibility != View.VISIBLE) {
                iv_bg_progress.visibility = View.VISIBLE
            }
        }
        bgAnimator?.start()
    }

    var currentHeight = 0f
    var clipDrawable: ClipDrawable? = null
    private var processAnimator: ValueAnimator? = null

    /***
     * 总高度75dp
     */
    private fun startUpgradeLevelView() {
        processAnimator = ValueAnimator.ofFloat(currentHeight, nextHeight)
        processAnimator?.duration = 500

        processAnimator?.addUpdateListener { valueAnimate ->
            if (iv_level_progress.visibility != View.VISIBLE) {
                iv_level_progress.visibility = View.VISIBLE
            }
            if (clipDrawable == null) {
                clipDrawable = iv_level_progress.background as? ClipDrawable
            }
            var alpLp = iv_temp.layoutParams as ViewGroup.LayoutParams
            val temp = if (valueAnimate.animatedValue as Float > 0) {
                valueAnimate.animatedValue as Float
            } else {
                1f
            }
            //获取一个临时高度和总高度的比例 如(70/100=0.7)
            val ratio = temp / totalHeight
            doWithWhiteLineWidth(temp)
            alpLp.height = dip(temp)
            iv_temp.requestLayout()
            //ClipDrawable是通过设置一个Drawable的当前显示比例来裁剪出另一张Drawable
            //调节显示比例可以实现类似Progress进度条的效果。
            //ClipDrawable的level值范围在[0,10000]，level的值越大裁剪的内容越少，如果level为10000时则完全显示。
            clipDrawable?.level = (ratio * 10000).toInt()
        }

        processAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isPlayOver = true
                if (current_value.visibility != View.VISIBLE) {
                    current_value.visibility = View.VISIBLE
                }
                if (svga_loop.visibility != View.VISIBLE) {
                    svga_loop.visibility = View.VISIBLE
                }
                doCyclerAnimator()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        processAnimator?.start()
        currentHeight = nextHeight
    }

    //顶部图片的圆角边距
    private val topCircleRadius = 8

    private fun doWithWhiteLineWidth(processHeight: Float) {
        val residue = totalHeight - processHeight
        if (residue >= topCircleRadius) {
            return
        }
        //直角边
        val right_angle_side = topCircleRadius - residue
        val diffenerceValue = topCircleRadius * topCircleRadius - right_angle_side * right_angle_side
        if (diffenerceValue > 0) {
            val iv_width = 2 * Math.sqrt(diffenerceValue.toDouble())
            val params = iv_white_line.layoutParams
            params.width = dip(iv_width.toFloat())
            params.height = dip((iv_width * 3 / 4f).toInt())
            iv_white_line.requestLayout()
        }
    }

    private var animatorSet: AnimatorSet? = null
    private var disposable: Disposable? = null

    private fun doCyclerAnimator() {
        if (animatorSet != null) {
            return
        }
        //按钮大小缩放动画
        //1f, 1.15f, 0.98f,1.04f,0.99f,1.02f,1f,1f,1f
        //1.66秒   加速-减速运动
        animatorSet = AnimatorSet()//组合动画
        val xAnimator = ObjectAnimator.ofFloat(iv_btn, "scaleX", 1f, 1.15f, 0.98f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)
        val yAnimator = ObjectAnimator.ofFloat(iv_btn, "scaleY", 1f, 1.15f, 0.98f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)
        val tvXAnimator = ObjectAnimator.ofFloat(tv_level_num, "scaleX", 1f, 1.15f, 0.98f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)
        val tvYAnimator = ObjectAnimator.ofFloat(tv_level_num, "scaleY", 1f, 1.15f, 0.98f, 1.04f, 0.99f, 1.02f, 1f, 1f, 1f)

        animatorSet?.duration = 1660
        animatorSet?.play(xAnimator)?.with(yAnimator)?.with(tvXAnimator)?.with(tvYAnimator)//两个动画同时开始

        //interval操作符是每隔一段时间就产生一个数字，这些数字从0开始，一次递增1直至2
        disposable = Flowable.interval(0L, 2L, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    svga_loop.startAnimation()

                    animatorSet?.cancel()
                    animatorSet?.start()
                }
    }
}