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
    CocheMecanicoCrossRef::class], version = 4, exportSchema = false)

abstract class ConcesionarioDatabase: RoomDatabase() {
    abstract fun cocheDao(): CocheDao
    abstract fun motorDao(): MotorDao
    abstract fun propietarioDao(): PropietarioDao
    abstract fun cocheMecanicoDao(): CocheMecanicoDao

    companion object {
        @Volatile
        private var INSTANCE: ConcesionarioDatabase? = null

        fun getDatabase(context: Context): ConcesionarioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                ConcesionarioDatabase::class.java,
                                "concesionario_database"
                            ).fallbackToDestructiveMigration(true).build() // para que no crashee al hacer cambios
                INSTANCE = instance
                return instance
            }
        }
    }
}