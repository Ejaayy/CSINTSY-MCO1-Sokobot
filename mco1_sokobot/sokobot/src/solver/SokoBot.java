package solver;

import java.awt.Point;
import java.util.*;

public class SokoBot {

  /**
   * BFS (Breadth-First Search) implementation for Sokoban.
   * This method searches for the shortest sequence of moves
   * to push all crates ('$') onto all targets ('.').
   *
   * Inputs:
   *   width, height → dimensions of the grid
   *   mapData       → static map layout (walls '#' and targets '.')
   *   itemsData     → dynamic items (player '@' and crates '$')
   *
   * Output:
   *   A string containing moves: 'u', 'd', 'l', 'r'
   */
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    // ---- Step 1: Collect target positions from mapData ----
    Set<Point> targets = new HashSet<>();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (mapData[i][j] == '.') {
          targets.add(new Point(j, i)); // store (x, y)
        }
      }
    }

    // ---- Step 2: Find initial player and crate positions from itemsData ----
    int startX = -1, startY = -1;
    Set<Point> crates = new HashSet<>();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (itemsData[i][j] == '@') {
          startX = j;
          startY = i;
        } else if (itemsData[i][j] == '$') {
          crates.add(new Point(j, i));
        }
      }
    }

    // ---- Step 3: Define a State class for BFS ----
    class State {
      int playerX, playerY;
      Set<Point> crates;
      String path; // sequence of moves to reach this state

      State(int px, int py, Set<Point> cs, String p) {
        this.playerX = px;
        this.playerY = py;
        this.crates = new HashSet<>(cs);
        this.path = p;
      }

      @Override
      public boolean equals(Object o) {
        if (!(o instanceof State)) return false;
        State s = (State) o;
        return playerX == s.playerX && playerY == s.playerY && crates.equals(s.crates);
      }

      @Override
      public int hashCode() {
        return Objects.hash(playerX, playerY, crates);
      }
    }

    // ---- Step 4: BFS Initialization ----
    State start = new State(startX, startY, crates, "");
    Queue<State> queue = new LinkedList<>();
    Set<State> visited = new HashSet<>();
    queue.add(start);

    // Move directions: dx, dy, and corresponding move character
    int[][] dirs = {{0, -1, 'u'}, {0, 1, 'd'}, {-1, 0, 'l'}, {1, 0, 'r'}};

    // ---- Step 5: BFS Loop ----
    while (!queue.isEmpty()) {
      State cur = queue.poll();

      // --- Check if current state is a goal (all crates on targets) ---
      if (cur.crates.containsAll(targets)) {
        return cur.path; // solution found!
      }

      if (visited.contains(cur)) continue;
      visited.add(cur);

      // --- Explore all possible moves ---
      for (int[] d : dirs) {
        int nx = cur.playerX + d[0];
        int ny = cur.playerY + d[1];
        char move = (char) d[2];

        // Skip if next cell is a wall
        if (mapData[ny][nx] == '#') continue;

        // Copy crate positions
        Set<Point> newCrates = new HashSet<>(cur.crates);

        // If there's a crate, try to push it
        if (newCrates.contains(new Point(nx, ny))) {
          int pushX = nx + d[0];
          int pushY = ny + d[1];

          // Blocked if next cell is a wall or another crate
          if (mapData[pushY][pushX] == '#' || newCrates.contains(new Point(pushX, pushY)))
            continue;

          // Push crate forward
          newCrates.remove(new Point(nx, ny));
          newCrates.add(new Point(pushX, pushY));
        }

        // Create new state with updated player position and crates
        State next = new State(nx, ny, newCrates, cur.path + move);

        if (!visited.contains(next)) {
          queue.add(next);
        }
      }
    }

    // No solution found
    return "";
  }
}
