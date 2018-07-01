# The Name Game: Android

## How to Play:
1. You will be presented with five pictures of WillowTree employees and a name. Your goal is to select the picture of the person matching the name.
2. If you get it wrong, don't worry! The correct answer will be displayed for you so you can match the face and name next time!
3. Your total correct and incorrect answers will be displayed so you can keep track of how well you're doing.

## Developer Notes:
* I used the MVP architecture style while completing this app. The allowed me to keep game and networking logic out of the view classes.
* I edited the code structure a bit to fit with MVP as well as the Dagger injections.
* I updated the model classes as well as the repository class to handle the new API version.
* I converted several classes to Kotlin and wrote new classes in it as well. This allowed me to leverage the language's features.
* I included score tracking to show the number of correct and incorrect answers.
* I leveraged the existing animations to fade out incorrect answers after a selection is made. This way it shows the player what the correct answer was.
* I fixed the existing tests as well as added a couple of POC ones.