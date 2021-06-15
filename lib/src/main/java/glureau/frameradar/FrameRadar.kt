package glureau.frameradar

import android.app.Activity
import android.app.Application
import android.graphics.Insets
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager


enum class ScreenPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

object FrameRadar {

    fun enable(
        application: Application,
        screenPosition: ScreenPosition = ScreenPosition.BOTTOM_RIGHT,
        sizeInPx: Int = 200
    ): Application.ActivityLifecycleCallbacks {
        val callbacks = object :
            ActivityLifecycleCallbacksAdapter() {
            var currentView: FrameRadarView? = null
            override fun onActivityResumed(activity: Activity) {
                currentView = enable(activity, screenPosition, sizeInPx)
            }

            override fun onActivityPaused(activity: Activity) {
                currentView?.let {
                    disable(activity, it)
                    currentView = null
                }
            }
        }
        application.registerActivityLifecycleCallbacks(callbacks)
        return callbacks
    }

    fun disable(application: Application, callbacks: Application.ActivityLifecycleCallbacks) {
        application.unregisterActivityLifecycleCallbacks(callbacks)
    }

    fun enable(
        activity: Activity,
        screenPosition: ScreenPosition = ScreenPosition.BOTTOM_RIGHT,
        sizeInPx: Int = 200
    ): FrameRadarView {
        val (w, h) = activity.windowManager.screenSize()

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val frameRadarView = FrameRadarView(activity)
        activity.windowManager.addView(
            frameRadarView,
            WindowManager.LayoutParams(
                sizeInPx, sizeInPx, xPos(screenPosition, w), yPos(screenPosition, h),
                type,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888
            )
        )
        return frameRadarView
    }

    fun disable(
        activity: Activity,
        frameRadarView: FrameRadarView
    ) {
        activity.windowManager.removeView(frameRadarView)
    }

    private fun xPos(screenPosition: ScreenPosition, screenWidth: Int): Int =
        when (screenPosition) {
            ScreenPosition.TOP_LEFT -> -screenWidth / 2
            ScreenPosition.TOP_RIGHT -> screenWidth / 2
            ScreenPosition.BOTTOM_LEFT -> -screenWidth / 2
            ScreenPosition.BOTTOM_RIGHT -> screenWidth / 2
        }

    private fun yPos(screenPosition: ScreenPosition, screenHeight: Int): Int =
        when (screenPosition) {
            ScreenPosition.TOP_LEFT -> -screenHeight / 2
            ScreenPosition.TOP_RIGHT -> -screenHeight / 2
            ScreenPosition.BOTTOM_LEFT -> screenHeight / 2
            ScreenPosition.BOTTOM_RIGHT -> screenHeight / 2
        }

    private fun WindowManager.screenSize(): Point {
        val windowSize: Point

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets: WindowInsets = currentWindowMetrics.windowInsets
            val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars()
                        or WindowInsets.Type.displayCutout()
            )

            val insetsWidth: Int = insets.right + insets.left
            val insetsHeight: Int = insets.top + insets.bottom

            windowSize = Point(insetsWidth, insetsHeight)
        } else {
            windowSize = Point()
            defaultDisplay.getSize(windowSize)
        }
        return windowSize
    }

    operator fun Point.component1() = x
    operator fun Point.component2() = y
}