# SokoBot

**Major Course Output 1**  
- **Release Date:** September 4, 2025  
- **Due Date:** October 24, 2025 (Friday), 11:59 PM

---

## Status and Updates
- 09/20/2025 Feat: Initialized Contributors and README

---

# Project Timeline for SokoBot

## Week 1 (Sept 21–27) → Setup & Research
- [ ] Install and configure Java JDK, clone the repo and ensure starter program compiles and runs (`freeplay` & `sokobot` modes).
- [ ] Play a few Sokoban levels to fully understand rules and mechanics.
- [ ] Research algorithms relevant to Sokoban:
  - BFS, DFS, Uniform Cost Search
  - A* with heuristics
  - Deadlock detection strategies (to avoid impossible states)
- [ ] Assign roles within the group (algorithm research, coding, testing, documentation).

## Week 2 (Sept 28–Oct 4) → Initial Implementation
- [ ] Represent states clearly (`mapData`, `itemsData`).
- [ ] Implement a simple solver (e.g., BFS or DFS) to confirm state representation works.
- [ ] Start documenting how the state and actions are represented for the report.
- [ ] Test on the simplest Sokoban levels.

## Week 3 (Oct 5–11) → Algorithm Refinement
- [ ] Add heuristics (A* or IDA*) to improve solving efficiency.
- [ ] Experiment with pruning strategies (deadlock checks, redundant move elimination).
- [ ] Ensure solutions finish within **15-second time limit**.
- [ ] Keep logs of performance for different algorithms (feeds into “Evaluation and Performance” section of the report).

## Week 4 (Oct 12–18) → Evaluation & Report Draft
- [ ] Test solver on multiple levels (both provided & downloaded).
- [ ] Record:
  - % of puzzles solved
  - Average time taken
  - Average number of moves
- [ ] Start writing the report:
  - Algorithm implementation details
  - Strengths & weaknesses (based on test results)
  - Challenges encountered
  - Contribution table

## Week 5 (Oct 19–23) → Finalization
- [ ] Polish the code: remove unused methods, ensure clarity and comments.
- [ ] Finalize report (keep it within 4 pages, concise and original).
- [ ] Package deliverables:
  - 📂 ZIP file of source code (whole project directory)
  - 📄 PDF report
- [ ] Run a last check: can group members explain the work if asked?

---

## 🚨 Deadline (Oct 24, 2025)
- [ ] Submit the project before **11:59 PM**.
- [ ] Double-check file names, completeness, and submission format.

---

## Project Description
SokoBot is an AI-based solver for the classic puzzle game **Sokoban**. The objective is to design and implement search algorithms and knowledge representations so the bot can automatically solve Sokoban puzzles within a reasonable time frame.

**Learning outcomes demonstrated**
- **LO1:** Design and evaluate informed search algorithms and knowledge representations for problem solving.  
- **LO2:** Collaboratively build systems that consider multiple strategies to improve performance.  
- **LO5:** Articulate ideas and present results in correct technical written and oral English.

---

## About Sokoban
Sokoban (倉庫番, "warehouse keeper") is a puzzle game where the player pushes crates to target locations inside a grid-based warehouse.  
- Movement: up, down, left, right.  
- Player stands only on empty or target spaces; cannot walk through walls.  
- Crates can be pushed (not pulled) and only one at a time.  
- Goal: place all crates on target spaces.

Sokoban is computationally challenging and relevant to motion-planning and search research.

---

## Project Specifications

### Modes
- **Free Play Mode** — play manually using arrow keys.  
- **SokoBot Mode** — runs the AI solver and animates the solution.

### Running
From the project directory:
```bash
# Free play (replace testlevel with a level filename without extension)
freeplay testlevel

# Run SokoBot mode
sokobot testlevel
