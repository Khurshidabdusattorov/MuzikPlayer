package uz.gita.muzikplayer.presentation.ui.screen.play

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.gita.muzikplayer.R
import uz.gita.muzikplayer.data.model.MusicData
import uz.gita.muzikplayer.data.model.enumdata.ActionEnum
import uz.gita.muzikplayer.databinding.ScreenPlayBinding
import uz.gita.muzikplayer.getAlbumImageAsync
import uz.gita.muzikplayer.presentation.service.MusicService
import uz.gita.muzikplayer.setChangeProgress
import uz.gita.muzikplayer.toDate
import uz.gita.muzikplayer.utils.AppManager

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 7:14 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.ui.screen.play
 */
class PlayScreen : Fragment(R.layout.screen_play) {

    private val binding: ScreenPlayBinding by viewBinding(ScreenPlayBinding::bind)

    private val bitmapScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            btnPrev.setOnClickListener { startMusicService(ActionEnum.PREV) }
            btnManage.setOnClickListener { startMusicService(ActionEnum.MANAGE) }
            btnNext.setOnClickListener { startMusicService(ActionEnum.NEXT) }
            AppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
            AppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)
            AppManager.currentTimeLiveData.observe(viewLifecycleOwner, currentTimeObserver)
            seekbar.setChangeProgress { startMusicService(ActionEnum.SEEK) }
            animation.pauseAnimation()
        }
    }

    private val playMusicObserver = Observer<MusicData> { data ->
        binding.apply {
            var imageBitMap: Bitmap? = null
            if (data.data != null) {
                bitmapScope.launch {
                    getAlbumImageAsync(data.data).collectLatest { bitmap -> imageBitMap = bitmap }
                    withContext(Dispatchers.Main) {
                        if (imageBitMap != null) {
                            Glide
                                .with(image)
                                .load(imageBitMap)
                                .into(image)
                        } else {
                            image.setImageResource(R.drawable.ic_music)
                        }
                    }
                }
            }
            seekbar.max = data.duration.toInt()
            textArtistName.text = data.artist
            textMusicName.text = data.title
            currentTime.text = data.duration.toDate()
        }
    }
    private val isPlayingObserver = Observer<Boolean> {
        if (it) {
            binding.btnManage.setImageResource(R.drawable.ic_pause)
            binding.animation.playAnimation()
        } else {
            binding.btnManage.setImageResource(R.drawable.ic_play)
            binding.animation.pauseAnimation()
        }
    }

    private val currentTimeObserver = Observer<Long> {
        binding.apply {
            seekbar.progress = AppManager.currentTime.toInt()
            currentTime.text = AppManager.currentTime.toDate()
        }
    }

    private fun startMusicService(action: ActionEnum) {
        val intent = Intent(requireContext(), MusicService::class.java)
        intent.putExtra("COMMAND", action)
        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else {
            requireActivity().startService(intent)
        }
    }
}