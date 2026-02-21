package com.example.practica_persistencia_consumo.clientektor

import com.example.practica_persistencia_consumo.Coche
import com.example.practica_persistencia_consumo.CocheMecanicoCrossRef
import com.example.practica_persistencia_consumo.Mecanico
import com.example.practica_persistencia_consumo.Motor
import com.example.practica_persistencia_consumo.Propietario
import kotlinx.serialization.Serializable

// DTOs para la API (separados de las entidades Room)

@Serializable
data class CocheDto(
    val id: Int = 0,
    val color: String,
    val marca: String,
    val modelo: String,
    val propietarioId: Int? = null
) {
    fun toEntity() =
        Coche(id = id, color = color, marca = marca, modelo = modelo, propietarioId = propietarioId)
}

fun Coche.toDto() = CocheDto(id = id, color = color, marca = marca, modelo = modelo, propietarioId = propietarioId)



@Serializable
data class MotorDto(
    val id: Int = 0,
    val marca: String,
    val modelo: String,
    val cilindrada: Int,
    val cocheId: Int
) {
    fun toEntity() =
        Motor(id = id, marca = marca, modelo = modelo, cilindrada = cilindrada, cocheId = cocheId)
}

fun Motor.toDto() = MotorDto(id = id, marca = marca, modelo = modelo, cilindrada = cilindrada, cocheId = cocheId)



@Serializable
data class PropietarioDto(
    val propietarioId: Int = 0,
    val nombre: String,
    val telefono: String
) {
    fun toEntity() =
        Propietario(propietarioId = propietarioId, nombre = nombre, telefono = telefono)
}

fun Propietario.toDto() = PropietarioDto(propietarioId = propietarioId, nombre = nombre, telefono = telefono)





@Serializable
data class MecanicoDto(
    val id: Int = 0,
    val nombre: String,
    val especialidad: String
) {
    fun toEntity() = Mecanico(id = id, nombre = nombre, especialidad = especialidad)
}

fun Mecanico.toDto() = MecanicoDto(id = id, nombre = nombre, especialidad = especialidad)




@Serializable
data class CocheMecanicoDto(
    val cocheId: Int,
    val mecanicoId: Int
) {
    fun toEntity() = CocheMecanicoCrossRef(cocheId = cocheId, mecanicoId = mecanicoId)
}