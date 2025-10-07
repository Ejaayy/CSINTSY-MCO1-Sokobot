package solver;

import java.awt.Point;
import java.util.*;

/*
A* SEARCH LOGIC

Key differences from BFS:
1. Use PriorityQueue instead of Queue (ordered by f(n) = g(n) + h(n))
2. Add g cost (actual path cost) and h heuristic (estimated cost to goal)
3. f = g + h determines exploration order
4. Lower f values explored first
   */

public class SokoBot {

public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
// ---- Step 1: Collect target positions, player, and crates ----
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

    // ---- Step 2: Define State class with A* costs ----
    class State {
      int playerX, playerY;
      Set<Point> crates;
      String path;
      int g; // actual cost from start (path length)
      int h; // heuristic cost to goal
      int f; // total cost: f = g + h

      State(int px, int py, Set<Point> cs, String p, int gCost) {
        this.playerX = px;
        this.playerY = py;
        this.crates = new HashSet<>(cs);
        this.path = p;
        this.g = gCost;
        this.h = calculateHeuristic(cs, targets);
        this.f = this.g + this.h;
      }

      // Heuristic: sum of minimum Manhattan distances from each crate to nearest target
      private int calculateHeuristic(Set<Point> crateSet, Set<Point> targetSet) {
        int totalDist = 0;
        for (Point crate : crateSet) {
          int minDist = Integer.MAX_VALUE;
          for (Point target : targetSet) {
            int dist = Math.abs(crate.x - target.x) + Math.abs(crate.y - target.y);
            minDist = Math.min(minDist, dist);
          }
          totalDist += minDist;
        }
        return totalDist;
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

    // ---- Step 3: A* Initialization ----
    State start = new State(startX, startY, crates, "", 0);

    // PriorityQueue ordered by f cost (lowest first)
    PriorityQueue<State> openSet = new PriorityQueue<>(Comparator.comparingInt(s -> s.f));
    Set<State> visited = new HashSet<>();

    openSet.add(start);

    int[][] dirs = {{0, -1, 'u'}, {0, 1, 'd'}, {-1, 0, 'l'}, {1, 0, 'r'}};

    // ---- Step 4: A* Loop ----
    while (!openSet.isEmpty()) {
      // Dequeue state with LOWEST f cost
      State cur = openSet.poll();

      // Goal check
      if (cur.crates.equals(targets)) {
        return cur.path;
      }

      if (visited.contains(cur)) continue;
      visited.add(cur);

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
          newCrates.add(new Point(pushX, pushY));
        }

        // Create new state with iancremented g cost
        State next = new State(nx, ny, newCrates, cur.path + move, cur.g + 1);

        if (!visited.contains(next)) {
          openSet.add(next);
        }
      }
    }

    return ""; // No solution
}
}