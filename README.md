# FrigoChef
Application mobile Android native de suggestions de recettes basée sur les ingrédients disponibles, développée en Kotlin avec une base de données SQLite et l'architecture MVP.

## Membres de l'équipe

| Nom | DA | Responsabilité principale |
|---|---|---|
| Nath, Arpan | 1581479 | Base de données + Écran Résultats |
| Ahmed, Sabia | 2371383 | Écran Accueil |
| Abdulali, Sabrina | 2184053 | Écran Questionnaire |

## Technologies utilisées

- Kotlin
- Android SDK (API 26+)
- SQLite
- Architecture MVP (Modèle-Vue-Présentateur)
- JUnit (tests unitaires)

## Installation

Cloner le dépôt :
```
git clone https://git.dti.crosemont.quebec/2184053/frigochef.git
```
Ouvrir le projet dans Android Studio  
Lancer sur un émulateur API 26+

## Architecture

Le projet suit l'architecture MVP avec une séparation claire des responsabilités :

- `model/` — Données et logique métier (data classes + accès base de données)
- `contract/` — Interfaces définissant la communication entre les couches
- `presenter/` — Orchestre la logique entre la Vue et le Modèle
- `view/` — Activités Android (affichage uniquement)

## Cours

420-G25-RO — Applications natives 2 — Hiver 2026 - Keven Chaussé