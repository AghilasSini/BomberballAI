# BOMBERBALL (IA)


Extension du travail du groupe GLHF sur le jeu Bomberball pour le projet d'intelligence artificielle.

## Getting Started
```
    git clone https://votre_login@bitbucket.org/glecorve/ia_bomberball_glhf.git
    cd ia_bomberball_glhf
    git remote remove origin
    git remote add prof https://bitbucket.org/glecorve/ia_bomberball_glhf.git
    git remote add groupe https://votre_login@bitbucket.org/glecorve/ia_bomberball_votre_groupe.git
    git push groupe
```
 (ou juste "git push" si vous avez choisi de le nommer "origin")


### Prerequisites

*	Java 8 (Oracle or JDK)
*	Eclipse / IntelliJ (Up to you)

### Installing

After cloning the repostiry on your machine:
```
./gradlew clean
```

Generate the project files for your IDE by using the following command line (inside the project):
* Eclipse: `./gradlew eclipse`
* IntelliJ: `./gradlew idea`

You can compile and build the project by using the following command line (inside the project): 

```
./gradlew build
```


## Authors

* **GLHF** - *Initial work*
