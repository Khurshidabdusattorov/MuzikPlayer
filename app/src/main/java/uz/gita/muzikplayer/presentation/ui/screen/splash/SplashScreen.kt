package uz.gita.muzikplayer.presentation.ui.screen.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.muzikplayer.AppManager
import uz.gita.muzikplayer.R
import uz.gita.muzikplayer.checkPermissions
import uz.gita.muzikplayer.getMusicCursor

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 7:14 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.ui.screen.splash
 */
class SplashScreen : Fragment(R.layout.screen_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().checkPermissions(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.POST_NOTIFICATIONS
        )) {
            requireContext().getMusicCursor().onEach {
                AppManager.cursor = it
                delay(2500)
                findNavController().navigate(SplashScreenDirections.actionSplashScreenToMainScreen())
            }.launchIn(lifecycleScope)
        }
    }
}