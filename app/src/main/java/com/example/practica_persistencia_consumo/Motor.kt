package com.example.practica_persistencia_consumo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName ="motores",
    //Esta es la parate necesiara para no tener que creal el CRUD
    // del resto de entidades que no sean Coche

    foreignKeys = [
        ForeignKey(
            entity = Coche::class,
            parentColumns = ["id"],      // El ID del Coche
            childColumns = ["cocheId"],  // La columna en Motor que lo referencia
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    // Para forzar que no se repitan los ids
    indices = [Index(value = ["cocheId"], unique = true)]
)

data class Motor (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val marca: String,
    val modelo: String,
    val cilindrada: Int,
    val cocheId: Int
)

@Dao
interface MotorDao {
    @Insert
    suspend fun insertMotor(motor: Motor)

    @Query("SELECT * FROM motores")
    fun getAllMotores(): Flow<List<Motor>>

    @Query("SELECT * FROM motores WHERE id = :id")
    fun getMotorById(id: Int): Flow<Motor?>

    @Delete
    suspend fun deleteMotor(motor: Motor)

    @Update
    suspend fun updateMotor(motor: Motor)
}
