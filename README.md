# Viajaí 🚗✈️

Aplicativo Android para planejamento, organização e acompanhamento de viagens de carro, desenvolvido em Kotlin com Jetpack Compose e persistência de dados utilizando Room.

## 👥 Integrantes

- Pedro Henrique Alves
- Eduardo Fogaça Bader

## 🎓 Informações acadêmicas

- **Instituição:** Universidade Positivo
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Disciplina:** Desenvolvimento de Aplicativos Móveis
- **Professor:** André Luis Jeller Selleti

---

## 📱 Sobre o projeto

O **Viajaí** é um aplicativo desenvolvido para auxiliar no planejamento e na organização de viagens de carro, oferecendo ferramentas que permitem ao usuário preparar sua viagem com mais praticidade e segurança.

O aplicativo disponibiliza funcionalidades como checklist de itens necessários para a viagem, controle e acompanhamento de gastos, pesquisa de locais e avaliação de destinos, incluindo cidades, restaurantes, hotéis e pontos turísticos.

Seu principal objetivo é ajudar os viajantes a se organizarem antecipadamente, evitando esquecimentos, imprevistos e dificuldades durante o percurso.

O aplicativo permite cadastrar várias viagens, mantendo as informações de cada uma organizadas individualmente, sem misturar checklists, despesas ou dados de destinos diferentes.

## ✨ Funcionalidades

### 1. Dashboard inicial

A tela inicial apresenta um painel com as principais informações da viagem selecionada:

- Origem e destino.
- Score de preparação da viagem.
- Botão para continuar o planejamento.
- Progresso do checklist e da bagagem.
- Total de gastos registrados.
- Destinos relacionados à viagem.
- Acesso à checklist e à gestão de gastos.
- Opção para adicionar uma nova viagem.
- Acesso às opções de hospedagem dos locais relacionados à viagem.

### 2. Gerenciamento de viagens

O usuário pode cadastrar e gerenciar mais de uma viagem.

Cada viagem possui suas próprias informações e seus próprios dados, permitindo que o planejamento seja realizado individualmente.

Na tela de checklist, é possível selecionar qual viagem está sendo preparada. Assim, o usuário consegue alternar entre viagens sem perder as informações já cadastradas.

### 3. Checklist de viagem

Permite organizar os itens necessários para a viagem, incluindo a conferência da bagagem.

O usuário pode acompanhar o progresso de preparação e visualizar quantos itens foram preenchidos em relação ao total.

O checklist é associado à viagem selecionada, mantendo os dados organizados.

### 4. Gestão de gastos

O aplicativo permite cadastrar despesas individualmente e acompanhar os valores relacionados à viagem.

Entre as funcionalidades estão:

- Cadastro de despesas.
- Consulta dos gastos registrados.
- Visualização do total gasto.
- Acompanhamento do orçamento e do saldo estimado.
- Gerenciamento de despesas durante o planejamento e o encerramento da viagem.

### 5. Pesquisa de locais

O usuário pode pesquisar locais específicos e salvar opções para utilizar em viagens futuras.

Os locais salvos ficam disponíveis na aba de perfil, organizados por categorias:

- Todos.
- Cidades.
- Hotéis.
- Restaurantes.
- Passeios.

Também é possível remover locais da lista de salvos.

### 6. Avaliações de locais

O aplicativo permite avaliar lugares por meio de notas em estrelas e comentários.

O usuário pode consultar suas avaliações e também editar ou excluir comentários já registrados.

### 7. Perfil e histórico

A aba de perfil reúne informações importantes do usuário:

- **Lugares salvos:** locais favoritos organizados por categorias e filtros.
- **Histórico de avaliações:** avaliações realizadas, com possibilidade de acessar e editar os comentários.
- **Histórico de viagens:** viagens encerradas, apresentando informações como local e data.
- **Viagens em andamento:** painel com o número de viagens planejadas, score de preparação, origem e destino, permitindo acessar diretamente a checklist.

### 8. Encerramento de viagem

O Viajaí permite finalizar uma viagem após a conferência das informações.

Caso existam itens pendentes na checklist, o aplicativo apresenta um alerta informando a quantidade de itens que ainda faltam preencher e solicita a confirmação do usuário antes de prosseguir.

Na etapa de encerramento, são apresentados:

- Origem.
- Chegada.
- Data da viagem.
- Score de preparação.
- Orçamento.
- Gastos registrados.
- Saldo estimado.

