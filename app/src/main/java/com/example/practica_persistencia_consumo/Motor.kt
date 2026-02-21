package com.example.practica_persistencia_consumo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow



@Entity(
    tableName ="motor",
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
    ]
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
    suspend fun insert(motor: Motor)

    @Query("SELECT * FROM motor")
    fun getAllMotores(): Flow<List<Motor>>

    @Query("SELECT * FROM motor WHERE id = :id")
    fun getMotorById(id: Int): Flow<List<Motor>>

    @Delete
    suspend fun deleteMotor(motor: Motor)

    @Update
    suspend fun updateMotor(motor: Motor)

}

class MotorRepository(private val motorDao:MotorDao){
    suspend fun insert(motor: Motor) {
        motorDao.insert(motor)
    }

    fun getAllMotores(): Flow<List<Motor>> = motorDao.getAllMotores()

    fun getMotorById(id : Int): Flow<List<Motor>> = motorDao.getMotorById(id)

    suspend fun deleteMotor(motor: Motor){
        motorDao.deleteMotor(motor)
    }

    suspend fun updateMotor(motor: Motor){
        motorDao.updateMotor(motor)
    }

}
