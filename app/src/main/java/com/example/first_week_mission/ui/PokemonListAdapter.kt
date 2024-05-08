package com.example.first_week_mission.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.first_week_mission.R
import com.example.first_week_mission.databinding.ViewholderDefaultBinding
import com.example.first_week_mission.databinding.ViewholderLikeBinding
import com.example.first_week_mission.ui.model.PokemonUiModel

class PokemonListAdapter(
    private val onClickLikeButton: (pokemon: PokemonUiModel, like: Boolean) -> Unit
) : ListAdapter<PokemonUiModel, ViewHolder>(PokemonDiffUtil()) {

    private var showOnlyLike = false

    fun showOnlyLike(arg: Boolean) {
        showOnlyLike = arg
    }

    override fun getItemViewType(position: Int): Int {
        return if (showOnlyLike) {
            LIKE_VIEW_TYPE
        } else {
            DEFAULT_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (viewType == LIKE_VIEW_TYPE) {
            val binding =
                ViewholderLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LikeViewHolder(binding, onClickLikeButton)
        } else {
            val binding =
                ViewholderDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DefaultViewHolder(binding, onClickLikeButton)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is DefaultViewHolder -> {
                holder.bind(getItem(position))
            }

            is LikeViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    private class PokemonDiffUtil : DiffUtil.ItemCallback<PokemonUiModel>() {
        override fun areItemsTheSame(oldItem: PokemonUiModel, newItem: PokemonUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PokemonUiModel, newItem: PokemonUiModel): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val DEFAULT_VIEW_TYPE = 0
        const val LIKE_VIEW_TYPE = 1
    }
}

private class DefaultViewHolder(
    private val binding: ViewholderDefaultBinding,
    val onClickLikeButton: (pokemon: PokemonUiModel, like: Boolean) -> Unit
) : ViewHolder(binding.root) {
    fun bind(pokemon: PokemonUiModel) {
        Glide.with(binding.root).load(pokemon.imageUrl).into(binding.ivPokemon)
        binding.tvPokemonName.text = pokemon.name

        val likeImageId = if (pokemon.like) {
            R.drawable.favorite_24dp
        } else {
            R.drawable.favorite_border_24dp
        }

        binding.ivLike.setOnClickListener {
            onClickLikeButton(pokemon, !pokemon.like)
        }
        binding.ivLike.setImageResource(likeImageId)
    }
}

private class LikeViewHolder(
    private val binding: ViewholderLikeBinding,
    val onClickLikeButton: (pokemon: PokemonUiModel, like: Boolean) -> Unit
) : ViewHolder(binding.root) {
    fun bind(pokemon: PokemonUiModel) {
        Glide.with(binding.root).load(pokemon.imageUrl).into(binding.ivPokemon)
        binding.tvPokemonName.text = pokemon.name

        val likeImageId = if (pokemon.like) {
            R.drawable.favorite_24dp
        } else {
            R.drawable.favorite_border_24dp
        }

        binding.ivLike.setOnClickListener {
            onClickLikeButton(pokemon, !pokemon.like)
        }

        binding.tvPokemonType.text = "타입: ${pokemon.type.joinToString(separator = ", ")}"
        binding.tvPokemonDescription.text = pokemon.description
        binding.ivLike.setImageResource(likeImageId)
    }
}