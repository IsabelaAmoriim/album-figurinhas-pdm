# Checklist de Desenvolvimento - Álbum de Figurinhas

## 🏗️ Infraestrutura Inicial
- [x] Configurar o `NavHost` no `MainActivity`
- [x] Definir as rotas de navegação (`Routes.kt`)
- [x] Implementar o `FigurinhaTheme` com as cores do Figma

## 💰 Mecânica de Moedas e Carteira
- [x] Criar o `CoinWallet.kt` (Componente de UI)
- [x] Implementar o `WalletViewModel.kt` para gerenciar o saldo
- [ ] Adicionar funcionalidade de "Recompensa Diária" (Logon inicial)

## 📡 Estabilidade e Diagnóstico
- [x] Implementar `ConnectivityObserver` (Monitor de internet)
- [x] Adicionar indicador de rede discreto na Home (Ponto Verde/Vermelho)
- [x] Configurar permissão `ACCESS_NETWORK_STATE`

## 🎴 Coleção e Figurinhas (Refinamento Premium)
- [x] Redesenhar Figurinha Mítica (Cores suaves e formato largo)
- [x] Implementar Mapeamento de Imagens Locais (Fallback total)
- [x] Melhorar design do `StickerCard` com brilho interno e sombras

## 📦 Sistema de Pacotes (Redesign Visual)
- [x] Criar Componente `PremiumPack` (Gradiente metálico e profundidade)
- [x] Lógica de "Escolha Única" no dado
- [x] Refinar animação de abertura (Flip 3D e Zoom)
