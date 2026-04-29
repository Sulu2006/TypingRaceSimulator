package Part2;

import java.awt.*;
import javax.swing.*;

// These fixed tables keep the GUI simple by driving dropdown choices
// and default typist data from one place.
public class TypingRaceGUI extends JFrame
{
    private static final String SETUP_CARD = "setup";
    private static final String RACE_CARD = "race";
    private static final int TRACK_DISPLAY_LENGTH = 40;
    private static final int PASSAGE_HTML_WIDTH = 680;

    private static final String[] DEFAULT_TYPING_NAMES = {
        "TURBOFINGERS",
        "QWERTY_QUEEN",
        "HUNT_N_PECK",
        "KEYSMASHER",
        "SHIFTSPRINTER",
        "TAPMASTER"
    };

    private static final char[] DEFAULT_TYPING_SYMBOLS = {
        '\u2460', '\u2461', '\u2462', '\u2463', '\u2464', '\u2465'
    };

    private static final double[] DEFAULT_TYPING_ACCURACIES = {
        0.85, 0.75, 0.65, 0.55, 0.45, 0.35
    };

    private static final String[] TYPING_STYLE_CHOICES = {
        "Touch Typist",
        "Hunt & Peck",
        "Phone Thumbs",
        "Voice-to-Text"
    };

    private static final String[] KEYBOARD_TYPE_CHOICES = {
        "Mechanical",
        "Membrane",
        "Touchscreen",
        "Stenography"
    };

    private static final String[] COLOUR_CHOICES = {
        "Green",
        "Blue",
        "Red",
        "Orange",
        "Purple",
        "Black"
    };

    private static final String[] ACCESSORY_CHOICES = {
        "None",
        "Wrist Support",
        "Energy Drink",
        "Noise-Cancelling Headphones"
    };

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JComboBox<String> passageChoiceBox;
    private JTextArea customPassageArea;
    private JSpinner seatCountSpinner;

    private JCheckBox autocorrectBox;
    private JCheckBox caffeineModeBox;
    private JCheckBox nightShiftBox;

    private JPanel typistCustomisationPanel;
    private JPanel[] typistConfigPanels;
    private JComboBox<String>[] typingStyleBoxes;
    private JComboBox<String>[] keyboardTypeBoxes;
    private JComboBox<String>[] colourBoxes;
    private JComboBox<String>[] accessoryBoxes;
    private JTextField[] symbolFields;
    private JLabel[] impactSummaryLabels;

    private JButton startButton;
    private JLabel configStatusLabel;

    private JTextArea passageDisplayArea;
    private JPanel raceLanesPanel;
    private JLabel turnLabel;
    private JLabel modifierLabel;
    private JLabel raceStatusLabel;
    private JButton backButton;

    private JLabel[] laneHeaderLabels;
    private JLabel[] laneTrackLabels;
    private JLabel[] lanePassageLabels;
    private Timer raceTimer;
    private TypingRace currentRace;

    private Color[] activeTypistColours;

    private String selectedPassage;
    private int selectedSeatCount;
    private boolean autocorrectOn;
    private boolean caffeineModeOn;
    private boolean nightShiftOn;

    public TypingRaceGUI()
    {
        setTitle("Typing Race GUI");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildGUI();
    }

    private void buildGUI()
    {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(buildSetupPanel(), SETUP_CARD);
        cardPanel.add(buildRacePanel(), RACE_CARD);

        add(cardPanel);
        cardLayout.show(cardPanel, SETUP_CARD);
    }

    // The setup screen collects the overall race options first,
    // then shows a basic customisation area for each possible seat.
    private JPanel buildSetupPanel()
    {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Typing Race Simulator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        JPanel configPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        configPanel.add(new JLabel("Passage:"));

        passageChoiceBox = new JComboBox<>(new String[] {
            "Short Passage",
            "Medium Passage",
            "Long Passage",
            "Custom Passage"
        });
        configPanel.add(passageChoiceBox);

        configPanel.add(new JLabel("Custom Passage:"));

        customPassageArea = new JTextArea(3, 20);
        customPassageArea.setEnabled(false);
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);

        JScrollPane customScrollPane = new JScrollPane(customPassageArea);
        configPanel.add(customScrollPane);

