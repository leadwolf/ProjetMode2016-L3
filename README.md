# Current Big Changes
## Merging BDD and Model to same JFrame

### Recent Changes list
These are the most recent changes continuously updated until a reset :
  - Changed the database to incorporate number of points and number of faces for all figures and set the name as primary key.
  - You can now use options `--r` and `--f` to reset and fill the DB. They can be used together as in `--rf` but not with a normal command. For example `--rall` will not work. But you can use them even when using a 3D command so you can display a model and update the DB at the same time.
  - All commands can now be launched from the same program and they are parsed by the same method in `Modelisationator`.
  - Changed the whole behaviour of `MainFenetre`:
    - You can now open new models by double clicking a model in `ModelBrowser`.
    - You can now quit a tab (except the DB).
     - All opened models are kept in memory. Now when the user quits a model, it can be reopened without re-reading the .ply file This will also save its 3D positioning.
     - Created a tool tip `toolLabel` at the bottom of MainFenetre to update according to the user's actions.
    - `ModelInfo` now updates accordingly when switching tab or when opening a new tab.
  - `JTableBDDNew` will replace the operations in `BDDPanel`, can be used for all displays and all DB operations will be available directly through `ButtonColumn`.
  - `BDDPanelNew` replaces `BDDPanel` and will now just be a simple panel instead of doing the SQL queries.
  - `JTableBDDNew` renamed to `Table` and `BDDPanelNew` to `BDDPanel`.
  - `Table` uses `ButtonColumn`:
    - **confirm edit/insert**, **reset** and **delete** buttons for every row.
    - `toolLabel` updates accoring to these button actions.
  - `BDDPanel` has a button to add a new row to the table. You can insert it into the DB using the **confirm edit/insert** button.
  - Refactored `TableDataModel` : 
    - Use ArrayList instead of static arrays to be able to easily add and delete rows.
    - The methods corresponding to the **confirm edit/insert**, **reset** and **delete** buttons have been redone and seem to work properly (need to write tests).
  - Refactored `BaseDeDonneesNew` :
    - Removed useless code in the command methods.
    - Refactored `verifArgs()` to verify the whole String[] for **all** of the commands.
    - Now uses `Table` for **all** commands.
    - Adding new line works properly, prevent insertion of `""` value for a model name.
    - ToolTip updates more often for `ButtonColumn` actions.
  - If launching a DB command, the syntax is verified right away in `Modelisationanator` instead of waiting to create `MainFenetre`.
  - Comprehensive tests for `BDDPanel` and `Table` with verifications done in the DB.
  
### What's next
Priority objectives :
  - Display shadows.
  - Write tests for all `BaseDeDonnesNew` and the SQL query methods in `Table`.

Non-Priority objectives :
  - *Finally delete `FenetreTable` and `BaseDeDonnesOld` ?*
  - Update `toolLabel` according to state changes.
  - Try to use more patterns for cleaner code.
  - Try to refactor using more MVC ?
  - Other ?

# Modelisationator

Modelisationanator est un programme permettant de visualiser des modèles .ply (ASCII uniquement) ainsi que de gérer une base de données décrivant ces modèles. A l'instant, elle est séparé en deux parties, dont deux programmes distincts.

Les fonctionnalités de la partie visualisation sont les suivantes :
  - Faire des rotations autour du centre du modèle.
  - Translater le modèle sur son écran.
  - Zoomer ou dézoomer sur le modèle.
  - Centrer le modèle sur son écran et la disposer de manière que tout le modèle soit visible

Les fonctionnalités de la partie base de données sont les suivantes :
  - `--name <nom_modele>` : Afficher les données sur le modèle précisé.
  - `--all` : Afficher tout le contenu de la base (nom du modèle, emplacement, date, mot clés).
  - `--find <list mots cles>` : Afficher les données sur les modèles ayant au moins un de ces mots dans leur description
  - `--add` : Ajouter un modèle dans la base.
  - `--delete <nom_modele>` : supprimmer le modèle précisé de la base
  - `--edit <nom_modele>` : Afficher les données sur le modèle avec la possibilité de les éditer.

### Utilisation

Dans le dossier livrable *livrable* voulu, utiliser soit `L3-livrable-3D.jar` pour la partie visualisation 3D, soit `L3-livrable-BDD.jar` pour la partie base de données.
Ceux-cis se lancent avec `java -jar programme.jar`

#### Précisions sur L3-*livrable*-3D.jar

Il faut lancer `L3-*livrable*-3D.jar` avec un fichier .ply en paramètre comme la suivante  
`java -jar L3-*livrable*-3D.jar fichier.ply`
Le programme affiche par défaut uniquement les faces avec une lumière directionnelle. A savoir qu'il est possible de changer la mode d'affichage par la suite.

Les paramètres pour ce programme sont les suivants :
  - `-s` : afficher les segments du modèle.
  - `-f` afficher les faces du modèle.
