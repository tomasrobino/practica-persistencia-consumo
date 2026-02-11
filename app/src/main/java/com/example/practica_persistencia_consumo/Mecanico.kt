package com.example.practica_persistencia_consumo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mecanico")

data class Mecanico (
    @PrimaryKey(autoGenerate = true) val propietarioId: Int = 0,
    val nombre: String,
    val especialidad: String
)