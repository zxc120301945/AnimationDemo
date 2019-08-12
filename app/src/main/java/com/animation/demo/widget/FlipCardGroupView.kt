package com.animation.demo.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import com.animation.demo.R
import kotlinx.android.synthetic.main.view_flip_card.view.*

/**
 * 翻牌自定义动画
 */
class FlipCardGroupView(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private fun getScreenWidth(): Float {
        return context?.applicationContext?.resources?.displayMetrics?.widthPixels?.toFloat() ?: 0f
    }

    private fun getScreenHeight(): Float {
        return context?.applicationContext?.resources?.displayMetrics?.heightPixels?.toFloat() ?: 0f
    }

    //如果控件被移除，那么把已添加的动画全部销毁，未添加的不再添加到动画列表中
    private var isDetachedWindow: Boolean = false

    private var isAnimator: Boolean = false

    //统一管理所有的animation避免内存泄漏问题
    private var mSetList: ArrayList<AnimatorSet> = arrayListOf()

    init {
        context?.let {
            LayoutInflater.from(it).inflate(R.layout.view_flip_card, this)
        }
    }

    fun doAnimator() {
        todoAnimation(this, ivCard, tvCard)
    }

    private fun todoAnimation(view: View, card: View, content: View) {
        if (isAnimator) {
            return
        }
        isAnimator = true
        view.bringToFront()
        translationAnimation(view)
        scaleAnimation(view)
        rotationYAniation(view, card, content)
    }

    private fun translationAnimation(view: View) {
        if (isDetachedWindow) {
            return
        }
        //先获取卡片初始xy轴坐标
        val viewX = view.x
        val viewY = view.y
        Log.e("animationX", "viewX = $viewX")
        Log.e("animationY", "viewY = $viewY")
        //再获取卡片宽高
        val viewWidth = view.width
        val viewHeight = view.height
        Log.e("animationX", "viewWidth = $viewWidth")
        Log.e("animationY", "viewHeight = $viewHeight")
        //获取屏幕宽度
        val screenWidth = getScreenWidth()
        //获取屏幕高度
        val dialogHeight = getScreenHeight()
        Log.e("animationX", "screenWidth = $screenWidth")
        Log.e("animationY", "dialogHeight = $dialogHeight")
        //获取屏幕宽度的中心位置
        val screenMiddleX = screenWidth / 2
        //获取弹窗高度的中心位置
        val screenMiddleY = dialogHeight / 2
        Log.e("animationX", "screenMiddleX = $screenMiddleX")
        Log.e("animationY", "screenMiddleY = $screenMiddleY")
        val viewToScreenX = viewX + (view.width / 2)
        val viewToScreenY = viewY + (view.height / 2)

        //把卡片往弹窗中心移动
        var demo = AnimatorSet()
        mSetList.add(demo)
        var translationX01: ObjectAnimator
        var translationY01: ObjectAnimator
        //以卡片X轴为起点，计算距离屏幕边缘的距离
        var translationX = 0f
        if (viewX > screenMiddleX) {
            //表示卡片在右边
            Log.e("animationX", "卡片在右边")
            //这个就是卡片X轴可以位置到屏幕中心的距离
            translationX = -((viewX + viewWidth / 2) - screenMiddleX)
            Log.e("animationX", "$translationX")
            translationX01 = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, translationX)
            val enter01 = AnimatorSet()
            enter01.duration = 600L
            enter01.interpolator = LinearInterpolator()
            enter01.playTogether(translationX01)
            demo.playTogether(enter01)
        } else if (viewX < screenMiddleX) {
            //表示卡片在左边
            Log.e("animationX", "卡片在左边")
            //X轴位置就是距离屏幕边缘的距离
            val x = viewX
            //这个就是卡片X轴可以位置到屏幕中心的距离
            translationX = (screenMiddleX - x) - (viewWidth / 2)
            Log.e("animationX", "$translationX")
            translationX01 = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, translationX)
            val enter01 = AnimatorSet()
            enter01.duration = 600L
            enter01.interpolator = LinearInterpolator()
            enter01.playTogether(translationX01)
            demo.playTogether(enter01)
        } else {
            //表示卡片就在中间,就不做横向平移动画
            Log.e("animationX", "卡片就在中间")
        }

        var translationY = 0f
        if (viewY < screenMiddleY) {
            //表示卡片在第一列
            Log.e("animationY", "卡片在第一列")
            //Y轴位置就是距离弹窗顶部边缘的距离
            val y = viewY
            //这个就是卡片Y轴可以位置到弹窗中心的距离
            translationY = (screenMiddleY - y) - viewHeight
            translationY01 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, translationY)
            val enter01 = AnimatorSet()
            enter01.duration = 600L
            enter01.interpolator = LinearInterpolator()
            enter01.playTogether(translationY01)
            demo.playTogether(enter01)
        } else if (viewY > screenMiddleY) {
            //表示卡片在最后一列
            Log.e("animationY", "卡片在最后一列")
            //Y轴位置就是距离弹窗顶部边缘的距离
            val y = viewY
            //这个就是卡片Y轴可以位置到弹窗中心的距离
            translationY = -((viewY + viewHeight) - screenMiddleY)
            translationY01 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, translationY)
            val enter01 = AnimatorSet()
            enter01.duration = 600L
            enter01.interpolator = LinearInterpolator()
            enter01.playTogether(translationY01)
            demo.playTogether(enter01)
        } else {
            //表示卡片在中间一列,就不做纵向移动动画
            Log.e("animationY", "卡片在中间一列")
        }
        demo.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                if (isDetachedWindow) {
                    return
                }
                var demo02 = AnimatorSet()
                mSetList.add(demo02)
                if (translationX != 0f) {
                    val translationX02 = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, translationX, 0f)
                    val enter01 = AnimatorSet()
                    enter01.duration = 700L
                    enter01.interpolator = LinearInterpolator()
                    enter01.playTogether(translationX02)
                    demo02.playTogether(enter01)
                }
                if (translationY != 0f) {
                    val translationY02 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, translationY, 0f)
                    val enter01 = AnimatorSet()
                    enter01.duration = 700L
                    enter01.interpolator = LinearInterpolator()
                    enter01.playTogether(translationY02)
                    demo02.playTogether(enter01)
                }
                demo02.startDelay = 1000L
                demo02.start()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}

        })
        demo.start()
    }

    private fun rotationYAniation(view: View, card: View, content: View) {
        if (isDetachedWindow) {
            return
        }
        var demo = AnimatorSet()
        mSetList.add(demo)
        val rotationY0201 = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0f, 90f)
        val enter01 = AnimatorSet()
        enter01.duration = 300L
        enter01.interpolator = LinearInterpolator()
        enter01.playTogether(rotationY0201)
        enter01.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                card.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })

        val rotationY0202 = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 270f, 360f)
        val enter02 = AnimatorSet()
        enter02.duration = 300L
        enter02.interpolator = LinearInterpolator()
        enter02.playTogether(rotationY0202)
        enter02.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
