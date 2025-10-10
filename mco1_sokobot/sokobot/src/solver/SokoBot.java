package solver;

import java.awt.Point;
import java.util.*;


/*
GREEDY BEST-FIRST SEARCH LOGIC

Key differences from A*:
1. Use PriorityQueue ordered ONLY by h(n) (heuristic)
2. Ignore g cost (actual path cost) for ordering
3. Always explores states that APPEAR closest to goal first
4. Faster but not guaranteed to find optimal solution
   */

public class SokoBot {


  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    System.out.println("eto yun man");

    //Collect target positions, player, and crates
    Set<Point> targets = new HashSet<>();
    int startX = -1, startY = -1;
    Set<Point> crates = new HashSet<>();

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (mapData[i][j] == '.') {
          targets.add(new Point(j, i));
        }
        if (itemsData[i][j] == '@') {
          startX = j;
          startY = i;
        } else if (itemsData[i][j] == '$') {
          crates.add(new Point(j, i));
        }
      }
    }

    //Define State class with GBFS heuristic
    class State {
      int playerX, playerY;
      Set<Point> crates;
      String path;
      int h; // heuristic cost to goal (ONLY metric used for ordering)

      State(int px, int py, Set<Point> cs, String p) {
        this.playerX = px;
        this.playerY = py;
        this.crates = new HashSet<>(cs);
        this.path = p;
        this.h = calculateHeuristic(cs, targets, mapData);
      }

      // Manhattan-distance heuristic
      private int calculateHeuristic(Set<Point> crateSet, Set<Point> targetSet, char[][] mapData) {
        int totalDist = 0;

        for (Point crate : crateSet) {
          int minDist = Integer.MAX_VALUE;

          for (Point target : targetSet) {
            // Manhattan distance formula
            int dist = Math.abs(crate.x - target.x) + Math.abs(crate.y - target.y);

            if (dist < minDist) {
              minDist = dist;
            }
          }

          totalDist += minDist;
        }

        return totalDist;
      }

      public String encodeState() {
        StringBuilder sb = new StringBuilder();
        sb.append(playerX).append(',').append(playerY).append('|');

        List<Point> sorted = new ArrayList<>(crates);
        sorted.sort(Comparator.comparingInt((Point p) -> p.x).thenComparingInt(p -> p.y));

        for (Point c : sorted)
          sb.append(c.x).append(',').append(c.y).append(';');

        return sb.toString();
      }

      public boolean isDeadlock(Point c) {
        return mapData[c.y][c.x] != '.' &&
                ((mapData[c.y-1][c.x] == '#' || mapData[c.y+1][c.x] == '#') &&
                        (mapData[c.y][c.x-1] == '#' || mapData[c.y][c.x+1] == '#'));
      }
    }

    // GBFS Initialization
    State start = new State(startX, startY, crates, "");
    PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(s -> s.h));

    Set<String> visited = new HashSet<>(); //Store visited States
    openSet.add(start);

    int[][] dirs = {{0, -1, 'u'}, {0, 1, 'd'}, {-1, 0, 'l'}, {1, 0, 'r'}};

    // GBFS Loop
    while (!openSet.isEmpty()) {
      // Dequeue state with LOWEST heuristic value
      State cur = openSet.poll();

      // Goal check
      if (cur.crates.equals(targets)) {
        return cur.path;
      }

      //encode state
      String encoded = cur.encodeState();
      if (visited.contains(encoded)) continue;
      visited.add(encoded);

      // Explore neighbors
      for (int[] d : dirs) {
        int nx = cur.playerX + d[0];
        int ny = cur.playerY + d[1];
        char move = (char) d[2];

        if (mapData[ny][nx] == '#') continue;

        Set<Point> newCrates = new HashSet<>(cur.crates);

        // Handle crate pushing
        if (newCrates.contains(new Point(nx, ny))) {
          int pushX = nx + d[0];
          int pushY = ny + d[1];

          if (mapData[pushY][pushX] == '#' || newCrates.contains(new Point(pushX, pushY)))
            continue;


          newCrates.remove(new Point(nx, ny));
          Point pushed = new Point(pushX, pushY);
          newCrates.add(pushed);

          if (cur.isDeadlock(pushed)) continue;
        }

        // Create new state (no g cost tracking needed)
        State next = new State(nx, ny, newCrates, cur.path + move);
        String nextEncoded = next.encodeState();

        if (!visited.contains(nextEncoded)) {
          openSet.add(next);
        }
      }
    }

    return ""; // No solution
  }
}