package com.example.practica_persistencia_consumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ElViewModel(private val cocheDao: CocheDao): ViewModel() {
    val allCoches = cocheDao.getAllCoches()

    fun insertCoche(color: String, marca: String, modelo: String) {
        viewModelScope.launch {
            val coche = Coche(color = color, marca = marca, modelo = modelo)
            cocheDao.insert(coche)
        }
    }
}