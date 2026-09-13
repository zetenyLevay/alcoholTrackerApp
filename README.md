# Alcohol Tracker

A small android project to connect to my uni years. It tracks and shows your alcohol drinking and behaviors.

## What it does right now

- Log a drink (category, amount, price, date/time) and see it in a filterable history list
- Local search over a small bundled beer dataset
- Favourite/recent drinks for quick re-logging
- Anonymous sign-in, backed by a local Room database + Firebase

## What's still stubbed out

- API for each category needs doings
- Analytics screens

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Hilt for DI, type-safe Compose Navigation
- Room for local persistence, Firebase (Auth + Firestore) for the rest
- Paging 3 for the history list

## Project layout

- `data/` - Room DB, DAOs, repositories, remote sources
- `domain/` - per-category logic (beer, wine, spirits, ...) and use cases
- `ui/` - `components/`, `screens/`, `viewmodel/` (fairly standard MVVM/UDF split)
- `utils/` - date helpers and misc

## Running it

1. Clone the repo
2. Create a Firebase project and drop your own `google-services.json` into `app/src/`
3. Open in Android Studio (Ladybug+, JDK 11), min SDK 32 / target SDK 36, run


