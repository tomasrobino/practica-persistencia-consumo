package com.example.practica_persistencia_consumo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "propietario")
data class Propietario(
    @PrimaryKey(autoGenerate = true) val propietarioId: Int = 0,
    val nombre: String,
    val telefono: String
)

data class PropietarioConCoches(
    @Embedded val propietario: Propietario,
    @Relation(
        parentColumn = "propietarioId",
        entityColumn = "propietarioId"
    )
    val coches: List<Coche>
)

@Dao
interface PropietarioDao {
    @Insert
    suspend fun insertPropietario(propietario: Propietario): Long

    /** Upsert para sincronización con la API */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPropietario(propietario: Propietario)

    @Update
    suspend fun updatePropietario(propietario: Propietario)

    @Delete
    suspend fun deletePropietario(propietario: Propietario)

    @Query("SELECT * FROM propietario")
    fun getAllPropietarios(): Flow<List<Propietario>>

    @Query("SELECT * FROM propietario WHERE propietarioId = :id")
    fun getPropietarioById(id: Int): Flow<Propietario?>

    /** Versión suspend (no Flow) para el repositorio */
    @Query("SELECT * FROM propietario WHERE propietarioId = :id")
    suspend fun getPropietarioByIdOnce(id: Int): Propietario?

    @Transaction
    @Query("SELECT * FROM propietario")
    fun getPropietariosConCoches(): Flow<List<PropietarioConCoches>>

    @Transaction
    @Query("SELECT * FROM propietario WHERE propietarioId = :id")
    fun getPropietarioConCochesById(id: Int): Flow<PropietarioConCoches?>
}