package com.example.viajai_maf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.viajai_maf.data.ChecklistEntity
import com.example.viajai_maf.data.ViagemEntity
import com.example.viajai_maf.data.repository.ChecklistRepository
import com.example.viajai_maf.data.repository.ViagemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Tudo que as telas de viagem precisam para se desenhar. */
data class ViagemUiState(
    val viagens: List<ViagemEntity> = emptyList(),
    val selecionadaId: Int = -1,
    val carregando: Boolean = true,
    val erro: String? = null
) {
    val ativas: List<ViagemEntity> get() = viagens.filter { !it.concluida && !it.cancelada }
    val encerradas: List<ViagemEntity> get() = viagens.filter { it.concluida || it.cancelada }
    /** Viagem escolhida pelo usuário; se ela não estiver mais ativa, cai para a mais recente. */
    val atual: ViagemEntity? get() = ativas.firstOrNull { it.id == selecionadaId } ?: ativas.firstOrNull()
}

class ViagemViewModel(
    private val viagemRepository: ViagemRepository,
    private val checklistRepository: ChecklistRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ViagemUiState())
    val uiState: StateFlow<ViagemUiState> = _uiState.asStateFlow()

    init {
        observar(
            viagemRepository.buscarTodos(),
            aoFalhar = { erro -> _uiState.update { it.copy(carregando = false, erro = erro) } }
        ) { lista -> _uiState.update { it.copy(viagens = lista, carregando = false, erro = null) } }
    }

    fun selecionar(id: Int) = _uiState.update { it.copy(selecionadaId = id) }

    /** Cria a viagem já com o checklist padrão e a deixa selecionada. */
    fun criar(origem: String, destino: String, data: String, orcamento: Double, titulo: String, onComplete: (Int) -> Unit) {
        executar("Falha ao salvar viagem") {
            val nome = titulo.ifBlank { "$origem → $destino" }
            val id = viagemRepository.inserir(ViagemEntity(origem = origem, destino = destino, data = data, orcamento = orcamento, titulo = nome)).toInt()
            CHECKLIST_INICIAL.forEach { (categoria, item) ->
                checklistRepository.inserir(ChecklistEntity(categoria = categoria, nome = item, marcado = false, viagemId = id))
            }
            _uiState.update { it.copy(selecionadaId = id) }
            onComplete(id)
        }
    }

    fun atualizar(id: Int, origem: String, destino: String, data: String, orcamento: Double, titulo: String, onComplete: () -> Unit = {}) {
        executar("Falha ao editar viagem") {
            val nome = titulo.ifBlank { "$origem → $destino" }
            viagemRepository.buscarPorId(id)?.let {
                viagemRepository.atualizar(it.copy(origem = origem, destino = destino, data = data, orcamento = orcamento, titulo = nome))
            }
            onComplete()
        }
    }

    fun concluir(id: Int, onComplete: () -> Unit) {
        executar("Falha ao concluir viagem") {
            viagemRepository.buscarPorId(id)?.let { viagemRepository.atualizar(it.copy(concluida = true)) }
            onComplete()
        }
    }

    fun cancelar(id: Int, onComplete: () -> Unit) {
        executar("Falha ao cancelar viagem") {
            viagemRepository.buscarPorId(id)?.let { viagemRepository.atualizar(it.copy(cancelada = true)) }
            onComplete()
        }
    }

    private companion object {
        val CHECKLIST_INICIAL = listOf(
            "Documentos" to "RG", "Documentos" to "CNH",
            "Revisão do carro" to "Pneus", "Revisão do carro" to "Estepe",
            "Emergência" to "Kit médico"
        )
    }
}

class ViagemViewModelFactory(
    private val viagemRepository: ViagemRepository,
    private val checklistRepository: ChecklistRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViagemViewModel::class.java)) return ViagemViewModel(viagemRepository, checklistRepository) as T
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
