package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.DespesaDao
import com.example.viajai_maf.data.DespesaEntity
import kotlinx.coroutines.flow.Flow

/** Acesso aos dados de Despesa, isolando o DAO das camadas superiores. */
class DespesaRepository(private val dao: DespesaDao) {
    fun buscarTodos(): Flow<List<DespesaEntity>> = dao.buscarTodos()
    suspend fun buscarPorId(id: Int) = dao.buscarPorId(id)
    suspend fun inserir(despesa: DespesaEntity) = dao.inserir(despesa)
    suspend fun atualizar(despesa: DespesaEntity) = dao.atualizar(despesa)
    suspend fun deletar(despesa: DespesaEntity) = dao.deletar(despesa)
    suspend fun buscarPorViagem(viagemId: Int) = dao.buscarPorViagem(viagemId)
}
