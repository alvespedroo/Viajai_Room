package com.example.viajai_maf.data.repository

import com.example.viajai_maf.data.PerfilUsuarioDao
import com.example.viajai_maf.data.PerfilUsuarioEntity

/** Repositório do perfil local do usuário. */
class PerfilUsuarioRepository(private val dao: PerfilUsuarioDao) {
    suspend fun buscar(): PerfilUsuarioEntity? = dao.buscar()
    suspend fun salvar(perfil: PerfilUsuarioEntity) = dao.salvar(perfil)
}
