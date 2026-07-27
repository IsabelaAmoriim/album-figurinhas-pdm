# Plano de Implementação - Design do Pacote, Cartas Míticas e Solução de Imagens

Este plano visa elevar a estética visual do pacote de figurinhas, redesenhar as cartas de nação (míticas) com cores suaves e resolver definitivamente o problema de exibição de imagens usando abordagens híbridas.

## User Review Required

> [!IMPORTANT]
> **Imagens:** O Android Studio/Emulador muitas vezes bloqueia conexões externas por DNS ou HTTPS. Vamos implementar um sistema de **"Image Source Switcher"**: ele tentará baixar da API, mas terá um mapeamento para imagens locais (`res/drawable`) para garantir que o app nunca fique vazio.
> **Design do Pacote:** O novo design usará gradientes dinâmicos e sombras projetadas para parecer um objeto 3D real na tela.

## Proposed Changes

### 1. Novo Design do Pacote (Store)
#### [MODIFY] [StoreScreen.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/screens/StoreScreen.kt)
- Criar um componente `PremiumPack` com gradiente metálico, bordas chanfradas e brilho dinâmico.
- Adicionar uma sombra suave (shadow) para dar profundidade.

### 2. Redesign da Carta do País (Mythic)
#### [MODIFY] [StickerCard.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/components/StickerCard.kt)
- Para raridade `MYTHIC`: usar um formato levemente mais largo e cores "pastel" ou suaves baseadas na seleção.
- A moldura será menos agressiva, focando em um brilho interno suave.

### 3. Solução Híbrida de Imagens
#### [MODIFY] [StickerCard.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/components/StickerCard.kt)
- Adicionar suporte a `Painter` que aceita tanto URLs quanto IDs de recursos locais.
- Criar um utilitário simples que mapeia IDs de jogadores famosos para ícones/imagens de reserva se a URL falhar.

### 4. Diagnóstico do Android Studio
- **Explicação:** Sim, o emulador do Android Studio pode ser o culpado. Às vezes, o DNS do computador host não é passado corretamente, ou o `cleartextTraffic` está bloqueado (embora estejamos usando HTTPS). Vou adicionar logs de erro específicos da Coil para vermos o código de erro exato (ex: 403 Forbidden ou UnknownHostException).

## Verification Plan

### Manual Verification
- Visualizar o novo pacote na loja.
- Abrir um pacote e ver se a imagem de reserva aparece caso a internet falhe.
- Entrar na tela do país e verificar as cores suavizadas da figurinha mítica.
