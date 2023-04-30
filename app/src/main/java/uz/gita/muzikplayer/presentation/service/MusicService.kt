package uz.gita.muzikplayer.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import uz.gita.muzikplayer.AppManager
import uz.gita.muzikplayer.R
import uz.gita.muzikplayer.data.model.MusicData
import uz.gita.muzikplayer.data.model.enumdata.ActionEnum
import uz.gita.muzikplayer.data.source.localdata.LocalData
import uz.gita.muzikplayer.getMusicDataByPosition
import java.io.File

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 6:45 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.service
 */
class MusicService : Service() {

    private val CHANNEL_ID = "Music Player"
    private val CHANNEL_NAME = "Music"
    private var _mediaPlayer: MediaPlayer? = null
    private val mediaPlayer get() = _mediaPlayer!!
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var job: Job? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        _mediaPlayer = MediaPlayer()
        createChannel()
        startMusicService()
    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setSound(null, null)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            //TODO
        }
    }

    private fun createRemoteView(): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        val musicData = AppManager.cursor?.getMusicDataByPosition(AppManager.selectMusicPos)!!

        view.setTextViewText(R.id.textMusicName, musicData.title)
        view.setTextViewText(R.id.textArtistName, musicData.artist)

        view.setImageViewResource(
            R.id.buttonManage,
            if (mediaPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(ActionEnum.PREV))
        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(ActionEnum.MANAGE))
        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(ActionEnum.NEXT))
        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(ActionEnum.CANCEL))
        return view
    }

    private fun createPendingIntent(action: ActionEnum): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("COMMAND", action)
        return PendingIntent.getService(
            this,
            action.code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun startMusicService() {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setContentTitle("Music player")
            .setCustomContentView(createRemoteView())
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMusicService()
        val command = intent!!.extras?.getSerializable("COMMAND") as ActionEnum
        doneCommand(command)
        return START_NOT_STICKY
    }
    private fun doneCommand(command: ActionEnum) {
        val data: MusicData = AppManager.cursor?.getMusicDataByPosition(AppManager.selectMusicPos)!!
        LocalData.musicPos = AppManager.selectMusicPos

        when (command) {
            ActionEnum.MANAGE -> {
                if (mediaPlayer.isPlaying) doneCommand(ActionEnum.PAUSE)
                else doneCommand(ActionEnum.PLAY)
            }
            ActionEnum.PLAY -> {

                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                _mediaPlayer = MediaPlayer.create(this, Uri.fromFile(File(data.data ?: "")))
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { doneCommand(ActionEnum.NEXT) }
                AppManager.fullTime = data.duration
                mediaPlayer.seekTo(AppManager.currentTime.toInt())
                job?.let { it.cancel() }
                job = scope.launch {
                    changeProgress().collectLatest {
                        AppManager.currentTime = it
                        AppManager.currentTimeLiveData.postValue(it)
                    }
                }
                AppManager.isPlayingLiveData.value = true
                AppManager.playMusicLiveData.value = data
                LocalData.setMusic(data)

                startMusicService()
            }
            ActionEnum.PAUSE -> {
                mediaPlayer.stop()
                job?.cancel()
                AppManager.isPlayingLiveData.value = false
                startMusicService()
            }
            ActionEnum.PREV -> {
                AppManager.currentTime = 0
                if (AppManager.selectMusicPos == 0) AppManager.selectMusicPos =
                    AppManager.cursor!!.count - 1
                else AppManager.selectMusicPos--
                doneCommand(ActionEnum.PLAY)
            }
            ActionEnum.NEXT -> {
                AppManager.currentTime = 0
                if (AppManager.selectMusicPos + 1 == AppManager.cursor!!.count) AppManager.selectMusicPos =
                    0
                else AppManager.selectMusicPos++
                doneCommand(ActionEnum.PLAY)
            }
            ActionEnum.CANCEL -> {
                mediaPlayer.stop()
                stopSelf()
            }
            ActionEnum.SEEK -> {
                mediaPlayer.seekTo(AppManager.progress)
                AppManager.currentTime = AppManager.progress.toLong()
                AppManager.currentTimeLiveData.postValue(AppManager.currentTime)
                job?.cancel()
                job = scope.launch {
                    changeProgress().collectLatest {
                        AppManager.currentTime = it
                        AppManager.currentTimeLiveData.postValue(it)
                    }
                }
            }
        }
    }
    private fun changeProgress(): Flow<Long> = flow {
        for (i in AppManager.currentTime until AppManager.fullTime step 490) {
            delay(490)
            emit(i)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}