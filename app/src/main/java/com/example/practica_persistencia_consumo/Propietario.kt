package com.example.practica_persistencia_consumo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "propietario")
 data class Propietario (

    @PrimaryKey(autoGenerate = true) val propietarioId: Int = 0,
    val nombre: String,
    val telefono: String

)


//Clase que relaciona Propietarios con Coches
data class PropietarioConCoches(
    @Embedded val propietario: Propietario,
    @Relation(
        parentColumn = "propietarioId",
        entityColumn = "propietarioId" // El campo que añadimos en Coche
    )
    val coches: List<Coche>
)