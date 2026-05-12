package com.example.frigochef.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FrigoDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME    = "frigochef.db"
        const val DB_VERSION = 1

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
                is_sans_gluten INTEGER NOT NULL DEFAULT 0,
                portions       INTEGER NOT NULL DEFAULT 4
            )
        """.trimIndent()

        private val CREATE_TABLE_RECETTE_INGREDIENT = """
            CREATE TABLE recette_ingredient (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                recette_id    INTEGER NOT NULL,
                ingredient_id INTEGER NOT NULL,
                quantite      TEXT,
                unite_mesure  TEXT,
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
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_INGREDIENT)
        db.execSQL(CREATE_TABLE_RECETTE)
        db.execSQL(CREATE_TABLE_RECETTE_INGREDIENT)
        db.execSQL(CREATE_TABLE_SESSION)
        seederIngredients(db)
        seederRecettes(db)
        seederRecetteIngredients(db)
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

    private fun seederIngredients(db: SQLiteDatabase) {
        val ingredients = listOf(
            // Légumes
            Pair("Ail",               "Légumes"),
            Pair("Oignon",            "Légumes"),
            Pair("Tomate",            "Légumes"),
            Pair("Poivron",           "Légumes"),
            Pair("Courgette",         "Légumes"),
            Pair("Aubergine",         "Légumes"),
            Pair("Épinards",          "Légumes"),
            Pair("Champignons",       "Légumes"),
            Pair("Concombre",         "Légumes"),
            Pair("Pomme de terre",    "Légumes"),
            Pair("Carotte",           "Légumes"),
            Pair("Céleri",            "Légumes"),
            Pair("Tofu",              "Protéines végétales"),
            Pair("Pois chiches",      "Légumineuses"),
            Pair("Lentilles",         "Légumineuses"),
            Pair("Haricots noirs",    "Légumineuses"),
            // Viandes
            Pair("Poulet",            "Viandes"),
            Pair("Boeuf haché",       "Viandes"),
            Pair("Agneau",            "Viandes"),
            Pair("Lardons",           "Viandes"),
            Pair("Saucisse",          "Viandes"),
            // Produits laitiers
            Pair("Oeuf",              "Produits laitiers"),
            Pair("Fromage",           "Produits laitiers"),
            Pair("Parmesan",          "Produits laitiers"),
            Pair("Feta",              "Produits laitiers"),
            Pair("Lait",              "Produits laitiers"),
            Pair("Beurre",            "Produits laitiers"),
            Pair("Crème fraîche",     "Produits laitiers"),
            Pair("Yogourt",           "Produits laitiers"),
            // Féculents
            Pair("Pâtes",             "Féculents"),
            Pair("Riz",               "Féculents"),
            Pair("Pain pita",         "Féculents"),
            Pair("Tortilla",          "Féculents"),
            Pair("Couscous",          "Féculents"),
            // Huiles et condiments
            Pair("Huile d'olive",     "Huiles"),
            Pair("Huile de sésame",   "Huiles"),
            Pair("Sauce soja",        "Condiments"),
            Pair("Pâte de tomate",    "Condiments"),
            Pair("Sauce piquante",    "Condiments"),
            Pair("Miel",              "Condiments"),
            Pair("Vinaigre balsamique","Condiments"),
            Pair("Moutarde",           "Condiments"),
            // Épices
            Pair("Cumin",             "Épices"),
            Pair("Paprika",           "Épices"),
            Pair("Curcuma",           "Épices"),
            Pair("Garam masala",      "Épices"),
            Pair("Coriandre",         "Épices"),
            Pair("Cannelle",          "Épices"),
            Pair("Piment",            "Épices"),
            // Autres
            Pair("Bouillon de poulet","Autres"),
            Pair("Noix de coco",      "Autres"),
            Pair("Citron",            "Fruits"),
            Pair("Avocat",            "Fruits"),
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

            // ── MOYEN-ORIENTALE ──
            listOf("Houmous", "Trempette crémeuse aux pois chiches",
                "1. Égoutter les pois chiches\n2. Mixer avec tahini, ail et citron\n3. Ajouter huile d'olive\n4. Assaisonner et servir",
                15, "Facile", "Moyen-Orientale", "Collation", 1, 1, 1, 6),
            listOf("Shawarma au poulet", "Poulet mariné aux épices du Moyen-Orient",
                "1. Mariner le poulet dans les épices\n2. Griller à feu vif\n3. Trancher finement\n4. Servir dans un pain pita avec légumes",
                40, "Moyen", "Moyen-Orientale", "Dîner", 0, 0, 0, 4),
            listOf("Taboulé", "Salade fraîche au persil et couscous",
                "1. Préparer le couscous\n2. Hacher persil, tomate et oignon\n3. Mélanger avec huile d'olive et citron\n4. Réfrigérer 30 min",
                20, "Facile", "Moyen-Orientale", "Déjeuner", 1, 1, 1, 6),

            // ── MÉDITERRANÉENNE ──
            listOf("Ratatouille", "Mijotée de légumes provençaux",
                "1. Couper tous les légumes\n2. Faire revenir l'ail et l'oignon\n3. Ajouter les légumes un par un\n4. Mijoter 40 min à feu doux",
                60, "Moyen", "Méditerranéenne", "Dîner", 1, 1, 1, 4),
            listOf("Salade niçoise", "Salade composée du sud de la France",
                "1. Cuire les pommes de terre et les oeufs\n2. Préparer la vinaigrette\n3. Disposer tous les ingrédients\n4. Assaisonner",
                25, "Facile", "Méditerranéenne", "Déjeuner", 0, 0, 1, 4),
            listOf("Paella aux légumes", "Riz espagnol coloré aux légumes",
                "1. Faire revenir oignon et ail\n2. Ajouter le riz et le paprika\n3. Incorporer bouillon et légumes\n4. Cuire 20 min sans remuer",
                45, "Difficile", "Méditerranéenne", "Dîner", 1, 1, 1, 6),

            // ── ITALIENNE ──
            listOf("Spaghetti Carbonara", "Pâtes crémeuses à l'italienne",
                "1. Cuire les pâtes al dente\n2. Faire revenir les lardons\n3. Battre les oeufs avec le parmesan\n4. Mélanger hors du feu",
                20, "Facile", "Italienne", "Dîner", 0, 0, 0, 4),
            listOf("Risotto aux champignons", "Risotto crémeux aux champignons",
                "1. Faire revenir l'oignon\n2. Ajouter le riz\n3. Incorporer le bouillon petit à petit\n4. Ajouter champignons et parmesan",
                35, "Moyen", "Italienne", "Dîner", 1, 0, 1, 4),
            listOf("Pâtes à la bolognaise", "Sauce à la viande mijotée",
                "1. Faire revenir oignon et ail\n2. Ajouter le boeuf haché\n3. Incorporer les tomates\n4. Mijoter 30 min\n5. Servir sur les pâtes",
                50, "Facile", "Italienne", "Dîner", 0, 0, 0, 4),

            // ── GRECQUE ──
            listOf("Salade grecque", "Salade fraîche à la feta",
                "1. Couper tomates, concombre et oignon\n2. Ajouter les olives et la feta\n3. Assaisonner d'huile d'olive et origan",
                10, "Facile", "Grecque", "Déjeuner", 1, 0, 1, 4),
            listOf("Moussaka", "Gratin d'aubergines à la grecque",
                "1. Faire revenir le boeuf haché avec oignon\n2. Griller les aubergines\n3. Préparer la béchamel\n4. Alterner les couches\n5. Cuire 45 min au four",
                90, "Difficile", "Grecque", "Dîner", 0, 0, 0, 6),
            listOf("Souvlaki", "Brochettes de poulet marinées",
                "1. Mariner le poulet dans huile, citron et épices\n2. Enfiler sur des brochettes\n3. Griller 15 min\n4. Servir avec pain pita",
                30, "Facile", "Grecque", "Dîner", 0, 0, 0, 4),

            // ── AMÉRICAINE ──
            listOf("Burger maison", "Burger juteux au boeuf haché",
                "1. Former les steaks\n2. Assaisonner généreusement\n3. Griller 4 min de chaque côté\n4. Assembler avec garnitures",
                20, "Facile", "Américaine", "Dîner", 0, 0, 0, 4),
            listOf("Mac and Cheese", "Pâtes crémeuses au fromage",
                "1. Cuire les pâtes\n2. Préparer la sauce fromage avec beurre, lait et fromage\n3. Mélanger et gratiner",
                25, "Facile", "Américaine", "Dîner", 1, 0, 0, 4),
            listOf("Chili con carne", "Ragoût épicé au boeuf",
                "1. Faire revenir oignon et ail\n2. Ajouter boeuf haché\n3. Incorporer tomates et haricots\n4. Assaisonner et mijoter 40 min",
                60, "Moyen", "Américaine", "Dîner", 0, 0, 1, 6),

            // ── INDIENNE ──
            listOf("Butter Chicken", "Poulet mijoté dans une sauce crémeuse aux épices",
                "1. Mariner le poulet dans yogourt et épices\n2. Griller le poulet\n3. Préparer la sauce avec beurre, tomates et crème\n4. Ajouter le poulet\n5. Mijoter 15 min",
                45, "Moyen", "Indienne", "Dîner", 0, 0, 1, 4),
            listOf("Dal aux lentilles", "Soupe épicée aux lentilles",
                "1. Faire revenir oignon, ail et épices\n2. Ajouter les lentilles et le bouillon\n3. Mijoter 25 min\n4. Garnir de coriandre",
                35, "Facile", "Indienne", "Dîner", 1, 1, 1, 6),
            listOf("Saag Paneer", "Épinards crémeux au fromage indien",
                "1. Blanchir les épinards\n2. Mixer en purée\n3. Faire revenir les épices\n4. Ajouter épinards et fromage\n5. Mijoter 10 min",
                30, "Moyen", "Indienne", "Dîner", 1, 0, 1, 4),

            // ── JAPONAISE ──
            listOf("Mapo Tofu", "Tofu épicé à la sauce soja et piment",
                "1. Faire revenir l'ail et le gingembre\n2. Ajouter le boeuf haché\n3. Incorporer la sauce soja et le piment\n4. Ajouter le tofu\n5. Épaissir et servir sur riz",
                25, "Moyen", "Japonaise", "Dîner", 0, 0, 1, 4),
            listOf("Ramen au poulet", "Soupe de nouilles japonaise",
                "1. Préparer le bouillon de poulet\n2. Cuire les nouilles\n3. Faire mariner les oeufs\n4. Assembler le bol avec garnitures",
                60, "Difficile", "Japonaise", "Dîner", 0, 0, 0, 2),
            listOf("Gyoza", "Raviolis japonais poêlés",
                "1. Mélanger porc haché et chou\n2. Farcir les feuilles de gyoza\n3. Poêler jusqu'à dorure\n4. Ajouter eau et couvrir\n5. Servir avec sauce soja",
                40, "Difficile", "Japonaise", "Collation", 0, 0, 0, 4),

            // ── MEXICAINE ──
            listOf("Tacos au boeuf", "Tacos festifs à la mexicaine",
                "1. Faire revenir le boeuf haché\n2. Assaisonner cumin et paprika\n3. Garnir les tortillas\n4. Ajouter tomate et fromage",
                25, "Facile", "Mexicaine", "Dîner", 0, 0, 0, 4),
            listOf("Guacamole", "Trempette fraîche à l'avocat",
                "1. Écraser les avocats\n2. Ajouter oignon, tomate et citron\n3. Assaisonner avec sel et piment\n4. Servir frais",
                10, "Facile", "Mexicaine", "Collation", 1, 1, 1, 6),
            listOf("Enchiladas végétariennes", "Tortillas farcies aux légumes gratinées",
                "1. Préparer la garniture de légumes\n2. Farcir les tortillas\n3. Rouler et disposer dans un plat\n4. Napper de sauce et fromage\n5. Cuire 20 min au four",
                40, "Moyen", "Mexicaine", "Dîner", 1, 0, 0, 4),

            // ── QUÉBÉCOISE ──
            listOf("Poutine", "Frites, fromage en grains et sauce",
                "1. Cuire les frites au four\n2. Préparer la sauce brune\n3. Disposer les frites\n4. Ajouter le fromage en grains\n5. Napper de sauce chaude",
                40, "Facile", "Québécoise", "Dîner", 1, 0, 0, 2),
            listOf("Soupe aux pois", "Soupe traditionnelle québécoise",
                "1. Faire revenir oignon et lardons\n2. Ajouter les pois et le bouillon\n3. Assaisonner\n4. Mijoter 1h à feu doux",
                70, "Facile", "Québécoise", "Déjeuner", 0, 0, 1, 6),
            listOf("Tourtière", "Pâté à la viande traditionnel",
                "1. Préparer la pâte brisée\n2. Faire revenir boeuf haché avec épices\n3. Remplir le moule\n4. Couvrir de pâte\n5. Cuire 45 min à 190°C",
                90, "Difficile", "Québécoise", "Dîner", 0, 0, 0, 6),

            // ── AFRICAINE ──
            listOf("Poulet yassa", "Poulet mariné à l'oignon et citron",
                "1. Mariner le poulet dans oignon et citron\n2. Griller le poulet\n3. Faire caraméliser les oignons\n4. Mijoter ensemble 20 min\n5. Servir avec riz",
                50, "Moyen", "Africaine", "Dîner", 0, 0, 1, 4),
            listOf("Tagine d'agneau", "Ragoût épicé à l'agneau et légumes",
                "1. Faire dorer l'agneau\n2. Ajouter oignon et épices\n3. Incorporer légumes et bouillon\n4. Mijoter 1h30 à feu doux",
                100, "Difficile", "Africaine", "Dîner", 0, 0, 1, 6),
            listOf("Riz jollof", "Riz épicé à la tomate",
                "1. Mixer tomates et poivrons\n2. Faire revenir oignon\n3. Ajouter la purée de tomates\n4. Incorporer le riz et bouillon\n5. Cuire 30 min à feu doux",
                45, "Moyen", "Africaine", "Dîner", 1, 1, 1, 6),
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
                put("portions",       r[10] as Int)
            }
            db.insert("recette", null, cv)
        }
    }

    private fun seederRecetteIngredients(db: SQLiteDatabase) {

        fun ingredientId(nom: String): Long {
            val cursor = db.rawQuery("SELECT id FROM ingredient WHERE nom = ?", arrayOf(nom))
            return cursor.use { if (it.moveToFirst()) it.getLong(0) else -1 }
        }

        fun recetteId(nom: String): Long {
            val cursor = db.rawQuery("SELECT id FROM recette WHERE nom = ?", arrayOf(nom))
            return cursor.use { if (it.moveToFirst()) it.getLong(0) else -1 }
        }

        fun lier(nomRecette: String, nomIngredient: String, quantite: String, uniteMesure: String) {
            val cv = ContentValues().apply {
                put("recette_id",    recetteId(nomRecette))
                put("ingredient_id", ingredientId(nomIngredient))
                put("quantite",      quantite)
                put("unite_mesure",  uniteMesure)
            }
            db.insert("recette_ingredient", null, cv)
        }

        // MOYEN-ORIENTALE
        lier("Houmous",            "Pois chiches",   "400", "g")
        lier("Houmous",            "Ail",            "2",   "gousses")
        lier("Houmous",            "Citron",         "1",   "unité")
        lier("Houmous",            "Huile d'olive",  "3",   "c. à soupe")
        lier("Shawarma au poulet", "Poulet",         "500", "g")
        lier("Shawarma au poulet", "Pain pita",      "4",   "unité")
        lier("Shawarma au poulet", "Oignon",         "1",   "unité")
        lier("Shawarma au poulet", "Cumin",          "1",   "c. à thé")
        lier("Shawarma au poulet", "Yogourt",        "100", "ml")
        lier("Taboulé",            "Couscous",       "200", "g")
        lier("Taboulé",            "Tomate",         "2",   "unité")
        lier("Taboulé",            "Oignon",         "1",   "unité")
        lier("Taboulé",            "Citron",         "1",   "unité")
        lier("Taboulé",            "Huile d'olive",  "3",   "c. à soupe")

        // MÉDITERRANÉENNE
        lier("Ratatouille",        "Aubergine",      "1",   "unité")
        lier("Ratatouille",        "Courgette",      "2",   "unité")
        lier("Ratatouille",        "Tomate",         "3",   "unité")
        lier("Ratatouille",        "Oignon",         "1",   "unité")
        lier("Ratatouille",        "Ail",            "3",   "gousses")
        lier("Ratatouille",        "Huile d'olive",  "4",   "c. à soupe")
        lier("Salade niçoise",     "Oeuf",           "3",   "unité")
        lier("Salade niçoise",     "Tomate",         "2",   "unité")
        lier("Salade niçoise",     "Pomme de terre", "200", "g")
        lier("Salade niçoise",     "Huile d'olive",  "3",   "c. à soupe")
        lier("Paella aux légumes", "Riz",            "300", "g")
        lier("Paella aux légumes", "Poivron",        "2",   "unité")
        lier("Paella aux légumes", "Tomate",         "2",   "unité")
        lier("Paella aux légumes", "Oignon",         "1",   "unité")
        lier("Paella aux légumes", "Paprika",        "1",   "c. à thé")
        lier("Paella aux légumes", "Bouillon de poulet", "500", "ml")

        // ITALIENNE
        lier("Spaghetti Carbonara",    "Pâtes",              "200", "g")
        lier("Spaghetti Carbonara",    "Lardons",            "100", "g")
        lier("Spaghetti Carbonara",    "Oeuf",               "3",   "unité")
        lier("Spaghetti Carbonara",    "Parmesan",           "50",  "g")
        lier("Spaghetti Carbonara",    "Ail",                "1",   "gousse")
        lier("Risotto aux champignons","Riz",                "300", "g")
        lier("Risotto aux champignons","Champignons",        "200", "g")
        lier("Risotto aux champignons","Oignon",             "1",   "unité")
        lier("Risotto aux champignons","Parmesan",           "50",  "g")
        lier("Risotto aux champignons","Beurre",             "30",  "g")
        lier("Risotto aux champignons","Bouillon de poulet", "500", "ml")
        lier("Pâtes à la bolognaise",  "Pâtes",             "200", "g")
        lier("Pâtes à la bolognaise",  "Boeuf haché",       "300", "g")
        lier("Pâtes à la bolognaise",  "Tomate",            "200", "g")
        lier("Pâtes à la bolognaise",  "Oignon",            "1",   "unité")
        lier("Pâtes à la bolognaise",  "Ail",               "2",   "gousses")

        // GRECQUE
        lier("Salade grecque", "Tomate",        "3",   "unité")
        lier("Salade grecque", "Concombre",     "1",   "unité")
        lier("Salade grecque", "Oignon",        "1",   "unité")
        lier("Salade grecque", "Feta",          "150", "g")
        lier("Salade grecque", "Huile d'olive", "3",   "c. à soupe")
        lier("Moussaka",       "Boeuf haché",   "400", "g")
        lier("Moussaka",       "Aubergine",     "2",   "unité")
        lier("Moussaka",       "Oignon",        "1",   "unité")
        lier("Moussaka",       "Tomate",        "200", "g")
        lier("Moussaka",       "Lait",          "300", "ml")
        lier("Moussaka",       "Oeuf",          "2",   "unité")
        lier("Souvlaki",       "Poulet",        "500", "g")
        lier("Souvlaki",       "Pain pita",     "4",   "unité")
        lier("Souvlaki",       "Citron",        "1",   "unité")
        lier("Souvlaki",       "Huile d'olive", "3",   "c. à soupe")
        lier("Souvlaki",       "Yogourt",       "150", "ml")

        // AMÉRICAINE
        lier("Burger maison",  "Boeuf haché", "400", "g")
        lier("Burger maison",  "Oignon",      "1",   "unité")
        lier("Burger maison",  "Fromage",     "4",   "tranches")
        lier("Burger maison",  "Tomate",      "1",   "unité")
        lier("Mac and Cheese", "Pâtes",       "300", "g")
        lier("Mac and Cheese", "Fromage",     "200", "g")
        lier("Mac and Cheese", "Lait",        "200", "ml")
        lier("Mac and Cheese", "Beurre",      "30",  "g")
        lier("Chili con carne","Boeuf haché", "400", "g")
        lier("Chili con carne","Tomate",      "300", "g")
        lier("Chili con carne","Haricots noirs","200","g")
        lier("Chili con carne","Oignon",      "1",   "unité")
        lier("Chili con carne","Ail",         "2",   "gousses")
        lier("Chili con carne","Cumin",       "1",   "c. à thé")

        // INDIENNE
        lier("Butter Chicken",    "Poulet",             "500", "g")
        lier("Butter Chicken",    "Tomate",             "200", "g")
        lier("Butter Chicken",    "Crème fraîche",      "100", "ml")
        lier("Butter Chicken",    "Beurre",             "30",  "g")
        lier("Butter Chicken",    "Ail",                "3",   "gousses")
        lier("Butter Chicken",    "Oignon",             "1",   "unité")
        lier("Butter Chicken",    "Garam masala",       "2",   "c. à thé")
        lier("Butter Chicken",    "Yogourt",            "100", "ml")
        lier("Dal aux lentilles", "Lentilles",          "300", "g")
        lier("Dal aux lentilles", "Oignon",             "1",   "unité")
        lier("Dal aux lentilles", "Ail",                "2",   "gousses")
        lier("Dal aux lentilles", "Tomate",             "2",   "unité")
        lier("Dal aux lentilles", "Curcuma",            "1",   "c. à thé")
        lier("Dal aux lentilles", "Cumin",              "1",   "c. à thé")
        lier("Dal aux lentilles", "Bouillon de poulet", "500", "ml")
        lier("Saag Paneer",       "Épinards",           "400", "g")
        lier("Saag Paneer",       "Fromage",            "200", "g")
        lier("Saag Paneer",       "Oignon",             "1",   "unité")
        lier("Saag Paneer",       "Ail",                "2",   "gousses")
        lier("Saag Paneer",       "Crème fraîche",      "100", "ml")
        lier("Saag Paneer",       "Garam masala",       "1",   "c. à thé")

        // JAPONAISE
        lier("Mapo Tofu",       "Tofu",               "400", "g")
        lier("Mapo Tofu",       "Boeuf haché",        "150", "g")
        lier("Mapo Tofu",       "Sauce soja",         "2",   "c. à soupe")
        lier("Mapo Tofu",       "Piment",             "1",   "c. à thé")
        lier("Mapo Tofu",       "Ail",                "2",   "gousses")
        lier("Mapo Tofu",       "Huile de sésame",    "1",   "c. à soupe")
        lier("Mapo Tofu",       "Riz",                "200", "g")
        lier("Ramen au poulet", "Poulet",             "300", "g")
        lier("Ramen au poulet", "Oeuf",               "2",   "unité")
        lier("Ramen au poulet", "Bouillon de poulet", "1",   "L")
        lier("Ramen au poulet", "Sauce soja",         "3",   "c. à soupe")
        lier("Ramen au poulet", "Oignon",             "1",   "unité")
        lier("Gyoza",           "Boeuf haché",        "200", "g")
        lier("Gyoza",           "Sauce soja",         "2",   "c. à soupe")
        lier("Gyoza",           "Huile de sésame",    "1",   "c. à soupe")
        lier("Gyoza",           "Ail",                "2",   "gousses")

        // MEXICAINE
        lier("Tacos au boeuf",           "Boeuf haché", "300", "g")
        lier("Tacos au boeuf",           "Tortilla",    "4",   "unité")
        lier("Tacos au boeuf",           "Tomate",      "2",   "unité")
        lier("Tacos au boeuf",           "Fromage",     "100", "g")
        lier("Tacos au boeuf",           "Oignon",      "1",   "unité")
        lier("Tacos au boeuf",           "Cumin",       "1",   "c. à thé")
        lier("Guacamole",                "Avocat",      "2",   "unité")
        lier("Guacamole",                "Tomate",      "1",   "unité")
        lier("Guacamole",                "Oignon",      "1",   "unité")
        lier("Guacamole",                "Citron",      "1",   "unité")
        lier("Guacamole",                "Piment",      "1",   "pincée")
        lier("Enchiladas végétariennes", "Tortilla",    "6",   "unité")
        lier("Enchiladas végétariennes", "Fromage",     "150", "g")
        lier("Enchiladas végétariennes", "Tomate",      "2",   "unité")
        lier("Enchiladas végétariennes", "Oignon",      "1",   "unité")
        lier("Enchiladas végétariennes", "Poivron",     "1",   "unité")

        // QUÉBÉCOISE
        lier("Poutine",       "Fromage",            "200", "g")
        lier("Poutine",       "Pomme de terre",     "500", "g")
        lier("Poutine",       "Bouillon de poulet", "300", "ml")
        lier("Soupe aux pois","Lardons",            "150", "g")
        lier("Soupe aux pois","Oignon",             "1",   "unité")
        lier("Soupe aux pois","Carotte",            "2",   "unité")
        lier("Soupe aux pois","Céleri",             "2",   "branches")
        lier("Soupe aux pois","Bouillon de poulet", "1",   "L")
        lier("Tourtière",     "Boeuf haché",        "500", "g")
        lier("Tourtière",     "Oignon",             "1",   "unité")
        lier("Tourtière",     "Pomme de terre",     "200", "g")
        lier("Tourtière",     "Cannelle",           "1",   "pincée")
        lier("Tourtière",     "Beurre",             "50",  "g")

        // AFRICAINE
        lier("Poulet yassa",    "Poulet",            "600", "g")
        lier("Poulet yassa",    "Oignon",            "3",   "unité")
        lier("Poulet yassa",    "Citron",            "2",   "unité")
        lier("Poulet yassa",    "Moutarde",          "2",   "c. à soupe")
        lier("Poulet yassa",    "Huile d'olive",     "3",   "c. à soupe")
        lier("Poulet yassa",    "Riz",               "300", "g")
        lier("Tagine d'agneau", "Agneau",            "600", "g")
        lier("Tagine d'agneau", "Oignon",            "2",   "unité")
        lier("Tagine d'agneau", "Carotte",           "3",   "unité")
        lier("Tagine d'agneau", "Tomate",            "200", "g")
        lier("Tagine d'agneau", "Cumin",             "1",   "c. à thé")
        lier("Tagine d'agneau", "Cannelle",          "1",   "pincée")
        lier("Riz jollof",      "Riz",               "300", "g")
        lier("Riz jollof",      "Tomate",            "300", "g")
        lier("Riz jollof",      "Poivron",           "1",   "unité")
        lier("Riz jollof",      "Oignon",            "1",   "unité")
        lier("Riz jollof",      "Bouillon de poulet","500", "ml")
        lier("Riz jollof",      "Paprika",           "1",   "c. à thé")
    }
}