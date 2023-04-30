package uz.gita.muzikplayer.data.source.localdata

import android.content.Context
import android.content.SharedPreferences
import uz.gita.muzikplayer.data.model.MusicData

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 5:56 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.data.source.localdata
 */
class LocalData {
    companion object {

        private lateinit var sharedPref: SharedPreferences

        fun init(context: Context) {
            if (!Companion::sharedPref.isInitialized) {
                sharedPref = context.getSharedPreferences("DATA", Context.MODE_PRIVATE)
            }
        }

        var musicPos: Int
            get() = sharedPref.getInt("musicPos", -1)
            set(value) = sharedPref.edit().putInt("musicPos", value).apply()

        fun getMusic(): MusicData {
            return MusicData(
                sharedPref.getInt("id", -1),
                sharedPref.getString("artist", ""),
                sharedPref.getString("title", ""),
                sharedPref.getString("data", ""),
                sharedPref.getLong("duration", 0L)
            )
        }

        fun setMusic(data: MusicData) {
            sharedPref.edit().putInt("id", data.id).apply()
            sharedPref.edit().putString("data", data.data).apply()
            sharedPref.edit().putString("artist", data.artist).apply()
            sharedPref.edit().putString("title", data.title).apply()
            sharedPref.edit().putLong("duration", data.duration).apply()
        }
    }
}