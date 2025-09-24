# SokoBot

**Major Course Output 1**  
- **Release Date:** September 4, 2025  
- **Due Date:** October 24, 2025 (Friday), 11:59 PM

---

## Status and Updates
- 09/20/2025 Feat: Initialized Contributors and README
- 09/24/2025 Feat: Testing Playing Sokoban
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
