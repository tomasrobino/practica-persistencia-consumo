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
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mecanicos")
data class Mecanico(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val especialidad: String
)

data class MecanicosConCoche(
    @Embedded val mecanico: Mecanico,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CocheMecanicoCrossRef::class,
            parentColumn = "mecanicoId",
            entityColumn = "cocheId"
        )
    )
    val coches: List<Coche>
)

@Dao
interface CocheMecanicoDao {
    @Insert
    suspend fun insertCocheMecanicoCrossRef(crossRef: CocheMecanicoCrossRef)

    @Delete
    suspend fun deleteCocheMecanicoCrossRef(crossRef: CocheMecanicoCrossRef)

    @Transaction
    @Query("SELECT * FROM coches")
    fun getCochesConMecanicos(): Flow<List<CocheConMecanicos>>

    @Transaction
    @Query("SELECT * FROM coches WHERE id = :cocheId")
    fun getCocheConMecanicosById(cocheId: Int): Flow<CocheConMecanicos?>

    @Transaction
    @Query("SELECT * FROM mecanicos")
    fun getMecanicosConCoches(): Flow<List<MecanicosConCoche>>

    @Transaction
    @Query("SELECT * FROM mecanicos WHERE id = :mecanicoId")
    fun getMecanicoConCochesById(mecanicoId: Int): Flow<MecanicosConCoche?>
}