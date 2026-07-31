# AI Code Review

## Code Reviewed

```kotlin
class MovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()
}

fun loadMovies() {
    val url = URL("https://api.example.com/movies")
    val data = url.readText()
    movies = parseMovies(data)
}
```

## Problems I Found

### 1. `loadMovies()` is outside the ViewModel

The ViewModel is already closed before `loadMovies()` is declared. Because of that, the function also cannot access `movies`.

**Fix:** Move `loadMovies()` inside the `MovieViewModel` class.

### 2. The request can block the main thread

`url.readText()` is a blocking operation. Calling it from the UI could freeze the app.

**Fix:** Use a suspend API function and call it inside `viewModelScope`.

### 3. There is no loading or error handling

The code only updates the movie list. It does not tell the UI when data is loading or when the request fails.

**Fix:** Add `Loading`, `Success`, and `Error` states so the UI can show the correct screen and provide a retry button.

### 4. The state is publicly mutable and not observable

`movies` can be changed from outside the ViewModel. A normal list also will not automatically update a Compose screen.

**Fix:** Use a private `MutableStateFlow` and expose it as a read-only `StateFlow`.

### 5. Networking is handled directly inside the ViewModel

The ViewModel handles the URL, request, and response parsing by itself. This makes the code harder to maintain and test.

**Fix:** Move the network logic into a repository and pass the repository into the ViewModel. Tests can then use a fake repository.

## Example Fix

```kotlin
class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)

    val uiState = _uiState.asStateFlow()

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading

            _uiState.value = try {
                MovieUiState.Success(
                    repository.getMovies()
                )
            } catch (exception: Exception) {
                MovieUiState.Error(
                    exception.message ?: "Failed to load movies."
                )
            }
        }
    }
}
```

## Summary
I would move the function inside the ViewModel, use Retrofit with coroutines, expose the result through a UI state, and move the actual network request into a repository. This is also the general approach I used in TVApp.

