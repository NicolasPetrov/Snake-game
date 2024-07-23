# Snake-game
The classic Snake game on Java.

**Implemented:**

* Different levels of difficulty easy, medium, high (the initial speed level depends on the difficulty + during the game the speed is adjusted based on the score by modifying the gameSpeed variable and updating the Timer delay in adjustSpeed() method);
* Scoring system (The score is increased by 10 for normal food);
* Bonuses for special food (The score increases by 50 for special food + special food is randomly spawned with a 20% chance when regular food is spawned. It is handled in the spawnFood() method and checked in the move() method);
* Sound effects and music.

Background music for the game is generated by Suno neural network, sound effects are taken from here - https://zvukipro.com/eda-napitki/253-zvuki-poedaniya-edy.html
