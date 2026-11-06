package com.example.viajai_maf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.viajai_maf.data.BagagemEntity
import com.example.viajai_maf.data.repository.BagagemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

data class BagagemUiState(
    val itens: List<BagagemEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class BagagemViewModel(private val repository: BagagemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(BagagemUiState())
    val uiState: StateFlow<BagagemUiState> = _uiState.asStateFlow()

    init {
        // Primeira abertura: cria os itens de exemplo da mala.
        executar("Falha ao criar bagagem inicial") {
            if (repository.buscarTodos().first().isEmpty()) {
                repository.inserir(BagagemEntity(nome = "Roupas", quantidade = 5, preparado = true))
                repository.inserir(BagagemEntity(nome = "Carregador", quantidade = 1, preparado = false))
            }
        }
        observar(
            repository.buscarTodos(),
            aoFalhar = { erro -> _uiState.update { it.copy(carregando = false, erro = erro) } }
        ) { lista -> _uiState.update { it.copy(itens = lista, carregando = false, erro = null) } }
    }

    fun adicionar(nome: String, quantidade: Int) {
        executar("Falha ao inserir item de bagagem") { repository.inserir(BagagemEntity(nome = nome, quantidade = quantidade, preparado = false)) }
    }

    fun atualizar(item: BagagemEntity) {
        executar("Falha ao atualizar item de bagagem") { repository.atualizar(item) }
    }

    fun remover(id: Int) {
        executar("Falha ao remover item de bagagem") { repository.buscarPorId(id)?.let { repository.deletar(it) } }
    }
}

class BagagemViewModelFactory(private val repository: BagagemRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BagagemViewModel::class.java)) return BagagemViewModel(repository) as T
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
