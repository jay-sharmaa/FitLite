# FitLite
FitLite is a comprehensive fitness tracking application that helps users monitor their health and fitness goals with advanced features and a seamless experience across mobile devices and wearables.

# Login / Signup Screen

<p float="left">
  <img src="https://github.com/jay-sharmaa/FitLite/blob/main/app/implimages/img1.jpg" width="200"/>
</p>

# Technical Features

Room Database: Local data persistence for offline access and performance
Authentication: Secure user accounts with multiple sign-in options
Paging: Smooth scrolling through large datasets with efficient memory usage
Jetpack Compose UI: Modern declarative UI built with Jetpack Compose
CameraX Integration: Take progress photos and scan QR codes for quick data entry
Voice Control: Hands-free operation during workouts
Wear OS Companion: Synchronized experience on smartwatches

# Getting Started
# Prerequisites

Android Studio Arctic Fox (2021.3.1) or newer
Kotlin 1.6.0+
JDK 11
Android SDK 31+
Gradle 7.0.2+

Installation

Clone the repository:

# git clone https://github.com/jay-sharmaa/Fitlite.git

Open the project in Android Studio
Sync Gradle files

Build and run the application

Architecture
FitLite follows the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles:

Data Layer: Room database, remote data sources, repositories
Domain Layer: Use cases, models, business logic
Presentation Layer: ViewModels, UI components, Jetpack Compose screens

Room Database Schema
The application uses Room for efficient local data storage with the following main entities:

User
Workout
Exercise
Nutrition
Sleep
Goal
Achievement

Wear OS Companion App
The FitLite Wear OS app provides a synchronized experience on smartwatches with:

Real-time workout tracking
Heart rate monitoring
Quick logging of activities
Custom complications for watch faces
Voice command support

Communication Between Devices
Data synchronization between the mobile and wearable apps happens through:

DataLayer API for small, frequent updates
Cloud synchronization for larger datasets
Wear OS health services integration

CameraX Integration
FitLite uses CameraX for:

Real-time exercise form analysis (beta feature)

Voice Control
Voice commands are available for hands-free operation:

Start/stop workout tracking

Contributing
We welcome contributions to FitLite! Please follow these steps:

Fork the repository
Create a feature branch (git checkout -b feature/amazing-feature)
Commit your changes (git commit -m 'Add some amazing feature')
Push to the branch (git push origin feature/amazing-feature)
Open a Pull Request

Jetpack Compose
Room Database
CameraX
Wear OS
Paging Library

Contact
# Project Link: [https://github.com/jay-sharmaa/fitlite](https://github.com/jay-sharmaa/Fitlite.git)
