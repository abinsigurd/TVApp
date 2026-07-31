# AI Code Review

## Code being reviewed

```kotlin
class MovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()

    fun loadMovies() {
        val url = URL("https://api.example.com/movies")
        val data = url.readText()
        movies = parseMovies(data)
    }
}
```

## Problems and suggested fixes

### 1. The network request may run on the main thread

`loadMovies()` is a normal function, and `url.readText()` performs blocking network I/O. If this function is called from the UI thread, it may freeze the interface or throw a `NetworkOnMainThreadException`.

**Fix:**

Run asynchronous work inside `viewModelScope` and move blocking I/O away from the main thread. A networking library such as Retrofit should expose a suspending function so the ViewModel does not perform blocking work directly.

```kotlin
viewModelScope.launch {
    repository.getMovies()
}
```

---

### 2. Networking logic is placed directly inside the ViewModel

The ViewModel creates the URL, performs the request, reads the response, and parses the data. This gives the ViewModel too many responsibilities and makes it tightly coupled to the network implementation.

**Fix:**

Move networking and parsing into a repository or remote data source. The ViewModel should request data from the repository and only manage UI state.

```kotlin
interface MovieRepository {
    suspend fun getMovies(): List<Movie>
}
```

---

### 3. The movie list is publicly mutable

The `movies` property can be changed from outside the ViewModel because it is declared as a public `var`. This makes state changes difficult to control and debug.

**Fix:**

Keep mutable state private and expose an immutable version to the UI.

```kotlin
private val _uiState = MutableStateFlow<MovieUiState>(
    MovieUiState.Loading
)

val uiState: StateFlow<MovieUiState> =
    _uiState.asStateFlow()
```

---

### 4. The state is not observable by Jetpack Compose

A normal `List<Movie>` property does not automatically notify Compose when its value changes. The UI may not recompose after the movies are loaded.

**Fix:**

Expose state using `StateFlow`, `LiveData`, or Compose state. For this architecture, `StateFlow` would provide a clear and lifecycle-friendly state holder.

---

### 5. There are no loading, error, or success states

The code only stores a movie list. The UI cannot distinguish between:

- Data that has not loaded yet
- An empty successful response
- A network failure
- A successful response containing movies

**Fix:**

Represent the screen using a sealed UI state.

```kotlin
sealed interface MovieUiState {
    data object Loading : MovieUiState

    data class Success(
        val movies: List<Movie>
    ) : MovieUiState

    data class Error(
        val message: String
    ) : MovieUiState
}
```

---

### 6. Exceptions are not handled

Several operations can fail:

- Creating or opening the URL
- Connecting to the server
- Reading the response
- Parsing malformed JSON
- Receiving an HTTP error

Without exception handling, a failure may crash the application.

**Fix:**

Catch expected failures and expose an error state to the UI. Coroutine cancellation should not be converted into a normal error.

```kotlin
try {
    val movies = repository.getMovies()
    _uiState.value = MovieUiState.Success(movies)
} catch (exception: CancellationException) {
    throw exception
} catch (exception: Exception) {
    _uiState.value = MovieUiState.Error(
        exception.message ?: "Unable to load movies."
    )
}
```

---

### 7. The URL is hardcoded inside the ViewModel

The endpoint is embedded directly in the presentation layer. Changing environments or endpoints would require editing the ViewModel.

**Fix:**

Configure the base URL in the networking layer and declare endpoints in an API interface.

```kotlin
interface MovieApi {
    @GET("movies")
    suspend fun getMovies(): List<Movie>
}
```

---

### 8. Raw `URL.readText()` provides limited HTTP handling

Using `URL.readText()` does not provide a clean way to configure timeouts, inspect response codes, deserialize JSON, or add interceptors.

**Fix:**

Use a networking library such as Retrofit with a JSON converter. It provides structured endpoint declarations, coroutine support, response conversion, and clearer HTTP error handling.

---

### 9. Parsing is coupled to the ViewModel

The ViewModel directly calls `parseMovies(data)`. Parsing API responses is a data-layer responsibility and should not be mixed with UI state management.

**Fix:**

Let the networking or repository layer deserialize the response into models before returning the result to the ViewModel.

---

### 10. The implementation is difficult to unit test

The ViewModel directly creates its network dependency. A unit test cannot easily replace the real request with controlled success or error results.

**Fix:**

Inject a repository through the constructor.

```kotlin
class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel()
```

Tests can then provide a fake repository without making real network calls.

---

### 11. Repeated calls may create duplicate requests

If `loadMovies()` is called several times quickly, multiple requests may run at the same time. An older request could finish after a newer one and overwrite the latest state.

**Fix:**

Track the current loading job or prevent another request while one is active.

```kotlin
private var loadJob: Job? = null

fun loadMovies() {
    loadJob?.cancel()

    loadJob = viewModelScope.launch {
        // Load movies
    }
}
```

For a simple screen, another valid approach is checking whether a request is already in progress before starting a new one.

---

### 12. There is no clear retry behavior

Calling `loadMovies()` again could act as a retry, but the code does not expose an error state or reset the state to loading. The UI therefore cannot present a proper retry action.

**Fix:**

Set the state to `Loading` at the beginning of each request and allow the error UI to call `loadMovies()` again.

## Suggested implementation

```kotlin
sealed interface MovieUiState {
    data object Loading : MovieUiState

    data class Success(
        val movies: List<Movie>
    ) : MovieUiState

    data class Error(
        val message: String
    ) : MovieUiState
}

interface MovieRepository {
    suspend fun getMovies(): List<Movie>
}

class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(
        MovieUiState.Loading
    )

    val uiState: StateFlow<MovieUiState> =
        _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadMovies()
    }

    fun loadMovies() {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _uiState.value = MovieUiState.Loading

            _uiState.value = try {
                val movies = repository.getMovies()

                MovieUiState.Success(
                    movies = movies
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                MovieUiState.Error(
                    message = exception.message
                        ?.takeIf { it.isNotBlank() }
                        ?: "Unable to load movies."
                )
            }
        }
    }
}
```

This version keeps the ViewModel focused on UI state, performs networking through an injected repository, supports Compose observation, handles failures, supports retry, and is easier to unit test.