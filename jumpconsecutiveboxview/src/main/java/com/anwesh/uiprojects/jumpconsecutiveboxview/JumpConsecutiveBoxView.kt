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

fun Canvas.drawJumpConsecutiveBox(i : Int, scale : Float, size : Float, h : Float, paint : Paint) {
    val y : Float = (h / 2 - size) * i + size
    val hy : Float = (h / 2 - size)
    val sf : Float = scale.sinify().divideScale(i, boxes)
    save()
    translate(0f, y + hy * sf)
    drawRect(RectF(-size, -size, size, size), paint)
    restore()
}

fun Canvas.drawBoxes(scale : Float, size : Float, h : Float, paint : Paint) {
    for (j in 0..(boxes - 1)) {
        drawJumpConsecutiveBox(j, scale, size, h, paint)
    }
}

fun Canvas.drawJCBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    save()
    translate(gap * (i + 1), 0f)
    drawBoxes(scale, size, h, paint)
    restore()
}

class JumpConsecutiveBoxView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class JCBNode(var i : Int, private val state : State = State()) {

        private var next : JCBNode? = null
        private var prev : JCBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = JCBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawJCBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : JCBNode {
            var curr : JCBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class JumpConsecutiveBox(var i : Int) {

        private val root : JCBNode = JCBNode(0)
        private var curr : JCBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : JumpConsecutiveBoxView) {

        private val animator : Animator = Animator(view)
        private val jcb : JumpConsecutiveBox = JumpConsecutiveBox(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            jcb.draw(canvas, paint)
            animator.animate {
                jcb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            jcb.startUpdating {
                animator.start()
            }
        }

    }

    companion object {

        fun create(activity : Activity) : JumpConsecutiveBoxView {
            val view : JumpConsecutiveBoxView = JumpConsecutiveBoxView(activity)
            activity.setContentView(view)
            return view
        }
    }
}