# FrigoChef
Application mobile Android native de suggestions de recettes basée sur les ingrédients disponibles, développée en Kotlin avec une base de données SQLite et l'architecture MVP.

---

## Membres de l'équipe

| Nom | Responsabilité principale |
|---|---|
| Nath, Arpan | Base de données (Principal) + Écran Résultats (Principal) + Écran Détail (Principal) + Revues de code + Tests unitaires (`ScoreCalculateur`, `DetailPresenter`) + Test bout-en-bout (`SessionPersistanceTest`) |
| Ahmed, Sabia | Écran Accueil (Principal) + Écran Détail (Support) + Revues de code + Tests unitaires (`AccueilPresenter`) + Test bout-en-bout (`AccueilSearchE2ETest`) |
| Abdulali, Sabrina | Base de données (Support) + Écran Questionnaire (Principal) + Revues de code + Layout XML (Principal) + Images des recettes + Tests unitaires (`QuestionnairePresenter`) + Tests bout-en-bout (`QuestionnaireE2ETest`) |

---

## Description

FrigoChef aide l'utilisateur à décider quoi cuisiner selon les ingrédients qu'il a chez lui. L'utilisateur peut rechercher une recette par nom, filtrer le catalogue par type de cuisine, difficulté ou contraintes alimentaires, ou saisir ses ingrédients disponibles avec leurs quantités pour obtenir des suggestions personnalisées accompagnées d'un score de compatibilité calculé de façon proportionnelle.

### Fonctionnalités principales

- **Accueil** — catalogue de 30 recettes avec recherche par nom et filtres rapides combinables
- **Questionnaire** — sélection guidée en 4 étapes : type de cuisine, contraintes, ingrédients disponibles avec quantités, récapitulatif
- **Résultats** — recettes filtrées triées par score de compatibilité décroissant
- **Détail** — instructions numérotées et liste des ingrédients avec statut possédé / partiel / manquant

---

## Technologies utilisées

- Kotlin
- Android SDK (API 26+)
- SQLite (SQLiteOpenHelper)
- Architecture MVP (Modèle-Vue-Présentateur)
- ViewBinding
- Material Components 1.13.0
- SharedPreferences (persistance des filtres)
- JUnit + Mockito (tests unitaires)
- Espresso (tests bout-en-bout)

---

## Installation

**1. Cloner le dépôt**
```bash
git clone https://github.com/arpan-nath/frigochef.git
```

**2. Ouvrir le projet dans Android Studio**

**3. Synchroniser Gradle**
```
File → Sync Project with Gradle Files
```

**4. Lancer l'application**

Sélectionner un émulateur API 26+ et cliquer sur **Run**

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
│   ├── ScoreCalculateur.kt
│   └── PrefsManager.kt
├── presenter/       — Logique applicative (ResultatsPresenter, DetailPresenter…)
└── view/
    ├── adapter/     — RecyclerView adapters (RecetteAdapter, IngredientDetailAdapter…)
    ├── fragment/    — Étapes du Questionnaire (Etape1 à Etape4) + PanneauFiltresFragment
    └── activités    — AccueilActivity, QuestionnaireActivity, ResultatsActivity, DetailRecetteActivity
