package uz.gita.muzikplayer.utils

import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import uz.gita.muzikplayer.data.model.MusicData
import uz.gita.muzikplayer.data.source.localdata.LocalData

object AppManager {
    var selectMusicPos: Int = -1
    var cursor: Cursor? = null

    var progress: Int = 0
    var currentTime: Long = 0L
    var fullTime: Long = 0L

    val currentTimeLiveData = MutableLiveData<Long>()

    val playMusicLiveData = MutableLiveData<MusicData>()
    val isPlayingLiveData = MutableLiveData<Boolean>()

    init {
        playMusicLiveData.value = LocalData.getMusic()
        selectMusicPos = LocalData.musicPos
    }

}