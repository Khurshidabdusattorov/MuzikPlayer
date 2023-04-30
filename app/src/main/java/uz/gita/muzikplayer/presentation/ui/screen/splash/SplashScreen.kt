package uz.gita.muzikplayer.presentation.ui.screen.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.gita.muzikplayer.R

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 7:14 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.ui.screen.splash
 */
class SplashScreen : Fragment(R.layout.screen_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().navigate(SplashScreenDirections.actionSplashScreenToMainScreen())
    }
}