package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.ViagemDao
import com.example.viajai_maf.data.ViagemEntity
import kotlinx.coroutines.flow.Flow

/** Única camada da aplicação que acessa diretamente o ViagemDao. */
class ViagemRepository(private val dao: ViagemDao) {
    fun buscarTodos(): Flow<List<ViagemEntity>> = dao.buscarTodos()
    suspend fun buscarPorId(id: Int): ViagemEntity? = dao.buscarPorId(id)
    suspend fun inserir(item: ViagemEntity): Long = dao.inserir(item)
    suspend fun atualizar(item: ViagemEntity) = dao.atualizar(item)
    suspend fun deletar(item: ViagemEntity) = dao.deletar(item)
    suspend fun buscarPorStatus(concluida: Boolean): List<ViagemEntity> = dao.buscarPorStatus(concluida)
}
