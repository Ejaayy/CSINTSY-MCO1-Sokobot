package solver;

import java.util.Random;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.

    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    */
    /*
     * Basic random test: generate a sequence of random moves
     * just to check that Sokobot runs correctly.
     * This will NOT solve the puzzle — it's just a baseline.
     */

    Random rand = new Random();
    String moves = "udlr"; // up, down, left, right
    StringBuilder path = new StringBuilder();

    int steps = 50; // number of random moves to try
    for (int i = 0; i < steps; i++) {
      path.append(moves.charAt(rand.nextInt(4)));
    }

    return path.toString();
  }

}
