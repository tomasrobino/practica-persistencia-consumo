package com.example.practica_persistencia_consumo

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ListaCoches(cocheDao: CocheDao) {
    val coches by cocheDao.getAllCoches().collectAsState(initial = emptyList())

    LazyColumn {
        items(coches) {
            coche -> CocheItem(coche = coche)
        }
    }
}