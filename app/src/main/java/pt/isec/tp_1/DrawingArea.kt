package pt.isec.tp_1

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View




class DrawingArea @JvmOverloads constructor(
    context      : Context,
    attrs        : AttributeSet? = null,
    defStyleAttr : Int = 0,
    defStyleRes  : Int = 0
) : View(context,attrs,defStyleAttr,defStyleRes), GestureDetector.OnGestureListener {

    override fun onDown(e: MotionEvent): Boolean {
        Log.i(TAG_DAREA, "onDown: ")
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG_DAREA, "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.i(TAG_DAREA, "onSingleTapUp: ")
        return false
    }

    //o arrastar vai ser isto aqui
    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onScroll: ")
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG_DAREA, "onLongPress: ")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onFling: ")
        return false
    }
}


