package uz.gita.muzikplayer.presentation.ui.screen.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.gita.muzikplayer.R
import uz.gita.muzikplayer.data.model.MusicData
import uz.gita.muzikplayer.data.model.enumdata.ActionEnum
import uz.gita.muzikplayer.databinding.ScreenMainBinding
import uz.gita.muzikplayer.getAlbumImageAsync
import uz.gita.muzikplayer.presentation.service.MusicService
import uz.gita.muzikplayer.utils.AppManager

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 7:14 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.ui.screen.main
 */
class MainScreen : Fragment(R.layout.screen_main) {
    private val myLifecycleScope = CoroutineScope(Dispatchers.Main + Job())


    private val binding: ScreenMainBinding by viewBinding(ScreenMainBinding::bind)
    private val adapter = MusicAdapter()
    private val bitmapScope = CoroutineScope(Dispatchers.IO + Job())
    private var job: Job? = null
    private var lastTime: Long = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter.cursor = AppManager.cursor

        adapter.setSelectMusicListener {
            job?.cancel()
            job = lifecycleScope.launch {
                if (System.currentTimeMillis() - lastTime >= 600) {
                    lastTime = System.currentTimeMillis()
                    AppManager.selectMusicPos = it
                    startMusicService(ActionEnum.PLAY)
                }
                delay(600)
            }
        }

        binding.apply {
            list.layoutManager = LinearLayoutManager(requireContext())
            list.adapter = adapter
            if (adapter.itemCount == 0) {
                animation.visibility = View.VISIBLE
                bottomBar.visibility = View.GONE
            } else {
                animation.visibility = View.GONE
            }
            bottomBar.setOnClickListener { findNavController().navigate(MainScreenDirections.actionMainScreenToPlayScreen()) }

            btnPrev.setOnClickListener { startMusicService(ActionEnum.PREV) }
            btnManage.setOnClickListener { startMusicService(ActionEnum.MANAGE) }
            btnNext.setOnClickListener { startMusicService(ActionEnum.NEXT) }
        }

        AppManager.playMusicLiveData.observe(viewLifecycleOwner, playMusicObserver)
        AppManager.isPlayingLiveData.observe(viewLifecycleOwner, isPlayingObserver)

    }

    private fun startMusicService(action: ActionEnum) {
        val intent = Intent(requireContext(), MusicService::class.java)
        intent.putExtra("COMMAND", action)

        if (Build.VERSION.SDK_INT >= 26) {
            requireActivity().startForegroundService(intent)
        } else requireActivity().startService(intent)
    }

    private val playMusicObserver = Observer<MusicData> { data ->
        if (data.id == -1) {
            binding.bottomBar.visibility = View.GONE
        } else {
            binding.apply {
                var imageBitMap: Bitmap? = null
                if (data.data != null) {
                    bitmapScope.launch {
                        getAlbumImageAsync(data.data).collectLatest { bitmap ->
                            imageBitMap = bitmap
                        }
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
                bottomBar.visibility = View.VISIBLE
                textMusicName.isSelected = true
                textMusicName.text = data.title
                textArtistName.text = data.artist
            }
        }
    }

    private val isPlayingObserver = Observer<Boolean> {
        binding.btnManage.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
    }
}