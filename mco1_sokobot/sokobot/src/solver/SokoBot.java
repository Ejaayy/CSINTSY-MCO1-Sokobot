package solver;

import java.awt.Point;
import java.util.*;


/**
 * Sokobot - An implementation of a solver for Sokoban puzzles using Greedy Best-First Search (GBFS) algorithm.
 *
 * <p>
 * This class solves the classic Sokoban puzzle of pushing crates to target locations.
 * The solver uses a heuristic-based search method that generates states in the search that appear closer to the goal using Manhattan distance calculations.
 * </p>
 *
 * <p><b>Algorithm Overview:</b></p>
 * <ol>
 *   <li>Search Algorithmn: Greedy Best-First Search (GBFS)</li>
 *   <li>Heuristic: The sum of the minimum Manhattan distances of each crate to the nearest target.</li>
 *   <li>State: player position + the set of crate positions.</li>
 *   <li>Deadlock Detection: Corner detection to eliminate unsolvable states.</li>
 * </ol>
 */

public class SokoBot {

  /**
   * Utilizes the Greedy Best-First Search algorithm to solve a Sokoban puzzle.
   *
   * <p>This algorithm will take a Sokoban puzzle configuration, and attempt to find a solution to the puzzle by evaluating alternative state
   * in the order of their heuristic value (the estimated distance to the goal).
   * The algorithm will complete when either a solution state is constructed, or all possible reachable states are exhausted.</p>
   *
   * <p><b>Map Legend:</b></p>
   * <ul>
   *   <li>'#' - Wall (Nothing can passthrough / push it)</li>
   *   <li>'.' - Target floor (Where crates must be pushed to)</li>
   *   <li>' ' - Empty floor</li>
   *   <li>'@' - Player starting position</li>
   *   <li>'$' - Crate (box that must be pushed to targets)</li>
   * </ul>
   *
   * <p><b>Solution Format:</b></p>
   * The string returned is a sequence of moves that the player must do in order to complete the puzzle. Each character represents a single move.:
   * <ul>
   *   <li>'l' - Move/Push Left</li>
   *   <li>'r' - Move/Push Right</li>
   *   <li>'u' - Move/Push Up</li>
   *   <li>'d' - Move/Push Down</li>
   * </ul>
   *
   * @param width The width of the puzzle map (columns)
   * @param height The height of the puzzle map (rows)
   * @param mapData 2D character array representing the static map content (walls, targets, floor).
   * @param itemsData 2D character array representing the dynamic puzzle content (the player and crates).
   * @return A string representing the sequence of moves required to solve the puzzle, or an empty string if the puzzle is unsolvable.
   */
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    /**
     * INITIALIZATION OF THE PUZZLE
     * You will need to scan the entire puzzle grid to gather information about the initial state of the puzzle:
     * - The target positions (where you need to place crates)
     * - The starting position of the player
     * - The initial positions of crates
     */

    /**
     * The set of all points (as Point objects) that are to be targets for where to place the crates. These are the '.' characters in the mapData array.
     * We use a HashSet for these target positions so we can check if the goal is reached in O(1).
     */
    Set<Point> targets = new HashSet<>();

    /**
     * X-coordinate (column) of the player's starting position.
     * It is initialized to -1 to check if player position is never found.
     * It will be set when the '@' character is found in itemsData.
     */
    int startX = -1;
    /**
     * Y-coordinate (row) of the player's starting position.
     * It is initialized to -1 to check if player position is never found.
     * It will be set when the '@' character is found in itemsData.
     */
    int startY = -1;

    /**
     * This set contains all initial crate positions (Point objects).
     * They correspond to '$' characters in itemsData array.
     * We are using HashSet so we can access O(1) to check for crate collisions.
     */
    Set<Point> crates = new HashSet<>();

