package com.example.mediaplayer

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var audioManager:AudioManager? = null
    private var songList = mutableListOf(R.raw.my_sound1, R.raw.my_sound2, R.raw.my_sound3)
    private var nowSound:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSoundSeekbar()
        playSound(nowSound)
    }

    private fun playSound(s: Int) {
        binding.playBTN.setOnClickListener {
            if (mediaPlayer == null) {
                changeSound(s)
            }
            mediaPlayer?.start()
        }
        binding.pauseBTN.setOnClickListener {
            if (mediaPlayer != null) mediaPlayer?.pause()
        }
        binding.prevBTN.setOnClickListener{
            if (mediaPlayer != null) changeNextSong(--nowSound)
        }
        binding.nextBTN.setOnClickListener{
            if (mediaPlayer != null) changeNextSong(++nowSound)
        }
        binding.stopBTN.setOnClickListener {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
        binding.durationSB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    fun changeNextSong(index:Int){
        nowSound=index
        if(nowSound<0) nowSound = songList.size-1
        if(nowSound>=songList.size) nowSound=0
        changeSound(nowSound)
    }

    fun changeSound(s:Int){
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, songList[s])
        mediaPlayer?.start()
        initializeDurationSeekbar()
    }

    private fun initializeDurationSeekbar() {
        binding.durationSB.max = mediaPlayer!!.duration
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.durationSB.progress = mediaPlayer!!.currentPosition
                    if(binding.durationSB.progress==binding.durationSB.max) {
                        changeNextSong(++nowSound)
                    }
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    binding.durationSB.progress = 0
                }
            }
        },9)
    }

    private fun initializeSoundSeekbar() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        binding.volumeSB.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        binding.volumeSB.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.volumeSB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
}