import java.util.concurrent.TimeUnit;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus, who left this project to "focus on his
 * two-finger technique". He assured us the code was "basically done".
 * We have found evidence to the contrary.
 *
 * @author Suleyman Macit, with contributions from Ty Posaurus
 * @version 1.0
 */
public class TypingRace
{
    private final int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    private boolean seat1JustMistyped;
    private boolean seat2JustMistyped;
    private boolean seat3JustMistyped;


    // Accuracy thresholds for mistype and burnout events
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    private static final double WINNER_ACCURACY_BONUS = 0.02;
    private static final double BURNOUT_ACCURACY_PENALTY = 0.02;


    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength)
    {
        if (passageLength < 1)
        {
            this.passageLength = 1;
        }
        else
        {
            this.passageLength = passageLength;
        }

        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;

        seat1JustMistyped = false;
        seat2JustMistyped = false;
        seat3JustMistyped = false;


    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat to place them in (1–3)
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (theTypist == null)
        {
            System.out.println("Cannot seat a null typist.");
            return;
        }

        switch (seatNumber) {
            case 1 -> seat1Typist = theTypist;
            case 2 -> seat2Typist = theTypist;
            case 3 -> seat3Typist = theTypist;
            default -> System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     *
     * Note from Ty: "I didn't bother printing the winner at the end,
     * you can probably figure that out yourself."
     */
    public void startRace()
    {
        if (seat1Typist == null || seat2Typist == null || seat3Typist == null)
        {
            System.out.println("Cannot start race - all three seats must have a typist.");
            return;
        }

        boolean finished = false;
        Typist winner = null;

        // Reset all typists to the start of the passage
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        while (!finished)
        {
            // Advance each typist by one turn
            seat1JustMistyped = advanceTypist(seat1Typist);
            seat2JustMistyped = advanceTypist(seat2Typist);
            seat3JustMistyped = advanceTypist(seat3Typist);


            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            if (raceFinishedBy(seat1Typist))
            {
                winner = seat1Typist;
                finished = true;
            }
            else if (raceFinishedBy(seat2Typist))
            {
                winner = seat2Typist;
                finished = true;
            }
            else if (raceFinishedBy(seat3Typist))
            {
                winner = seat3Typist;
                finished = true;
            }

            if (!finished)
            {
                // Wait 200ms between turns so the animation is visible
                try
                {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (winner != null)
        {
            double oldAccuracy = winner.getAccuracy();
            winner.setAccuracy(oldAccuracy + WINNER_ACCURACY_BONUS);

            System.out.println("And the winner is... " + winner.getName() + "!");
            System.out.printf(
                "Final accuracy: %.2f (improved from %.2f)%n",
                winner.getAccuracy(),
                oldAccuracy
            );
        }

    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of a mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private boolean advanceTypist(Typist theTypist)
    {
        if (theTypist == null)
        {
            return false;
        }

        if (theTypist.isBurntOut())
        {
            theTypist.recoverFromBurnout();
            return false;
        }

        boolean justMistyped = false;

        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }
        else
        {
            if (Math.random() < MISTYPE_BASE_CHANCE)
            {
                theTypist.slideBack(SLIDE_BACK_AMOUNT);
                justMistyped = true;
            }
        }

        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.burnOut(BURNOUT_DURATION);
            theTypist.setAccuracy(theTypist.getAccuracy() - BURNOUT_ACCURACY_PENALTY);
        }

        return justMistyped;
    }


    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
        private boolean raceFinishedBy(Typist theTypist)
    {
        if (theTypist == null)
        {
            return false;
        }

        return theTypist.getProgress() >= passageLength;
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE - passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist, seat1JustMistyped);
        System.out.println();

        printSeat(seat2Typist, seat2JustMistyped);
        System.out.println();

        printSeat(seat3Typist, seat3JustMistyped);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * Examples:
     *   |          ⌨           | TURBOFINGERS (Accuracy: 0.85)
     *   |    [~]              | HUNT_N_PECK  (Accuracy: 0.40) BURNT OUT (2 turns)
     *
     * Note: Ty forgot to show when a typist has just mistyped. That would
     * be a nice improvement — perhaps a [<] marker after their symbol.
     *
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist, boolean justMistyped)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        System.out.print(theTypist.getSymbol());

        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--;
        }

        if (justMistyped)
        {
            System.out.print(" [<]");
            spacesAfter = spacesAfter - 4;
        }

        multiplePrint(' ', Math.max(0, spacesAfter));
        System.out.print('|');
        System.out.print(' ');

        System.out.printf("%s (Accuracy: %.2f)",
            theTypist.getName(),
            theTypist.getAccuracy());

        if (justMistyped)
        {
            System.out.print(" \u2190 just mistyped");
        }

        if (theTypist.isBurntOut())
        {
            System.out.print(" BURNT OUT ("
                + theTypist.getBurnoutTurnsRemaining()
                + " turns)");
        }
    }


    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }
}
