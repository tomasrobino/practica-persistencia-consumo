package com.example.practica_persistencia_consumo

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
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
}

class CocheRepository(private val cocheDao: CocheDao) {
    fun getAllCoches(): Flow<List<Coche>> = cocheDao.getAllCoches()
    suspend fun insertCoche(coche: Coche) {
        cocheDao.insert(coche)
    }
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

@Database(entities = [
    Coche::class,
    Motor::class,
    Propietario::class,
    Mecanico::class,
    CocheMecanicoCrossRef::class], version = 1, exportSchema = false)

abstract class CocheDatabase: RoomDatabase() {
    abstract fun cocheDao(): CocheDao
    abstract fun MotorDao(): MotorDao
    companion object {
        @Volatile
        private var INSTANCE: CocheDatabase? = null

        fun getDatabase(context: Context): CocheDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CocheDatabase::class.java,
                    "concesionario_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

