# Product Catalogue

An Android product catalogue built against the public [DummyJSON](https://dummyjson.com) API.
Browse products, page through the catalogue as you scroll, search, and open a product
for its full detail.

## Stack

| | |
|---|---|
| Language | Kotlin |
| UI | XML views + ViewBinding (no Compose) |
| Architecture | MVVM + Repository |
| Networking | Retrofit + OkHttp |
| JSON | Moshi (reflective adapter) |
| Images | Coil |
| State | LiveData |
| Async | Coroutines |
| Min / target SDK | 24 / 36 |

## Running it

```bash
git clone https://github.com/ajoshua738/product-catalogue-app.git
cd product-catalogue-app
./gradlew assembleDebug
```

Or open the folder in Android Studio and press Run. No API key, no `local.properties`
entries, no setup beyond the Android SDK.

## Architecture

Three layers. Dependencies point inwards — `ui` and `data` both know about `domain`;
`domain` knows about neither.

```
ui/          Activities, ViewModels, adapters, UI state
   |
domain/      Product, ProductPage, ProductRepository (interface)
   |
data/        Retrofit API, DTOs, mapper, ProductRepositoryImpl
```

```
ProductApi ──► ProductRepositoryImpl ──► ViewModel ──► Activity
   (DTO)            (AppResult<Domain>)     (UiState)     (render)
```

`di/ServiceLocator` wires the graph by hand.

### Why these choices

**Server-side search.** Chose server side for a more simpler approach due to time constraint.
Client side would require some handling, also the API doesnt return every item only 20 at a
time, so searching for an item not yet retrieved from API would return not found. Client side
would require searching our local cache first then calling the endpoint.

**No DI framework.** Chose a simpler approach of one `ServiceLocator` object rather than using
DI Framework like Hilt

**DTO separate from the domain model.** `ProductDto` has every field nullable because the
server decides what it sends. `Product` is non-null throughout. The mapper is the only
place that has to think about missing data — and a product with no `id` is dropped rather
than faked, so one malformed row costs one item instead of the screen.

**The repository never throws.** Every call returns `AppResult.Success` or
`AppResult.Failure(AppError)`, so the ViewModel contains no `try`/`catch`. 

**Detail screen takes an id, not a parcelled product.** It refetches from
`/products/{id}`, which exercises the detail endpoint as specified and gives the screen its
own independent loading and error states.

## States

The list screen models two independent axes:

```kotlin
data class ProductListUiState(
    val screenState: ScreenState,  // Loading | Success | Error | Empty
    val isAppending: Boolean,      // footer spinner
    val appendFailed: Boolean,     // footer retry
    ...
)
```

The whole state is one object behind a single `LiveData`, so the Activity has one
observer and one `render` path rather than a field-per-observer.

`screenState` owns the whole screen; the append flags own only the footer. A failed
*first* page blanks the screen and offers **Try again**. A failed *fifth* page leaves the
80 items already loaded exactly where they are and turns the footer into
"Couldn't load more. Retry". Those are different failures and they get different treatment.

## What I would improve

**LiveData instead of Flow.** `StateFlow` with `repeatOnLifecycle` is the modern choice and
would have given me operators like `debounce` for free. I chose LiveData because I'm more
familiar with how it behaves around the Activity lifecycle, and for a timed exercise I'd
rather use the tool I can reason about than the one that reads better on paper. Search is
debounced with a `Handler` and `postDelayed` instead, which does the same job.

## Not finished

- **No error state on the detail screen.** If the detail request fails the screen stays
  empty — the failure is logged but nothing is shown and there is no retry. The list screen
  has both. `ProductDetailViewModel.onRetry()` already exists, so this is a missing view,
  not missing logic; it was the first thing I cut for time.
- **Pull-to-refresh** — ran out of time.
- **Unit tests** — ran out of time. The mapper and the list ViewModel are the two things
  I would have covered first: the mapper because it's pure and cheap to test, and the
  ViewModel because the append-failure behaviour above is easy to break by accident.
- **Light theme only.** No dark theme; `values-night` was removed rather than shipped broken.
- **No offline cache.** Every launch hits the network; there is no local storage like Room for client side searching.

## Notes on AI assistance

Claude was used to help me plan my project structure and what files I needed. Also to get latest
implementation style of MVVM. Claude Design was used to create UI mockups to create the screens
quicker.
