# Improve Image Resolution and Add Country Assets

This plan aims to improve the visual quality of stickers and logos, fix missing team assets, add country flags, and refine the locked player silhouette.

## Proposed Changes

### [Util]
#### [MODIFY] [StickerImageResolver.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/util/StickerImageResolver.kt)
- Add a mapping for country codes (BR, AR, FR, PT).
- Add `getCountryFlagUrl(teamId: Int)` to return high-resolution flag URLs (`@3x.png`).
- Update `getTeamShieldUrl` to use `https://cdn.sofifa.net/teams/{id}/120.png` for better resolution and reliability.
- Ensure player images continue using the best available headshot origin.

### [UI Components]
#### [MODIFY] [StickerCard.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/components/StickerCard.kt)
- Redesign the silhouette effect: instead of a darkened photo, use a solid dark-gray color filter that preserves only the alpha channel (masking effect) if possible, or a very aggressive black-out filter to create a pure silhouette.
- Ensure the silhouette looks clean and consistent.

### [UI Screens]
#### [MODIFY] [CountryDetailScreen.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/screens/CountryDetailScreen.kt)
- Replace the text-based circular icon (e.g., "BR") with the actual country flag image.
- Use the high-resolution flag in the Mythic sticker for each selection.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/screens/HomeScreen.kt)
- Ensure the team selection items use the updated high-resolution shield URLs.

#### [MODIFY] [SelectionDetailScreen.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/screens/SelectionDetailScreen.kt)
- Verify that the team shield in the header is using the 120px version for maximum clarity.

## Verification Plan

### Manual Verification
- **Flags**: Open the "About Country" screen for each selection and verify the flag appears in the header and on the Mythic sticker.
- **Resolution**: Check the Brazil/Argentina shields on the Home and Selection screens to ensure they are crisp.
- **Silhouettes**: Verify that locked players (like Neymar in the current mock) appear as solid dark gray silhouettes.
- **Logos**: Verify that France and Portugal logos now appear correctly if they were missing before.
