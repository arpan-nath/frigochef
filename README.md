# FrigoChef
Application mobile Android native de suggestions de recettes basée sur les ingrédients disponibles, développée en Kotlin avec une base de données SQLite et l'architecture MVP.

---

## Membres de l'équipe

| Nom | DA | Responsabilité principale                             |
|---|---|-------------------------------------------------------|
| Nath, Arpan | 1581479 | Base de données + Écran Résultats + Écran Détails     |
| Ahmed, Sabia | 2371383 | Écran Accueil + Écran Détails                         |
| Abdulali, Sabrina | 2184053 | Base de données + Écran Questionnaire |

---

## Description

FrigoChef aide l'utilisateur à décider quoi cuisiner selon les ingrédients qu'il a chez lui. L'utilisateur peut rechercher une recette par nom, filtrer le catalogue par type de cuisine, difficulté ou contraintes alimentaires, ou saisir ses ingrédients disponibles avec leurs quantités pour obtenir des suggestions personnalisées accompagnées d'un score de compatibilité calculé de façon proportionnelle.

### Fonctionnalités principales

- **Accueil** — catalogue de 30 recettes avec recherche par nom et filtres rapides
- **Questionnaire** — sélection guidée du type de cuisine, contraintes et ingrédients disponibles
- **Résultats** — recettes filtrées triées par score de compatibilité décroissant
- **Détail** — instructions complètes et liste des ingrédients possédés / manquants

---

## Technologies utilisées

- Kotlin
- Android SDK (API 26+)
- SQLite (SQLiteOpenHelper)
- Architecture MVP (Modèle-Vue-Présentateur)
- ViewBinding
- Material Components 1.13.0
- JUnit + Mockito (tests unitaires)
- Espresso (tests bout-en-bout)

---

## Installation

**1. Cloner le dépôt**
```bash
git clone https://git.dti.crosemont.quebec/2184053/frigochef.git
```

**2. Ouvrir le projet dans Android Studio**

**3. Synchroniser Gradle**
```
File → Sync Project with Gradle Files
```

**4. Lancer l'application**

Sélectionner un émulateur API 26+ et cliquer sur **Run ▶**

---

## Architecture

Le projet suit l'architecture MVP avec une séparation claire des responsabilités :

```
app/src/main/java/com/example/frigochef/
├── contract/        — Interfaces définissant la communication Vue ↔ Présentateur
├── database/        — FrigoDBHelper (SQLiteOpenHelper, seed data)
├── model/
│   ├── entity/      — Data classes (Recette, Ingredient, FiltreRecette, IngredientQuantite…)
│   ├── repository/  — Accès base de données (RecetteRepository, IngredientRepository…)
│   └── ScoreCalculateur.kt
├── presenter/       — Logique applicative (ResultatsPresenter, DetailPresenter…)
└── view/
    ├── adapter/     — RecyclerView adapters (RecetteAdapter, IngredientDetailAdapter…)
    └── activités    — AccueilActivity, QuestionnaireActivity, ResultatsActivity, DetailRecetteActivity
```

---

## Base de données

4 tables SQLite pré-chargées au démarrage :

| Table | Description |
|---|---|
| `ingredient` | 54 ingrédients de référence |
| `recette` | 30 recettes réparties dans 10 cuisines |
| `recette_ingredient` | Jointure recette ↔ ingrédient avec quantités et unités |
| `session_ingredients` | Ingrédients de la dernière session de l'utilisateur |

**10 cuisines** : Africaine, Américaine, Grecque, Indienne, Italienne, Japonaise, Méditerranéenne, Mexicaine, Moyen-Orientale, Québécoise

---

## Calcul du score de compatibilité

Le score est calculé de façon **proportionnelle** par `ScoreCalculateur` :

```
score = (somme des proportions / nombre d'ingrédients) × 100

proportion par ingrédient :
  - absent                          → 0.0
  - quantite_dispo / quantite_requise → plafonné à 1.0
```

Seuils de couleur :
- Vert  ≥ 75%
- Orange ≥ 50%
- Rouge  < 50%

---

## Tests

### Tests unitaires

| Classe testée            | Nombre de tests |
|--------------------------|-----------------|
| `ScoreCalculateur`       | 3               |
| `QuestionnairePresenter` | 3               |
| `AccueilPresenter`       | 3               |

### Tests bout-en-bout

| # | Parcours | À compléter |
|---|---|------------|
| 1 | Questionnaire → sélectionner ingrédients → valider → vérifier résultats |            |

---

## Cours

420-G25-RO — Applications natives 2 — Hiver 2026 — Keven Chaussé