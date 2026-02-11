package com.example.practica_persistencia_consumo

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "coches")
data class Coche(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val color: String,
    val marca: String,
    val modelo: String
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

@Database(entities = [Coche::class,Motor::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun cocheDao(): CocheDao
    abstract fun MotorDao(): MotorDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "concesionario_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

