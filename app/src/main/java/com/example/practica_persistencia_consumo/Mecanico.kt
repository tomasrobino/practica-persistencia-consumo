package com.example.practica_persistencia_consumo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mecanicos")
data class Mecanico (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val especialidad: String
)


data class MecanicosConCoche(
    @Embedded val mecanico: Mecanico,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(CocheMecanicoCrossRef::class) // La tabla cruzada
    )
    val coches: List<Coche>
)

@Dao
interface CocheMecanicoDao {
    // Link a car to a mechanic)
    @Insert
    suspend fun insertCocheMecanicoCrossRef(crossRef: CocheMecanicoCrossRef)

    // Unlink a car from a mechanic)
    @Delete
    suspend fun deleteCocheMecanicoCrossRef(crossRef: CocheMecanicoCrossRef)

    // Get all cars with their mechanics
    @Transaction
    @Query("SELECT * FROM coches")
    fun getCochesConMecanicos(): Flow<List<CocheConMecanicos>>

    // Get a specific car with its mechanics
    @Transaction
    @Query("SELECT * FROM coches WHERE id = :cocheId")
    fun getCocheConMecanicosById(cocheId: Int): Flow<CocheConMecanicos?>

    // Get all mechanics with their cars
    @Transaction
    @Query("SELECT * FROM mecanicos")
    fun getMecanicosConCoches(): Flow<List<MecanicosConCoche>>

    // Get a specific mechanic with their cars
    @Transaction
    @Query("SELECT * FROM mecanicos WHERE id = :mecanicoId")
    fun getMecanicoConCochesById(mecanicoId: Int): Flow<MecanicosConCoche?>
}