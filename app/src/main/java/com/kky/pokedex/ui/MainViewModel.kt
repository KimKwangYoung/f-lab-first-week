package com.kky.pokedex.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kky.pokedex.domain.model.Pokemon
import com.kky.pokedex.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
): ViewModel() {
    private var data = emptyList<Pokemon>()

    private val filteredData: List<Pokemon>
        get() = if (showOnlyLike) data.filter { it.like } else data

    private val _dataFlow: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.Loading)
    val dataFlow: StateFlow<MainUiState> = _dataFlow

    private var showOnlyLike = false

    init {
        pokemonRepository.addDataChangeListener(this) {
            data = it
            emitSuccess()
        }
    }

    fun loadPokemon() {
        viewModelScope.launch {
            _dataFlow.value = MainUiState.Loading
            runCatching {
                pokemonRepository.loadPokemon()
            }.onFailure {
                _dataFlow.value = MainUiState.Fail("데이터를 불러오는 데 실패하였습니다.")
            }
        }
    }

    fun setLike(pokemon: Pokemon, like: Boolean) {
        viewModelScope.launch {
            runCatching {
                if (like) {
                    pokemonRepository.like(pokemon.id)
                } else {
                    pokemonRepository.unlike(pokemon.id)
                }
            }.onFailure {
                it.printStackTrace()
                _dataFlow.value = MainUiState.Fail("다시 시동해주세요.")
            }.onSuccess {
                Log.d("MainViewModel", "갱신 성공")
            }
        }
    }

    fun showOnlyLike(arg: Boolean) {
        if (showOnlyLike != arg) {
            showOnlyLike = arg

            viewModelScope.launch { emitSuccess() }
        }
    }

    private fun emitSuccess() {
        _dataFlow.value = MainUiState.Success(
            data = filteredData,
            showOnlyLike = showOnlyLike
        )
    }

    override fun onCleared() {
        pokemonRepository.clear()
        super.onCleared()
    }

    sealed interface MainUiState {
        data object Loading : MainUiState

        data class Success(
            val data: List<Pokemon>,
            val showOnlyLike: Boolean
        ) : MainUiState {
            override fun equals(other: Any?): Boolean {
                return super.equals(other)
            }

            override fun hashCode(): Int {
                return super.hashCode()
            }
        }

        data class Fail(
            val errorMessage: String
        ) : MainUiState
    }
}