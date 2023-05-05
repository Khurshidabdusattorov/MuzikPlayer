package uz.gita.muzikplayer.presentation.ui.screen.main

import android.database.Cursor
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.gita.muzikplayer.R
import uz.gita.muzikplayer.databinding.ItemMusicBinding
import uz.gita.muzikplayer.getAlbumImageAsync
import uz.gita.muzikplayer.utils.getMusicDataByPosition

/**
 * Creator : Xurshid Abdusattorov
 * Date : 4/30/2023 Time : 7:14 PM
 * Project : MuzikPlayer
 * Package : uz.gita.muzikplayer.presentation.ui.screen.main
 */
class MusicAdapter() : RecyclerView.Adapter<MusicAdapter.MusicCursorHolder>() {
    var cursor: Cursor? = null
    var playingPos = -1

    private var selectMusicListener: ((Int) -> Unit)? = null
    private val bitmapScope = CoroutineScope(Dispatchers.IO + Job())

    fun setSelectMusicListener(block: (Int) -> Unit) {
        selectMusicListener = block
    }

    inner class MusicCursorHolder(private val binding: ItemMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                selectMusicListener?.invoke(absoluteAdapterPosition)
                playingPos = absoluteAdapterPosition
            }
        }

        fun bind() {
            cursor?.getMusicDataByPosition(absoluteAdapterPosition)?.let { data ->
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
                    textMusicName.text = data.title
                    textArtistName.text = data.artist
//                    if (absoluteAdapterPosition == playingPos) {
//                        animation.visibility = View.VISIBLE
//                    } else {
                    animation.visibility = View.INVISIBLE
//                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicAdapter.MusicCursorHolder =
        MusicCursorHolder(
            ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun onBindViewHolder(holder: MusicCursorHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = cursor?.count ?: 0

}