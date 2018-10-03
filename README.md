SwissBot
========

Files
-----

For source code and the jar file go [here]

Project description
-------------------

This was really a first attempt at making a bot to play the puzzle game
Bejeweled Blitz. I enjoy these puzzle games and was intrigued by the
problem of automating them. Before I started programming though I wanted
to have clearly goals to make it worthwhile.

Goals
-----

-   Extendable: The bot couldn\'t just be specifically for Bejeweled
    Blitz. It would to be a game playing bot that could play Bejeweled
    Blitz so that I could extend it to other games, or even better other
    people could extend it.
-   Optimality: It would have to be good. It\'s easy enough to throw a
    bot together that would play the game decently, but I wanted it to
    be the best. Why program mediocrity?\
    -   Speed: Make use of parallelism and existing multi-core computing
        for speed
    -   Board Recognition: Use of heuristics and machine learning to
        correctly interpret game board
    -   Game Logic: Model to the best of my ability the logic of the
        game
-   Use Standard Interfaces: Only make use of controls available to a
    human, aka monitor, mouse, and keyboard. Stay away from tricks like
    monitoring internal game states, interpreting web packets, or other
    trickery, which would be a lot of fun but I felt violated the spirit
    of extendability in this project.

Results
-------

I can say what I want about my own goals and how I did against them but
let\'s cut to the true measure, score. Unfortunately here I am not
happy, my current implementation is not nearly as good as I would like.
Here is my highest score so far.\
![][1]

Some other games\
![][2]

For context here\'s what I know about scores in general. The Good Human
is a friend of mine and trust me, he\'s very good. His high score of
182,000 according to the Bejeweled Blitz site puts that in the top 4% of
all scores

SwissBot\* | Good Human | Best Human | Best Bot\* | Best Bot\*\*
--- | --- | --- | --- | ---
Max | 272,000 | 182,000 | 271,000 | 322,000 | 439,000
Average | 143,000 | 47,000
Standard Dev | 37,000 | 44,000
Min | 55,000 | 25,000

  : *Scores*

\*: Standard Interfaces: The bot has to capture the screen and parse the
board information. Also uses the mouse to move and click.\
\*\*: Bot can get game state directly from javascript. Sends move
information directly to game without using mouse.

#### Goal Results

-   Extendable: Decent, I think I have abstracted a lot of functionality
    that is common to all games of this nature. Of course until I
    program another game with it this is all subjective. The use of Java
    also helps in this area as it\'s kind of a cobble together sort of
    language.
-   Optimality: Mixed Results\
    -   Speed: Good, Multithreading and Parallelism implemented. The
        program itself runs quickly, you can see details of my machine
        below
    -   Board Recognition: Meh, as I thought when I started correctly
        interpreting the game board is hard and remains an issue, see
        details below
    -   Game Logic: Good, most of the game state and breaking logic
        implemented.
-   Use Standard Interfaces: Good and Done.

Details & Difficulties
----------------------

### Implementation Details

SwissBot is written in Java, mainly because of java.awt.Robot which has
the great features for doing screen capture and the ability to control
the mouse without using system specific libraries.

It has implemented parallel threaded versions of breadth first
search(BFS) and depth first search(DFS).

Uses a machine learning approach for interpreting the game board using
the support vector machine library from [libsvm]

### Difficulties

Probably the most difficult part of the project was correctly
identifying the board state. Individual blocks can often be occluded, be
off center(while dropping), or will glow brightly.\
![][occl1] ![][occl2]![][occl3]

Additionally the high amount of white, or lightness, can make
distinguishing individual blocks problematic. Below are a white block
and a glowing white block which visually are nearly identical.\
![][4]![][5]

\
Here another example which is also hard(though much easier than the
above). On the left a selected orange piece and on the right a glowing
orange.\
![][6]![][7]

\

### Computer Details

![][8]

  [here]: swissbot_files/code
  [1]: swissbot_files/bb_highscore_272.png
  [2]: swissbot_files/bb_avg.png
  [libsvm]: http://www.csie.ntu.edu.tw/~cjlin/libsvm/
  [occl1]: swissbot_files/occlusion.png
  [occl2]: swissbot_files/power_break.png
  [occl3]: swissbot_files/3xscoring.png
  [4]: swissbot_files/white.png
  [5]: swissbot_files/gl_white1.png
  [6]: swissbot_files/orange_selected.png
  [7]: swissbot_files/gl_orange.png
  [8]: swissbot_files/comp_details.png