package com.example.frigochef.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FrigoDBHelper (context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME    = "frigochef.db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_INGREDIENT)
        db.execSQL(CREATE_TABLE_RECETTE)
        db.execSQL(CREATE_TABLE_RECETTE_INGREDIENT)
        db.execSQL(CREATE_TABLE_SESSION)
        seederIngredients(db)
        seederRecettes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS recette_ingredient")
        db.execSQL("DROP TABLE IF EXISTS recette")
        db.execSQL("DROP TABLE IF EXISTS session_ingredients")
        db.execSQL("DROP TABLE IF EXISTS ingredient")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) db.execSQL("PRAGMA foreign_keys=ON")
    }

    private val CREATE_TABLE_INGREDIENT = """
        CREATE TABLE ingredient (
            id        INTEGER PRIMARY KEY AUTOINCREMENT,
            nom       TEXT    NOT NULL UNIQUE,
            categorie TEXT    NOT NULL
        )
    """.trimIndent()

    private val CREATE_TABLE_RECETTE = """
        CREATE TABLE recette (
            id             INTEGER PRIMARY KEY AUTOINCREMENT,
            nom            TEXT    NOT NULL,
            description    TEXT,
            instructions   TEXT    NOT NULL,
            temps_prep     INTEGER NOT NULL,
            difficulte     TEXT    NOT NULL,
            type_cuisine   TEXT    NOT NULL,
            type_repas     TEXT    NOT NULL,
            image_url      TEXT,
            is_vege        INTEGER NOT NULL DEFAULT 0,
            is_vegan       INTEGER NOT NULL DEFAULT 0,
            is_sans_gluten INTEGER NOT NULL DEFAULT 0
        )
    """.trimIndent()

    private val CREATE_TABLE_RECETTE_INGREDIENT = """
        CREATE TABLE recette_ingredient (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            recette_id    INTEGER NOT NULL,
            ingredient_id INTEGER NOT NULL,
            quantite      TEXT,
            unite         TEXT,
            FOREIGN KEY (recette_id)    REFERENCES recette(id)    ON DELETE CASCADE,
            FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
        )
    """.trimIndent()

    private val CREATE_TABLE_SESSION = """
        CREATE TABLE session_ingredients (
            id                   INTEGER PRIMARY KEY AUTOINCREMENT,
            ingredient_id        INTEGER NOT NULL UNIQUE,
            date_derniere_saisie INTEGER NOT NULL,
            frequence_usage      INTEGER NOT NULL DEFAULT 1,
            FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
        )
    """.trimIndent()

    private fun seederIngredients(db: SQLiteDatabase) {
        val ingredients = listOf(
            Pair("Ail",            "Légumes"),
            Pair("Oignon",         "Légumes"),
            Pair("Tomate",         "Légumes"),
            Pair("Poulet",         "Viandes"),
            Pair("Boeuf haché",    "Viandes"),
            Pair("Pâtes",          "Féculents"),
            Pair("Riz",            "Féculents"),
            Pair("Oeuf",           "Produits laitiers"),
            Pair("Fromage",        "Produits laitiers"),
            Pair("Lait",           "Produits laitiers"),
            Pair("Beurre",         "Produits laitiers"),
            Pair("Huile d'olive",  "Huiles"),
            Pair("Lardons",        "Viandes"),
            Pair("Parmesan",       "Produits laitiers"),
            Pair("Crème fraîche",  "Produits laitiers")
        )
        ingredients.forEach { (nom, categorie) ->
            val cv = ContentValues().apply {
                put("nom",       nom)
                put("categorie", categorie)
            }
            db.insert("ingredient", null, cv)
        }
    }

    private fun seederRecettes(db: SQLiteDatabase) {
        val recettes = listOf(
            // ITALIENNES
            listOf("Spaghetti Carbonara", "Pâtes crémeuses à l'italienne",
                "1. Cuire les pâtes al dente\n2. Faire revenir les lardons\n3. Battre les oeufs avec le parmesan\n4. Mélanger hors du feu",
                20, "Facile", "Italienne", "Dîner", 0, 0, 0),
            listOf("Pizza Margherita", "Pizza classique tomate mozzarella",
                "1. Préparer la pâte\n2. Étaler la sauce tomate\n3. Ajouter la mozzarella\n4. Cuire 12 min à 220°C",
                45, "Moyen", "Italienne", "Dîner", 1, 0, 0),
            listOf("Risotto aux champignons", "Risotto crémeux aux champignons",
                "1. Faire revenir l'oignon\n2. Ajouter le riz\n3. Incorporer le bouillon petit à petit\n4. Ajouter les champignons et le parmesan",
                35, "Moyen", "Italienne", "Dîner", 1, 0, 0),
            listOf("Pâtes à la bolognaise", "Sauce à la viande mijotée",
                "1. Faire revenir oignon et ail\n2. Ajouter le boeuf haché\n3. Incorporer les tomates\n4. Laisser mijoter 30 min\n5. Servir sur les pâtes",
                50, "Facile", "Italienne", "Dîner", 0, 0, 0),

            // FRANÇAISES
            listOf("Poulet rôti", "Classique du dimanche",
                "1. Préchauffer le four à 180°C\n2. Assaisonner le poulet\n3. Cuire 1h30 en arrosant régulièrement",
                90, "Moyen", "Française", "Dîner", 0, 0, 0),
            listOf("Omelette fromage", "Rapide et nourrissante",
                "1. Battre les oeufs\n2. Faire fondre le beurre\n3. Verser les oeufs\n4. Ajouter le fromage\n5. Plier et servir",
                10, "Facile", "Française", "Déjeuner", 1, 0, 1),
            listOf("Soupe à l'oignon", "Soupe gratinée réconfortante",
                "1. Caraméliser les oignons 30 min\n2. Ajouter le bouillon\n3. Laisser mijoter 20 min\n4. Gratiner avec le fromage",
                60, "Moyen", "Française", "Dîner", 1, 0, 1),
            listOf("Quiche Lorraine", "Tarte salée aux lardons",
                "1. Préparer la pâte brisée\n2. Mélanger oeufs, crème et lardons\n3. Verser dans le moule\n4. Cuire 35 min à 180°C",
                55, "Moyen", "Française", "Déjeuner", 0, 0, 0),
            listOf("Crêpes", "Crêpes légères sucrées ou salées",
                "1. Mélanger farine, oeufs et lait\n2. Laisser reposer 30 min\n3. Cuire à la poêle",
                40, "Facile", "Française", "Collation", 1, 0, 0),

            // ASIATIQUES
            listOf("Riz sauté aux légumes", "Riz sauté végane rapide",
                "1. Cuire le riz\n2. Faire sauter les légumes à feu vif\n3. Ajouter le riz\n4. Assaisonner avec sauce soja",
                15, "Facile", "Asiatique", "Dîner", 1, 1, 1),
            listOf("Poulet teriyaki", "Poulet glacé sauce teriyaki",
                "1. Mariner le poulet\n2. Cuire à la poêle\n3. Ajouter la sauce teriyaki\n4. Laisser caraméliser",
                25, "Facile", "Asiatique", "Dîner", 0, 0, 1),
            listOf("Soupe miso", "Soupe japonaise légère",
                "1. Chauffer le bouillon dashi\n2. Dissoudre la pâte miso\n3. Ajouter le tofu et les algues\n4. Servir chaud",
                10, "Facile", "Asiatique", "Déjeuner", 1, 1, 1),
            listOf("Pad Thaï", "Nouilles sautées thaïlandaises",
                "1. Tremper les nouilles\n2. Faire sauter avec oeuf et légumes\n3. Ajouter sauce poisson et citron\n4. Garnir d'arachides",
                30, "Difficile", "Asiatique", "Dîner", 0, 0, 1),
            listOf("Rouleaux de printemps", "Rouleaux frais végétariens",
                "1. Tremper les feuilles de riz\n2. Garnir de légumes et vermicelles\n3. Rouler serré\n4. Servir avec sauce",
                30, "Moyen", "Asiatique", "Collation", 1, 1, 1),
            listOf("Butter Chicken", "Poulet mijoté dans une sauce au beurre et aux épices",
                "1. Mariner le poulet dans le yogourt et les épices\n2. Faire griller le poulet\n3. Préparer la sauce avec beurre, tomates et crème\n4. Ajouter le poulet à la sauce\n5. Laisser mijoter 15 min",
                45, "Moyen", "Asiatique", "Dîner", 0, 0, 1),

            // MEXICAINES
            listOf("Tacos au boeuf", "Tacos festifs à la mexicaine",
                "1. Faire revenir le boeuf haché\n2. Assaisonner cumin et paprika\n3. Garnir les tacos\n4. Ajouter tomate et fromage",
                25, "Moyen", "Mexicaine", "Dîner", 0, 0, 0),
            listOf("Guacamole", "Trempette à l'avocat fraîche",
                "1. Écraser les avocats\n2. Ajouter oignon, tomate et citron\n3. Assaisonner\n4. Servir frais",
                10, "Facile", "Mexicaine", "Collation", 1, 1, 1),
            listOf("Burrito au poulet", "Burrito complet au poulet",
                "1. Cuire le poulet assaisonné\n2. Réchauffer la tortilla\n3. Garnir de riz, haricots et poulet\n4. Rouler",
                30, "Moyen", "Mexicaine", "Dîner", 0, 0, 0),
            listOf("Quesadillas fromage", "Tortillas grillées au fromage",
                "1. Garnir la tortilla de fromage\n2. Plier\n3. Griller à la poêle des deux côtés",
                10, "Facile", "Mexicaine", "Collation", 1, 0, 0),
            listOf("Chili con carne", "Ragoût épicé mexicain",
                "1. Faire revenir oignon et ail\n2. Ajouter boeuf haché\n3. Incorporer tomates et haricots\n4. Mijoter 40 min",
                60, "Moyen", "Mexicaine", "Dîner", 0, 0, 1),
            listOf("Enchiladas végétariennes", "Tortillas farcies aux légumes",
                "1. Préparer la garniture de légumes\n2. Farcir les tortillas\n3. Napper de sauce\n4. Cuire 20 min au four",
                40, "Moyen", "Mexicaine", "Dîner", 1, 0, 0),
        )

        recettes.forEach { r ->
            val cv = ContentValues().apply {
                put("nom",            r[0] as String)
                put("description",    r[1] as String)
                put("instructions",   r[2] as String)
                put("temps_prep",     r[3] as Int)
                put("difficulte",     r[4] as String)
                put("type_cuisine",   r[5] as String)
                put("type_repas",     r[6] as String)
                put("is_vege",        r[7] as Int)
                put("is_vegan",       r[8] as Int)
                put("is_sans_gluten", r[9] as Int)
            }
            db.insert("recette", null, cv)
        }
    }
}