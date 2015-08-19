(README Last updated 2015-08-19)
Implementation of Rocket Launcher Chess, including an AI, which will ideally be strong enough to solve Rocket Launcher Chess.

Rocket Launcher Chess is a chess variant which blends Rifle Chess and Atomic Chess. In Rifle Chess, pieces stay in their original position when they capture. In Atomic Chess, when pieces capture, all pieces in a 3x3 square centered at the capture point die.

In Rocket Launcher Chess, well, you can imagine. If you're thinking "wait, doesn't that make queens incredibly overpowered?", you probably understand the game mechanics correctly.

Currently, there is a GUI implemented, which can be accessed by running ui.GUI. I haven't bothered to make the GUI pretty yet, since I'm mostly working on improving the AI. Things which are not obvious: the snowman is a reset button, and clicking on the names of White and Black will toggle activating the AI of that color. Checkmate (or king death) detection has been implemented, though right now it just creates an annoying popup every time the board updates. I haven't implemented draw detection yet, again because I'm focusing on the AI.
