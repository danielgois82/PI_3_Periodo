package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.TipoUsuario
import com.example.mpi.data.Usuario

class TipoUsuarioRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: TipoUsuarioRepository

        fun getInstance(context: Context): TipoUsuarioRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = TipoUsuarioRepository(context)
                }
            }
            return instance
        }
    }

    fun obterTipoUsuarioPorId(id: Int): TipoUsuario? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM tipoUsuario WHERE id = ?", arrayOf(id.toString()))

        var tipoUsuario: TipoUsuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPOUSUARIO_ID))
            val cargo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPOUSUARIO_CARGO))

            tipoUsuario = TipoUsuario(id, cargo)
        }

        cursor.close()
        db.close()

        return tipoUsuario
    }

}