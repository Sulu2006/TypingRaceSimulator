package Part2;

/**
 * Represents one competitor in the typing race.
 * A typist stores their identity, current progress, accuracy,
 * and any temporary burnout state applied during the race.
 *
 * @author Suleyman Macit
 * @version 1.0
 */
public final class Typist
{
    // Core state is kept private so accuracy, progress, and burnout
    // can only be changed through validated methods.
    private final String name;
    private char symbol;
    private int progress;
    private boolean burntOut;
    private int burnoutTurnsRemaining;
    private double accuracy;
    private double typingBonus;
    private double mistypeChanceMultiplier;
    private double burnoutChanceMultiplier;

    /**
     * Creates a typist with a display symbol, name, and starting accuracy.
     * Accuracy is passed through setAccuracy() so invalid values are clamped.
     *
     * @param typistSymbol the Unicode character used to represent this typist
     * @param typistName the typist's display name
     * @param typistAccuracy the typist's starting accuracy
     */
    public Typist(char typistSymbol, String typistName, double typistAccuracy)
    {
        symbol = typistSymbol;
        name = typistName;
        progress = 0;
        burntOut = false;
        burnoutTurnsRemaining = 0;
        setAccuracy(typistAccuracy);
        typingBonus = 0.0;
        mistypeChanceMultiplier = 1.0;
        burnoutChanceMultiplier = 1.0;
    }

    /**
     * Applies burnout for the given number of turns.
     * Passing zero or less clears any existing burnout state.
     *
     * @param turns the number of turns the burnout should last
     */
    public void burnOut(int turns)
    {
        if (turns <= 0)
        {
            burntOut = false;
            burnoutTurnsRemaining = 0;
        }
        else
        {
            burntOut = true;
            burnoutTurnsRemaining = turns;
        }
    }

    /**
     * Counts burnout down by one turn and clears the state at zero.
     */
    public void recoverFromBurnout()
    {
        if (burntOut)
        {
            burnoutTurnsRemaining--;

            if (burnoutTurnsRemaining <= 0)
            {
                burntOut = false;
                burnoutTurnsRemaining = 0;
            }
        }
    }

    /**
     * Returns the typist's current accuracy value.
     *
     * @return the accuracy value in the range 0.0 to 1.0
     */
    public double getAccuracy()
    {
        return accuracy;
    }

    /**
     * Returns the typist's progress through the passage.
     *
     * @return the number of characters typed so far
     */
    public int getProgress()
    {
        return progress;
    }

    /**
     * Returns the typist's display name.
     *
     * @return the typist's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the character used to show this typist on screen.
     *
     * @return the typist's symbol
     */
    public char getSymbol()
    {
        return symbol;
    }

    /**
     * Returns how many burnout turns are still active.
     *
     * @return the remaining burnout turns, or 0 when not burnt out
     */
    public int getBurnoutTurnsRemaining()
    {
        return burnoutTurnsRemaining;
    }

    /**
     * Returns the extra typing chance applied to this typist.
     *
     * @return the typing bonus value
     */
    public double getTypingBonus()
    {
        return typingBonus;
    }

    /**
     * Returns the multiplier applied to mistype chance.
     *
     * @return the mistype chance multiplier
     */
    public double getMistypeChanceMultiplier()
    {
        return mistypeChanceMultiplier;
    }

    /**
     * Returns the multiplier applied to burnout chance.
     *
     * @return the burnout chance multiplier
     */
    public double getBurnoutChanceMultiplier()
    {
        return burnoutChanceMultiplier;
    }

    /**
     * Restores race state to the starting position without changing
     * the typist's name, symbol, or accuracy.
     */
    public void resetToStart()
    {
        progress = 0;
        burntOut = false;
        burnoutTurnsRemaining = 0;
    }

    /**
     * Reports whether this typist is currently unable to type.
     *
     * @return true when the typist is burnt out
     */
    public boolean isBurntOut()
    {
        return burntOut;
    }

    /**
     * Moves the typist forward by one character when they are able to type.
     */
    public void typeCharacter()
    {
        if (!burntOut)
        {
            progress++;
        }
    }

    /**
     * Moves the typist backwards by the requested amount.
     * Progress is clamped so it can never fall below zero.
     *
     * @param amount the number of characters to move back
     */
    public void slideBack(int amount)
    {
        if (amount <= 0)
        {
            return;
        }

        progress = progress - amount;

        if (progress < 0)
        {
            progress = 0;
        }
    }

    /**
     * Updates the typist's accuracy, clamping values into the valid range.
     *
     * @param newAccuracy the new accuracy value to store
     */
    public void setAccuracy(double newAccuracy)
    {
        if (newAccuracy < 0.0)
        {
            accuracy = 0.0;
        }
        else if (newAccuracy > 1.0)
        {
            accuracy = 1.0;
        }
        else
        {
            accuracy = newAccuracy;
        }
    }

    /**
     * Changes the display symbol used for this typist.
     *
     * @param newSymbol the replacement symbol
     */
    public void setSymbol(char newSymbol)
    {
        symbol = newSymbol;
    }

    /**
     * Changes the extra typing chance given to this typist.
     *
     * @param newTypingBonus the replacement typing bonus
     */
    public void setTypingBonus(double newTypingBonus)
    {
        typingBonus = newTypingBonus;
    }

    /**
     * Changes the multiplier applied to mistype chance.
     * Values below zero are clamped to zero.
     *
     * @param newMistypeChanceMultiplier the replacement multiplier
     */
    public void setMistypeChanceMultiplier(double newMistypeChanceMultiplier)
    {
        if (newMistypeChanceMultiplier < 0.0)
        {
            mistypeChanceMultiplier = 0.0;
        }
        else
        {
            mistypeChanceMultiplier = newMistypeChanceMultiplier;
        }
    }

    /**
     * Changes the multiplier applied to burnout chance.
     * Values below zero are clamped to zero.
     *
     * @param newBurnoutChanceMultiplier the replacement multiplier
     */
    public void setBurnoutChanceMultiplier(double newBurnoutChanceMultiplier)
    {
        if (newBurnoutChanceMultiplier < 0.0)
        {
            burnoutChanceMultiplier = 0.0;
        }
        else
        {
            burnoutChanceMultiplier = newBurnoutChanceMultiplier;
        }
    }
}
