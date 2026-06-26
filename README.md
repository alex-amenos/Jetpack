# Jetpack Playground 🚀

A personal project and sandbox for experimenting with modern Android development, architecture, and libraries.

ℹ️ **Note:** As an experimental playground, some features are works-in-progress and may lack complete test coverage or a fully separated data/domain layer.

## 📚 Documentation

* **[Project Setup Guide](./docs/SETUP.md):** Instructions on building the project and configuring required API keys (Google Maps, TMDB).
* **[Jetpack Playgrounds](./docs/jetpack_playgrounds.md):** Detailed information about the specific experiments and features.
* **[Architecture Overview](https://deepwiki.com/alex-amenos/Jetpack):** Deep dive into the project's architectural decisions.

## 🛠️ Tech Stack & Architecture

Built with modern Android development practices in mind:

* **UI:** Jetpack Compose
* **Language:** Kotlin & Coroutines/Flows
* **Architecture:** MVI (Model-View-Intent)
* **Functional Programming:** Arrow-kt (`Either`, Optics)
* **Dependency Injection:** Koin
* **Networking:** Retrofit
* **Testing:** JUnit 5, Turbine, Roborazzi (Screenshot Testing)
* **Modularization:** Multi-module architecture by feature

## 🧩 Features

The app is split into several standalone feature modules for experimentation, including:

* `movies`: TMDB API integration.
* `location-tracker`: Google Maps SDK integration.
* `authentication`: Login/Registration UI and flows.
* `posts`, `file-downloader`, `notifications`, `game`, and more.

## 🚦 Project Status

![Jetpack - Main status](https://github.com/alex-amenos/Jetpack/actions/workflows/android_ci.yml/badge.svg?branch=main)

## 🔗 Useful Links for Development

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Lint Rules by Slack](https://slackhq.github.io/compose-lints/rules/)
- [Ktlint Rules by Pinterest](https://pinterest.github.io/ktlint/rules/standard/)
- [AndroidX Libraries Versions](https://developer.android.com/jetpack/androidx/versions)
- [Compose BOM Version Mapping](https://developer.android.com/jetpack/compose/bom/bom-mapping)
- [Jetpack Compose to Kotlin Compatibility Map](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
- [Google App Architecture](https://developer.android.com/topic/architecture/intro)
