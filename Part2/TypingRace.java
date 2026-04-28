/**
 * A GUI-friendly typing race engine.
 * The GUI can configure the race, start it, and then advance it
 * one turn at a time while reading the current state from getters.
 *
 * @author Suleyman Macit
 * @version 2.0
 */
public class TypingRace
{
    private static final int MIN_SEAT_COUNT = 2;
    private static final int MAX_SEAT_COUNT = 6;

    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int SLIDE_BACK_AMOUNT = 2;
    private static final int BURNOUT_DURATION = 3;

    private static final double WINNER_ACCURACY_BONUS = 0.02;
    private static final double BURNOUT_ACCURACY_PENALTY = 0.02;

    private static final int CAFFEINE_BOOST_TURNS = 10;
    private static final double CAFFEINE_TYPING_BONUS = 0.10;
    private static final double CAFFEINE_BURNOUT_MULTIPLIER = 1.5;
    private static final double NIGHT_SHIFT_ACCURACY_PENALTY = 0.05;

    private final String passageText;
    private final int passageLength;
    private final Typist[] typists;
    private final boolean[] justMistyped;

    private boolean autocorrectEnabled;
    private boolean caffeineModeEnabled;
    private boolean nightShiftEnabled;

    private boolean raceStarted;
    private boolean raceFinished;
    private int turnNumber;
    private Typist winner;

    /**
     * Creates a race using the full passage text and number of seats.
     *
     * @param racePassage the passage the typists must complete
     * @param seatCount the number of typists allowed in the race
     */
    public TypingRace(String racePassage, int seatCount)
    {
        if (racePassage == null || racePassage.trim().isEmpty())
        {
            passageText = " ";
        }
        else
        {
            passageText = racePassage;
        }

        passageLength = passageText.length();
        typists = new Typist[clampSeatCount(seatCount)];
        justMistyped = new boolean[typists.length];

        autocorrectEnabled = false;
        caffeineModeEnabled = false;
        nightShiftEnabled = false;

        raceStarted = false;
        raceFinished = false;
        turnNumber = 0;
        winner = null;
    }

    /**
     * Creates a race with the default of three seats.
     *
     * @param racePassage the passage the typists must complete
     */
    public TypingRace(String racePassage)
    {
        this(racePassage, 3);
    }

    /**
     * Places a typist into a seat in the race.
     *
     * @param theTypist the typist to add
     * @param seatNumber the seat number to fill, starting from 1
     * @return true if the typist was seated successfully
     */
    public boolean addTypist(Typist theTypist, int seatNumber)
    {
        int seatIndex = seatNumber - 1;

        if (theTypist == null)
        {
            return false;
        }

        if (seatIndex < 0 || seatIndex >= typists.length)
        {
            return false;
        }

        typists[seatIndex] = theTypist;
        return true;
    }

    /**
     * Starts a new race by resetting all typists and race state.
     *
     * @return true if the race was ready to start
     */
    public boolean startRace()
    {
        if (!allSeatsFilled())
        {
            return false;
        }

        for (Typist typist : typists)
        {
            typist.resetToStart();
        }

        clearMistypeFlags();
        raceStarted = true;
        raceFinished = false;
        turnNumber = 0;
        winner = null;
        return true;
    }

    /**
     * Advances the race by one turn.
     * Each seated typist gets one chance to type, mistype, or burn out.
     *
     * @return true when the race has finished after this turn
     */
    public boolean advanceRace()
    {
        if (!raceStarted || raceFinished)
        {
            return raceFinished;
        }

        turnNumber++;
        clearMistypeFlags();

        for (int seatIndex = 0; seatIndex < typists.length; seatIndex++)
        {
            justMistyped[seatIndex] = advanceTypist(typists[seatIndex]);
        }

        winner = findWinner();

        if (winner != null)
        {
            raceFinished = true;
            winner.setAccuracy(winner.getAccuracy() + WINNER_ACCURACY_BONUS);
        }

        return raceFinished;
    }

