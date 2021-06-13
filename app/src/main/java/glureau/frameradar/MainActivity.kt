package glureau.frameradar

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.randomJankButton).apply {
            setOnClickListener {
                Thread.sleep(Random.nextLong(0, 200))
            }
        }
        findViewById<Button>(R.id.randomBigJankButton).apply {
            setOnClickListener {
                Thread.sleep(Random.nextLong(200, 2000))
            }
        }
        //FrameRadar.enable(this)
    }
}