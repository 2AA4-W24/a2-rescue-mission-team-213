# [A2] Island Explorer

- Authors:
  - [Colin, Chambachan](chambacc@mcmaster.ca) 
  - [Jinwoo, Hong](hongj54@mcmaster.ca)
  - [Gary, Qin](qinz51@mcmaster.ca)

## Product Description

This product is an _exploration command center_ for the [Island](https://ace-design.github.io/island/) game. On this island, there exists an emergency site and multiple creeks. You have absolutely no information about the island, and all you have is a drone that can be used to explore with a battery life. The objective of this game is to explore the island to find the emergency site and its closest creek while using as little battery as possible.

- The `ca.mcmaster.se2aa4.island.team_XXX_.Explorer` class implements the command center, used to compete with the others. (XXX being the team identifier)
- The `Runner` class allows one to run the command center on a specific map.

Available actions can be found at https://ace-design.github.io/island/actions/.

### Strategy description

#### Finding the island
At any point during this stage, if an echo/scan action spots land, the drone will fly to the land to begin the next stage. 
- Echo forward & scan to determine if the drone is facing the island / on the island
- Do a loop of echo left, echo right, and fly until the drone reaches the border of the map
- Do a 180, fly to the same axis as the starting location, and do a loop of echo left, echo right, and fly

#### Scouting the island
This stage involves determining the length and width of the island.
- Fly until the end of the island is reached.
- Turn right, fly until the end of the island is reached.
- Turn right, fly until the end of the island is reached.
- Turn right, fly until the end of the island is reached.
After these actions, the dimensions of the island can be determined.
> Prototype diagram describing how this stage works

![image](https://github.com/user-attachments/assets/d501423b-96e9-447b-be61-b6d9f605af81)

#### Scanning the island
This stage involves efficiently traversing the tiles of the island to find the POIs. The general idea is to do a grid search by doing two passthroughs of the island. Two passthroughs are necessary because of how turning results in a forward translation as well as a translation in the direction you're turning in. The edgecases for this are rather complicated, so we drew a diagram and wrote pseudocode for this process where we pay special attention to the edges of the island, labelling them as the "turn points."

![image](https://github.com/user-attachments/assets/048b79e8-1631-4e5f-adf3-2c90142fc026)

This is the general idea, but many optimizations were implemented to minimize battery usage. These include:
- After doing a double turn, echo forward once to skip scanning all the non-land (water, sea, lake) tiles
- Once the emergency site and any creek is found, limit the search to only the area that could result in finding a closer creek to the emergency site.

### Example explorations
![image](https://github.com/user-attachments/assets/48bdd8c8-71f2-48df-b627-e0798240ae71)
![image](https://github.com/user-attachments/assets/769ac6f1-5a3a-4edf-bcb5-96fcbbd2c79e)


## How to compile, run and deploy

### Compiling the project:

```
mosser@azrael a2-template % mvn clean package
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.960 s
[INFO] Finished at: 2024-01-20T18:26:43-05:00
[INFO] ------------------------------------------------------------------------
mosser@azrael a2-template % 
```

This creates one jar file in the `target` directory, named after the team identifier (i.e., team 00 uses `team00-1.0.jar`).

As the project is intended to run in the competition arena, this jar is not executable. 

### Run the project

The project is not intended to be started by the user, but instead to be part of the competition arena. However, one might one to execute their command center on local files for testing purposes.

To do so, we ask maven to execute the `Runner` class, using a map provided as parameter:

```
mosser@azrael a2-template % mvn exec:java -q -Dexec.args="./maps/map03.json"
```

It creates three files in the `outputs` directory:

- `_pois.json`: the location of the points of interests
- `Explorer_Island.json`: a transcript of the dialogue between the player and the game engine
- `Explorer.svg`: the map explored by the player, with a fog of war for the tiles that were not visited.