    /**
     * A nested loop to scan the entire puzzle grid.
     * We will scan it from the top-left to the bottom-right.
     * The outer loop (i) iterates through rows (height).
     * The inner loop (j) iterates through columns (width).
     */
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        /**
         * If the current map cell is a target marker ('.'), add this position to the targets set.
         * Note: Point constructor takes (x,y) and x=column and y=row.
         */
        if (mapData[i][j] == '.') {
          targets.add(new Point(j, i));
        }
        /**
         * If the current items cell is a player marker ('@'), store the player's starting coordinates.
         */
        if (itemsData[i][j] == '@') {
          startX = j;
          startY = i;
        }
        /**
         * If the current items cell is a crate marker ('$'), add this crate position to the crates set.
         */
        else if (itemsData[i][j] == '$') {
          crates.add(new Point(j, i));
        }
      }
    }

    /**
     * State - Denotes the configuration of the puzzle at one point in time during the search.
     * This inner class encompasses all necessary information to represent a distinct configuration of the Sokoban puzzle during searching.
     * It contains the positions of the player, all crates, the collection of moves that led to this state, and a heuristic value that estimates the distance to the goal.
     * GBFS Property: States are compared and ordered only by their heuristic value (h). There is no tracking of the g-cost
     * (actual cost of the path) in contrast to A* search.
     */
    class State {
      int playerX, playerY;
      Set<Point> crates;
      String path;
      int h;

      /**
       * Constructor for instantiating a new State object.
       * This constructor initializes all relevant state variables and will automatically determine the heuristic value for the state based on the Manhattan distance from each crate to the target.
       * @param px the x-coordinate of the player (the column location).
       * @param py the y-coordinate of the player (the row location).
       * @param cs the set of crate positions (this will be cloned to ensure that the original set is not modified).
       * @param p a path string that represents the moves made to reach the state.
       */
      State(int px, int py, Set<Point> cs, String p) {
        this.playerX = px;
        this.playerY = py;
        this.crates = new HashSet<>(cs);
        this.path = p;
        this.h = calculateHeuristic(cs, targets, mapData);
      }

      /**
       * Computes the heuristic estimate for the configuration of crates given.
       * This method provides a Manhattan distance heuristic that is admissible (never overestimates) but may not be optimal for Sokoban
       * given the push properties and wall constraints, in which the heuristic simply sums the minimum Manhattan distance of each crate to their nearest target.Where: Manhattan Distance = |x1 - x2| + |y1 - y2|.
       *
       * Reason for this heuristic:
       * Admissible - Never overestimates the actual cost.
       * Fast computation: O(n*m) where n=crates, m=targets.
       * Encourages search towards states where the crates are near targets.
       *
       * @param crateSet a set of crates to evaluate.
       * @param targetSet a set of target locations (goal locations for crates).
       * @param mapData a 2D map array (passed for possible future use).
       * @return total minimum distance from each crate to the nearest target.
       */
      private int calculateHeuristic(Set<Point> crateSet, Set<Point> targetSet, char[][] mapData) {
        int totalDist = 0;

        for (Point crate : crateSet) {
          int minDist = Integer.MAX_VALUE;

          for (Point target : targetSet) {

            int dist = Math.abs(crate.x - target.x) + Math.abs(crate.y - target.y);

            if (dist < minDist) {
              minDist = dist;
            }
          }

          totalDist += minDist;
        }

        return totalDist;
      }

      /**
       * Transforms the current state into a distinct string.
       * This method produces a canonical string that uniquely represents a state based upon the position of the player and the position of the crates.
       * The encoding is used to search for duplicate states in the visited set to avoid infinite loops and duplicative searches.
       *
       * Encoding ("string") format: "playerX,playerY|crateX1,crateY1;crateX2,crateY2;…"
       *
       * Why sort crates?
       * The crates are sorted by coordinate so that a state that has the same configuration of crates will always produce the same string,
       * even if the crates had been added to the set in different orders.
       *
       * @return A distinct string representation of this state's configuration.
       */
      public String encodeState() {
        StringBuilder sb = new StringBuilder();
        sb.append(playerX).append(',').append(playerY).append('|');

        List<Point> sorted = new ArrayList<>(crates);
        sorted.sort(Comparator.comparingInt((Point p) -> p.x).thenComparingInt(p -> p.y));

        for (Point c : sorted)
          sb.append(c.x).append(',').append(c.y).append(';');

        return sb.toString();
      }

      /**
       * Determines if a crate at the specified position is in a deadlock (unsolvable) configuration.
       *
       * <p>This method identifies straightforward corner deadlocks in which a crate has been pushed into a corner which is not a target location.
       * As soon as the crate has been pushed into the corner, it would not be possible to get the crate out of the corner.
       * Once a crate is in such a corner, the puzzle is deemed unsolvable in its current state.</p>
       *
       * <p><b>Deadlock Conditions:</b></p>
       * <ol>
       *   <li>The crate is NOT on a target position (mapData != '.')</li>
       *   <li>AND there are walls blocking two adjacent perpendicular directions</li>
       * </ol>
       *
       * <p><b>Examples of Corner Deadlocks:</b></p>
       * <pre>
       * # #     # $     $ #     # #
       * $ .     # .     # .     $ #
       * (BAD)   (BAD)   (BAD)   (BAD)
       *
       * # #     # .
       * . .     . .
       * (OK)    (OK - on target)
       * </pre>
       *
       * <p><b>Limitation:</b> This will only identify simple corner deadlocks. It will do nothing to identify
       * more complex deadlocks (freeze deadlocks, corral deadlocks).</p>
       *
       * @param c Point representing the crate position to check for deadlock
       * @return true if the crate is in a deadlock position, false otherwise
       */
      public boolean isDeadlock(Point c) {
        return mapData[c.y][c.x] != '.' &&
                ((mapData[c.y-1][c.x] == '#' || mapData[c.y+1][c.x] == '#') &&
                        (mapData[c.y][c.x-1] == '#' || mapData[c.y][c.x+1] == '#'));
      }
    }

    /**
     * Create the initial starting state of the puzzle.
     * Contains the player's starting position, initial crate positions, and an empty path string.
     */
    State start = new State(startX, startY, crates, "");

    /**
     * Priority queue (open set) for GBFS exploration.
     * - States are automatically ordered by their heuristic value (h)
     * - The state with the lowest h value will be explored first.
     */
    PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(s -> s.h));

    /**
     * A set of visited states to avoid re-exploration.
     */
    Set<String> visited = new HashSet<>();

    /**
     * Add the initial starting state to the priority queue.
     */
    openSet.add(start);

    /**
     * Direction vectors for player movement in all four cardinal directions.
     * For more information Refer to the comments on the Sokobot class and solveSokobanPuzzle method.
     */
    int[][] dirs = {{0, -1, 'u'}, {0, 1, 'd'}, {-1, 0, 'l'}, {1, 0, 'r'}};

    /**
     * The primary GBFS loop continues until there are no more states to visit.
     * The loop ends if:
     * - A goal state is found, and the solution is returned
     * - The queue is empty (as explored all reachable states accomplished no solution)
     */
    while (!openSet.isEmpty()) {
      /**
       * Dequeue state with LOWEST heuristic value
       */
      State cur = openSet.poll();

      /**
       * Check if the current state is a goal state (all crates on targets).
       */
      if (cur.crates.equals(targets)) {
        return cur.path;
      }

      /**
       * Mark the current state as visited.
       */
      String encoded = cur.encodeState();
      if (visited.contains(encoded)) continue;
      visited.add(encoded);

      /**
       * Iterate through all four possible movement directions.
       * For each direction, attempt to generate a new valid state.
       */
      for (int[] d : dirs) {
        int nx = cur.playerX + d[0];
        int ny = cur.playerY + d[1];
        char move = (char) d[2];

        /**
         * Check for wall collisions
         */
        if (mapData[ny][nx] == '#') continue;

        /**
         * Clone the current crate positions for modification.
         */
        Set<Point> newCrates = new HashSet<>(cur.crates);

        /**
         * Check for crate collisions and handle pushing logic.
         */
        if (newCrates.contains(new Point(nx, ny))) {
          /**
           * Identify the position to which the crate would be pushed.
           * The crate moves in the same direction of the player.
           * pushX and pushY represent the cell further than the crate.
           */
          int pushX = nx + d[0];
          int pushY = ny + d[1];

          /**
           * Determine whether the crate can be pushed.
           *
           * A push cannot occur if either of the following conditions is true:
           * - The resulting cell is a wall (cannot push through walls)
           * - The resulting cell has another crate (cannot push two crates)
           *
           * If either of these conditions is true, this move is invalid - move onto the next direction.
           */
          if (mapData[pushY][pushX] == '#' || newCrates.contains(new Point(pushX, pushY)))
            continue;

          /**
           * Execute the push:
           * - Remove the crate from its current position.
           * - Add the crate to its new position.
           * - Check for deadlock after the push.
           */
          newCrates.remove(new Point(nx, ny));
          Point pushed = new Point(pushX, pushY);
          newCrates.add(pushed);
          if (cur.isDeadlock(pushed)) continue;
        }

        /**
         * Create the new state after the move/push.
         */
        State next = new State(nx, ny, newCrates, cur.path + move);
        String nextEncoded = next.encodeState();

        /**
         * Add the new state to the priority queue if it has not been visited.
         */
        if (!visited.contains(nextEncoded)) {
          openSet.add(next);
        }
      }
    }

    /**
     * If the loop has concluded without finding a solution, return an empty string which signifies the puzzle cannot be solved.
     */
    return "";
  }
}