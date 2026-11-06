package com.example.viajai_maf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.viajai_maf.data.ChecklistEntity
import com.example.viajai_maf.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChecklistUiState(
    val itens: List<ChecklistEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class ChecklistViewModel(private val repository: ChecklistRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ChecklistUiState())
    val uiState: StateFlow<ChecklistUiState> = _uiState.asStateFlow()

    init {
        observar(
            repository.buscarTodos(),
            aoFalhar = { erro -> _uiState.update { it.copy(carregando = false, erro = erro) } }
        ) { lista -> _uiState.update { it.copy(itens = lista, carregando = false, erro = null) } }
    }

    fun adicionar(categoria: String, nome: String, viagemId: Int, marcado: Boolean = false) {
        executar("Falha ao inserir item de checklist") {
            repository.inserir(ChecklistEntity(categoria = categoria, nome = nome, marcado = marcado, viagemId = viagemId))
        }
    }

    fun atualizar(item: ChecklistEntity) {
        executar("Falha ao atualizar item de checklist") { repository.atualizar(item) }
    }

    fun remover(id: Int) {
        executar("Falha ao remover item de checklist") { repository.buscarPorId(id)?.let { repository.deletar(it) } }
    }

    fun removerCategoria(viagemId: Int, categoria: String) {
        executar("Falha ao remover categoria") { repository.deletarCategoria(viagemId, categoria) }
    }
}

class ChecklistViewModelFactory(private val repository: ChecklistRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChecklistViewModel::class.java)) return ChecklistViewModel(repository) as T
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
