package com.example.viajai_maf.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/** Roda uma ação suspensa no viewModelScope e registra no Logcat qualquer falha (sem fechar o app). */
internal fun ViewModel.executar(mensagemErro: String, bloco: suspend () -> Unit) {
    viewModelScope.launch {
        try {
            bloco()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("ViajaI-Room", mensagemErro, e)
        }
    }
}

/** Observa um Flow do Repository: cada nova emissão chama [aoReceber]; falhas chamam [aoFalhar]. */
internal fun <T> ViewModel.observar(fluxo: Flow<T>, aoFalhar: (String?) -> Unit, aoReceber: (T) -> Unit) {
    viewModelScope.launch {
        fluxo.catch { aoFalhar(it.message) }.collect { aoReceber(it) }
    }
}