O usuário ainda pode gerenciar os gastos nessa etapa antes de concluir a viagem.

Após a conclusão, a viagem fica registrada no histórico de viagens encerradas.

---

## 🗄️ Persistência de dados com Room

Neste projeto, foi utilizada a biblioteca **Room**, uma solução de persistência de dados do Android que facilita o armazenamento e a consulta de informações em um banco de dados SQLite.

A implementação foi organizada utilizando:

- **Entities:** representam as tabelas e os dados armazenados no banco.
- **DAOs (Data Access Objects):** responsáveis pelas operações de acesso aos dados.
- **AppDatabase:** classe responsável pela configuração e disponibilização do banco de dados.
- **KSP:** utilizado para processar as anotações do Room e gerar os códigos necessários.

O projeto possui múltiplas entidades e DAOs, com operações de inserção, consulta, atualização e exclusão, além de consultas específicas para recuperar registros por identificador e outros critérios.

### Operações de banco de dados

As operações de persistência são realizadas utilizando corrotinas, com execução das tarefas de banco de dados na thread de entrada e saída (IO).

Também são utilizados blocos `try/catch` para tratamento de possíveis erros, com registro de informações de depuração por meio de `Log.e`.

As telas utilizam `LaunchedEffect` para carregar os dados necessários durante sua inicialização ou quando determinados identificadores são alterados.

### Organização da implementação

A estrutura do banco de dados está concentrada no arquivo:

`RoomDatabase.kt`

A lógica de acesso aos dados e integração com as telas está organizada principalmente em:

`ViajaiMAFScreens.kt`

A navegação entre as telas é organizada em:

`AppNavigation.kt`

---

## 🧪 Testes de persistência

Foram realizados testes para verificar o funcionamento das operações de cadastro, consulta, edição e exclusão dos dados.

Também foi verificado o comportamento da persistência após fechar e abrir novamente o aplicativo.

**Resultado observado:** os dados cadastrados permanecem armazenados após o encerramento e a reabertura do aplicativo, demonstrando o funcionamento da persistência local com Room.

A demonstração em vídeo apresenta o funcionamento do aplicativo e suas principais funcionalidades.

---

## 🛠️ Tecnologias utilizadas

- **Kotlin:** linguagem de programação.
- **Android Studio:** ambiente de desenvolvimento.
- **Jetpack Compose:** construção das interfaces.
- **Material 3:** componentes visuais.
- **Room:** persistência local de dados.
- **SQLite:** armazenamento do banco de dados.
- **KSP:** processamento das anotações do Room.
- **Coroutines:** execução de operações assíncronas.
- **Navigation Compose:** navegação entre telas.
- **ViewModel e Lifecycle:** suporte ao gerenciamento do ciclo de vida da interface.

---

## ▶️ Como executar o projeto

### Pré-requisitos

- Android Studio instalado.
- SDK Android 35.
- Dispositivo Android ou emulador com API 26 ou superior.

### Passos para execução

1. Clone o repositório:

   ```bash
   git clone URL_DO_REPOSITORIO
   ```

2. Abra o projeto no Android Studio.

3. Aguarde a sincronização do Gradle e o download das dependências.

4. Selecione um emulador ou conecte um dispositivo Android.

5. Execute o aplicativo pelo botão **Run** do Android Studio.

---

## 🎥 Demonstração do aplicativo

O vídeo abaixo apresenta o funcionamento do Viajaí, demonstrando suas principais funcionalidades e o comportamento da persistência de dados.

**Vídeo de demonstração:**  
[https://drive.google.com/file/d/12I-5NOh7JItBI3p0qnywtjgC6FGj2zLd/view?usp=drive_link]

---

## 🔗 Repositório

[https://github.com/alvespedroo/Viajai_Room]

---

## 📌 Considerações finais

O desenvolvimento do Viajaí permitiu aplicar conceitos de desenvolvimento Android utilizando Kotlin e Jetpack Compose, além de trabalhar com persistência local de dados por meio do Room.

A implementação possibilitou organizar informações de diferentes viagens, realizar operações CRUD, desenvolver consultas específicas e manter os dados armazenados mesmo após o fechamento do aplicativo.

O projeto busca oferecer uma experiência prática e organizada para o planejamento de viagens, reunindo em um único aplicativo recursos de checklist, controle financeiro, pesquisa de locais e avaliações.
