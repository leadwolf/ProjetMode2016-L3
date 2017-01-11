Bienvenue à Modelesationator!
===================

Modelesationator est un programme permettant de visualiser des modèles **.ply**[^.ply] (ASCII uniquement) ainsi que de gérer une base de données décrivant ces modèles.


----------


# Présentation de Modelesationator
## Comment lancer Modelesationator
Modelesationator doit s'ouvrir avec une commande. Elle peut être l'un des suivants

 - Une commande lancant directement la vue sur la base de données (avec options).
 - Une commande lançant la visualisation du modèle en paramètre (avec options).

Les utilisations de chacun sont détaillées par la suite : 

### Base de données
#### Commandes
Une commande de la base de données peut être l'un des suivants :
 - `--name <nom de modele>` : Affiche uniquement les données sur *nom de modele*.
 -  `--all` : Affiche tout le contenu de la base (nom du modèle, chemin absolu...).
 - `--find <liste mots cles>` : Affiche les données sur les modèles ayant au moins une partie d'un de ces mots dans leur description.
 - `--add` : Formulaire d'ajout d'un modèle et ses informations dans la base.
 - `--delete <nom modele>` : Supprimme *nom modele* de la base
 - `--edit <nom_modele>` : Affiche les données sur le modèle avec la possibilité de les éditer... *(commande obsolète puiqu'on peut toujours éditer)*.
> **Note:**
> Toutes ces commandes sauf `--delete`lancent la même type de fenêtre : une table présentant pour chaque ligne une ligne de la base de données. Cette table est modifiable à tout moment et on peut sauvegarder les changements dans la base avec les boutons associés à l'insertion/mise à jour.

#### Options
Il n'y a qu'une option pour la base de données. Vous pouvez la lancer avec différentes méthodes :

 - `--rf`
 - `--reset --fill`
 - `--r --fill`
 - ...
 - et toute autre combinaison

Cette option drop la table, la recrée et la remplit avec les modeles dans le dossier data/ dans la même racine que le **.jar**.
 >**Attention!**
 > Vous devez obligatoirement préciser `--reset` accompagné de `--fill`, de même avec leurs raccourcis.  
 


----------


### Visualisation de modèle
#### Commandes
Il suffit de préciser un chemin *(relatif ou absolu)* vers un fichier **.ply**.
> **Exemple:**
> `java -jar programme.jar data/galleon.ply`
> Notez que le lancement de la **.jar** peut varier en fonction de votre système d'exploitation.
#### Options
Vous pouvez lancer la visualisation en choisissant directement le mode d'affichage avec les options suivantes

 - `-f` : affiche les faces du modèle.
 - `-p` : affiche les points du modèle.
 - `-s` : affiche les segments du modèle.
 
> **Note:**
> Ces options ne sont pas exclusifs.
> Le programme se lance par défaut avec uniquement `-f`.


----------


### Autres précisions de lancement
On ne peut lancer une commande de base de données en même temps qu'une commande lançant la visualisation du modèle. Vous devez forcément choisir l'un ou l'autre.
>**Attention!**
>Il y a une exception à la règle : l'option `--rf` peut être spécifié à tout moment car la fenêtre principale propose de changer de modèles à tout moment. Or, ces modèles suivent le chemin décrit dans la base de données. On pourrait donc visualiser ses modèles en s'assurant que le ModelBrowser est correcte.

---
## Comment utiliser le Modelisationator
### Fenêtre principale
![Main window](https://git-iut.univ-lille1.fr/cantac/ProjetMode2016-L3/raw/6dbe7fc0a524a979ed3d7de540b39be4d8cf5ace/screenshots/main_window.PNG)

Comme vous pouvez le voir, la fenêtre se compose de trois parties majeures : 

 - A droite vous avez deux onglets principaux : la base et vos modèles. L'onglet des modèles propose des sous onglets avec chacun associé à une modèle **.ply**.
 - En haut à gauche, vous avez les informations sur le modèle courant tirées de la base de données. Ainsi vous avez toute les données d'un modèle sous vos yeux à la fois.
 - En bas à gauche il y a le Model Browser. Celui ci récupère les noms et chemins des modèles dans la base de données et propose une sorte de raccourci vers le chemin du modèle. Ainsi si vous double cliquez sur un nom de modèle, il ouvre un nouvel sou onglet dans l'onglet principal "Modèles".


----------


#### Onglet Modèles
Cet onglet se compose de 0 ou plusieurs sous onglets, un sous onglet par modèle **.ply**. Dans chaque onglet de modèle, vous avez  d'une partie la visualisation du modèle en haut et d'autre partie, une barre dans la partie basse proposant des boutons de translation et de rotation ainsi que des options d'affichage et de contrôle du modèle.
##### Partie visualisation
La visualisation du modèle se contrôle soit par les touches du clavier soit par la souris

 > Clavier 
  - **WASD** pour la rotation.
  - **HAUT**, **BAS**, **DROITE**, **GAUCHE** pour les translations.
  - **R** recharger le modèle depuis le fichier **.ply**.
  - **C** recentrer le modèle et l'adapter à ce que sa plus grand dimension prenne 65% de l'écran.

> Souris
  - **CLIC GAUCHE** pour translater le modèle.
  - **CLIC DROITE** pour faire une rotation.


##### Partie basse
Cette partie est assez évidente, non?


----------

#### Onglet Base
![Onglet base](https://git-iut.univ-lille1.fr/cantac/ProjetMode2016-L3/raw/6dbe7fc0a524a979ed3d7de540b39be4d8cf5ace/screenshots/base.PNG)
La base se compose d'une table au centre avec à droite des lignes, des boutons pour opérer sur chaque ligne, et des boutons tout en bas de la table pour opérer sur la table entière.

>Boutons de ligne
 - **"Confirmation/Insertion"** permet soit de mettre à jour la ligne correspondante dans la base de données si on a réalisé des changements, soit d'insérer la ligne si on vient d'utiliser le bouton "Ajouter une ligne" tout en bas.
 - **"Reset"** permet de remettre la ligne à ce qu'elle avait avant que vous aviez modifié ses valeurs. **Attention!** Si vous sauvgardez vos changements de ligne dans la base, vous ne pouvez pas remettre la ligne à sa valeur originelle. Elle ne fonctionne tant que vous n'avez pas mis à jour la ligne dans la base.
 - **"Supprimer"** permet de supprimer la ligne à la fois dans la table ainsi que dans la base de donneés. Elle lance une fenêtre de confirmation afin d'éviter des erreurs éventuels.

>Boutons en bas
 - **"Ajouter une ligne"** permet d'ajouter une ligne dans la table. Celle ci pourra par la suite être insérée dans la base. **Attention!** Vous devez saisir des chiffres dans les colonnes **"Nombre de points"** et **"Nombre de faces"**.
 - **"Reset table à base"** permet de mettre toutes les valeurs dans la table à celles dans la base.



Voila tout ! j'espère que vous appréciez Modelisationator !

 

[^.ply]: Regarder la description des fichiers **.ply** [ici](https://people.sc.fsu.edu/~jburkardt/data/ply/ply.html).