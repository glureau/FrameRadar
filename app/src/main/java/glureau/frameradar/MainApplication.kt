package glureau.frameradar

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FrameRadar.enable(this)
    }
}