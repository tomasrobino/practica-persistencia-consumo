package com.example.practica_persistencia_consumo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    Coche::class,
    Motor::class,
    Propietario::class,
    Mecanico::class,
    CocheMecanicoCrossRef::class], version = 2, exportSchema = false)

abstract class ConcesionarioDatabase: RoomDatabase() {
    abstract fun cocheDao(): CocheDao
    abstract fun MotorDao(): MotorDao
    companion object {
        @Volatile
        private var INSTANCE: ConcesionarioDatabase? = null

        fun getDatabase(context: Context): ConcesionarioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ConcesionarioDatabase::class.java,
                                "concesionario_database"
                            ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}