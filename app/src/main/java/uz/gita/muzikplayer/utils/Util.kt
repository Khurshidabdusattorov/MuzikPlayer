package uz.gita.muzikplayer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.media.MediaMetadataRetriever
import android.os.Build
import android.widget.SeekBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.gita.muzikplayer.utils.AppManager
import java.util.*

/**
 * Creator : Azimjon Makhmudov
 * Date : 12/26/2022 Time : 14:46
 * Project : Music Player
 * Package : uz.gita.musicplayerAZS.utils
 */

fun myLog(message: String, tag: String = "ttt") {
    if (BuildConfig.DEBUG) {
//        Log.d(tag, message)
    }
}

fun Long.toDate(): String {
    val dateTime = Date(this)
    val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat("mm:ss", Locale.US)
    } else {
        TODO("VERSION.SDK_INT < N")
    }
    return format.format(dateTime)
}

fun SeekBar.setChangeProgress(block: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            p0?.let {
                myLog(it.progress.toString())
                AppManager.progress = it.progress
                block.invoke(it.progress)
            }
        }
    })
}

fun getAlbumImageAsync(path: String): Flow<Bitmap?> = flow<Bitmap?> {
    val mmr: MediaMetadataRetriever = MediaMetadataRetriever()
    mmr.setDataSource(path)
    val data: ByteArray? = mmr.embeddedPicture
    when {
        data != null -> emit(BitmapFactory.decodeByteArray(data, 0, data.size))
        else -> emit(null)
    }
}.flowOn(Dispatchers.IO)
