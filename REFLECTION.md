# Reflection

## 1. Which part of your submission are you least confident about, and why?

I am least confident about the pagination feature because I added it near the end and had less time to test it than the main list and detail screens.

It worked during my testing, including loading more shows and retrying failed requests, but there may still be scrolling or network edge cases that I did not encounter.

## 2. Describe a moment during this project where you got completely stuck. What did you do, step by step?

I got stuck while adding cast, seasons, and episodes to the detail screen. The first version made `ShowDetailScreen.kt` very large, and changing one part often affected another part. There were also layout problems, such as cast members with longer names making the content below them move.

I first stopped adding more features and checked which responsibilities were mixed together. I separated the cast, season, episode, image, and common detail components into different files. I then removed the unfinished season and episode code so I could finish and commit the cast feature by itself.

After that, I tested the cast layout, fixed the image placeholders and text spacing, and only added seasons and episodes again after the cast version was stable. I also checked the Git history because I did not want the deleted season files to appear inside the cast commit.

I used ChatGPT for suggestions, but I did not keep the first structure it generated. I decided how the files should be separated, reviewed each change, and tested the features step by step. I also faced some Gradle memory errors during the project, but those were smaller environment problems compared to organizing the detail feature correctly.code.

## 3. Imagine it is Thursday, the task is due Friday, and you realize that you misunderstood the requirement and half of your work is wrong. What are you doing now?

I would reread the requirements and separate the required features from the optional ones. Then I would check which parts of my existing work could still be reused.

I would fix the required user flow first and postpone optional features or visual improvements. I would also communicate the problem early if it could affect the deadline.

During this project, I used a similar approach when I needed to separate cast from seasons and episodes. I cleaned up only the affected files and commits instead of restarting the whole project.

## 4. Your mentor asks you to change an approach you believe is worse. What do you do?

I would first ask why they prefer that approach because they may know about a requirement or limitation that I missed.

I would explain my concern using clear trade-offs, such as maintainability, performance, risk, or development time. If needed, I would suggest comparing both approaches with a small prototype.

If my mentor still chooses their approach, I would follow the decision and implement it properly. I would only raise the issue further if it could cause a serious security, privacy, data-loss, or ethical problem.

## 5. What is something technical you taught yourself recently outside of class or work, and how did you learn it?

After previously learning Android development with XML Views, I taught myself Jetpack Compose before taking on freelance work at Majka.

I learned through the official documentation and small experiments with layouts, state, and navigation. I later applied and improved those skills while independently handling the Android application for Majka. By the time I built TVApp, I was already familiar with Compose, and this project gave me another opportunity to apply those skills in a different context.