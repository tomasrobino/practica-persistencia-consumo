package com.example.practica_persistencia_consumo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "coches")
data class Coche(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val color: String,
    val marca: String,
    val modelo: String,
    val propietarioId: Int? = null // <--- CONEXIÓN CON PROPIETARIO
)

@Dao
interface CocheDao {
    @Insert
    suspend fun insert(coche: Coche)

    @Query("SELECT * FROM coches")
    fun getAllCoches(): Flow<List<Coche>>

    @Query("SELECT * FROM coches WHERE id = :id")
    fun getCocheById(id: Int): Flow<List<Coche>>

    @Delete
    suspend fun deleteCoche(coche: Coche)

    @Update
    suspend fun updateCoche(coche: Coche)
}

//Tabla relacional de muchos a muchos
@Entity(primaryKeys = ["cocheId", "mecanicoId"])
data class CocheMecanicoCrossRef(
    val cocheId: Int,
    val mecanicoId: Int
)

//Clase que relaciona Coches con Mecanicos.
data class CocheConMecanicos(
    @Embedded val coche: Coche,
    @Relation(
        parentColumn = "id",
        entityColumn = "mecanicoId",
        associateBy = Junction(CocheMecanicoCrossRef::class) // La tabla cruzada
    )
    val mecanicos: List<Mecanico>
)