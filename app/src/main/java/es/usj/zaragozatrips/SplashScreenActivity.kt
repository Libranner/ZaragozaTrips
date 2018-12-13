package es.usj.zaragozatrips

import android.graphics.Color
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import gr.net.maroulis.library.EasySplashScreen

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideTopBar()

        val config = EasySplashScreen(this@SplashScreenActivity)
        config.withBackgroundColor(Color.WHITE)
        config.withLogo(R.drawable.logo)
        config.withTargetActivity(MenuActivity::class.java)
        config.withSplashTimeOut(2500)
        config.withFullScreen()

        val mediaPlayer = MediaPlayer.create(this, R.raw.z_trips)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
        }
        mediaPlayer.isLooping = false

        val view = config.create()
        setContentView(view)
    }

    private fun hideTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if(supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }
}
