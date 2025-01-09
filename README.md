# Description
Ce projet a été réalisé dans le cadre du cours de génie logiciel orienté objet à l'Université Laval. Le mandat consistait à développer, pour le lab-usine de l'Université Laval, un logiciel capable de configurer une planche et de permettre à l'utilisateur de définir des coupes, qui seront ensuite effectuées par la machine de coupe CNC du lab-usine.

L'exportation du projet en GCode permet à la machine de réaliser les coupes une fois que l'utilisateur est satisfait de la configuration. Vous trouverez ci-dessous des explications sur l'utilisation du logiciel.

## Membres de l'équipe
- Antoine Morin
- Louis-Etienne Messier
- Adam Côté
- Sébastien Dubé
- Kamran Charles Nayebi

### Lancement de l'application
Le fichier .jar pour l'application se trouve à la racine du projet. Il suffit
de lancer ce fichier pour lancer l'application.

### Création d'un nouveau projet
Une fois l'application lancée, vous accédez au menu des fichiers. Pour créer un nouveau projet, cliquez sur le bouton "Nouveau projet".

### Visualisation du panneau
Après avoir créé un projet, vous arrivez dans le menu de configuration. Un panneau est automatiquement créé et affiché.
Pour vérifier les dimensions de ce panneau, cliquez dessus. Les informations correspondantes s'afficheront à droite, dans le panneau des attributs. Vous pourrez alors modifier la taille du panneau dans les champs dédiés.
N'hésitez pas à zoomer, dézoomer ou déplacer le panneau pour ajuster la visualisation selon vos besoins.

### Création d'une coupe
Pour créer une coupe, il faut être dans la fenêtre de coupe. Pour passer à cette fenêtre, il suffit de cliquer sur le bouton
suivant situé au bas de l'écran à droite. <br>
Une fois dans la fenêtre, on doit sélectionner le type de coupe dans la barre d'icone située à droite. <br>
Pour créer une coupe verticale, on sélectionne l'icone avec la barre verticale. <br>
![img.png](icone_coupe_verticale.png)
<br>
Le curseur devient alors un cercle rouge. Pour créer la coupe, on doit positionner son curseur au point de départ sur la planche.
Si le curseur devient vert, cela signifie que la coupe est possible. On doit alors cliquer pour sélectionner le premier point.
On peut alors déplacer la souris verticalement pour définir la taille de la coupe. Si la ligne est verte, cela signifie que la coupe
est correcte. Si la ligne est jaune, cela signifie que le point final sera accroché à la ligne la plus près. Si le curseur
est rouge, la coupe n'est pas possible.
Pour confirmer l'ajout de la coupe, il suffit de cliquer une seconde fois. Il est à noter que si la coupe est invalide, elle ne sera pas ajoutée et il faudra resélectionner l'outil de coupe verticale.

### Modification de la coupe
Pour modifier une coupe, il faut sélectionner la coupe dans la fenêtre situé en bas à droite nommé coupe. Après avoir sélectionné la coupe,
ses informations s'afficheront dans le panneau des attributs. On pourra alors modifier la position des points qui constituent la coupe.
Si on veut déplacer la coupe à 400mm, on doit modifier la position x du point 1 à 400mm et la position x du point 2 à 400mm.
La coupe sera alors déplacée à 400mm.

### Suppression d'une coupe
Pour supprimer une coupe, on peut cliquer sur le bouton de la corbeille rouge qui correspond à la coupe que l'on veut
supprimer. La coupe sera alors supprimée et retirée du panneau.
