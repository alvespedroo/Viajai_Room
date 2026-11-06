package com.example.viajai_maf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.viajai_maf.data.AvaliacaoEntity
import com.example.viajai_maf.data.repository.AvaliacaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AvaliacaoUiState(
    val avaliacoes: List<AvaliacaoEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class AvaliacaoViewModel(private val repository: AvaliacaoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AvaliacaoUiState())
    val uiState: StateFlow<AvaliacaoUiState> = _uiState.asStateFlow()

    init {
        observar(
            repository.buscarTodos(),
            aoFalhar = { erro -> _uiState.update { it.copy(carregando = false, erro = erro) } }
        ) { lista -> _uiState.update { it.copy(avaliacoes = lista, carregando = false, erro = null) } }
    }

    fun adicionar(lugarId: Int, autor: String, texto: String, nota: Double, onResult: (AvaliacaoEntity) -> Unit = {}) {
        executar("Falha ao salvar avaliação") {
            val item = AvaliacaoEntity(lugarId = lugarId, autor = autor, texto = texto, nota = nota, isMinha = true)
            val id = repository.inserir(item).toInt()
            onResult(item.copy(id = id))
        }
    }

    fun alternarCurtida(item: AvaliacaoEntity, onResult: (AvaliacaoEntity) -> Unit = {}) {
        executar("Falha ao salvar curtida") {
            val atualizado = item.copy(
                curtidas = (item.curtidas + if (item.curtiu) -1 else 1).coerceAtLeast(0),
                curtiu = !item.curtiu
            )
            repository.atualizar(atualizado)
            onResult(atualizado)
        }
    }

    fun editar(item: AvaliacaoEntity, texto: String, nota: Double, onResult: (AvaliacaoEntity) -> Unit = {}) {
        executar("Falha ao editar avaliação") {
            val atualizada = item.copy(texto = texto, nota = nota)
            repository.atualizar(atualizada)
            onResult(atualizada)
        }
    }

    fun excluir(item: AvaliacaoEntity, onComplete: () -> Unit = {}) {
        executar("Falha ao excluir avaliação") {
            repository.deletar(item)
            onComplete()
        }
    }
}

class AvaliacaoViewModelFactory(private val repository: AvaliacaoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvaliacaoViewModel::class.java)) return AvaliacaoViewModel(repository) as T
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
