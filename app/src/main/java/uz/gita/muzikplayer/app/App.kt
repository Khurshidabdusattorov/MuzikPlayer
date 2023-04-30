package uz.gita.muzikplayer.app

import android.app.Application
import uz.gita.muzikplayer.data.source.localdata.LocalData


/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 5:49 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.app
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        LocalData.init(this)
    }
}