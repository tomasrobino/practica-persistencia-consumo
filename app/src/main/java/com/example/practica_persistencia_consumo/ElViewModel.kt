package com.example.practica_persistencia_consumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ElViewModel(private val cocheDao: CocheDao): ViewModel() {
    val allCoches: StateFlow<List<Coche>> = cocheDao.getAllCoches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertCoche(color: String, marca: String, modelo: String) {
        viewModelScope.launch {
            val coche = Coche(color = color, marca = marca, modelo = modelo)
            cocheDao.insert(coche)
        }
    }
}