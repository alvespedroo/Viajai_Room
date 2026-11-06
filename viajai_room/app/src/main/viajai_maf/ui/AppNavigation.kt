package com.example.viajai_maf.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.viajai_maf.data.AppContainer
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

object Rotas {
    const val HOME = "home"
    const val CHECKLIST = "checklist"
    const val DESTINOS = "destinos"
    const val PESQUISAR = "pesquisar"
    const val PERFIL = "perfil"
    const val GASTOS = "gastos"
    const val BAGAGEM = "bagagem"
    const val DETALHE_BAGAGEM = "bagagem/{id}"
    const val DETALHE_VIAGEM = "viagem/{id}"
    const val REVIEWS = "reviews/{id}"
    const val EDITAR = "editar"
    const val EDITAR_ID = "editar/{id}"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val container = remember(context) { AppContainer(context.applicationContext) }
    val dados = rememberViajaiDados(container)
    val backStack by nav.currentBackStackEntryAsState()
    val rota = backStack?.destination?.route
    val abas = listOf(Rotas.HOME, Rotas.CHECKLIST, Rotas.PESQUISAR, Rotas.PERFIL)
    Scaffold(bottomBar = {
        if (rota in abas) {
            NavigationBar(containerColor = Color.White) {
                listOf(Triple(Rotas.HOME, "Início", Icons.Default.Home),
                    Triple(Rotas.CHECKLIST, "Checklist", Icons.Default.List),
                    Triple(Rotas.PESQUISAR, "Pesquisar", Icons.Default.Search),
                    Triple(Rotas.PERFIL, "Perfil", Icons.Default.Person)
                ).forEach { (dest, label, icon) ->
                    NavigationBarItem(selected = rota == dest,
                        onClick = { nav.navigate(dest) { launchSingleTop = true; popUpTo(Rotas.HOME) { saveState = false }; restoreState = false } },
                        icon = { Icon(icon, contentDescription = label) }, label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF982D26),
                            selectedTextColor = Color(0xFF982D26), indicatorColor = Color(0xFFFFE8DF)))
                }
            }
        }
    }) { padding ->
        NavHost(navController = nav, startDestination = Rotas.HOME, modifier = Modifier.padding(padding)) {
            composable(Rotas.HOME) {
                ViajaiHomeScreen(onNavigateToChecklist = { nav.navigate(Rotas.CHECKLIST) },
                    onNavigateToDestinos = { nav.navigate(Rotas.EDITAR) },
                    onNavigateToHome = { nav.navigate(Rotas.HOME) },
                    onNavigateToBagagem = { nav.navigate(Rotas.BAGAGEM) },
                    onNavigateToGastos = { nav.navigate(Rotas.GASTOS) }, onContinue = { nav.navigate("viagem/$it") }, onExplore = { nav.navigate(Rotas.DESTINOS) }, dados = dados)
            }
            composable(Rotas.CHECKLIST) {
                // Checklist original preservado; score reativo compartilhado no novo painel.
                ChecklistPainelScreen(dados, onViagem = { nav.navigate(Rotas.EDITAR) },
                    onBagagem = { nav.navigate(Rotas.BAGAGEM) }, onGastos = { nav.navigate(Rotas.GASTOS) },
                    onDetalhe = { nav.navigate("viagem/$it") }, onEditarViagem = { nav.navigate("editar/$it") })
            }
            composable(Rotas.DESTINOS) {
                ViajaiDestinosScreen(onNavigateToChecklist = { nav.navigate(Rotas.CHECKLIST) },
                    onNavigateToHome = { nav.navigate(Rotas.HOME) },
                    onNavigateToDestinos = { nav.navigate(Rotas.EDITAR) },
                    onNavigateToGastos = { nav.navigate(Rotas.GASTOS) }, dados = dados,
                    onEditarViagem = { nav.navigate("editar/$it") },
                    onAvaliacoes = { nav.navigate("reviews/$it") })
            }
            composable(Rotas.PESQUISAR) { PesquisarScreen(onSelecionarDestino = { nav.navigate("reviews/$it") }, dados = dados) }
            composable(Rotas.PERFIL) { PerfilViajaiScreen(dados, onViagens = { nav.navigate(Rotas.CHECKLIST) }, onAbrirLugar = { nav.navigate("reviews/$it") }, onEditarViagem = { nav.navigate("editar/$it") }) }
            composable(Rotas.GASTOS) { GastosViajaiScreen(dados, onVoltar = { nav.popBackStack() }, onEditarOrcamento = { nav.navigate("editar/$it") }) }
            composable(Rotas.BAGAGEM) { BagagemViajaiScreen(dados, onVoltar = { nav.popBackStack() }, onDetalhe = { nav.navigate("bagagem/$it") }) }
            composable(Rotas.DETALHE_BAGAGEM, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                DetalheBagagemScreen(dados, entry.arguments?.getInt("id") ?: -1, onVoltar = { nav.popBackStack() })
            }
            composable(Rotas.DETALHE_VIAGEM, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                DetalheViagemScreen(dados, entry.arguments?.getInt("id") ?: -1, onVoltar = { nav.popBackStack() }, onGastos = { nav.navigate(Rotas.GASTOS) }, onEditar = { nav.navigate("editar/$it") }, onBagagem = { nav.navigate(Rotas.BAGAGEM) })
            }
            composable(Rotas.REVIEWS, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                ComentariosReviewScreen(pousadaId = entry.arguments?.getInt("id") ?: 1, onVoltar = { nav.popBackStack() }, dados = dados)
            }
            composable(Rotas.EDITAR) { EditarViagemScreen(onVoltar = { nav.popBackStack() }, onSalvar = { origem, destino, data, orcamento, titulo ->
                dados.inserirViagem(origem, destino, data, orcamento, titulo) {
                    nav.navigate(Rotas.CHECKLIST) { popUpTo(Rotas.HOME) { inclusive = false }; launchSingleTop = true }
                }
            }) }
            composable(Rotas.EDITAR_ID, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                val id = entry.arguments?.getInt("id") ?: -1
                val atual = dados.viagemPorId(id)
                if (atual == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
                } else {
                    EditarViagemScreen(tituloInicial = atual.titulo, origemInicial = atual.origem, destinoInicial = atual.destino,
                        dataInicial = atual.data, orcamentoInicial = atual.orcamento, editando = true,
                        onVoltar = { nav.popBackStack() }, onSalvar = { origem, destino, data, orcamento, titulo ->
                            dados.atualizarViagem(id, origem, destino, data, orcamento, titulo) { nav.popBackStack() }
                        })
                }
            }
        }
    }
}
