import java.awt.*;
import javax.swing.*;

public class TypingRaceGUI extends JFrame
{
    private JComboBox<String> passageChoiceBox;
    private JTextArea customPassageArea;
    private JSpinner seatCountSpinner;

    private JCheckBox autocorrectBox;
    private JCheckBox caffeineModeBox;
    private JCheckBox nightShiftBox;

    private JButton startButton;
    private JLabel statusLabel;

    private String selectedPassage;
    private int selectedSeatCount;
    private boolean autocorrectOn;
    private boolean caffeineModeOn;
    private boolean nightShiftOn;

    public TypingRaceGUI()
    {
        setTitle("Typing Race GUI");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildGUI();
    }

    private void buildGUI()
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Typing Race Simulator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(6, 2, 10, 10));
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

        JScrollPane customScrollPane = new JScrollPane(customPassageArea);
        configPanel.add(customScrollPane);

        passageChoiceBox.addActionListener(e -> {
            String selectedChoice = (String) passageChoiceBox.getSelectedItem();

            if (selectedChoice.equals("Custom Passage"))
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

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        startButton = new JButton("Start Race");
        statusLabel = new JLabel("Choose race settings, then press Start Race.", SwingConstants.CENTER);

        bottomPanel.add(startButton, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(configPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        startButton.addActionListener(e -> readRaceConfiguration());
    }

    private void readRaceConfiguration()
    {
        selectedPassage = getSelectedPassage();

        if (selectedPassage.length() == 0)
        {
            statusLabel.setText("Please enter a custom passage.");
            return;
        }

        selectedSeatCount = (int) seatCountSpinner.getValue();

        autocorrectOn = autocorrectBox.isSelected();
        caffeineModeOn = caffeineModeBox.isSelected();
        nightShiftOn = nightShiftBox.isSelected();

        statusLabel.setText(
            "Race configured: "
            + selectedSeatCount + " typists, "
            + selectedPassage.length() + " characters, "
            + "Autocorrect: " + autocorrectOn + ", "
            + "Caffeine: " + caffeineModeOn + ", "
            + "Night Shift: " + nightShiftOn
        );
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