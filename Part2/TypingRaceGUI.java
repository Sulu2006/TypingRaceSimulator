import java.awt.*;
import javax.swing.*;

public class TypingRaceGUI extends JFrame
{
    private JLabel statusLabel;
    private JButton startButton;

    public TypingRaceGUI()
    {
        setTitle("Typing Race GUI");
        setSize(500, 300);
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

        startButton = new JButton("Start Race");

        statusLabel = new JLabel("GUI shell created.", SwingConstants.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(startButton, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);

        startButton.addActionListener(e -> statusLabel.setText("Start button clicked."));
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