        passageChoiceBox.addActionListener(e -> {
            String selectedChoice = (String) passageChoiceBox.getSelectedItem();

            if ("Custom Passage".equals(selectedChoice))
            {
                customPassageArea.setEnabled(true);
            }
            else
            {
                customPassageArea.setEnabled(false);
                customPassageArea.setText("");
            }
        });

        configPanel.add(new JLabel("Number of Typists:"));

        seatCountSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 6, 1));
        configPanel.add(seatCountSpinner);

        configPanel.add(new JLabel("Autocorrect:"));

        autocorrectBox = new JCheckBox("Slide-back is reduced");
        configPanel.add(autocorrectBox);

        configPanel.add(new JLabel("Caffeine Mode:"));

        caffeineModeBox = new JCheckBox("Early boost, higher burnout risk");
        configPanel.add(caffeineModeBox);

        configPanel.add(new JLabel("Night Shift:"));

        nightShiftBox = new JCheckBox("Accuracy reduced");
        configPanel.add(nightShiftBox);

        JPanel centrePanel = new JPanel(new BorderLayout(10, 10));
        centrePanel.add(configPanel, BorderLayout.NORTH);
        centrePanel.add(buildTypistCustomisationScrollPane(), BorderLayout.CENTER);

        seatCountSpinner.addChangeListener(e -> updateTypistCustomisationVisibility());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        startButton = new JButton("Start Race");
        configStatusLabel = new JLabel(
            "Choose race settings, customise your typists, then press Start Race.",
            SwingConstants.CENTER
        );

        bottomPanel.add(startButton, BorderLayout.NORTH);
        bottomPanel.add(configStatusLabel, BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centrePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startConfiguredRace());

        return mainPanel;
    }

    // Build one repeated config panel per seat and simply hide the ones
    // above the currently selected seat count.
    @SuppressWarnings("unchecked")
    private JScrollPane buildTypistCustomisationScrollPane()
    {
        typistCustomisationPanel = new JPanel();
        typistCustomisationPanel.setLayout(new BoxLayout(typistCustomisationPanel, BoxLayout.Y_AXIS));
        typistCustomisationPanel.setBorder(BorderFactory.createTitledBorder("Typist Customisation"));

        typistConfigPanels = new JPanel[DEFAULT_TYPING_NAMES.length];
        typingStyleBoxes = new JComboBox[DEFAULT_TYPING_NAMES.length];
        keyboardTypeBoxes = new JComboBox[DEFAULT_TYPING_NAMES.length];
        colourBoxes = new JComboBox[DEFAULT_TYPING_NAMES.length];
        accessoryBoxes = new JComboBox[DEFAULT_TYPING_NAMES.length];
        symbolFields = new JTextField[DEFAULT_TYPING_NAMES.length];
        impactSummaryLabels = new JLabel[DEFAULT_TYPING_NAMES.length];

        for (int i = 0; i < DEFAULT_TYPING_NAMES.length; i++)
        {
            int configIndex = i;
            JPanel seatPanel = new JPanel(new GridLayout(6, 2, 8, 8));
            seatPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Seat " + (i + 1) + " - " + DEFAULT_TYPING_NAMES[i]),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
            ));

            JTextField symbolField = new JTextField(String.valueOf(DEFAULT_TYPING_SYMBOLS[i]));
            JComboBox<String> styleBox = new JComboBox<>(TYPING_STYLE_CHOICES);
            JComboBox<String> keyboardBox = new JComboBox<>(KEYBOARD_TYPE_CHOICES);
            JComboBox<String> colourBox = new JComboBox<>(COLOUR_CHOICES);
            JComboBox<String> accessoryBox = new JComboBox<>(ACCESSORY_CHOICES);
            JLabel impactLabel = new JLabel();

            symbolField.setColumns(2);
            colourBox.setSelectedIndex(i % COLOUR_CHOICES.length);

            symbolFields[i] = symbolField;
            typingStyleBoxes[i] = styleBox;
            keyboardTypeBoxes[i] = keyboardBox;
            colourBoxes[i] = colourBox;
            accessoryBoxes[i] = accessoryBox;
            impactSummaryLabels[i] = impactLabel;
            typistConfigPanels[i] = seatPanel;

            seatPanel.add(new JLabel("Symbol:"));
            seatPanel.add(symbolField);
            seatPanel.add(new JLabel("Typing Style:"));
            seatPanel.add(styleBox);
            seatPanel.add(new JLabel("Keyboard Type:"));
            seatPanel.add(keyboardBox);
            seatPanel.add(new JLabel("Colour:"));
            seatPanel.add(colourBox);
            seatPanel.add(new JLabel("Accessory:"));
            seatPanel.add(accessoryBox);
            seatPanel.add(new JLabel("Impact:"));
            seatPanel.add(impactLabel);

            styleBox.addActionListener(e -> updateImpactSummary(configIndex));
            keyboardBox.addActionListener(e -> updateImpactSummary(configIndex));
            accessoryBox.addActionListener(e -> updateImpactSummary(configIndex));

            updateImpactSummary(configIndex);

            typistCustomisationPanel.add(seatPanel);
        }

        updateTypistCustomisationVisibility();

        JScrollPane customisationScrollPane = new JScrollPane(typistCustomisationPanel);
        customisationScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        customisationScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        return customisationScrollPane;
    }

    private JPanel buildRacePanel()
    {
        JPanel racePanel = new JPanel(new BorderLayout(10, 10));
        racePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel raceTitleLabel = new JLabel("Race In Progress", SwingConstants.CENTER);
        raceTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        turnLabel = new JLabel("Turn: 0", SwingConstants.CENTER);
        modifierLabel = new JLabel("", SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        topPanel.add(raceTitleLabel);
        topPanel.add(turnLabel);
        topPanel.add(modifierLabel);

        passageDisplayArea = new JTextArea(4, 40);
        passageDisplayArea.setEditable(false);
        passageDisplayArea.setLineWrap(true);
        passageDisplayArea.setWrapStyleWord(true);
        passageDisplayArea.setFont(new Font("Arial", Font.PLAIN, 15));
        passageDisplayArea.setBackground(new Color(245, 245, 245));

        JScrollPane passageScrollPane = new JScrollPane(passageDisplayArea);
        passageScrollPane.setBorder(BorderFactory.createTitledBorder("Selected Passage"));

        raceLanesPanel = new JPanel();
        raceLanesPanel.setLayout(new BoxLayout(raceLanesPanel, BoxLayout.Y_AXIS));

        JScrollPane laneScrollPane = new JScrollPane(raceLanesPanel);
        laneScrollPane.setBorder(BorderFactory.createTitledBorder("Typist Progress"));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(passageScrollPane, BorderLayout.NORTH);
        centerPanel.add(laneScrollPane, BorderLayout.CENTER);

        raceStatusLabel = new JLabel("Race not started yet.", SwingConstants.CENTER);
        backButton = new JButton("Back to Setup");
        backButton.addActionListener(e -> returnToSetup());

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(raceStatusLabel, BorderLayout.CENTER);

        racePanel.add(topPanel, BorderLayout.NORTH);
        racePanel.add(centerPanel, BorderLayout.CENTER);
        racePanel.add(bottomPanel, BorderLayout.SOUTH);

        return racePanel;
    }

    // Once Start is pressed, the form values are copied into the backend
    // and converted into the typists used by the race engine.
    private void startConfiguredRace()
    {
        selectedPassage = getSelectedPassage();

        if (selectedPassage.isEmpty())
        {
            configStatusLabel.setText("Please enter a custom passage.");
            return;
        }

        selectedSeatCount = (int) seatCountSpinner.getValue();
        autocorrectOn = autocorrectBox.isSelected();
        caffeineModeOn = caffeineModeBox.isSelected();
        nightShiftOn = nightShiftBox.isSelected();

        activeTypistColours = new Color[selectedSeatCount];

        currentRace = new TypingRace(selectedPassage, selectedSeatCount);
        currentRace.setAutocorrectEnabled(autocorrectOn);
        currentRace.setCaffeineModeEnabled(caffeineModeOn);
        currentRace.setNightShiftEnabled(nightShiftOn);

        addConfiguredTypistsToRace();

        if (!currentRace.startRace())
        {
            configStatusLabel.setText("Race could not start.");
            return;
        }

        prepareRaceScreen();
        cardLayout.show(cardPanel, RACE_CARD);
        startRaceTimer();
    }

    private void addConfiguredTypistsToRace()
    {
        for (int seatNumber = 1; seatNumber <= selectedSeatCount; seatNumber++)
        {
            int typistIndex = seatNumber - 1;
            Typist typist = new Typist(
                getSelectedSymbol(typistIndex),
                DEFAULT_TYPING_NAMES[typistIndex],
                DEFAULT_TYPING_ACCURACIES[typistIndex]
            );

            applyTypistCustomisation(typist, typistIndex);
            currentRace.addTypist(typist, seatNumber);
        }
    }

    private void applyTypistCustomisation(Typist typist, int typistIndex)
    {
        String typingStyle = (String) typingStyleBoxes[typistIndex].getSelectedItem();
        String keyboardType = (String) keyboardTypeBoxes[typistIndex].getSelectedItem();
        String colourChoice = (String) colourBoxes[typistIndex].getSelectedItem();
        String accessoryChoice = (String) accessoryBoxes[typistIndex].getSelectedItem();

        double updatedAccuracy = typist.getAccuracy()
            + getTypingStyleAccuracyBonus(typingStyle)
            + getKeyboardAccuracyBonus(keyboardType)
            + getAccessoryAccuracyBonus(accessoryChoice);

        typist.setAccuracy(updatedAccuracy);
        typist.setTypingBonus(
            getTypingStyleSpeedBonus(typingStyle)
            + getKeyboardSpeedBonus(keyboardType)
            + getAccessorySpeedBonus(accessoryChoice)
        );
        typist.setMistypeChanceMultiplier(
            getKeyboardMistypeMultiplier(keyboardType)
            * getAccessoryMistypeMultiplier(accessoryChoice)
        );
        typist.setBurnoutChanceMultiplier(
            getTypingStyleBurnoutMultiplier(typingStyle)
            * getAccessoryBurnoutMultiplier(accessoryChoice)
        );

        activeTypistColours[typistIndex] = getColourValue(colourChoice);
    }

    private void prepareRaceScreen()
    {
        passageDisplayArea.setText(currentRace.getPassageText());
        passageDisplayArea.setCaretPosition(0);
        turnLabel.setText("Turn: 0");
        modifierLabel.setText(buildModifierSummary());
        raceStatusLabel.setText("Race in progress...");

        buildLanePanels();
        updateRaceScreen();
    }

    private void buildLanePanels()
    {
        raceLanesPanel.removeAll();
        laneHeaderLabels = new JLabel[currentRace.getSeatCount()];
        laneTrackLabels = new JLabel[currentRace.getSeatCount()];
        lanePassageLabels = new JLabel[currentRace.getSeatCount()];

        for (int i = 0; i < laneHeaderLabels.length; i++)
        {
            JPanel lanePanel = new JPanel(new BorderLayout(5, 5));
            lanePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            lanePanel.setBackground(Color.WHITE);
            lanePanel.setOpaque(true);
            lanePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            JLabel headerLabel = new JLabel();
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel trackLabel = new JLabel();
            trackLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

            JLabel passageLabel = new JLabel();
            passageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            passageLabel.setVerticalAlignment(SwingConstants.TOP);

            laneHeaderLabels[i] = headerLabel;
            laneTrackLabels[i] = trackLabel;
            lanePassageLabels[i] = passageLabel;

            lanePanel.add(headerLabel, BorderLayout.NORTH);
            lanePanel.add(trackLabel, BorderLayout.CENTER);
            lanePanel.add(passageLabel, BorderLayout.SOUTH);

            raceLanesPanel.add(lanePanel);
            raceLanesPanel.add(Box.createVerticalStrut(10));
        }

        raceLanesPanel.revalidate();
        raceLanesPanel.repaint();
    }

    private void startRaceTimer()
    {
        stopRaceTimer();

        raceTimer = new Timer(200, e -> {
            currentRace.advanceRace();
            updateRaceScreen();

            if (currentRace.isFinished())
            {
                stopRaceTimer();
            }
        });

        raceTimer.start();
    }

    private void stopRaceTimer()
    {
        if (raceTimer != null)
        {
            raceTimer.stop();
            raceTimer = null;
        }
    }

    // The Swing timer advances the backend one turn at a time, and this
    // method refreshes every typist panel from the latest race state.
    private void updateRaceScreen()
    {
        turnLabel.setText("Turn: " + currentRace.getTurnNumber());

        for (int seatNumber = 1; seatNumber <= currentRace.getSeatCount(); seatNumber++)
        {
            int seatIndex = seatNumber - 1;
            Typist typist = currentRace.getTypist(seatNumber);
            Color typistColour = activeTypistColours[seatIndex];

            laneHeaderLabels[seatIndex].setText(buildLaneHeaderText(typist, seatNumber));
            laneHeaderLabels[seatIndex].setForeground(typistColour);
            laneTrackLabels[seatIndex].setText(buildTrackText(typist));
            laneTrackLabels[seatIndex].setForeground(typistColour);
            lanePassageLabels[seatIndex].setText(buildPassageProgressHtml(typist, seatIndex));
        }

        if (currentRace.isFinished())
        {
            raceStatusLabel.setText(
                "Winner: "
                + currentRace.getWinner().getName()
                + " after "
                + currentRace.getTurnNumber()
                + " turns."
            );
        }
        else
        {
            raceStatusLabel.setText("Race in progress...");
        }
    }

    private String buildLaneHeaderText(Typist typist, int seatNumber)
    {
        StringBuilder laneText = new StringBuilder();
        laneText.append("Seat ");
        laneText.append(seatNumber);
        laneText.append("  ");
        laneText.append(typist.getSymbol());
        laneText.append("  ");
        laneText.append(typist.getName());
        laneText.append("  Progress: ");
        laneText.append(typist.getProgress());
        laneText.append("/");
        laneText.append(currentRace.getPassageLength());

        if (currentRace.didSeatJustMistype(seatNumber))
        {
            laneText.append("  Mistyped");
        }

        if (typist.isBurntOut())
        {
            laneText.append("  BURNT OUT (");
            laneText.append(typist.getBurnoutTurnsRemaining());
            laneText.append(")");
        }

        return laneText.toString();
    }

    // HTML labels are used here so the already-typed part of the passage
    // can be coloured without needing a more advanced text component.
    private String buildPassageProgressHtml(Typist typist, int seatIndex)
    {
        String passage = currentRace.getPassageText();
        int progress = typist.getProgress();

        if (progress < 0)
        {
            progress = 0;
        }
        else if (progress > passage.length())
        {
            progress = passage.length();
        }

        String typedText = escapeHtml(passage.substring(0, progress));
        String currentText = "";
        String remainingText;

        if (progress < passage.length())
        {
            currentText = escapeHtml(passage.substring(progress, progress + 1));
            remainingText = escapeHtml(passage.substring(progress + 1));
        }
        else
        {
            remainingText = "";
        }

        return "<html><div style='width:"
            + PASSAGE_HTML_WIDTH
            + "px; font-family:Arial; font-size:12px;'>"
            + "<span style='color:"
            + getHexColour(activeTypistColours[seatIndex])
            + "; font-weight:bold;'>"
            + typedText
            + "</span>"
            + (currentText.isEmpty()
                ? ""
                : "<span style='background-color:#ffe599; color:#1f1f1f; font-weight:bold;'>"
                    + currentText
                    + "</span>")
            + "<span style='color:#333333;'>"
            + remainingText
            + "</span></div></html>";
    }

    private String buildTrackText(Typist typist)
    {
        int trackPosition = 0;

        if (currentRace.getPassageLength() > 1)
        {
            trackPosition = (int) Math.round(
                ((double) typist.getProgress() / currentRace.getPassageLength())
                * (TRACK_DISPLAY_LENGTH - 1)
            );
        }
        else if (typist.getProgress() > 0)
        {
            trackPosition = TRACK_DISPLAY_LENGTH - 1;
        }

        if (trackPosition < 0)
        {
            trackPosition = 0;
        }
        else if (trackPosition >= TRACK_DISPLAY_LENGTH)
        {
            trackPosition = TRACK_DISPLAY_LENGTH - 1;
        }

        StringBuilder trackText = new StringBuilder();
        trackText.append("Track  |");

        for (int i = 0; i < TRACK_DISPLAY_LENGTH; i++)
        {
            if (i == trackPosition)
            {
                trackText.append(typist.getSymbol());
            }
            else
            {
                trackText.append('-');
            }
        }

        trackText.append('|');
        return trackText.toString();
    }

    private String buildModifierSummary()
    {
        return "<html>Modifiers: "
            + (autocorrectOn
                ? "<span style='color:#2e8b57;'>Autocorrect ON</span> (slide-back 1)"
                : "<span style='color:#666666;'>Autocorrect OFF</span> (slide-back 2)")
            + "  |  "
            + (caffeineModeOn
                ? "<span style='color:#2e8b57;'>Caffeine ON</span> (+speed first 10 turns, more burnout later)"
                : "<span style='color:#666666;'>Caffeine OFF</span>")
            + "  |  "
            + (nightShiftOn
                ? "<span style='color:#2e8b57;'>Night Shift ON</span> (-accuracy for everyone)"
                : "<span style='color:#666666;'>Night Shift OFF</span>")
            + "</html>";
    }

    private void updateTypistCustomisationVisibility()
    {
        int visibleSeatCount = (int) seatCountSpinner.getValue();

        for (int i = 0; i < typistConfigPanels.length; i++)
        {
            typistConfigPanels[i].setVisible(i < visibleSeatCount);
        }

        typistCustomisationPanel.revalidate();
        typistCustomisationPanel.repaint();
    }

    private char getSelectedSymbol(int typistIndex)
    {
        String symbolText = symbolFields[typistIndex].getText().trim();

        if (symbolText.isEmpty())
        {
            return DEFAULT_TYPING_SYMBOLS[typistIndex];
        }

        return symbolText.charAt(0);
    }

    // These helper methods keep the effect values in one place so the
    // simple customisation system is easier to read and tweak.
    private double getTypingStyleAccuracyBonus(String typingStyle)
    {
        return switch (typingStyle) {
            case "Touch Typist" -> 0.08;
            case "Hunt & Peck" -> -0.05;
            case "Phone Thumbs" -> -0.10;
            case "Voice-to-Text" -> 0.12;
            default -> 0.0;
        };
    }

    private double getTypingStyleSpeedBonus(String typingStyle)
    {
        return switch (typingStyle) {
            case "Touch Typist" -> 0.08;
            case "Hunt & Peck" -> -0.03;
            case "Phone Thumbs" -> -0.02;
            case "Voice-to-Text" -> 0.15;
            default -> 0.0;
        };
    }

    private double getTypingStyleBurnoutMultiplier(String typingStyle)
    {
        return switch (typingStyle) {
            case "Touch Typist" -> 1.10;
            case "Hunt & Peck" -> 0.80;
            case "Phone Thumbs" -> 0.90;
            case "Voice-to-Text" -> 1.25;
            default -> 1.0;
        };
    }

    private double getKeyboardAccuracyBonus(String keyboardType)
    {
        return switch (keyboardType) {
            case "Mechanical" -> 0.05;
            case "Membrane" -> 0.0;
            case "Touchscreen" -> -0.08;
            case "Stenography" -> 0.10;
            default -> 0.0;
        };
    }

    private double getKeyboardSpeedBonus(String keyboardType)
    {
        return switch (keyboardType) {
            case "Mechanical" -> 0.03;
            case "Membrane" -> 0.0;
            case "Touchscreen" -> -0.05;
            case "Stenography" -> 0.12;
            default -> 0.0;
        };
    }

    private double getKeyboardMistypeMultiplier(String keyboardType)
    {
        return switch (keyboardType) {
            case "Mechanical" -> 0.90;
            case "Membrane" -> 1.0;
            case "Touchscreen" -> 1.25;
            case "Stenography" -> 0.85;
            default -> 1.0;
        };
    }

    private double getAccessoryAccuracyBonus(String accessoryChoice)
    {
        return switch (accessoryChoice) {
            case "Energy Drink" -> 0.04;
            case "Noise-Cancelling Headphones" -> 0.03;
            default -> 0.0;
        };
    }

    private double getAccessorySpeedBonus(String accessoryChoice)
    {
        return switch (accessoryChoice) {
            case "Energy Drink" -> 0.06;
            default -> 0.0;
        };
    }

    private double getAccessoryMistypeMultiplier(String accessoryChoice)
    {
        return switch (accessoryChoice) {
            case "Noise-Cancelling Headphones" -> 0.75;
            case "Energy Drink" -> 1.10;
            default -> 1.0;
        };
    }

    private double getAccessoryBurnoutMultiplier(String accessoryChoice)
    {
        return switch (accessoryChoice) {
            case "Wrist Support" -> 0.70;
            case "Energy Drink" -> 1.30;
            default -> 1.0;
        };
    }

    private Color getColourValue(String colourChoice)
    {
        return switch (colourChoice) {
            case "Blue" -> new Color(30, 144, 255);
            case "Red" -> new Color(204, 51, 51);
            case "Orange" -> new Color(255, 140, 0);
            case "Purple" -> new Color(138, 43, 226);
            case "Black" -> new Color(34, 34, 34);
            default -> new Color(46, 139, 87);
        };
    }

    private void updateImpactSummary(int typistIndex)
    {
        impactSummaryLabels[typistIndex].setText(buildImpactSummaryText(typistIndex));
    }

    private String buildImpactSummaryText(int typistIndex)
    {
        String typingStyle = (String) typingStyleBoxes[typistIndex].getSelectedItem();
        String keyboardType = (String) keyboardTypeBoxes[typistIndex].getSelectedItem();
        String accessoryChoice = (String) accessoryBoxes[typistIndex].getSelectedItem();

        double accuracyChange = getTypingStyleAccuracyBonus(typingStyle)
            + getKeyboardAccuracyBonus(keyboardType)
            + getAccessoryAccuracyBonus(accessoryChoice);
        double speedChange = getTypingStyleSpeedBonus(typingStyle)
            + getKeyboardSpeedBonus(keyboardType)
            + getAccessorySpeedBonus(accessoryChoice);
        double mistypeMultiplier = getKeyboardMistypeMultiplier(keyboardType)
            * getAccessoryMistypeMultiplier(accessoryChoice);
        double burnoutMultiplier = getTypingStyleBurnoutMultiplier(typingStyle)
            * getAccessoryBurnoutMultiplier(accessoryChoice);

        return "<html>"
            + "Accuracy: " + describeChange(accuracyChange)
            + " | Speed: " + describeChange(speedChange)
            + "<br>Mistypes: " + describeRisk(mistypeMultiplier)
            + " | Burnout: " + describeRisk(burnoutMultiplier)
            + "</html>";
    }

    private String describeChange(double changeValue)
    {
        if (changeValue > 0.02)
        {
            return "higher";
        }

        if (changeValue < -0.02)
        {
            return "lower";
        }

        return "normal";
    }

    private String describeRisk(double riskMultiplier)
    {
        if (riskMultiplier > 1.05)
        {
            return "higher";
        }

        if (riskMultiplier < 0.95)
        {
            return "lower";
        }

        return "normal";
    }

    private String getHexColour(Color colour)
    {
        return String.format("#%02x%02x%02x", colour.getRed(), colour.getGreen(), colour.getBlue());
    }

    private void returnToSetup()
    {
        stopRaceTimer();
        configStatusLabel.setText("Choose race settings, customise your typists, then press Start Race.");
        cardLayout.show(cardPanel, SETUP_CARD);
    }

    private String escapeHtml(String text)
    {
        StringBuilder escapedText = new StringBuilder();

        for (int i = 0; i < text.length(); i++)
        {
            char currentCharacter = text.charAt(i);

            switch (currentCharacter) {
                case '&' -> escapedText.append("&amp;");
                case '<' -> escapedText.append("&lt;");
                case '>' -> escapedText.append("&gt;");
                case '"' -> escapedText.append("&quot;");
                case '\'' -> escapedText.append("&#39;");
                case '\n' -> escapedText.append("<br>");
                case '\r' -> {
                }
                default -> escapedText.append(currentCharacter);
            }
        }

        return escapedText.toString();
    }

    private String getSelectedPassage()
    {
        String choice = (String) passageChoiceBox.getSelectedItem();

        return switch (choice) {
            case "Short Passage" -> "The quick brown fox jumps over the lazy dog.";
            case "Medium Passage" -> "Typing quickly and accurately takes practice every single day.";
            case "Long Passage" -> "In this typing race simulation, several typists compete while mistakes, progress, and burnout affect the final result.";
            default -> customPassageArea.getText().trim();
        };
    }

    public void startRaceGUI()
    {
        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            TypingRaceGUI gui = new TypingRaceGUI();
            gui.startRaceGUI();
        });
    }
}
