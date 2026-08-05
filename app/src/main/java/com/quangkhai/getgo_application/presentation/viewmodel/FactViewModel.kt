package com.quangkhai.getgo_application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quangkhai.getgo_application.data.repository.WikidataFactRepositoryImpl
import com.quangkhai.getgo_application.domain.model.Fact
import com.quangkhai.getgo_application.domain.usecase.fact.GetRandomFactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// "I'm Feeling Bored" - fetches a random Wikidata fact + place
class FactViewModel : ViewModel() {

    private val factRepository = WikidataFactRepositoryImpl()
    private val getRandomFactUseCase = GetRandomFactUseCase(factRepository)

    private val _fact = MutableStateFlow<Fact?>(null)
    val fact: StateFlow<Fact?> = _fact.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun loadRandomFact(lat: Double, long: Double) {
        viewModelScope.launch {
            _loading.value = true
            _fact.value = getRandomFactUseCase(lat, long).getOrNull()
            _loading.value = false
        }
    }

    // dismiss the fact modal
    fun clear() {
        _fact.value = null
    }
}
