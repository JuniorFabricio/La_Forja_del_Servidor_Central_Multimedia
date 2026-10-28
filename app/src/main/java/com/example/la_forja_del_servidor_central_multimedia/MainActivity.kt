package com.example.la_forja_del_servidor_central_multimedia

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.common.collect.ComparisonChain.start

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerCanales: Spinner
    private lateinit var playerView: PlayerView
    private lateinit var estadoConexion: TextView

    private var exoPlayer: ExoPlayer?=null
    private var alertaMediaPLayer: MediaPlayer?=null

    private val canalesMap = mapOf(
        "Minas Tirith" to "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
        "Isengard" to "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
        "Rivendell" to "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laboratorio3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lab3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerCanales=findViewById(R.id.spinnerCanales)
        playerView = findViewById(R.id.playerView)
        estadoConexion = findViewById(R.id.tvEstadoConexion)

        inicializarSpinner()
        inicializarExoPlayer()

    }

    private fun inicializarSpinner()
    {
        val nombreCanales=canalesMap.keys.toList()

        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,nombreCanales)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCanales.adapter = adapter

    spinnerCanales.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val canalSelecionado=nombreCanales[position]
            val urlVideo=canalesMap[canalSelecionado]
            urlVideo?.let { cambiarCanal(it) }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    }
    private fun cambiarCanal(ult: String)
    {
        exoPlayer?.let { player ->
            val mediaItem= MediaItem.fromUri(ult)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady=true
        }
    }
    private fun inicializarExoPlayer()
    {
        exoPlayer= ExoPlayer.Builder(this).build().also{
            player -> playerView.player=player

            player.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when(playbackState)
                    {
                        Player.STATE_BUFFERING->{
                                estadoConexion.text="Cargado Bufer..."
                        }
                        Player.STATE_READY -> {
                            estadoConexion.text = "Transmitiendo en vivo"
                        }
                        Player.STATE_ENDED -> {
                            estadoConexion.text = "Conexión terminada"
                            dispararAlerta()
                        }
                        Player.STATE_IDLE -> {
                            // Estado inactivo
                        }
                    }

                }


            })
        }


    }


    private fun dispararAlerta()
    {
        try {
            liberarAlarma()
            // Creamos un reproductor de audio usando el archivo local de la carpeta res/raw
            alertaMediaPLayer = MediaPlayer.create(this, R.raw.campanas_gondor).apply {
                isLooping = false
                start() // Comienza a reproducir el sonido en paralelo
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun liberarAlarma() {
        alertaMediaPLayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            alertaMediaPLayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberamos por completo el hardware de vídeo y audio al cerrar la app para evitar fugas de memoria RAM
        exoPlayer?.release()
        exoPlayer = null
        liberarAlarma()
    }
}