    /**
     * Returns true when all seats contain a typist.
     *
     * @return true if every seat is filled
     */
    public boolean allSeatsFilled()
    {
        for (Typist typist : typists)
        {
            if (typist == null)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the passage text for display in the GUI.
     *
     * @return the passage text
     */
    public String getPassageText()
    {
        return passageText;
    }

    /**
     * Returns the number of characters in the passage.
     *
     * @return the passage length
     */
    public int getPassageLength()
    {
        return passageLength;
    }

    /**
     * Returns the number of seats in this race.
     *
     * @return the seat count
     */
    public int getSeatCount()
    {
        return typists.length;
    }

    /**
     * Returns a copy of the typist array so the GUI can inspect it safely.
     *
     * @return the current typists in seat order
     */
    public Typist[] getTypists()
    {
        return typists.clone();
    }

    /**
     * Returns the typist in a specific seat.
     *
     * @param seatNumber the seat number, starting from 1
     * @return the typist in that seat, or null if the seat number is invalid
     */
    public Typist getTypist(int seatNumber)
    {
        int seatIndex = seatNumber - 1;

        if (seatIndex < 0 || seatIndex >= typists.length)
        {
            return null;
        }

        return typists[seatIndex];
    }

    /**
     * Reports whether a given seat mistyped on the most recent turn.
     *
     * @param seatNumber the seat number, starting from 1
     * @return true if that typist just mistyped
     */
    public boolean didSeatJustMistype(int seatNumber)
    {
        int seatIndex = seatNumber - 1;

        if (seatIndex < 0 || seatIndex >= justMistyped.length)
        {
            return false;
        }

        return justMistyped[seatIndex];
    }

    /**
     * Returns whether the race has started.
     *
     * @return true if startRace has been called successfully
     */
    public boolean hasStarted()
    {
        return raceStarted;
    }

    /**
     * Returns whether the race has finished.
     *
     * @return true if a winner has been found
     */
    public boolean isFinished()
    {
        return raceFinished;
    }

    /**
     * Returns the number of turns completed so far.
     *
     * @return the current turn number
     */
    public int getTurnNumber()
    {
        return turnNumber;
    }

    /**
     * Returns the winner of the race, or null if there is none yet.
     *
     * @return the winning typist
     */
    public Typist getWinner()
    {
        return winner;
    }

    /**
     * Enables or disables the autocorrect modifier.
     *
     * @param enabled true to enable autocorrect
     */
    public void setAutocorrectEnabled(boolean enabled)
    {
        autocorrectEnabled = enabled;
    }

    /**
     * Enables or disables the caffeine mode modifier.
     *
     * @param enabled true to enable caffeine mode
     */
    public void setCaffeineModeEnabled(boolean enabled)
    {
        caffeineModeEnabled = enabled;
    }

    /**
     * Enables or disables the night shift modifier.
     *
     * @param enabled true to enable night shift
     */
    public void setNightShiftEnabled(boolean enabled)
    {
        nightShiftEnabled = enabled;
    }

    /**
     * Returns whether autocorrect is enabled.
     *
     * @return true if autocorrect is on
     */
    public boolean isAutocorrectEnabled()
    {
        return autocorrectEnabled;
    }

    /**
     * Returns whether caffeine mode is enabled.
     *
     * @return true if caffeine mode is on
     */
    public boolean isCaffeineModeEnabled()
    {
        return caffeineModeEnabled;
    }

    /**
     * Returns whether night shift is enabled.
     *
     * @return true if night shift is on
     */
    public boolean isNightShiftEnabled()
    {
        return nightShiftEnabled;
    }

    /**
     * Simulates one turn for a single typist.
     *
     * @param theTypist the typist to advance
     * @return true if they mistyped on this turn
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

        boolean seatJustMistyped = false;
        double typingChance = getTypingChance(theTypist);

        if (Math.random() < typingChance)
        {
            theTypist.typeCharacter();
        }
        else if (Math.random() < MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(getSlideBackAmount());
            seatJustMistyped = true;
        }

        if (Math.random() < getBurnoutChance(theTypist))
        {
            theTypist.burnOut(BURNOUT_DURATION);
            theTypist.setAccuracy(theTypist.getAccuracy() - BURNOUT_ACCURACY_PENALTY);
        }

        return seatJustMistyped;
    }

    /**
     * Returns the current chance that a typist will type a character.
     *
     * @param theTypist the typist being checked
     * @return the typing chance for this turn
     */
    private double getTypingChance(Typist theTypist)
    {
        double typingChance = theTypist.getAccuracy();

        if (nightShiftEnabled)
        {
            typingChance = typingChance - NIGHT_SHIFT_ACCURACY_PENALTY;
        }

        if (caffeineModeEnabled && turnNumber <= CAFFEINE_BOOST_TURNS)
        {
            typingChance = typingChance + CAFFEINE_TYPING_BONUS;
        }

        return clampProbability(typingChance);
    }

    /**
     * Returns the chance that a typist burns out on the current turn.
     *
     * @param theTypist the typist being checked
     * @return the burnout chance for this turn
     */
    private double getBurnoutChance(Typist theTypist)
    {
        double burnoutChance = 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy();

        if (caffeineModeEnabled && turnNumber > CAFFEINE_BOOST_TURNS)
        {
            burnoutChance = burnoutChance * CAFFEINE_BURNOUT_MULTIPLIER;
        }

        return clampProbability(burnoutChance);
    }

    /**
     * Returns the number of spaces to slide back after a mistype.
     *
     * @return the current slide-back amount
     */
    private int getSlideBackAmount()
    {
        if (autocorrectEnabled)
        {
            return 1;
        }

        return SLIDE_BACK_AMOUNT;
    }

    /**
     * Searches for the first typist who has finished the full passage.
     *
     * @return the winning typist, or null if nobody has finished yet
     */
    private Typist findWinner()
    {
        for (Typist typist : typists)
        {
            if (typist != null && typist.getProgress() >= passageLength)
            {
                return typist;
            }
        }

        return null;
    }

    /**
     * Clears the recent mistype markers ready for the next turn.
     */
    private void clearMistypeFlags()
    {
        for (int i = 0; i < justMistyped.length; i++)
        {
            justMistyped[i] = false;
        }
    }

    /**
     * Forces the seat count into the supported range.
     *
     * @param seatCount the requested seat count
     * @return a seat count between 2 and 6
     */
    private int clampSeatCount(int seatCount)
    {
        if (seatCount < MIN_SEAT_COUNT)
        {
            return MIN_SEAT_COUNT;
        }

        if (seatCount > MAX_SEAT_COUNT)
        {
            return MAX_SEAT_COUNT;
        }

        return seatCount;
    }

    /**
     * Clamps a probability into the valid range 0.0 to 1.0.
     *
     * @param value the probability to clamp
     * @return the clamped probability
     */
    private double clampProbability(double value)
    {
        if (value < 0.0)
        {
            return 0.0;
        }

        if (value > 1.0)
        {
            return 1.0;
        }

        return value;
    }
}
