import java.awt.*;
import javax.swing.*;

public class TypingRaceGUI extends JFrame
{
    private static final String SETUP_CARD = "setup";
    private static final String RACE_CARD = "race";
    private static final int TRACK_DISPLAY_LENGTH = 40;

    private static final String[] DEFAULT_TYPING_NAMES = {
        "TURBOFINGERS",
        "QWERTY_QUEEN",
        "HUNT_N_PECK",
        "KEYSMASHER",
        "SHIFTSPRINTER",
        "TAPMASTER"
    };

    private static final char[] DEFAULT_TYPING_SYMBOLS = {
        '①', '②', '③', '④', '⑤', '⑥'
    };


    private static final double[] DEFAULT_TYPING_ACCURACIES = {
        0.85, 0.75, 0.65, 0.55, 0.45, 0.35
    };

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JComboBox<String> passageChoiceBox;
    private JTextArea customPassageArea;
    private JSpinner seatCountSpinner;

    private JCheckBox autocorrectBox;
    private JCheckBox caffeineModeBox;
    private JCheckBox nightShiftBox;

    private JButton startButton;
    private JLabel configStatusLabel;

    private JTextArea passageDisplayArea;
    private JPanel raceLanesPanel;
    private JLabel turnLabel;
    private JLabel raceStatusLabel;
    private JButton backButton;

    private JLabel[] laneLabels;
    private Timer raceTimer;
    private TypingRace currentRace;

    private String selectedPassage;
    private int selectedSeatCount;
    private boolean autocorrectOn;
    private boolean caffeineModeOn;
    private boolean nightShiftOn;

    public TypingRaceGUI()
    {
        setTitle("Typing Race GUI");
        setSize(850, 600);
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

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        startButton = new JButton("Start Race");
        configStatusLabel = new JLabel(
            "Choose race settings, then press Start Race.",
            SwingConstants.CENTER
        );

        bottomPanel.add(startButton, BorderLayout.NORTH);
        bottomPanel.add(configStatusLabel, BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(configPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startConfiguredRace());

        return mainPanel;
    }

    private JPanel buildRacePanel()
    {
        JPanel racePanel = new JPanel(new BorderLayout(10, 10));
        racePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel raceTitleLabel = new JLabel("Race In Progress", SwingConstants.CENTER);
        raceTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        turnLabel = new JLabel("Turn: 0", SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(raceTitleLabel, BorderLayout.NORTH);
        topPanel.add(turnLabel, BorderLayout.SOUTH);

        passageDisplayArea = new JTextArea(4, 40);
        passageDisplayArea.setEditable(false);
        passageDisplayArea.setLineWrap(true);
        passageDisplayArea.setWrapStyleWord(true);
        passageDisplayArea.setFont(new Font("Arial", Font.PLAIN, 15));

        JScrollPane passageScrollPane = new JScrollPane(passageDisplayArea);
        passageScrollPane.setBorder(BorderFactory.createTitledBorder("Passage"));

        raceLanesPanel = new JPanel();
        raceLanesPanel.setLayout(new BoxLayout(raceLanesPanel, BoxLayout.Y_AXIS));

        JScrollPane laneScrollPane = new JScrollPane(raceLanesPanel);
        laneScrollPane.setBorder(BorderFactory.createTitledBorder("Typists"));

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

        currentRace = new TypingRace(selectedPassage, selectedSeatCount);
        currentRace.setAutocorrectEnabled(autocorrectOn);
        currentRace.setCaffeineModeEnabled(caffeineModeOn);
        currentRace.setNightShiftEnabled(nightShiftOn);

        addDefaultTypistsToRace();

        if (!currentRace.startRace())
        {
            configStatusLabel.setText("Race could not start.");
            return;
        }

        prepareRaceScreen();
        cardLayout.show(cardPanel, RACE_CARD);
        startRaceTimer();
    }

    private void addDefaultTypistsToRace()
    {
        for (int seatNumber = 1; seatNumber <= selectedSeatCount; seatNumber++)
        {
            int typistIndex = seatNumber - 1;
            Typist typist = new Typist(
                DEFAULT_TYPING_SYMBOLS[typistIndex],
                DEFAULT_TYPING_NAMES[typistIndex],
                DEFAULT_TYPING_ACCURACIES[typistIndex]
            );

            currentRace.addTypist(typist, seatNumber);
        }
    }

    private void prepareRaceScreen()
    {
        passageDisplayArea.setText(currentRace.getPassageText());
        turnLabel.setText("Turn: 0");
        raceStatusLabel.setText("Race in progress...");

        buildLaneLabels();
        updateRaceScreen();
    }

    private void buildLaneLabels()
    {
        raceLanesPanel.removeAll();
        laneLabels = new JLabel[currentRace.getSeatCount()];

        for (int i = 0; i < laneLabels.length; i++)
        {
            JLabel laneLabel = new JLabel();
            laneLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            laneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            laneLabels[i] = laneLabel;
            raceLanesPanel.add(laneLabel);
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

    private void updateRaceScreen()
    {
        turnLabel.setText("Turn: " + currentRace.getTurnNumber());

        for (int seatNumber = 1; seatNumber <= currentRace.getSeatCount(); seatNumber++)
        {
            Typist typist = currentRace.getTypist(seatNumber);
            laneLabels[seatNumber - 1].setText(buildLaneText(typist, seatNumber));
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

    private String buildLaneText(Typist typist, int seatNumber)
    {
        StringBuilder laneText = new StringBuilder();
        laneText.append(typist.getName());
        laneText.append("  ");
        laneText.append(buildTrackText(typist));
        laneText.append("  ");
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
        trackText.append('|');

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

    private void returnToSetup()
    {
        stopRaceTimer();
        configStatusLabel.setText("Choose race settings, then press Start Race.");
        cardLayout.show(cardPanel, SETUP_CARD);
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
