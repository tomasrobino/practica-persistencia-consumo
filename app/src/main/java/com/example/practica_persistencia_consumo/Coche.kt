package com.example.practica_persistencia_consumo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "coches")
data class Coche(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val color: String,
    val marca: String,
    val modelo: String,
    val propietarioId: Int? = null
)

@Dao
interface CocheDao {
    @Insert
    suspend fun insert(coche: Coche): Long

    @Insert
    suspend fun insertMotor(motor: Motor)

    @Transaction
    suspend fun insertCocheConMotor(coche: Coche, motor: Motor) {
        val cocheId = insert(coche).toInt()
        insertMotor(motor.copy(cocheId = cocheId))
    }

    @Query("SELECT * FROM coches")
    fun getAllCoches(): Flow<List<Coche>>

    @Query("SELECT * FROM coches WHERE id = :id")
    fun getCocheById(id: Int): Flow<Coche?>

    @Delete
    suspend fun deleteCoche(coche: Coche)

    @Update
    suspend fun updateCoche(coche: Coche)

    @Transaction
    @Query("SELECT * FROM coches")
    fun getCochesConMotor(): Flow<List<CocheConMotor>>

    @Transaction
    @Query("SELECT * FROM coches WHERE id = :id")
    fun getCocheConMotorById(id: Int): Flow<CocheConMotor?>
}

data class CocheConMotor(
    @Embedded val coche: Coche,
    @Relation(
        parentColumn = "id",
        entityColumn = "cocheId"
    )
    val motor: Motor?
)

@Entity(
    primaryKeys = ["cocheId", "mecanicoId"],
    indices = [Index("mecanicoId")]
)
data class CocheMecanicoCrossRef(
    val cocheId: Int,
    val mecanicoId: Int
)

data class CocheConMecanicos(
    @Embedded val coche: Coche,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CocheMecanicoCrossRef::class,
            parentColumn = "cocheId",
            entityColumn = "mecanicoId"
        )
    )
    val mecanicos: List<Mecanico>
)