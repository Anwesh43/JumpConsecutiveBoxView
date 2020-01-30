package com.anwesh.uiprojects.jumpconsecutiveboxview

/**
 * Created by anweshmishra on 30/01/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5
val boxes : Int = 2
val scGap : Float = 0.02f
val strokeFactor : Float = 90f
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#01579B")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawJumpConsecutiveBox(i : Int, gap : Float, scale : Float, size : Float, h : Float, paint : Paint) {
    val y : Float = (h / 2) * i
    val hy : Float = (h / 2 - size / 2)
    val sf : Float = scale.sinify().divideScale(i, boxes)
    save()
    translate(gap * i, y + hy * sf)
    drawRect(RectF(-size / 2, -size / 2, size / 2, size / 2), paint)
    restore()
}

fun Canvas.drawBoxes(gap : Float, scale : Float, size : Float, h : Float, paint : Paint) {
    for (j in 0..(boxes - 1)) {
        drawJumpConsecutiveBox(j, gap, scale, size, h, paint)
    }
}

fun Canvas.drawJCBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    drawBoxes(gap, scale, size, h, paint)
}

class JumpConsecutiveBoxView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}