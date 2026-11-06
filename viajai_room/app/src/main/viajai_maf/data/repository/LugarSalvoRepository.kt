package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.LugarSalvoDao
import com.example.viajai_maf.data.LugarSalvoEntity
import kotlinx.coroutines.flow.Flow

/** Acesso aos dados de LugarSalvo, isolando o DAO das camadas superiores. */
class LugarSalvoRepository(private val dao: LugarSalvoDao) {
    fun buscarTodos(): Flow<List<LugarSalvoEntity>> = dao.buscarTodos()
    suspend fun inserir(lugar: LugarSalvoEntity) = dao.inserir(lugar)
    suspend fun atualizar(lugar: LugarSalvoEntity) = dao.atualizar(lugar)
    suspend fun deletar(lugar: LugarSalvoEntity) = dao.deletar(lugar)
    suspend fun buscarPorLugarId(lugarId: Int) = dao.buscarPorLugarId(lugarId)
}
