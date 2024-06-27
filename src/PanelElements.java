import dbTables.PostAddress;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelElements {

    // Elements of main frame
    private JFormattedTextField postCodeFromField;
    private JFormattedTextField postCodeToField;
    private String SelectedVehicle;
    private JButton goButton;
    private JButton algorithmButton;
    private JButton accessibilityButton;
    private JToggleButton busRouteButton;
    private JToggleButton footButton;
    private JToggleButton bikeButton;
    private JToggleButton carButton;

    // Singleton instance
    private static PanelElements instance;

    // Private constructor to prevent instantiation
    private PanelElements() {
    }

    // Public method to return the single instance of the class
    public static synchronized PanelElements getInstance() {
        if (instance == null) {
            instance = new PanelElements();
        }
        return instance;
    }

    // Creates the map image
    JPanel createMapPanel() {
        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (DrawingUtils.getMapImage() != null) {
                    g.drawImage(DrawingUtils.getMapImage(), 0, 0, 621, 557, this);
                }
            }
        };
        mapPanel.setPreferredSize(new Dimension(621, 557));
        return mapPanel;
    }

    // Creates custom input field
    private JFormattedTextField createPostCodeField(String contents, Point textPanelPosition) {
        JFormattedTextField postCodeField = new JFormattedTextField();
        postCodeField.setPreferredSize(new Dimension(60, 30));
        postCodeField.setText(contents);

        int xOffset = textPanelPosition.x + 100 + 20;
        int yOffset = textPanelPosition.y;
        postCodeField.setBounds(xOffset, yOffset, 80, 30);

        return postCodeField;
    }

    // Untoggles all buttons
    void untoggleAllButtons() {
        footButton.setSelected(false);
        bikeButton.setSelected(false);
        carButton.setSelected(false);
        busRouteButton.setSelected(false);
    }

    // Assembles the control panel by initializing all elements and adding them to a single panel
    JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setPreferredSize(new Dimension(200, 600));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel fromLabel = new JLabel("From: ");
        controlPanel.add(fromLabel, gbc);

        gbc.gridy++;
        postCodeFromField = createPostCodeField("6218BK", fromLabel.getLocation());
        controlPanel.add(postCodeFromField, gbc);

        gbc.gridy++;
        JLabel toLabel = new JLabel("To: ");
        controlPanel.add(toLabel, gbc);

        gbc.gridy++;
        postCodeToField = createPostCodeField("6229GV", toLabel.getLocation());
        controlPanel.add(postCodeToField, gbc);

        gbc.gridy++;
        footButton = createToggleButton("FOOT", "walk_hollow.png", "walk.png");
        controlPanel.add(footButton, gbc);

        gbc.gridy++;
        bikeButton = createToggleButton("BIKE", "bike_hollow.png", "bike.png");
        controlPanel.add(bikeButton, gbc);

        gbc.gridy++;
        carButton = createToggleButton("CAR", "car_hollow.png", "car.png");
        controlPanel.add(carButton, gbc);

        gbc.gridy++;
        busRouteButton = createToggleButton("BUS", "bus_hollow.png", "bus.png");
        controlPanel.add(busRouteButton, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        goButton = createAlgorithmButton("Straight Line");
        controlPanel.add(goButton, gbc);

        gbc.gridy++;
        algorithmButton = createAlgorithmButton("Shortest Route");
        controlPanel.add(algorithmButton, gbc);

        gbc.gridy++;
        accessibilityButton = createAlgorithmButton("Accessibility Score");
        controlPanel.add(accessibilityButton, gbc);

        return controlPanel;
    }

    // Create button that calls any algorithm
    private JButton createAlgorithmButton(String name) {
        JButton algorithmButton = new JButton(name);
        algorithmButton.setPreferredSize(new Dimension(150, 30));
        algorithmButton.setBackground(Color.WHITE);
        algorithmButton.setForeground(Color.BLUE);
        return algorithmButton;
    }

    // Create button for any vehicle
    private JToggleButton createToggleButton(String text, String hollowIcon, String fillIcon) {
        String prefix = "data/img/icons/";
        int width = 20;
        int height = 20;
        ImageIcon hollow = new ImageIcon(new ImageIcon(prefix + hollowIcon).getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        ImageIcon filled = new ImageIcon(new ImageIcon(prefix + fillIcon).getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        JToggleButton button = new JToggleButton(text, hollow);
        button.setSelectedIcon(filled);
        button.setPreferredSize(new Dimension(width + 100, height));
        button.addActionListener(e -> {
            untoggleAllButtons();
            button.setSelected(true);
            SelectedVehicle = text;
        });
        return button;
    }

    public JToggleButton getBusRouteButton() {
        return busRouteButton;
    }

    public String getSelectedVehicle() {
        return SelectedVehicle;
    }

    public JFormattedTextField getPostCodeFromField() {
        return postCodeFromField;
    }

    public JFormattedTextField getPostCodeToField() {
        return postCodeToField;
    }

    public JButton getGoButton() {
        return goButton;
    }

    public JButton getAlgorithmButton() {
        return algorithmButton;
    }

    public JButton getAccessibilityButton() {
        return accessibilityButton;
    }
}
