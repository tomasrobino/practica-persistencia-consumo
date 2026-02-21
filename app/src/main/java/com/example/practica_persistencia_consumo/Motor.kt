package com.example.practica_persistencia_consumo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "motores",
    foreignKeys = [
        ForeignKey(
            entity = Coche::class,
            parentColumns = ["id"],
            childColumns = ["cocheId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cocheId"], unique = true)]
)
data class Motor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val marca: String,
    val modelo: String,
    val cilindrada: Int,
    val cocheId: Int
)

@Dao
interface MotorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotor(motor: Motor)

    /** Upsert para sincronización con la API */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMotor(motor: Motor)

    @Query("SELECT * FROM motores")
    fun getAllMotores(): Flow<List<Motor>>

    @Query("SELECT * FROM motores WHERE id = :id")
    fun getMotorById(id: Int): Flow<Motor?>

    /** Versión suspend (no Flow) para el repositorio */
    @Query("SELECT * FROM motores WHERE id = :id")
    suspend fun getMotorByIdOnce(id: Int): Motor?

    @Delete
    suspend fun deleteMotor(motor: Motor)

    @Update
    suspend fun updateMotor(motor: Motor)
}