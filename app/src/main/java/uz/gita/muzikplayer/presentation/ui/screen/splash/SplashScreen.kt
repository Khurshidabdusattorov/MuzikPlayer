package uz.gita.muzikplayer.presentation.ui.screen.splash

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View


import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.muzikplayer.BuildConfig
import uz.gita.muzikplayer.utils.AppManager
import uz.gita.muzikplayer.R
import uz.gita.muzikplayer.utils.checkPermissions
import uz.gita.muzikplayer.databinding.ScreenSplashBinding
import uz.gita.muzikplayer.utils.getMusicCursor

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 7:14 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.ui.screen.splash
 */
@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment(R.layout.screen_splash) {

    private val binding: ScreenSplashBinding by viewBinding(ScreenSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.version.text =
            "${requireContext().getString(R.string.version_1_0)} ${BuildConfig.VERSION_NAME}"

        if (Build.VERSION_CODES.TIRAMISU == Build.VERSION.SDK_INT) {
            requireActivity().checkPermissions(
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
            ) {
                navigateMain()
            }
        }else{
            requireActivity().checkPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                navigateMain()
            }
        }
    }

    fun navigateMain() {
        requireContext().getMusicCursor().onEach {
            AppManager.cursor = it
            delay(2500)
            findNavController().navigate(SplashScreenDirections.actionSplashScreenToMainScreen())
        }.launchIn(lifecycleScope)
    }
}