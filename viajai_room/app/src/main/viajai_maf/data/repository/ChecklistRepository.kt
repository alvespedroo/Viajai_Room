package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.ChecklistDao
import com.example.viajai_maf.data.ChecklistEntity
import kotlinx.coroutines.flow.Flow

/** Única camada da aplicação que acessa diretamente o ChecklistDao. */
class ChecklistRepository(private val dao: ChecklistDao) {
    fun buscarTodos(): Flow<List<ChecklistEntity>> = dao.buscarTodos()
    suspend fun buscarPorId(id: Int): ChecklistEntity? = dao.buscarPorId(id)
    suspend fun buscarPorViagem(viagemId: Int): List<ChecklistEntity> = dao.buscarPorViagem(viagemId)
    suspend fun inserir(item: ChecklistEntity): Long = dao.inserir(item)
    suspend fun atualizar(item: ChecklistEntity) = dao.atualizar(item)
    suspend fun deletar(item: ChecklistEntity) = dao.deletar(item)
    suspend fun deletarCategoria(viagemId: Int, categoria: String) = dao.deletarCategoria(viagemId, categoria)
}
