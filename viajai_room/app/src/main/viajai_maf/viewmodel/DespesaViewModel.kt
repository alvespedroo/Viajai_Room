package com.example.viajai_maf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.viajai_maf.data.DespesaEntity
import com.example.viajai_maf.data.repository.DespesaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DespesaUiState(
    val itens: List<DespesaEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class DespesaViewModel(private val repository: DespesaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DespesaUiState())
    val uiState: StateFlow<DespesaUiState> = _uiState.asStateFlow()

    init {
        observar(
            repository.buscarTodos(),
            aoFalhar = { erro -> _uiState.update { it.copy(carregando = false, erro = erro) } }
        ) { lista -> _uiState.update { it.copy(itens = lista, carregando = false, erro = null) } }
    }

    fun adicionar(descricao: String, categoria: String, valor: Double, viagemId: Int) {
        executar("Falha ao inserir despesa") {
            repository.inserir(DespesaEntity(descricao = descricao, categoria = categoria, valor = valor, viagemId = viagemId))
        }
    }

    fun remover(id: Int) {
        executar("Falha ao remover despesa") { repository.buscarPorId(id)?.let { repository.deletar(it) } }
    }
}

class DespesaViewModelFactory(private val repository: DespesaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DespesaViewModel::class.java)) return DespesaViewModel(repository) as T
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
