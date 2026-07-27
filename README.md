# Álbum de Figurinhas Digital

Aplicativo Android desenvolvido para a disciplina de Programação para Dispositivos Móveis (PDM) da Universidade Federal de Uberlândia (UFU).

## Objetivo

Desenvolver um álbum de figurinhas digital inspirado na Copa do Mundo de 2026, permitindo que o usuário:

- Visualize seleções, jogadores e treinadores;
- Colecione figurinhas;
- Abra pacotinhos utilizando moedas;
- Receba recompensas diárias;
- Venda figurinhas repetidas para obter novas moedas.

## Tecnologias

- Kotlin
- Jetpack Compose
- MVVM
- Retrofit
- Coroutines
- Navigation Compose

## Equipe

- Isabela Amorim de Paula
- Clara Magalhães dos Santos
- Karen Kristine Ferreira Muniz
- Heitor Borges Resende
- Erick Antunes Raposo
- Pedro da Costa Aguiar

## Status

🚧 Projeto em desenvolvimento.

Aplicativo Android desenvolvido para a disciplina de Programação para Dispositivos Móveis (PDM) da Universidade Federal de Uberlândia (UFU).

Um álbum de figurinhas digital inspirado na Copa do Mundo de 2026: o usuário navega pelas seleções, coleciona figurinhas, compra pacotinhos com moedas, recebe recompensa diária e vende figurinhas repetidas para obter mais moedas.

## Stack

- **Kotlin** — linguagem
- **Jetpack Compose** — UI declarativa
- **MVVM** — arquitetura
- **Navigation Compose** — navegação entre telas
- **Retrofit + Gson + OkHttp** — acesso à API-Sports
- **Coroutines / StateFlow** — assincronismo e estado reativo
- **Coil** — carregamento de imagens
- **SharedPreferences** — persistfência local
- **JUnit** — testes unitários

## Arquitetura

O projeto segue MVVM em três camadas. A UI observa `StateFlow`s expostos pelos ViewModels e nunca acessa repositório diretamente; regras de negócio (progresso, elegibilidade de venda, preço) ficam em ViewModel/Repository.

```text
app/src/main/java/com/album/figurinha/
├── MainActivity.kt # NavHost e composição dos ViewModels
├── api/
│   ├── ApiClient.kt # Retrofit + OkHttp (User-Agent, logging)
│   ├── ApiInterceptor.kt # Interceptor de chave (ver dívida técnica)
│   └── FootballApi.kt # endpoints teams / players / coachs
├── model/
│   ├── Album.kt # Album
│   ├── Carteira.kt # moedas e recompensa diária
│   ├── CatalogSticker.kt # CatalogSticker + StickerCategory
│   ├── Coach.kt # Coach + respostas da API
│   ├── ColecaoFigurinha.kt # figurinha do usuário (quantidade, repetida)
│   ├── Competition.kt
│   ├── Figurinha.kt
│   ├── PacoteFigurinha.kt
│   ├── Player.kt # Player + respostas da API
│   ├── StickerRarity.kt # níveis de raridade
│   └── Team.kt # Team + respostas da API
├── repository/
│   ├── FootballRepository.kt # acesso à API-Sports
│   ├── PlayersData.kt # dados fixos dos jogadores
│   ├── StickerCatalog.kt # catálogo único de figurinhas
│   └── StickerPricing.kt # regras de preço e venda
├── ui/
│   ├── components/
│   │   ├── AlbumComponents.kt # progresso, divisor, abas, empty state
│   │   ├── AlbumDialogs.kt # detalhe da figurinha, confirmação de venda
│   │   ├── CoinWallet.kt # saldo de moedas
│   │   ├── RepeatedStickerRow.kt # linha da aba Repetidas + seletor
│   │   └── StickerCard.kt # card de figurinha (bloqueada/obtida)
│   ├── navigation/Routes.kt # rotas do NavHost
│   ├── screens/ # Album, Home, Store, Selection/Player/Coach/Country
│   └── theme/ # Color.kt, Theme.kt, Type.kt
├── util/
│   ├── ConnectivityObserver.kt # estado de conectividade
│   ├── ImageMapper.kt # drawables locais (mapa vazio hoje)
│   └── StickerImageResolver.kt # URLs de imagem (CDN SoFIFA)
└── viewmodel/
    ├── AlbumViewModel.kt # coleção, progresso e venda de repetidas
    ├── PackViewModel.kt # compra e abertura de pacotes
    └── WalletViewModel.kt # saldo e recompensa diária
