# Rapport d'utilisation de l'intelligence artificielle

**Projet :** FrigoChef — 420-G25-RO  
**Équipe :** Sabia Ahmed · Sabrina Abdulali · Arpan Nath  
**Outil utilisé :** Claude (Anthropic) — claude.ai

---

## 1. Outil utilisé

| Outil | Version | Utilisation |
|---|---|---|
| Claude (Anthropic) | Sonnet 4.6 | Assistant de développement conversationnel |

---

## 2. Utilisation par membre

---

### Sabia Ahmed — `feature/activite-accueil`

J'ai utilisé Claude comme assistant de développement pour la réalisation de l'`AccueilActivity`.

#### 2.1 Développement de l'interface
- Génération du mockup visuel de l'`AccueilActivity` selon le thème FrigoChef
- Création du layout XML `activity_accueil.xml` et `item_recette_card.xml`
- Configuration du thème, des couleurs et des drawables

#### 2.2 Architecture MVP
- Génération de la structure de base de `AccueilActivity.kt` et `AccueilPresenter.kt`
- Déplacement de la logique de chargement des ingrédients de session vers le Présentateur (`chargerSessionIngredients()`) pour respecter le patron MVP
- Implémentation des interfaces `AccueilContract.View` et `AccueilContract.Presenter`

#### 2.3 Fonctionnalités
- Implémentation de la logique des filtres combinés (`appliquerFiltresCombines()`)
- Connexion du bouton filtre sidebar à `PanneauFiltresFragment`
- Gestion de la navigation vers `DetailRecetteActivity` avec le flag `depuis_accueil`

#### 2.4 Configuration du projet
- Correction des erreurs de build (Gradle, ViewBinding, dépendances)
- Configuration de `libs.versions.toml` et `build.gradle.kts`
- Résolution des conflits Git lors du merge

#### 2.5 Tests
- Génération des tests unitaires pour `AccueilPresenter`
- Génération du test bout-en-bout pour le parcours de recherche de recette

#### 2.6 Revue de code
- Analyse des fichiers de mes coéquipiers
- Identification des erreurs critiques et des améliorations mineures
- Rédaction des commentaires de revue

---

### Sabrina Abdulali — `feature/activite-questionnaire`

J'ai utilisé Claude comme assistant de développement pour la réalisation de `QuestionnaireActivity` et des écrans associés, ainsi que Gemini pour la génération des images des recettes.

#### 2.7 Analyse et débogage

- Analyse complète du code du questionnaire pour identifier les bogues de filtrage
- Identification de trois bogues distincts affectant les résultats retournés :
    - **Bogue #1 (multi-cuisine)** : seule la première cuisine sélectionnée était transmise via `.first()`, ignorant les autres sélections
    - **Bogue #2 (filtres persistants)** : les filtres de la session précédente étaient chargés depuis `SharedPreferences` au démarrage, rendant le bouton « Ignorer » inefficace
    - **Bogue #3 (slider temps)** : la valeur par défaut du slider (60 min) était appliquée comme filtre actif même sans interaction de l'utilisateur
- Analyse des traces d'exécution (`logcat`) pour diagnostiquer un crash au lancement de `ResultatsActivity` causé par un appel au présentateur avant l'initialisation de l'`adapter`

#### 2.8 Corrections apportées

- **`QuestionnaireContract.kt`** : mise à jour des signatures de `valider()` et `naviguerVersResultats()` pour transmettre la liste complète des cuisines et les IDs pré-filtrés
- **`QuestionnairePresenter.kt`** : implémentation de la fusion multi-cuisine — une requête par cuisine sélectionnée, déduplication par ID via `distinctBy { it.id }`
- **`QuestionnaireActivity.kt`** : introduction du champ `filtresInitiaux` pour séparer la restauration visuelle de l'état actif ; réinitialisation systématique de `filtres` à `FiltreRecette()` au démarrage
- **`Etape2ContraintesFragment.kt`** : ajout du booléen `sliderModifie` pour n'inclure `tempsMax` dans les filtres que si l'utilisateur a explicitement bougé le curseur ; lecture de `filtresInitiaux` dans `restaurerFiltres()` au lieu de l'état actif
- **`Etape3IngredientsFragment.kt`** : suppression du bloc `.first()` dans `configurerBoutonVoirRecettes()` ; transmission de la liste complète `cuisinesSelectionnees` à `valider()`
- **`ResultatsContract.kt` et `ResultatsPresenter.kt`** : ajout de `chargerResultatsParIds()` pour recevoir les IDs pré-filtrés par cuisine depuis le questionnaire
- **`ResultatsActivity.kt`** : correction de l'ordre d'initialisation dans `onCreate()` — l'`adapter` et le `RecyclerView` sont désormais initialisés avant le déclenchement du chargement

