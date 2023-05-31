package ru.kanogor.skillcinema.presentation.adapters.stafffilms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kanogor.skillcinema.R
import ru.kanogor.skillcinema.databinding.MovieItemVerticalBinding
import ru.kanogor.skillcinema.databinding.ViewAllFilmsButtonBinding
import ru.kanogor.skillcinema.data.entity.retrofit.IdFilm
import ru.kanogor.skillcinema.presentation.adapters.diffutil.IdFilmsDiffUtilCallBack
import ru.kanogor.skillcinema.presentation.adapters.viewholders.ButtonViewHolder
import ru.kanogor.skillcinema.presentation.adapters.viewholders.FilmsListHolder

class StaffFilmsAdapter : ListAdapter<IdFilm, RecyclerView.ViewHolder>(IdFilmsDiffUtilCallBack()) {

    companion object {
        const val NUM_OF_LIST = 20
    }

    var onButtonClick: ((IdFilm) -> Unit)? = null
    var onItemClick: ((IdFilm) -> Unit)? = null

    override fun submitList(list: List<IdFilm?>?) {
        if ((list?.size ?: 0) > NUM_OF_LIST) super.submitList(list?.take(NUM_OF_LIST))
        else super.submitList(list)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            NUM_OF_LIST - 1 -> R.layout.view_all_films_button
            else -> R.layout.movie_item_vertical
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val titleText = if (item?.nameRu == null) item.nameOriginal else item.nameRu
        val genresText = item?.genres?.joinToString(separator = ", ", limit = 3) { it.genre }
        val ratingText =
            if (item?.ratingKinopoisk == null) item?.ratingImdb else item.ratingKinopoisk
        when (getItemViewType(position)) {
            R.layout.movie_item_vertical -> {
                with((holder as FilmsListHolder).binding) {
                    title.text = titleText
                    genres.text = genresText
                    rating.text = ratingText.toString()
                    item.let {
                        Glide.with(poster.context)
                            .load(it?.posterUrl)
                            .placeholder(R.drawable.no_poster)
                            .into(poster)
                    }
                    root.setOnClickListener {
                        item?.let {
                            onItemClick?.invoke(item)
                        }
                    }
                }
            }

            R.layout.view_all_films_button -> {
                (holder as ButtonViewHolder).binding.pointer.setOnClickListener {
                    item?.let {
                        onButtonClick?.invoke(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.movie_item_vertical -> {
                val binding =
                    MovieItemVerticalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                FilmsListHolder(binding)
            }

            R.layout.view_all_films_button -> {
                val binding = ViewAllFilmsButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ButtonViewHolder(binding)
            }

            else -> {
                throw IllegalArgumentException("unknown view type $viewType")
            }
        }
    }
}
