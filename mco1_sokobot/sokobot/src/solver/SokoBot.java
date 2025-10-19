package solver;

import java.awt.Point;
import java.util.*;


/*
GREEDY BEST-FIRST SEARCH LOGIC
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
      int h; // heuristic cost to goal

      State(int px, int py, Set<Point> cs, String p) {
        this.playerX = px;
        this.playerY = py;
        this.crates = new HashSet<>(cs);
        this.path = p;
        this.h = calculateHeuristic(cs, targets, mapData);
      }

      // Manhattan distance heuristic +
      private int calculateHeuristic(Set<Point> crateSet, Set<Point> targetSet, char[][] mapData) {

        int heuristic = 0;

        //list the targets' positions
        List<Point> remainingTargets = new ArrayList<>(targetSet);

        //find the nearest crate to that target
        for (Point crate : crateSet) {
          Point bestTarget = null;
          int bestDist = Integer.MAX_VALUE;

          // loop through all available targets to find the nearest one
          for (Point target : remainingTargets) {
            int dist = Math.abs(crate.x - target.x) + Math.abs(crate.y - target.y);

            //keep the smallest distance found
            if (dist < bestDist) {
              bestDist = dist;
              bestTarget = target;
            }
          }

          // Once the closest target is found, add that distance to the total heuristic
          if (bestTarget != null) {
            heuristic += bestDist;
            remainingTargets.remove(bestTarget); // mark this target as used
          }
        }
        return heuristic;

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

      //checks if a crate is in a position where its trapped
      public boolean isDeadlock(Point c, Set<Point> newCrates) {
        int x = c.x;
        int y = c.y;

        // Skip target positions
        if (mapData[y][x] == '.') return false;

        boolean up = mapData[y-1][x] == '#';
        boolean down = mapData[y+1][x] == '#';
        boolean left = mapData[y][x-1] == '#';
        boolean right = mapData[y][x+1] == '#';

        //Corner deadlock
        if ((up && left) || (up && right) || (down && left) || (down && right)) {
          return true;
        }

        // Crate stuck against a wall with another crate
        if ((up && crates.contains(new Point(x, y-1))) ||
                (down && crates.contains(new Point(x, y+1)))) {
          return true;
        }

        return false;
      }

    }

    // GBFS Initialization
    State start = new State(startX, startY, crates, "");
    PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(s -> s.h));

    Set<String> visited = new HashSet<>(); //Store visited States
    openSet.add(start);

    int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
    char[] moves = {'u', 'd', 'l', 'r'};

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
      if (visited.contains(encoded)) continue; // skips checking visited states
      visited.add(encoded);

      // Explore all directions in that state
      for (int i = 0; i < dirs.length; i++) {

        //compute next player position
        int nx = cur.playerX + dirs[i][0];
        int ny = cur.playerY + dirs[i][1];
        char move = moves[i];

        //check if that tile is a wall
        if (mapData[ny][nx] == '#') continue;

        //copy current state crates since we'll try moving them
        Set<Point> newCrates = new HashSet<>(cur.crates);

        // check if that tile contains a crate
        if (newCrates.contains(new Point(nx, ny))) {

          //compute where that crate will be pushed
          int pushX = nx + dirs[i][0];
          int pushY = ny + dirs[i][1];

          //check if we cna push the crate there
          if (mapData[pushY][pushX] == '#' || newCrates.contains(new Point(pushX, pushY)))
            continue;

          //update crate positions in newCrates
          newCrates.remove(new Point(nx, ny));
          Point pushed = new Point(pushX, pushY);
          newCrates.add(pushed);

          //check for deadlocks after pushed
          if (cur.isDeadlock(pushed, newCrates)) continue;
        }

        // Create new state on that directipn
        State next = new State(nx, ny, newCrates, cur.path + move);

        //add this to open set if not visited to avoid visiting the same state
        String nextEncoded = next.encodeState();
        if (!visited.contains(nextEncoded)) {
          openSet.add(next);
        }


      }
    }

    return ""; // No solution
  }
}

