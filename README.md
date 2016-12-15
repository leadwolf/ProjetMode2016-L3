
# Current Changes
## Merging BDD and Model to same JFrame

### Changes
This commit introduces the follwing changes :
  - Both bdd and ply MVCs are in the "ply" package under `ply.bdd` and `ply.plyModel`.  
  - The executing classes are in `ply.main`.  
  - Changed **Fenetre** to **ModelPanel** to separate the view of the model.
  - Created a new class **MainFenetre** to present both **ModelPanel** and **BDDPanel** in `ply.vues`.
  - 
  
### What's next
For now, Modelisationator executes either **ply.main.ModelVisualizer** or **ply.main.BaseDeDonnes** corresponding to which command the user executes.  
Here is what needs to be done/decided :
  - **ply.main.ModelVisualizer** already implements **MainFenetre** so all we need to do is change **ply.main.BaseDeDonnes** to launch it as well.
  - We also need to decide whether **MainFenetre** will display both **ModelPanel** and **BDDPanel** at the same time or alternate the right side between these two and then a simple model navigator on the left.

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
