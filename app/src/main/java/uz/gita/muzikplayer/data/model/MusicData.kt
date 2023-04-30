package uz.gita.muzikplayer.data.model


/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 5:51 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.data.model
 */
data class MusicData(
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long
)