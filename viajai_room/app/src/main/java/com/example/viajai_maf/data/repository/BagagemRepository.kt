package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.BagagemDao
import com.example.viajai_maf.data.BagagemEntity
import kotlinx.coroutines.flow.Flow

/** Acesso aos dados de Bagagem, isolando o DAO das camadas superiores. */
class BagagemRepository(private val dao: BagagemDao) {
    fun buscarTodos(): Flow<List<BagagemEntity>> = dao.buscarTodos()
    suspend fun buscarPorId(id: Int) = dao.buscarPorId(id)
    suspend fun inserir(item: BagagemEntity) = dao.inserir(item)
    suspend fun atualizar(item: BagagemEntity) = dao.atualizar(item)
    suspend fun deletar(item: BagagemEntity) = dao.deletar(item)
}
