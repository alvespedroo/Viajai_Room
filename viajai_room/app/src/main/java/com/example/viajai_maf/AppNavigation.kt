package com.example.viajai_maf

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val dados = remember(context) { ViajaiDados(context.applicationContext) }
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
                    onDetalhe = { nav.navigate("viagem/$it") })
            }
            composable(Rotas.DESTINOS) {
                ViajaiDestinosScreen(onNavigateToChecklist = { nav.navigate(Rotas.CHECKLIST) },
                    onNavigateToHome = { nav.navigate(Rotas.HOME) },
                    onNavigateToDestinos = { nav.navigate(Rotas.EDITAR) },
                    onNavigateToGastos = { nav.navigate(Rotas.GASTOS) }, dados = dados,
                    onAvaliacoes = { nav.navigate("reviews/$it") })
            }
            composable(Rotas.PESQUISAR) { PesquisarScreen(onSelecionarDestino = { nav.navigate("reviews/$it") }, dados = dados) }
            composable(Rotas.PERFIL) { PerfilViajaiScreen(dados, onViagens = { nav.navigate(Rotas.CHECKLIST) }, onAbrirLugar = { nav.navigate("reviews/$it") }) }
            composable(Rotas.GASTOS) { GastosViajaiScreen(dados, onVoltar = { nav.popBackStack() }) }
            composable(Rotas.BAGAGEM) { BagagemViajaiScreen(dados, onVoltar = { nav.popBackStack() }, onDetalhe = { nav.navigate("bagagem/$it") }) }
            composable(Rotas.DETALHE_BAGAGEM, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                DetalheBagagemScreen(dados, entry.arguments?.getInt("id") ?: -1, onVoltar = { nav.popBackStack() })
            }
            composable(Rotas.DETALHE_VIAGEM, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                DetalheViagemScreen(dados, entry.arguments?.getInt("id") ?: -1, onVoltar = { nav.popBackStack() }, onGastos = { nav.navigate(Rotas.GASTOS) })
            }
            composable(Rotas.REVIEWS, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                ComentariosReviewScreen(pousadaId = entry.arguments?.getInt("id") ?: 1, onVoltar = { nav.popBackStack() }, dados = dados)
            }
            composable(Rotas.EDITAR) { EditarViagemScreen(onVoltar = { nav.popBackStack() }, onSalvar = { origem, destino, data, orcamento ->
                dados.inserirViagem(origem, destino, data, orcamento) {
                    nav.navigate(Rotas.CHECKLIST) { popUpTo(Rotas.HOME) { inclusive = false }; launchSingleTop = true }
                }
            }) }
        }
    }
}
