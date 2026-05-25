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

        private val IMAGE_PAR_RECETTE = mapOf(
            "Houmous" to "images/houmous",
            "Shawarma au poulet" to "images/shawarma",
            "Taboulé" to "images/taboulé",
            "Ratatouille" to "images/ratatoille",
            "Salade niçoise" to "images/salade_nicoice",
            "Paella aux légumes" to "images/paella_aux_legumes",
            "Spaghetti Carbonara" to "images/spaghetti_carbonara",
            "Risotto aux champignons" to "images/risotto_aux_champignon",
            "Pâtes à la bolognaise" to "images/pates_a_la_bolognaise",
            "Salade grecque" to "images/salade_grecque",
            "Moussaka" to "images/moussaka",
            "Souvlaki" to "images/souvlaki",
            "Burger maison" to "images/burger_maison",
            "Mac and cheese" to "images/mac_n_cheese",
            "Chili con carne" to "images/chilli_con_carne",
            "Butter Chicken" to "images/butter_chicken",
            "Dal aux lentilles" to "images/dal_aux_lentilles",
            "Saag Paneer" to "images/saag_paneer",
            "Mapo Tofu" to "images/mapo_tofu",
            "Ramen au poulet" to "images/ramen_au_poulet",
            "Gyoza" to "images/gyoza",
            "Tacos au boeuf" to "images/tacos_au_boeuf",
            "Guacamole" to "images/guacamole",
            "Enchiladas végétariennes" to "images/enchilidas_vegetariennes",
            "Poutine" to "images/poutine",
            "Soupe aux pois" to "images/soupe_aux_pois",
            "Tourtière" to "images/tourtiere",
            "Poulet yassa" to "images/poulet_yassa",
            "Tagine d'agneau" to "images/tagine_agneau",
            "Riz jollof" to "images/riz_jollof",
        )
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
                "1. Commencez par égoutter et rincer abondamment les pois chiches.\n2. Dans le bol d'un robot culinaire, mixez-les avec le tahini, les gousses d'ail hachées et le jus de citron fraîchement pressé.\n3. Incorporez progressivement l'huile d'olive en filet jusqu'à l'obtention d'une texture lisse et crémeuse.\n4. Assaisonnez de sel et de poivre à votre goût, puis servez frais.",
                15, "Facile", "Moyen-Orientale", "Collation", 1, 1, 1, 6),
            listOf("Shawarma au poulet", "Poulet mariné aux épices du Moyen-Orient",
                "1. Dans un grand bol, mélangez les épices et enrobez généreusement les morceaux de poulet pour les faire mariner.\n2. Saisissez la viande sur un gril ou une poêle à feu vif pour bien la dorer.\n3. Laissez reposer quelques instants pour détendre la viande, puis tranchez le poulet en fines lanières.\n4. Servez chaud à l'intérieur d'un pain pita garni de légumes frais et de sauce.",
                40, "Moyen", "Moyen-Orientale", "Dîner", 0, 0, 0, 4),
            listOf("Taboulé", "Salade fraîche au persil et couscous",
                "1. Préparez le couscous selon les indications de l'emballage et laissez-le refroidir.\n2. Hachez finement le persil frais, les tomates épépinées et l'oignon.\n3. Dans un grand saladier, combinez le couscous et les légumes, puis arrosez d'un généreux filet d'huile d'olive et de jus de citron.\n4. Couvrez et laissez reposer au réfrigérateur pendant au moins 30 minutes pour marier les saveurs.",
                20, "Facile", "Moyen-Orientale", "Déjeuner", 1, 1, 1, 6),

            // ── MÉDITERRANÉENNE ──
            listOf("Ratatouille", "Mijotée de légumes provençaux",
                "1. Lavez et taillez tous les légumes en dés de taille régulière.\n2. Dans une grande cocotte, faites suer l'oignon émincé et l'ail écrasé avec un filet d'huile d'olive.\n3. Ajoutez les légumes un par un, en commençant par les plus fermes (aubergines, poivrons) puis les courgettes et les tomates.\n4. Couvrez et laissez mijoter doucement pendant environ 40 minutes à feu doux, jusqu'à ce que les légumes soient tendres.",
                60, "Moyen", "Méditerranéenne", "Dîner", 1, 1, 1, 4),
            listOf("Salade niçoise", "Salade composée du sud de la France",
                "1. Plongez les pommes de terre dans l'eau bouillante salée jusqu'à tendreté, et cuisez les œufs pour qu'ils soient durs.\n2. Préparez une vinaigrette classique avec de l'huile d'olive, du vinaigre, du sel et du poivre.\n3. Dans un grand plat de présentation, disposez harmonieusement tous les ingrédients préparés.\n4. Nappez généreusement de vinaigrette juste avant de servir.",
                25, "Facile", "Méditerranéenne", "Déjeuner", 0, 0, 1, 4),
            listOf("Paella aux légumes", "Riz espagnol coloré aux légumes",
                "1. Dans une grande poêle à paella, faites revenir doucement l'oignon haché et l'ail.\n2. Ajoutez le riz sec et saupoudrez de paprika, en remuant pour nacrer les grains.\n3. Versez le bouillon bien chaud et incorporez délicatement les légumes coupés.\n4. Laissez cuire à découvert et sans remuer pendant environ 20 minutes, jusqu'à l'absorption complète du liquide.",
                45, "Difficile", "Méditerranéenne", "Dîner", 1, 1, 1, 6),

            // ── ITALIENNE ──
            listOf("Spaghetti Carbonara", "Pâtes crémeuses à l'italienne",
                "1. Plongez les pâtes dans une grande marmite d'eau bouillante salée et cuisez-les al dente.\n2. Pendant ce temps, faites dorer les lardons dans une poêle sans ajout de matière grasse jusqu'à ce qu'ils soient croustillants.\n3. Dans un bol, fouettez vigoureusement les œufs entiers avec le parmesan fraîchement râpé et beaucoup de poivre noir.\n4. Égouttez les pâtes et mélangez-les immédiatement aux lardons, puis, hors du feu, incorporez le mélange œufs-fromage pour créer une sauce soyeuse.",
                20, "Facile", "Italienne", "Dîner", 0, 0, 0, 4),
            listOf("Risotto aux champignons", "Risotto crémeux aux champignons",
                "1. Dans une casserole, faites suer l'oignon finement ciselé dans un peu de beurre.\n2. Ajoutez le riz arborio et remuez pendant une minute jusqu'à ce que les grains deviennent translucides.\n3. Incorporez le bouillon chaud louche par louche, en attendant que le liquide soit absorbé entre chaque ajout.\n4. En fin de cuisson, ajoutez les champignons préalablement poêlés et liez le tout avec une généreuse portion de parmesan.",
                35, "Moyen", "Italienne", "Dîner", 1, 0, 1, 4),
            listOf("Pâtes à la bolognaise", "Sauce à la viande mijotée",
                "1. Faites revenir l'oignon et l'ail finement hachés dans un filet d'huile d'olive.\n2. Ajoutez le bœuf haché et faites-le brunir en l'égrenant à l'aide d'une spatule en bois.\n3. Versez les tomates concassées, baissez le feu et couvrez.\n4. Laissez mijoter doucement pendant au moins 30 minutes pour développer les arômes.\n5. Nappez vos pâtes chaudes de cette sauce onctueuse.",
                50, "Facile", "Italienne", "Dîner", 0, 0, 0, 4),

            // ── GRECQUE ──
            listOf("Salade grecque", "Salade fraîche à la feta",
                "1. Coupez les tomates en quartiers, le concombre en demi-lunes et émincez finement l'oignon rouge.\n2. Transférez le tout dans un grand saladier, ajoutez les olives Kalamata et déposez de généreux morceaux de fromage feta sur le dessus.\n3. Arrosez d'une excellente huile d'olive extra vierge et saupoudrez d'origan séché avant de servir.",
                10, "Facile", "Grecque", "Déjeuner", 1, 0, 1, 4),
            listOf("Moussaka", "Gratin d'aubergines à la grecque",
                "1. Dans une sauteuse, faites dorer le bœuf haché avec l'oignon émincé, puis assaisonnez à votre goût.\n2. Tranchez les aubergines, badigeonnez-les d'huile et faites-les griller jusqu'à ce qu'elles soient tendres.\n3. Préparez une sauce béchamel onctueuse parfumée à la noix de muscade.\n4. Dans un plat à gratin, alternez les couches d'aubergines et de préparation à la viande.\n5. Nappez généreusement de béchamel et enfournez à 180°C pendant 45 minutes pour obtenir une belle croûte dorée.",
                90, "Difficile", "Grecque", "Dîner", 0, 0, 0, 6),
            listOf("Souvlaki", "Brochettes de poulet marinées",
                "1. Coupez le poulet en cubes et plongez-les dans une marinade d'huile d'olive, de jus de citron, d'ail et d'herbes séchées pendant au moins 1 heure.\n2. Enfilez les morceaux de viande marinés sur des piques en bois.\n3. Faites griller les brochettes sur un barbecue ou une poêle striée pendant 15 minutes en les retournant régulièrement.\n4. Servez chaud, accompagné de pains pitas moelleux et de sauce tzatziki.",
                30, "Facile", "Grecque", "Dîner", 0, 0, 0, 4),

            // ── AMÉRICAINE ──
            listOf("Burger maison", "Burger juteux au boeuf haché",
                "1. Façonnez de beaux disques avec la viande de bœuf hachée, sans trop presser pour conserver la tendreté.\n2. Assaisonnez très généreusement les deux faces avec du sel fin et du poivre du moulin.\n3. Faites saisir les galettes sur une poêle bien chaude ou un gril, environ 4 minutes de chaque côté pour une cuisson à point.\n4. Assemblez vos burgers dans des pains briochés toastés avec vos garnitures et sauces préférées.",
                20, "Facile", "Américaine", "Dîner", 0, 0, 0, 4),
            listOf("Mac and Cheese", "Pâtes crémeuses au fromage",
                "1. Cuisez les macaronis dans une grande quantité d'eau bouillante salée, puis égouttez-les.\n2. Réalisez une sauce au fromage riche en faisant fondre le beurre, en ajoutant de la farine, du lait chaud et un mélange de vos fromages râpés préférés.\n3. Incorporez délicatement les pâtes à cette sauce onctueuse, transférez dans un plat et passez sous le gril du four pour faire gratiner le dessus.",
                25, "Facile", "Américaine", "Dîner", 1, 0, 0, 4),
            listOf("Chili con carne", "Ragoût épicé au boeuf",
                "1. Dans une grande cocotte, faites revenir doucement l'oignon et l'ail hachés finement.\n2. Incorporez le bœuf haché et faites-le dorer en remuant fréquemment.\n3. Ajoutez les tomates concassées, les épices à chili et les haricots rouges préalablement rincés.\n4. Rectifiez l'assaisonnement, couvrez et laissez mijoter à feu doux pendant environ 40 minutes pour que les arômes s'entremêlent.",
                60, "Moyen", "Américaine", "Dîner", 0, 0, 1, 6),

            // ── INDIENNE ──
            listOf("Butter Chicken", "Poulet mijoté dans une sauce crémeuse aux épices",
                "1. Faites mariner les morceaux de poulet dans un mélange de yogourt nature et d'épices garam masala pendant au moins deux heures.\n2. Saisissez le poulet à feu vif jusqu'à ce qu'il soit bien coloré.\n3. Dans la même poêle, préparez une sauce riche avec du beurre, de la purée de tomates et de la crème épaisse.\n4. Remettez le poulet dans la sauce parfumée.\n5. Laissez mijoter doucement pendant 15 minutes et servez avec du pain naan.",
                45, "Moyen", "Indienne", "Dîner", 0, 0, 1, 4),
            listOf("Dal aux lentilles", "Soupe épicée aux lentilles",
                "1. Dans une casserole, faites rissoler l'oignon, l'ail et un mélange d'épices pour libérer leurs arômes.\n2. Ajoutez les lentilles bien rincées et mouillez avec le bouillon de légumes.\n3. Portez à ébullition, puis baissez le feu et laissez mijoter à couvert pendant 25 minutes.\n4. Servez chaud dans des bols, copieusement garni de feuilles de coriandre fraîche.",
                35, "Facile", "Indienne", "Dîner", 1, 1, 1, 6),
            listOf("Saag Paneer", "Épinards crémeux au fromage indien",
                "1. Plongez les feuilles d'épinards dans l'eau bouillante quelques instants, puis égouttez-les et essorez-les bien.\n2. Mixez grossièrement les épinards pour obtenir une purée texturée.\n3. Dans une grande poêle, faites torréfier les épices indiennes dans un peu de matière grasse.\n4. Ajoutez la purée d'épinards et les cubes de fromage paneer délicatement dorés.\n5. Laissez mijoter environ 10 minutes pour bien amalgamer les saveurs.",
                30, "Moyen", "Indienne", "Dîner", 1, 0, 1, 4),

            // ── JAPONAISE ──
            listOf("Mapo Tofu", "Tofu épicé à la sauce soja et piment",
                "1. Dans un wok bien chaud, faites revenir l'ail finement émincé et le gingembre frais râpé.\n2. Ajoutez la viande de bœuf hachée et faites-la sauter rapidement à feu vif.\n3. Déglacez avec la sauce soja et incorporez de la pâte de piment selon votre tolérance à la chaleur.\n4. Ajoutez délicatement les cubes de tofu soyeux pour ne pas les briser.\n5. Laissez épaissir la sauce quelques instants et servez immédiatement sur un bol de riz blanc fumant.",
                25, "Moyen", "Japonaise", "Dîner", 0, 0, 1, 4),
            listOf("Ramen au poulet", "Soupe de nouilles japonaise",
                "1. Préparez un bouillon de poulet savoureux en le faisant mijoter longuement avec des aromates japonais (kombu, gingembre).\n2. Dans une casserole d'eau bouillante, cuisez les nouilles à ramen selon les indications de l'emballage, puis égouttez-les.\n3. Préparez des œufs mollets et faites-les mariner dans un mélange de sauce soja et de mirin.\n4. Dans de grands bols, répartissez les nouilles, versez le bouillon bien chaud, puis disposez harmonieusement le poulet, l'œuf coupé en deux et les garnitures.",
                60, "Difficile", "Japonaise", "Dîner", 0, 0, 0, 2),
            listOf("Gyoza", "Raviolis japonais poêlés",
                "1. Préparez la farce en mélangeant intimement la viande de porc hachée, du chou finement émincé, de l'ail et du gingembre.\n2. Déposez une petite cuillère de farce au centre de chaque feuille de gyoza, mouillez les bords et scellez-les en formant des plis réguliers.\n3. Dans une poêle huilée, faites dorer le dessous des raviolis à feu moyen.\n4. Versez un fond d'eau, couvrez immédiatement et laissez cuire à la vapeur jusqu'à évaporation complète.\n5. Servez bien chaud avec une sauce trempette à base de sauce soja et de vinaigre de riz.",
                40, "Difficile", "Japonaise", "Collation", 0, 0, 0, 4),

            // ── MEXICAINE ──
            listOf("Tacos au boeuf", "Tacos festifs à la mexicaine",
                "1. Faites revenir le bœuf haché dans une poêle jusqu'à ce qu'il soit bien cuit et légèrement croustillant.\n2. Saupoudrez généreusement de cumin en poudre, de paprika et d'une pincée de piment, puis mélangez bien.\n3. Réchauffez doucement les tortillas de maïs ou de blé pour les assouplir.\n4. Garnissez chaque tortilla de viande épicée, puis ajoutez des dés de tomates fraîches, de la laitue croquante et du fromage râpé.",
                25, "Facile", "Mexicaine", "Dîner", 0, 0, 0, 4),
            listOf("Guacamole", "Trempette fraîche à l'avocat",
                "1. Coupez les avocats mûrs en deux, retirez le noyau et écrasez grossièrement la chair à la fourchette dans un grand bol.\n2. Incorporez l'oignon rouge finement ciselé, des dés de tomates épépinées et un généreux filet de jus de citron vert.\n3. Assaisonnez avec du sel marin, du poivre et un peu de piment frais haché selon vos goûts.\n4. Mélangez doucement et servez immédiatement avec des croustilles de maïs.",
                10, "Facile", "Mexicaine", "Collation", 1, 1, 1, 6),
            listOf("Enchiladas végétariennes", "Tortillas farcies aux légumes gratinées",
                "1. Dans une sauteuse, préparez une garniture savoureuse en faisant revenir divers légumes de saison coupés en dés.\n2. Garnissez généreusement le centre de chaque tortilla avec cette préparation.\n3. Roulez les tortillas serrées et disposez-les côte à côte dans un plat à gratin légèrement huilé.\n4. Nappez le tout d'une sauce enchilada, puis parsemez de fromage fondant.\n5. Enfournez à 190°C pendant une vingtaine de minutes, jusqu'à ce que le fromage soit doré et bouillonnant.",
                40, "Moyen", "Mexicaine", "Dîner", 1, 0, 0, 4),

            // ── QUÉBÉCOISE ──
            listOf("Poutine", "Frites, fromage en grains et sauce",
                "1. Préchauffez votre four ou friteuse et faites cuire les frites jusqu'à ce qu'elles soient parfaitement dorées et croustillantes.\n2. Pendant ce temps, préparez une sauce brune onctueuse et bien chaude (sauce à poutine).\n3. Dans un bol large ou une assiette creuse, répartissez généreusement les frites chaudes.\n4. Parsemez le tout de véritables grains de fromage frais du jour (fromage en grains).\n5. Nappez immédiatement de sauce bouillante pour commencer à faire fondre doucement le fromage.",
                40, "Facile", "Québécoise", "Dîner", 1, 0, 0, 2),
            listOf("Soupe aux pois", "Soupe traditionnelle québécoise",
                "1. Dans une grande marmite, faites fondre l'oignon haché et des lardons salés jusqu'à ce qu'ils colorent légèrement.\n2. Incorporez les pois jaunes (préalablement trempés) et mouillez abondamment avec un bon bouillon.\n3. Assaisonnez de poivre, de sarriette et d'une feuille de laurier.\n4. Laissez mijoter très doucement à couvert pendant environ 1 heure à 1 heure 30, jusqu'à ce que les pois soient fondants.",
                70, "Facile", "Québécoise", "Déjeuner", 0, 0, 1, 6),
            listOf("Tourtière", "Pâté à la viande traditionnel",
                "1. Préparez votre pâte brisée maison, abaissez-la et foncez un moule à tarte profond.\n2. Dans une grande poêle, faites brunir doucement le porc et le bœuf hachés avec des oignons, puis assaisonnez d'épices traditionnelles (cannelle, clou de girofle).\n3. Transférez cette garniture parfumée dans le fond de tarte préparé.\n4. Recouvrez avec un second abaisse de pâte, scellez bien les bords et incisez le centre pour laisser échapper la vapeur.\n5. Enfournez à 190°C pendant 45 minutes, jusqu'à ce que la croûte soit bien dorée.",
                90, "Difficile", "Québécoise", "Dîner", 0, 0, 0, 6),

            // ── AFRICAINE ──
            listOf("Poulet yassa", "Poulet mariné à l'oignon et citron",
                "1. La veille ou quelques heures avant, faites mariner les morceaux de poulet dans un généreux mélange d'oignons émincés, de jus de citron, d'ail et de moutarde.\n2. Retirez le poulet de la marinade et faites-le griller sur toutes ses faces pour bien le colorer.\n3. Dans une marmite, faites doucement caraméliser les oignons de la marinade.\n4. Remettez le poulet avec les oignons, ajoutez un petit fond d'eau et laissez mijoter à couvert pendant 20 à 30 minutes.\n5. Servez ce plat réconfortant très chaud, accompagné d'un généreux dôme de riz blanc.",
                50, "Moyen", "Africaine", "Dîner", 0, 0, 1, 4),
            listOf("Tagine d'agneau", "Ragoût épicé à l'agneau et légumes",
                "1. Dans un plat à tajine ou une cocotte en fonte, faites dorer les morceaux d'agneau sur toutes les faces avec un peu d'huile d'olive.\n2. Ajoutez les oignons émincés, l'ail écrasé et vos épices pour les faire torréfier légèrement.\n3. Incorporez des légumes de saison coupés en gros morceaux et mouillez avec du bouillon chaud.\n4. Couvrez hermétiquement et laissez mijoter à feu très doux pendant environ 1h30, jusqu'à ce que la viande soit confite et se détache à la cuillère.",
                100, "Difficile", "Africaine", "Dîner", 0, 0, 1, 6),
            listOf("Riz jollof", "Riz épicé à la tomate",
                "1. Dans un blender, mixez les tomates fraîches, les poivrons rouges et l'oignon pour obtenir une purée lisse.\n2. Dans une grande cocotte, faites revenir de l'oignon haché finement dans un peu d'huile jusqu'à translucidité.\n3. Versez la purée de tomates et laissez réduire à feu moyen jusqu'à ce que la sauce épaississe.\n4. Incorporez le riz rincé, le bouillon et mélangez bien pour enrober les grains.\n5. Couvrez hermétiquement et laissez cuire à feu très doux pendant environ 30 minutes sans soulever le couvercle.",
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