#### 2.9 Gestion de la session précédente (étape 3)

- Diagnostic du problème de timing : `afficherIngredientsPrecaches()` était appelée avant que le fragment de l'étape 3 soit créé par le `ViewPager2`
- Correction par inversion de responsabilité : le fragment lit lui-même `idsSessionPrecedente` dans `onViewCreated()`, une fois qu'il est pleinement initialisé
- Ajout du champ `idsSessionPrecedente` dans `QuestionnaireActivity` comme pont entre les deux cycles de vie

#### 2.10 Tests

- Génération des tests unitaires pour `QuestionnairePresenter` (logique de validation et gestion de session)
- Génération du test bout-en-bout `QuestionnaireE2ETest` couvrant trois parcours :
    - Parcours complet : sélection cuisine → contraintes → ingrédients → résultats → détail
    - Parcours « Peu importe » : aucun filtre sélectionné → toutes les recettes retournées
    - Parcours multi-cuisine : deux cuisines sélectionnées → résultats fusionnés des deux cuisines

---

### Arpan Nath — `feature/base-de-donnees`, `feature/architecture-mvp`, `feature/activite-resultats`, `feature/activite-detail`

J'ai utilisé Claude comme assistant de développement pour la base de données, l'architecture MVP, l'écran Résultats et l'écran Détail.

#### 2.11 Base de données
- Génération du schéma SQLite complet avec les 4 tables (`ingredient`, `recette`, `recette_ingredient`, `session_ingredients`)
- Implémentation de `FrigoDBHelper` avec `onCreate()`, `onUpgrade()` et `onOpen()` (activation des clés étrangères via `PRAGMA foreign_keys=ON`)
- Génération des seeders : 54 ingrédients, 30 recettes réparties dans 10 cuisines et leurs associations avec quantités et unités
- Uniformisation des unités de mesure à travers toutes les recettes

#### 2.12 Architecture MVP
- Implémentation de `ScoreCalculateur` (singleton `object`) avec calcul proportionnel plafonné à 1.0 par ingrédient
- Conception et justification des décisions architecturales : 3 booléens séparés pour le régime alimentaire, `quantite` en `String`, stockage de `date_derniere_saisie` en `INTEGER`

#### 2.13 Écran Résultats
- Génération de `ResultatsPresenter` et `ResultatsActivity`
- Implémentation de `RecetteAdapter` avec badge de score coloré, image via `getIdentifier()`, chips de régime et couleurs par type de cuisine
- Implémentation de `findParFiltres()` dans `RecetteRepository` avec construction dynamique de la clause `WHERE`
- Connexion de `PanneauFiltresFragment` avec carte de noms d'ingrédients
- Correction d'un bogue dans `ResultatsActivity` : `ingredientsDispos` était récupéré après avoir été utilisé, causant un score de 0 % pour toutes les recettes

#### 2.14 Écran Détail
- Génération de `DetailPresenter` et `DetailRecetteActivity`
- Implémentation de `IngredientDetailAdapter` avec 3 états visuels : vert (quantité suffisante), orange (quantité insuffisante), rouge (absent)
- Implémentation de `InstructionAdapter` avec parsing des étapes via regex pour supprimer les numéros existants et rénuméroter automatiquement
- Implémentation de `findIngredientsDetailParRecette()` dans `RecetteRepository`
- Gestion du fallback image : couleur de fond par type de cuisine si `imageUrl` est null ou invalide

#### 2.15 Tests
- Génération des tests unitaires pour `DetailPresenter` (3 tests) et `ScoreCalculateur` (3 tests)
- Génération du test bout-en-bout pour la persistance de session

