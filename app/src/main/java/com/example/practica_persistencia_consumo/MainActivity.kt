package com.example.practica_persistencia_consumo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.practica_persistencia_consumo.ui.theme.PracticapersistenciaconsumoTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
fun Padre() {
    val cocheDao = CocheDatabase.getDatabase(LocalContext.current).cocheDao()
    val repository = CocheRepository(cocheDao)
    ListaCoches(repository)
}