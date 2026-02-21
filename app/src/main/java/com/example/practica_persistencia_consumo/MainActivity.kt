package com.example.practica_persistencia_consumo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.practica_persistencia_consumo.ui.theme.PracticapersistenciaconsumoTheme

class MainActivity : ComponentActivity() {

    private val vm: ElViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticapersistenciaconsumoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    PantallaPrincipal(vm)
                }
            }
        }
    }
}