---

## 3. Extraits de code générés avec l'aide de Claude

Les fonctions suivantes ont été produites à l'aide de Claude et sont identifiées dans le code par le commentaire `// Code produit à l'aide de Claude` ou `// Code généré à l'aide de Claude AI` :

### Sabia Ahmed

| Fichier | Fonction | Description |
|---|---|---|
| `AccueilActivity.kt` | `afficherChipsSession()` | Affiche les ingrédients de session sous forme de chips |
| `AccueilActivity.kt` | `appliquerFiltresCombines()` | Construit un filtre combiné depuis l'état des chips |
| `AccueilPresenter.kt` | `chargerSessionIngredients()` | Charge les ingrédients de session via les repositories |
| `AccueilPresenter.kt` | `filtrerParFiltres()` | Filtre les recettes avec un FiltreRecette combiné |

### Sabrina Abdulali

| Fichier | Fonction | Description |
|---|---|---|
| `QuestionnairePresenter.kt` | `valider()` | Fusion multi-cuisine : requête par cuisine, déduplication par ID |
| `QuestionnaireActivity.kt`   | `mettreAJourUI()`    | Mise à jour de la barre de progression et du bouton selon l'étape active |
| `Etape1CuisineFragment.kt`   | `construireGrille()` | Création dynamique des 10 cartes cuisine via LayoutInflater dans un GridLayout |
| `Etape2ContraintesFragment.kt` | `configurerSlider()` | Écoute des changements du curseur avec le flag `fromUser` |
| `Etape2ContraintesFragment.kt` | `sauvegarderFiltres()` | Construction du FiltreRecette avec tempsMax conditionnel |
| `ResultatsPresenter.kt` | `chargerResultatsParIds()` | Chargement des résultats depuis une liste d'IDs pré-filtrés |
| `Etape3IngredientsFragment.kt` | `ajouterIngredient()` | Récupération de l'unité par défaut via `findUniteParDefaut()` |


### Arpan Nath

| Fichier | Fonction / Section | Description |
|---|---|---|
| `FrigoDBHelper.kt` | `seederRecettes()` | Insertion des 30 recettes avec instructions détaillées |
| `FrigoDBHelper.kt` | `seederRecetteIngredients()` | Association recette ↔ ingrédient avec quantités et unités |
| `RecetteRepository.kt` | `findParFiltres()` | Construction dynamique de la clause WHERE multi-critères |
| `RecetteRepository.kt` | `findIngredientQuantitesParRecette()` | Jointure SQL pour le calcul du score |
| `RecetteRepository.kt` | `findIngredientsDetailParRecette()` | Jointure SQL pour l'affichage dans l'écran Détail |
| `ScoreCalculateur.kt` | `calculer()` | Calcul proportionnel du score de compatibilité |
| `ResultatsActivity.kt` | `afficherChipsFiltresActifs()` | Affichage des filtres actifs sous forme de chips |
| `DetailRecetteActivity.kt` | `afficherCouleurCuisine()` | Fallback couleur de fond par type de cuisine |
| `IngredientDetailAdapter.kt` | `onBindViewHolder()` | Logique des 3 états visuels (vert / orange / rouge) |
| `InstructionAdapter.kt` | `soumettre()` | Parsing des instructions avec regex et rénumérotation |
| `RecetteAdapter.kt` | `bind()` | Chargement de l'image via `getIdentifier()` avec fallback |

---

## 4. Réflexion critique sur l'apport de l'IAg

L'utilisation de Claude a permis d'accélérer significativement le développement, notamment pour les parties répétitives (seeders, adapters, layouts XML) et le débogage de bogues complexes liés à l'architecture multi-fragments.

Les principales limites rencontrées sont les suivantes : le code généré nécessitait systématiquement une adaptation au contexte exact du projet (noms de variables, structure de packages, signatures de contrats déjà en place). Certaines suggestions initiales proposaient des solutions qui auraient impliqué de modifier des fichiers appartenant à d'autres membres de l'équipe, ce qui a nécessité plusieurs itérations pour trouver une approche respectant les frontières de responsabilité. Dans tous les cas, le code généré a été relu, compris et modifié avant d'être intégré au projet.