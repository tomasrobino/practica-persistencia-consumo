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
fun CocheConMotorItem(cocheConMotor: CocheConMotor) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = cocheConMotor.coche.color, style = MaterialTheme.typography.bodyLarge)
            Text(text = cocheConMotor.coche.marca, style = MaterialTheme.typography.bodyLarge)
            Text(text = cocheConMotor.coche.modelo, style = MaterialTheme.typography.bodyLarge)
            Text(text = cocheConMotor.motor?.marca ?: "", style = MaterialTheme.typography.bodyLarge)
            Text(text = cocheConMotor.motor?.modelo ?: "", style = MaterialTheme.typography.bodyLarge)
            Text(text = cocheConMotor.motor?.cilindrada.toString(), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ListaCoches(cocheDao: CocheDao) {
    val cochesConMotor by cocheDao.getCochesConMotor().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    Column {
        Button(onClick = {
            scope.launch {
                cocheDao.insertCocheConMotor(
                    Coche(
                        color = "fdsfds",
                        modelo = "fdsfsdf",
                        marca = "sddsad"
                    ),
                    Motor(
                        marca = "BMW",
                        modelo = "V8",
                        cilindrada = 4000,
                        cocheId = 0 // temporary
                    )
                )
            }
        }) {
            Text("Agregar coche")
        }


        LazyColumn {
            items(cochesConMotor) {
                    cocheConMotor -> CocheConMotorItem(cocheConMotor = cocheConMotor)
            }
        }
    }
}