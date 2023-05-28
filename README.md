<a href=""><img src="labyrinth-ui/src/main/resources/icons/icon_64.png" align="left"></a> 
# Labyrinth [![Build Status](https://github.com/MrExplode/Labyrinth/actions/workflows/build.yml/badge.svg)](https://github.com/MrExplode/Labyrinth/actions/workflows/build.yml/) [![wakatime](https://wakatime.com/badge/user/dae8630b-97ba-4b93-b0f3-eff976e35e3b/project/38f72b36-29e7-45b4-b8cd-7893d3888c5d.svg)](https://wakatime.com/@MrExplode)

<br>
<img src="images/maze.png" align="left" width="150">
<p>
The goal of this game is to escape from a labyrinth, before the monster catches the player. 
In each turn we can move one step horizontally or vertivally. 
Neither the player nor the monster can cross walls. 
After each player move, the monster takes two steps towards the player, preferring horizontal movement.
</p>
<br>
<br>

<img src="images/demo.png" align="right" width="400"/>
<h2>Features</h2>
        <ul>
            <li>Map editor</li>
            <li>Language selection</li>
            <li>Highscores</li>
        </ul>

<br>
<br>
<br>

## Building & Usage
You can build the project by running:
```bash
./mvnw package # Linux
mvnw.cmd package # Windows
```
You can find the built artifact in the `labyrinth-ui/target` folder.
```bash
java -jar labyrinth-ui/target/labyrinth-ui-1.0.jar
```

## Site generation
Please note, even though the site generation might seem broken when built locally, it is not [when deployed](https://maven.apache.org/plugins/maven-site-plugin/faq.html#Why_dont_the_links_between_parent_and_child_modules_work_when_I_run_mvn_site).

