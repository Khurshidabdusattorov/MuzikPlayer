package uz.gita.muzikplayer.data.model.enumdata

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 5:52 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.data.model.enumdata
 */
enum class ActionEnum(val code: Int) {
    MANAGE(0),
    PLAY(1),
    PAUSE(2),
    PREV(3),
    NEXT(4),
    CANCEL(5),
    SEEK(6)
}