//                alphaAnimation(card, content)
                rotationYAniationTwo(view, card, content)
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {
                content.visibility = View.VISIBLE
            }
        })

        demo.play(enter01).before(enter02)
        demo.start()
    }

    private fun scaleAnimation(view: View) {
        if (isDetachedWindow) {
            return
        }
        var demo = AnimatorSet()
        mSetList.add(demo)
        val scaleX01 = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 2f)
        val enter01 = AnimatorSet()
        enter01.duration = 600L
        enter01.interpolator = LinearInterpolator()
        enter01.playTogether(scaleX01)
        val scaleY01 = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 2f)
        val enter02 = AnimatorSet()
        enter02.duration = 600L
        enter02.interpolator = LinearInterpolator()
        enter02.playTogether(scaleY01)
        demo.playTogether(enter01, enter02)
        demo.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                if (isDetachedWindow) {
                    return
                }
                var demo = AnimatorSet()
                mSetList.add(demo)
                val scaleX02 = ObjectAnimator.ofFloat(view, View.SCALE_X, 2f, 1f)
                val enter01 = AnimatorSet()
                enter01.duration = 700L
                enter01.interpolator = LinearInterpolator()
                enter01.playTogether(scaleX02)
                val scaleY02 = ObjectAnimator.ofFloat(view, View.SCALE_Y, 2f, 1f)
                val enter02 = AnimatorSet()
                enter02.duration = 700L
                enter02.interpolator = LinearInterpolator()
                enter02.playTogether(scaleY02)
                demo.playTogether(enter01, enter02)
                demo.startDelay = 1000L
                demo.start()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        demo.start()
    }

    private fun rotationYAniationTwo(view: View, card: View, content: View) {
        if (isDetachedWindow) {
            return
        }
        var demo = AnimatorSet()
        mSetList.add(demo)
        val rotationY0201 = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 360f, 270f)
        val enter01 = AnimatorSet()
        enter01.duration = 300L
        enter01.interpolator = LinearInterpolator()
        enter01.playTogether(rotationY0201)
        enter01.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                content.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })

        val rotationY0202 = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 90f, 0f)
        val enter02 = AnimatorSet()
        enter02.duration = 300L
        enter02.interpolator = LinearInterpolator()
        enter02.playTogether(rotationY0202)
        enter02.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                isAnimator = false
                try {
                    if (mSetList.isNotEmpty()) {
                        mSetList.forEach {
                            it?.removeAllListeners()
                        }
                        mSetList.clear()
                    }
                } catch (e: Exception) {

                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {
                card.visibility = View.VISIBLE
            }
        })

        demo.play(enter01).before(enter02)
        demo.startDelay = 2400L
        demo.start()
    }

    private fun alphaAnimation(card: View, content: View) {
        if (isDetachedWindow) {
            return
        }
        var demo = AnimatorSet()
        mSetList.add(demo)
        val alpha01 = ObjectAnimator.ofFloat(card, View.ALPHA, 0f, 1f)
        val enter01 = AnimatorSet()
        enter01.duration = 1000L
        enter01.interpolator = LinearInterpolator()
        enter01.playTogether(alpha01)
        enter01.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {
                card.visibility = View.VISIBLE
            }
        })

        val alpha02 = ObjectAnimator.ofFloat(content, View.ALPHA, 1f, 0f)
        val enter02 = AnimatorSet()
        enter02.duration = 1000L
        enter02.interpolator = LinearInterpolator()
        enter02.playTogether(alpha02)
        enter02.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                content.alpha = 1f
                content.visibility = View.GONE
                isAnimator = false
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        demo.playTogether(enter01, enter02)
        demo.startDelay = 2500L
        demo.start()
    }

    override fun onDetachedFromWindow() {
        isDetachedWindow = true
        //卡牌被移除，释放所有的动画监听避免内存泄漏，同时所有动画取消不再执行
        try {
            if (mSetList.isNotEmpty()) {
                mSetList.forEach {
                    it?.removeAllListeners()
                    it?.cancel()
                }
                mSetList.clear()
            }
        } catch (e: Exception) {

        }
        super.onDetachedFromWindow()
    }
}