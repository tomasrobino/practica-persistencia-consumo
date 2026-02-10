package com.example.practica_persistencia_consumo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CocheItem(coche: Coche) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = coche.color, style = MaterialTheme.typography.bodyLarge)
            Text(text = coche.marca, style = MaterialTheme.typography.bodyLarge)
            Text(text = coche.modelo, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ListaCoches(cocheRepository: CocheRepository) {
    val coches by cocheRepository.getAllCoches().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    Column() {
        Button(onClick = {
            scope.launch {
                cocheRepository.insertCoche(
                    Coche(color = "fdsfds", modelo = "fdsfsdf", marca = "sddsad")
                )
            }
        }) {
            Text("Agregar coche")
        }


        LazyColumn {
            items(coches) {
                    coche -> CocheItem(coche = coche)
            }
        }
    }
}