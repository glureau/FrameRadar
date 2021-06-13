package glureau.frameradar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class FrameRadarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val FULL_CIRCLE_DURATION = 4000
        private const val ONE_FRAME_DURATION = 16
        private const val ONE_FRAME_ANGLE =
            (ONE_FRAME_DURATION.toFloat() / FULL_CIRCLE_DURATION) * 360f
        private const val VERY_BAD_DURATION = 1000 // used for red color
        private const val VERY_BAD_DURATION_ANGLE =
            (VERY_BAD_DURATION.toFloat() / FULL_CIRCLE_DURATION) * 360f
    }

    private var timeInitialDraw = 0L
    private var timeLastDraw = 0L
    private val backgroundPaint = Paint()
    private val paint = Paint()
    private val queue = LinkedList<Float>()

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val now = System.currentTimeMillis()
        canvas.drawArc(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            0f,
            360f,
            true,
            backgroundPaint
        )
        if (timeInitialDraw == 0L) {
            timeInitialDraw = now
            timeLastDraw = now
            queue.add(0f)
        } else {
            val currentPosition =
                ((now - timeInitialDraw) % FULL_CIRCLE_DURATION).toFloat() / FULL_CIRCLE_DURATION
            val currentAngle = currentPosition * 360f

            queue.add(currentAngle)

            while (queue.size > 100) {
                queue.remove()
            }

            var previousAngle = queue.first
            val queueSize = queue.size
            queue.forEachIndexed { index, angle ->
                if (previousAngle == angle) {
                    // Initial point, don't care for now
                } else {
                    var sweepAngle = previousAngle - angle
                    if (previousAngle > angle) {
                        sweepAngle -= 360f
                    }

                    val colorRatio = (-sweepAngle - ONE_FRAME_ANGLE).coerceIn(
                        0f,
                        VERY_BAD_DURATION_ANGLE
                    ) / VERY_BAD_DURATION_ANGLE


                    paint.color = Color.HSVToColor(
                        floatArrayOf(
                            (1f - colorRatio).coerceIn(0f, 1f) * 120f,
                            1f,
                            1f
                        )
                    )
                    paint.alpha = (index * 255) / queueSize
                    canvas.drawArc(
                        10f,
                        10f,
                        width.toFloat() - 10f,
                        height.toFloat() - 10f,
                        angle,
                        sweepAngle,
                        true,
                        paint
                    )
                }
                previousAngle = angle
            }
            timeLastDraw = now
        }
        postInvalidate()
    }
}