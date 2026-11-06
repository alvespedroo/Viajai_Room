package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.AvaliacaoDao
import com.example.viajai_maf.data.AvaliacaoEntity
import kotlinx.coroutines.flow.Flow

/** Acesso aos dados de Avaliacao, isolando o DAO das camadas superiores. */
class AvaliacaoRepository(private val dao: AvaliacaoDao) {
    fun buscarTodos(): Flow<List<AvaliacaoEntity>> = dao.buscarTodos()
    suspend fun buscarTodas() = dao.buscarTodas()
    suspend fun buscarPorId(id: Int) = dao.buscarPorId(id)
    suspend fun inserir(avaliacao: AvaliacaoEntity) = dao.inserir(avaliacao)
    suspend fun atualizar(avaliacao: AvaliacaoEntity) = dao.atualizar(avaliacao)
    suspend fun deletar(avaliacao: AvaliacaoEntity) = dao.deletar(avaliacao)
    suspend fun buscarPorLugar(lugarId: Int) = dao.buscarPorLugar(lugarId)
    suspend fun buscarMinhas() = dao.buscarMinhas()
    suspend fun atualizarAutorMinhas(novoNome: String) = dao.atualizarAutorMinhas(novoNome)
}