```

---

## Base de données

4 tables SQLite pré-chargées au démarrage :

| Table | Description |
|---|---|
| `ingredient` | 54 ingrédients de référence |
| `recette` | 30 recettes réparties dans 10 cuisines |
| `recette_ingredient` | Jointure recette <-> ingrédient avec quantités et unités |
| `session_ingredients` | Ingrédients de la dernière session de l'utilisateur |

**10 cuisines** : Africaine, Américaine, Grecque, Indienne, Italienne, Japonaise, Méditerranéenne, Mexicaine, Moyen-Orientale, Québécoise

---

## Calcul du score de compatibilité

Le score est calculé de façon **proportionnelle** par `ScoreCalculateur` :

```
score = (somme des proportions / nombre d'ingrédients) × 100

proportion par ingrédient :
  - absent                            → 0.0
  - quantite_dispo / quantite_requise → plafonné à 1.0
```

Seuils de couleur :
- Vert  >= 75%
- Orange >= 50%
- Rouge  < 50%

Statut des ingrédients dans l'écran Détail :
- Vert — quantité suffisante
- Orange — possédé mais quantité insuffisante
- Rouge — absent

---

## Persistance

- **SQLite** — données de référence (recettes, ingrédients) et session de l'utilisateur
- **SharedPreferences** — filtres du Questionnaire sauvegardés entre les sessions via `PrefsManager`

---

## Tests

### Tests unitaires

Les tests unitaires se trouvent dans :
```
app/src/test/java/com/example/frigochef/
```

#### ScoreCalculateurTest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `score 100 si tous les ingredients presents` | Score de 100% quand toutes les quantités sont suffisantes |
| 2 | `score 0 si aucun ingredient dispo` | Score de 0% quand aucun ingrédient n'est disponible |
| 3 | `score proportionnel si quantite insuffisante` | Score proportionnel quand la quantité disponible est insuffisante |

#### DetailPresenterTest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `afficherErreur si recette introuvable` | `afficherErreur()` est appelé si la recette n'existe pas en BD |
| 2 | `afficherRecette si recette trouvee` | `afficherRecette()` est appelé avec la bonne recette |
| 3 | `afficherIngredients appele avec bons ingredients` | `afficherIngredients()` est appelé avec les bons ingrédients et dispos |

#### QuestionnairePresenterTest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `chargerSessionPrecedente appelle afficherIngredientsPrecaches si session non vide` | Les ingrédients de la session sont transmis à la vue |
| 2 | `rechercherIngredient appelle afficherIngredientsSuggeres avec les resultats` | La recherche retourne les suggestions au bon format |
| 3 | `valider sauvegarde les ingredients et navigue vers resultats` | Chaque ingrédient est sauvegardé via `upsert()` et la navigation est déclenchée |

#### AccueilPresenterTest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `chargerRecettes appelle afficherRecettes si liste non vide` | La vue reçoit la liste quand des recettes existent |
| 2 | `chargerRecettes appelle afficherMessageVide si liste vide` | La vue affiche l'état vide quand aucune recette n'existe |
| 3 | `rechercherRecettes appelle afficherRecettes si resultats` | La recherche par nom retourne les résultats correspondants |
| 4 | `rechercherRecettes appelle afficherMessageVide si aucun resultat` | La vue affiche l'état vide si aucun résultat ne correspond |
| 5 | `filtrerParFiltres appelle afficherRecettes si resultats` | Le filtrage retourne les recettes correspondant aux critères |
| 6 | `filtrerParFiltres appelle afficherMessageVide si aucun resultat` | La vue affiche l'état vide si aucune recette ne correspond aux filtres |
| 7 | `chargerSessionIngredients appelle afficherChipsSession` | Les ingrédients de la session sont transmis à la vue pour affichage |

Pour lancer les tests unitaires :
```bash
./gradlew test
```

---

### Tests bout-en-bout (Espresso)

Les tests bout-en-bout se trouvent dans :
```
app/src/androidTest/java/com/example/frigochef/
```

#### AccueilSearchE2ETest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `parcours_complet_recherche_recette_et_affichage_detail` | Rechercher une recette depuis l'Accueil et vérifier que l'écran Détail s'affiche |

#### SessionPersistanceTest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `ingredientsSauvegardesEtPrecochesAuRelancement` | Saisir des ingrédients dans le Questionnaire, quitter, relancer et vérifier que les ingrédients sont pré-cochés dans l'étape 3 |

#### QuestionnaireE2ETest

| # | Nom du test | Ce qui est vérifié |
|---|---|---|
| 1 | `parcours_complet_cuisine_ingredients_resultats_et_detail` | Sélectionner une cuisine, saisir des ingrédients, valider et vérifier que l'écran Résultats puis Détail s'affichent |
| 2 | `parcours_ignorer_toutes_etapes_retourne_resultats_non_vides` | Ignorer toutes les étapes du Questionnaire et vérifier que des résultats sont tout de même retournés |
| 3 | `parcours_multi_cuisine_affiche_resultats_des_deux_cuisines` | Sélectionner deux cuisines et vérifier que les recettes des deux cuisines apparaissent dans les résultats |

Pour lancer les tests bout-en-bout :
```bash
./gradlew connectedAndroidTest
```

---

## Cours

420-G25-RO — Applications natives 2 — Hiver 2026 — Keven Chaussé