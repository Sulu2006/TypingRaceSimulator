# Part 1: Textual Typing Race Simulator

This folder contains the Part 1 command-line version of the `TypingRaceSimulator` project for `ECS414U`.

## What is included

- `Main.java` - example entry point used to run the race
- `Typist.java` - stores each typist's data and behaviour
- `TypingRace.java` - controls the race simulation and terminal output
- `Testing.ijnb` - testing evidence for Part 1

## Dependencies

- Java Development Kit (`JDK`) 11 or later
- No external libraries are required

## How to compile and run Part 1 from the terminal

1. Make sure you're in the folder `TypingRaceSimulator`.
2. Compile the Part 1 Java files:

```powershell
javac Part1\*.java
```

3. Run the Part 1 program:

```powershell
java Part1.Main
```

This starts the textual typing race simulation in the terminal.

If your system does not recognise `javac` or `java`, make sure the JDK is installed and added to your system `PATH`.

Make sure to add the 'Part1.' as this file is in the package: 'Part1'

## Usage guidelines

- The race is created in `Main.java` using `new TypingRace(...)`.
- Three `Typist` objects are created in `Main.java` and added to the race using `addTypist(...)`.
- The simulation begins when `startRace()` is called.
- You can edit `Main.java` to change:
  - the passage length
  - typist names
  - typist symbols
  - typist accuracy values

## Notes

- The program is designed to compile and run using standard command-line Java tools.
- The race output is text-based and updates turn by turn in the terminal.
- If Unicode symbols do not display properly in your terminal, try running the program in an IDE terminal or another UTF-8 compatible terminal.

## Testing

The file `Testing.ijnb` contains Part 1 testing evidence, including checks for:

- `slideBack()` not allowing progress to go below zero
- burnout counting down correctly
- `resetToStart()` clearing race state
- `setAccuracy()` clamping invalid values
- normal forward movement using `typeCharacter()`
