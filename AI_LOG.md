# AI Usage Log

I used ChatGPT as a development assistant during this project. I reviewed, modified, rejected, and tested its suggestions before including them in the final application.

## 1. Planning the project structure

### What I asked the AI / the problem I was solving

I asked the AI to help break down the take-home requirements and suggest a reasonable Android project structure.

### What it gave me

It suggested using an MVVM-based structure with separate packages for models, networking, repositories, list UI, detail UI, shared components, and themes.

### What I did

I accepted the general MVVM structure but kept it simple. I did not add use-case classes, a dependency injection framework, a local database, or other layers that were not necessary for the scope of the project.

### What the AI got wrong or what I verified myself

Some early suggestions added more structure than the project needed. I reviewed the assignment again and kept only the parts that directly supported the list screen, detail screen, state handling, sharing, pagination, and testing.

---

## 2. Fixing dependency compatibility

### What I asked the AI / the problem I was solving

Gradle reported that some AndroidX dependencies required a newer compile SDK than the project was using. I asked the AI to help identify the cause.

### What it gave me

It explained that the generated dependency versions were incompatible with the current compile SDK and suggested using compatible versions of AndroidX Core, Lifecycle, and Activity Compose.

### What I did

I reviewed the Gradle error output and updated the affected versions in the version catalog.

### What the AI got wrong or what I verified myself

I did not assume that the suggested versions would work. I synchronized Gradle and ran a debug build to confirm that the dependency metadata error had been resolved.

---

## 3. Improving the user interface

### What I asked the AI / the problem I was solving

I asked for help making the application look more polished while keeping the design reasonable for an internship take-home project.

### What it gave me

It suggested using a restrained Material 3 color palette, consistent poster sizes, rating badges, light and dark themes, image placeholders, and clearer loading and error states.

### What I did

I accepted the general design direction but adjusted the card spacing, rating badge size, badge contrast, text alignment, and image layout after checking the results on the emulator.

I also rejected more complex effects such as gradients and custom screen transitions because they did not add enough value for the project scope.

### What the AI got wrong or what I verified myself

The AI initially suggested using `minLines = 2` for titles while placing the rating below the title. This kept card heights equal but created an awkward gap between one-line titles and their ratings.

I changed the design by moving the rating badge onto the poster and reserving the two-line area only for the show title. This kept the grid aligned without creating a large empty gap.

The AI also suggested a custom navigation transition that caused a visible white flash. I removed the transition and kept the normal navigation behavior with an explicit themed background.

---

## 4. Adding cast, seasons, and episodes

### What I asked the AI / the problem I was solving

I asked for help implementing the optional TVMaze features for cast members, seasons, and episodes.

### What it gave me

It suggested extending the data models, embedding cast and episodes in the show-detail request, requesting season metadata separately, and displaying the results with Jetpack Compose.

### What I did

I accepted the general API approach but modified the code structure.

I separated cast, season, and episode UI into dedicated components rather than placing everything in one screen file. I also added stable image sizes and placeholders for missing, loading, and failed cast, season, and episode images.

For cast cards, I adjusted the text layout so longer names would not make the content below the cast section shift unexpectedly.

### What the AI got wrong or what I verified myself

An early AI suggestion created a very large detail screen file containing most of the feature in one place. I rejected that structure and divided it into smaller files with clearer responsibilities.

---

## 5. Adding pagination

### What I asked the AI / the problem I was solving

I asked for help implementing the optional pagination feature for the TVMaze show list.

### What it gave me

It suggested a manual pagination approach with separate handling for:

- Initial loading
- Loading additional pages
- Pagination errors
- Retry behavior
- Duplicate prevention
- End-of-list detection

### What I did

I used the manual pagination approach because adding Paging 3 would have introduced unnecessary complexity for this project.

I kept previously loaded shows visible when a later page failed, prevented multiple requests from running at the same time, prevented duplicate show IDs, and only advanced the page after a successful request.

### What the AI got wrong or what I verified myself

The first pagination UI suggestion placed the screen coordinator, grid, show cards, pagination detection, loading states, error states, and footer components inside one `ShowListScreen.kt` file of approximately 600 lines.

I rejected that structure and split it into smaller files for the screen, grid, show card, and feedback components before committing the feature.

I manually verified that scrolling loaded another page, failed requests kept existing content visible, retry worked, and the same shows were not appended twice.

---

## 6. Writing ViewModel unit tests

### What I asked the AI / the problem I was solving

I asked for help testing the list and detail ViewModels, including their coroutine-based state updates.

### What it gave me

It suggested using `kotlinx-coroutines-test`, a custom main dispatcher rule, and fake repository implementations.

### What I did

I used those tools and added tests for:

- Successful initial show-list loading
- Initial loading errors
- Retry after an initial error
- Loading additional pages
- Pagination errors
- Reaching the end of pagination
- Loading show details
- Selecting a valid season
- Ignoring an unavailable season

### What the AI got wrong or what I verified myself

I reviewed the fake repositories to make sure they followed the same interface as the real repository. I also modified the tests when the repository return type changed during the pagination implementation.

I verified the tests by running:

`./gradlew testDebugUnitTest`

I only kept the changes after the test task completed successfully.