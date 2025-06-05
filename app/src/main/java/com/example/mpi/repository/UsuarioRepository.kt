package com.example.mpi.repository

import android.content.Context
import com.example.mpi.data.DatabaseHelper
import com.example.mpi.data.Usuario

class UsuarioRepository (context: Context) {
    private var dataBase: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private lateinit var instance: UsuarioRepository

        fun getInstance(context: Context): UsuarioRepository {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = UsuarioRepository(context)
                }
            }
            return instance
        }
    }

    fun obterUsuarioPorEmailESenha(email: String, senha: String): Usuario? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuario WHERE email = ? AND senha = ?", arrayOf(email, senha))

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_EMAIL))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_SENHA))
            val idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO))

            usuario = Usuario(id, nome, email, senha, idTipoUsuario)
        }

        cursor.close()
        db.close()

        return usuario
    }

    fun obterUsuarioPorId(id: Int): Usuario? {
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuario WHERE id = ?", arrayOf(id.toString()))

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_EMAIL))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_SENHA))
            val idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO))

            usuario = Usuario(idUsuario, nome, email, senha, idTipoUsuario)
        }

        cursor.close()
        db.close()

        return usuario
    }

    fun obterNomeUsuarioPorId(id:Int): String{
        val db = dataBase.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuario WHERE id = ?", arrayOf(id.toString()))
        var nomeUsuario = ""
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_NOME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_EMAIL))
            val senha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_SENHA))
            val idTipoUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USUARIO_ID_TIPOUSUARIO))

            nomeUsuario = nome

        }

        cursor.close()
        db.close()

        return nomeUsuario
    }

}