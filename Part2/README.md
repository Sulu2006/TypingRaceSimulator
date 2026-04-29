# Part 2: GUI Typing Race Simulator

This folder contains the Part 2 graphical version of the `TypingRaceSimulator` project for `ECS414U`.

## What is included

- `TypingRaceGUI.java` - the main GUI application for configuring and running the race
- `TypingRace.java` - the race engine used by the GUI
- `Typist.java` - stores each typist's data and behaviour

## Dependencies

- Java Development Kit (`JDK`) 11 or later
- No external libraries are required

## How to compile and run Part 2 from the terminal

1. Make sure you're in the folder `TypingRaceSimulator`.
2. Compile the Part 2 Java files:

```powershell
javac Part2\*.java
```

3. Run the Part 2 program:

```powershell
java Part2.TypingRaceGUI
```

This command uses the terminal to launch the GUI application.
The typing race itself then runs in a separate window, not inside the terminal.

If your system does not recognise `javac` or `java`, make sure the JDK is installed and added to your system `PATH`.

Make sure to add the 'Part2.' as this file is in the package: Part2

## Usage guidelines

- Choose a passage or enter a custom passage.
- Select the number of typists taking part in the race.
- Turn the global modifiers on or off before starting the race.
- Customise each visible typist using:
  - typing style
  - keyboard type
  - colour
  - accessory
  - symbol
- Press `Start Race` to begin the simulation.

## Notes

- This part uses Java Swing for the graphical interface.
- The race runs turn by turn in the GUI using the same basic simulation idea as Part 1.
- The selected options for typing style, keyboard type, accessories, and modifiers affect the race behaviour.
- The program is designed to compile and run using standard Java tools without extra libraries.
