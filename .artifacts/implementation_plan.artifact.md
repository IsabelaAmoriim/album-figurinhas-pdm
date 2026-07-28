# Implementation Plan - Fix Coach Request, Country Icon, and Country Sticker

## Goal Description
1.  **Remove `season` from Coach Request**: The API for coaches doesn't seem to strictly require `season` or it's causing issues. Removing it to see if it improves data fetching for the coach photo and info.
2.  **Fix Country Page Icon**: Correct the ID mismatch in `resolveCountryCode` and `resolveTeamColor` to ensure flags and colors load for the selected teams (Brasil, Argentina, Espanha).
3.  **Fix Country Sticker Saving**: Ensure the country sticker in `CountryDetailScreen` uses the correct ID from the catalog and reflects the actual collection status from `AlbumViewModel`.

## User Review Required
> [!IMPORTANT]
> I am updating the team ID mappings for Brasil, Argentina, and Espanha to match the API IDs (6, 9, 8) used in the repository. I will also add a specific red color for Spain.

## Proposed Changes

### API & Repository

#### [MODIFY] [FootballApi.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/api/FootballApi.kt)
- Remove `season` query parameter from `getCoach` function.

#### [MODIFY] [FootballRepository.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/repository/FootballRepository.kt)
- Remove `season` argument in the call to `api.getCoach`.

### UI & Utilities

#### [MODIFY] [Color.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/theme/Color.kt)
- Add `SpainRed` color.

#### [MODIFY] [SelectionDetailScreen.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/screens/SelectionDetailScreen.kt)
- Update `resolveTeamColor` to handle team IDs 6, 9, 8.
- Update `resolveCountryCode` to handle team IDs 6, 9, 8.

#### [MODIFY] [StickerImageResolver.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/util/StickerImageResolver.kt)
- Update `teamToSoFifaMap` and `teamToIsoMap` to use the correct team IDs (6, 9, 8).

#### [MODIFY] [CountryDetailScreen.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/ui/screens/CountryDetailScreen.kt)
- Update parameters to accept `albumViewModel: AlbumViewModel`.
- Use `StickerCatalog.selectionStickerId(teamId)` for the sticker ID.
- Check `albumViewModel.isCollected(stickerId)` for `isCollected` parameter in `StickerCard`.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/costa/StudioProjects/album-figurinhas-pdm/app/src/main/java/com/album/figurinha/MainActivity.kt)
- Pass `albumViewModel` to `CountryDetailScreen` in the navigation graph.

## Verification Plan

### Automated Tests
- Build project to verify no compilation errors.

### Manual Verification
- Deploy the app.
- Open Selection Detail and then Country Detail for Brasil/Argentina/Espanha.
- Verify flag icon is visible in the header.
- Verify the "Mythic Collectible" reflects ownership (colored if owned, grayed out if not).
- Verify coach image loads if returned by API.
