package com.example.practica_persistencia_consumo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.practica_persistencia_consumo.ui.theme.PracticapersistenciaconsumoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticapersistenciaconsumoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Padre()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun Padre() {
    val cocheDao = CocheDatabase.getDatabase(LocalContext.current).cocheDao()
    val repository = CocheRepository(cocheDao)
    ListaCoches(repository)

}