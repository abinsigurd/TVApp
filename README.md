# TVApp

TVApp is a simple Android application for browsing TV shows using data from the TVMaze API. It was developed as an Android internship take-home assignment using Kotlin and Jetpack Compose.

The application allows users to browse shows, open detailed information, view cast members, browse seasons and episodes, and share show information.

## Features

- Browse TV shows in a two-column grid
- Automatically load more shows while scrolling
- View show title, poster, rating, premiere date, and summary
- View cast members and their characters
- Browse available seasons
- View episodes based on the selected season
- Loading and error states with retry actions
- Image placeholders for missing or failed images
- Share show information through other applications
- Light and dark theme support
- Scroll-to-top button on the show list

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Kotlin Coroutines
- StateFlow
- Retrofit
- Gson
- Coil
- JUnit
- kotlinx-coroutines-test

## How to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/abinsigurd/TVApp.git
   ```

2. Open the project in Android Studio.

3. Wait for Gradle synchronization to finish.

4. Run the application on an Android emulator or physical device with an internet connection.

The application does not require an API key.

## Project Structure

The project uses a simple MVVM-based structure:

```text
data/
├── model
├── remote
└── repository

ui/
├── components
├── detail
├── list
└── theme
```

- `data/model` contains the models used for TVMaze responses.
- `data/remote` contains the Retrofit API interface and client.
- `data/repository` handles data retrieval and API-specific behavior.
- `ui/list` contains the show list, pagination state, and related components.
- `ui/detail` contains the show detail, cast, seasons, and episodes.
- ViewModels expose UI state using `StateFlow`.

Only the show ID is passed through navigation. The detail screen retrieves the required data through its ViewModel and repository.

Pagination is handled manually because the TVMaze pagination behavior is simple enough for the scope of this project.

## Testing

The project includes ViewModel unit tests for:

- Successful show list loading
- Initial loading errors
- Retry behavior
- Loading additional pages
- Pagination errors
- End-of-list behavior
- Loading show details
- Selecting valid and invalid seasons

Run the unit tests with:

```bash
./gradlew testDebugUnitTest
```

Build the debug application with:

```bash
./gradlew assembleDebug
```

## Demo Video

[Watch the TVApp demo video](https://www.youtube.com/watch?v=CMa9ZgcsMoI)

## Future Improvements

With more development time, I would add:

- UI and navigation tests
- Local caching for better offline support
- Persisted theme preferences
- Search and filtering
- More detailed episode information
- Links for opening individual episodes on their TVMaze pages

## API

TV show data is provided by the [TVMaze API](https://api.tvmaze.com/).