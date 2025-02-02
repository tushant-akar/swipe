# Swipe App

## Overview
The **Swipe App** is an Android application designed to manage products by allowing users to add, save, and sync product details locally and remotely. The app supports offline functionality by saving product data in a local database when there is no internet connection and syncing it with a remote server once the connection is restored.

Key Features:
- Add products with details such as name, type, price, tax, and images.
- Save products locally when there is no internet connection.
- Sync locally saved products with the remote server when online.
- Display a list of products with search functionality.
- Handle network connectivity changes gracefully.

---

## Table of Contents
1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [Setup Instructions](#setup-instructions)
4. [App Structure](#app-structure)

---

## Features

### Core Features
- **Add Product**: Users can add product details (name, type, price, tax, and images) via a form.
- **Local Storage**: Products are saved locally in a Room database when there is no internet connection.
- **Sync Mechanism**: Locally saved products are synced with the remote server using WorkManager when the device reconnects to the internet.
- **Product List**: Displays a list of products fetched from the remote server or stored locally.
- **Search Functionality**: Allows users to search for products by name or type.
- **Offline Support**: Ensures seamless functionality even without an internet connection.

### Additional Features
- Image validation: Only JPEG and PNG images are allowed.
- Network monitoring: Automatically detects network connectivity changes and triggers sync operations.

---

## Technologies Used

### Programming Languages
- **Kotlin**: Primary language for development.
- **Java**: Used for some legacy Android components.

### Libraries and Frameworks
- **Room**: For local database management.
- **Retrofit**: For API calls to the remote server.
- **WorkManager**: For background sync operations.
- **Glide**: For image loading and caching.
- **Koin**: For dependency injection.
- **Coroutines**: For asynchronous programming.
- **LiveData & Flow**: For reactive programming and state management.
- **Material Design Components**: For UI components like BottomSheetDialogFragment and RecyclerView.

### Tools
- **ProGuard/R8**: For code shrinking and obfuscation.
- **OkHttp**: For HTTP client operations.
- **Gson**: For JSON parsing.

---

## Setup Instructions

### Prerequisites
- Android Studio (latest version recommended).
- JDK 11 or higher.
- A physical or virtual Android device/emulator.

### Steps to run the project
To build and run this Android app project in Android Studio on your local machine, follow these steps:

1. Clone this repository using Android Studio:

    - Open Android Studio.
    - Click on "File" in the top menu.
    - Select "New" and then "Project from Version Control."
    - Choose "Git" and enter the repository URL: `https://github.com/tushant-akar/swipe`.
    - Click "Clone" to download the project.

2. Open the Project:

    - After cloning, Android Studio will automatically detect and open the project.

3. Configure Dependencies:

    - Ensure that you have the required dependencies and SDK versions installed as specified in the project's `build.gradle` files.

4. Build and Run the App:

    - Once the project is opened in Android Studio, click the "Run" button (usually a green triangle) in the top toolbar.
    - Select your target device (emulator or physical device) and click "OK."

5. Wait for the app to build and launch on your selected device.

---

## App Structure

### Key Packages

#### `com.tushant.swipe.data`
- **Description**: Contains data-related classes, including Room database, DAO, repository, and Retrofit API interfaces.
- **Responsibilities**: Handles local and remote data operations.

#### `com.tushant.swipe.di`
- **Description**: Dependency injection setup using Koin.
- **Responsibilities**: Manages dependencies for repositories, ViewModels, and other components.

#### `com.tushant.swipe.utils`
- **Description**: Utility classes for network monitoring, image handling, and constants.
- **Key Utilities**:
  - `uriToFile`
  - `NetworkMonitor`
  - `NetworkUtils`

#### `com.tushant.swipe.view`
- **Description**: UI components, including fragments, adapters, and activities.
- **Key Components**:
  - `ProductListFragment`
  - `AddProductFragment`
  - `ProductAdapter`
  - `MainActivity`

#### `com.tushant.swipe.viewModel`
- **Description**: ViewModels for managing UI logic and state.
- **Responsibilities**: Acts as a bridge between the UI layer and the data layer.

### Key Classes

#### `ProductRepository`
- **Responsibilities**:
  - Handles data operations (local and remote).
  - Communicates with:
    - Room database (`ProductDao`)
    - Retrofit API (`ProductService`).

#### `SyncWorker`
- **Responsibilities**:
  - Manages background sync operations using WorkManager.
  - Ensures locally saved products are synced with the remote server when the device reconnects to the internet.

#### `ProductViewModel`
- **Responsibilities**:
  - Provides data to the UI layer and handles business logic.
  - Uses `MutableStateFlow` to manage:
    - Product lists
    - Search queries
    - Loading states

#### `MainActivity`
- **Responsibilities**:
  - Serves as the main entry point of the app.
  - Sets up:
    - Navigation
    - Splash screen
    - Edge-to-edge display

#### `ProductListFragment`
- **Responsibilities**:
  - Displays a list of products fetched from the remote server or stored locally.
  - Supports:
    - Search functionality
    - Pull-to-refresh

#### `AddProductFragment`
- **Responsibilities**:
  - Allows users to add new products.
  - Handles:
    - Input field validation
    - Image processing
    - Saving products locally or remotely
