package com.animation.demo.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.graphics.LinearGradient
import org.jetbrains.anko.dip


/**
 * Created by my on 2018/06/29 0029.
 * 流光效果的控件
 */
class IconLightView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    constructor(context: Context?) : this(context, null)

    private val path = Path()
    private val paint = Paint()
    private val sPaint = Paint()
    private val colors = intArrayOf(Color.parseColor("#00FFFFFF"), Color.parseColor("#FFFFFFFF"), Color.parseColor("#00FFFFFF"))
    private var shape: LinearGradient? = null
    private var xAnimator: ValueAnimator? = null
    //二元一次方程的泄漏
    private var k: Float = 0f
    //二元一次方程的偏移量
    private var b: Float = 0f
    //流光条的x宽度
    private var xOff = 15f

    init {
        paint.isAntiAlias = true
        sPaint.isAntiAlias = true
        paint.color = Color.parseColor("#000000")
        paint.style = Paint.Style.STROKE
    }

    private fun initPoint() {
        val point1 = Point(0, 0)
        val point2 = Point(measuredWidth, 0)
        val point3 = Point(measuredWidth, measuredHeight)
        val point4 = Point(0, measuredHeight)

        path.moveTo(point1.x.toFloat(), point1.y.toFloat())
        path.lineTo(point2.x.toFloat(), point2.y.toFloat())
        path.lineTo(point3.x.toFloat(), point3.y.toFloat())
        path.lineTo(point4.x.toFloat(), point4.y.toFloat())
        path.close()

        //计算出左边和右边的坐标
//        val leftCenterPoint = Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2)
//        val rightCenterPoint = Point((point3.x + point4.x) / 2, (point3.y + point4.y) / 2)

        //计算斜率
//        if (rightCenterPoint.x - leftCenterPoint.x != 0) {
//            k = (rightCenterPoint.y - leftCenterPoint.y) / (rightCenterPoint.x - leftCenterPoint.x).toFloat()
//        }
        //计算偏移量
//        b = leftCenterPoint.y - k * leftCenterPoint.x
                    //xOff表示这个流光的高度
        xAnimator = ValueAnimator.ofFloat((point1.y + point2.y) / 2.toFloat() - xOff, (point3.y + point4.y) / 2.toFloat() + xOff)
        xAnimator?.let {
            it.interpolator = LinearInterpolator()
            it.duration = 5000
            it.addUpdateListener {
                val tempX = it.animatedValue as Float
                //设置流光的颜色
                shape = LinearGradient(tempX, tempX, tempX, tempX+ xOff, colors, null, Shader.TileMode.CLAMP)
                invalidate()
            }
            //不停止播放动画
            it.repeatCount = -1
            it.start()
        }

        //                shape = LinearGradient(getYParams(tempX), tempX, getYParams(tempX + xOff), tempX+ xOff, colors, null, Shader.TileMode.CLAMP)

    }

    private val yTopOff = 13
    private val xSlant = 35
    private val ySlant = 19
    private val length = 40
    private fun initPoints() {
        //point是为了获取六边形图片的六个角对应的x和y轴，中间加上的固定像素是UI给定的数值
        //前三个角是从中心顶部的角开始往右算
        //这是第一个角，六边形中心顶部
        val point1 = Point(measuredWidth / 2, dip(yTopOff))//得到坐标X = 图片一半的位置，Y=固定从图片顶部0Y轴位置加上固定的13dp的点，
        // 下面以此类推，这个不适用于其他流光控件，需要重新计算x y坐标
        //六边形中心顶部往右数第一个
        val point2 = Point(measuredWidth / 2 + dip(xSlant), dip(yTopOff + ySlant))//
        //六边形中心顶部往右数第二个
        val point3 = Point(measuredWidth / 2 + dip(xSlant), dip(yTopOff + ySlant + length))
        //从底部中心的角开始往左边算三个
        //底部中心的角
        val point4 = Point(measuredWidth / 2, dip(yTopOff + ySlant * 2 + length))
        //底部中心的角往左数第一个
        val point5 = Point(measuredWidth / 2 - dip(xSlant), dip(yTopOff + ySlant + length))
        //底部中心的角往左数第二个
        val point6 = Point(measuredWidth / 2 - dip(xSlant), dip(yTopOff + ySlant))
        //moveTo这个api的意思是point1的x y坐标的起始点开始
        path.moveTo(point1.x.toFloat(), point1.y.toFloat())
        //lineTo表示这条线移动到的x y轴坐标
        path.lineTo(point2.x.toFloat(), point2.y.toFloat())
        path.lineTo(point3.x.toFloat(), point3.y.toFloat())
        path.lineTo(point4.x.toFloat(), point4.y.toFloat())
        path.lineTo(point5.x.toFloat(), point5.y.toFloat())
        path.lineTo(point6.x.toFloat(), point6.y.toFloat())
        path.close()

        //得出出现的位置和消失的位置
        val leftCenterPoint = Point((point1.x + point6.x) / 2, (point1.y + point6.y) / 2)
        val rightCenterPoint = Point((point3.x + point4.x) / 2, (point3.y + point4.y) / 2)

        //计算斜率
        if (rightCenterPoint.x - leftCenterPoint.x != 0) {
            k = (rightCenterPoint.y - leftCenterPoint.y) / (rightCenterPoint.x - leftCenterPoint.x).toFloat()
        }
        //计算偏移量
        b = leftCenterPoint.y - k * leftCenterPoint.x

        xAnimator = ValueAnimator.ofFloat((point1.x + point6.x) / 2.toFloat() - xOff, (point3.x + point4.x) / 2.toFloat() + xOff)
        xAnimator?.let {
            it.interpolator = LinearInterpolator()
            it.duration = 5000
            it.addUpdateListener {
                val tempX = it.animatedValue as Float
                shape = LinearGradient(tempX, getYParams(tempX), tempX + xOff, getYParams(tempX + xOff), colors, null, Shader.TileMode.CLAMP)
                invalidate()
            }
            it.repeatCount = -1
            it.start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initPoint()
    }

    private fun getYParams(x: Float) = k * x + b
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        xAnimator?.let { it.cancel() }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        sPaint.shader = shape
        canvas.drawPath(path, sPaint)
    }
}