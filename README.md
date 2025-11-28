# kotlin_task9
![Screenshot 2025-11-28 174535](https://github.com/user-attachments/assets/aebcc226-2e47-495d-8f9f-95b84979e25c)
![Screenshot 2025-11-28 174749](https://github.com/user-attachments/assets/9c8909f9-537e-42a2-a376-fa7ae530a0f7)
An Android application built with Kotlin 2.0 and Jetpack Compose that uses a local Room Database (SQLite) to persistently store a shopping list.

Features

Local Persistence: Uses the Room Library to store data on the device. Data remains even after closing the app.

CRUD Operations:

Create: Add new items with Name, Quantity, Unit, and Price.

Read: Display all items in a structured table format using LazyColumn.

Delete: Remove individual items from the database using a delete button.

Reactive UI: Uses Flow and StateFlow to automatically update the UI whenever the database changes.

Kotlin 2.0 Support: Configured using the modern KSP (Kotlin Symbol Processing) compiler plugin instead of the deprecated KAPT.

Technologies Used

Language: Kotlin 2.0.21

UI Framework: Jetpack Compose (Material3)

Database: Android Room (SQLite abstraction)

Architecture: MVVM (Model-View-ViewModel)

Concurrency: Kotlin Coroutines & Flow

Setup & Requirements

Android Studio: Ladybug or newer (Required for Kotlin 2.0 support)

Min SDK: 26 (Android 8.0)

Target SDK: 34

