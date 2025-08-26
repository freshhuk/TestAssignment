package org.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


public class NumberSorterApp extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final Random random = new Random();

    // --- UI Components ---
    private JTextField inputField;
    private JPanel numbersPanel;
    private int[] numbers;
    private boolean sortDescending = true;

    // References to the Sort and Reset buttons
    private JButton sortButton;
    private JButton resetButton;

    public NumberSorterApp() {
        setTitle("Number Sorter App");
        setSize(800, 600); // Increased size for better column display
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the two main screens to the card layout
        mainPanel.add(createIntroScreen(), "INTRO");
        mainPanel.add(createSortScreen(), "SORT");

        add(mainPanel);
        cardLayout.show(mainPanel, "INTRO");
    }

    /**
     * Creates the initial screen where the user enters the quantity of numbers.
     */
    private JPanel createIntroScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel("How many numbers to display?");
        inputField = new JTextField(10);
        JButton enterButton = new JButton("Enter");

        enterButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(inputField.getText().trim());
                if (count <= 0) {
                    throw new NumberFormatException();
                }
                generateNumbers(count);
                updateNumbersPanel();
                cardLayout.show(mainPanel, "SORT");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        gbc.gridy = 1;
        panel.add(inputField, gbc);
        gbc.gridy = 2;
        panel.add(enterButton, gbc);

        return panel;
    }

    /**
     * Creates the main screen for displaying and sorting numbers.
     */
    private JPanel createSortScreen() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // We set GridLayout with 10 rows and a variable number of columns (0).
        numbersPanel = new JPanel(new GridLayout(10, 0, 5, 5));

        JScrollPane scrollPane = new JScrollPane(numbersPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel for control buttons
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        sortButton = new JButton("Sort");
        sortButton.addActionListener(e -> new Thread(this::quickSortAndAnimate).start());
        sortButton.setBackground(new Color(60, 179, 113)); // A shade of green
        sortButton.setForeground(Color.WHITE); // White text for contrast
        sortButton.setOpaque(true);
        sortButton.setBorderPainted(false);


        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            inputField.setText("");
            cardLayout.show(mainPanel, "INTRO");
        });
        resetButton.setBackground(new Color(60, 179, 113)); // A shade of green
        resetButton.setForeground(Color.WHITE); // White text for contrast
        resetButton.setOpaque(true);
        resetButton.setBorderPainted(false);


        gbc.gridy = 0;
        buttonsPanel.add(sortButton, gbc);
        gbc.gridy = 1;
        buttonsPanel.add(resetButton, gbc);

        // Wrapper to keep buttons vertically centered and at a fixed width
        JPanel eastPanel = new JPanel();
        eastPanel.add(buttonsPanel);
        panel.add(eastPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Generates an array of random numbers based on the user's input.
     */
    private void generateNumbers(int count) {
        numbers = new int[count];
        boolean hasSmallNumber = false;
        for (int i = 0; i < count; i++) {
            numbers[i] = random.nextInt(1000) + 1;
            if (numbers[i] <= 30) {
                hasSmallNumber = true;
            }
        }
        if (!hasSmallNumber && count > 0) {
            numbers[random.nextInt(count)] = random.nextInt(30) + 1;
        }
    }

    /**
     * Clears and repopulates the numbers panel with buttons.
     */
    private void updateNumbersPanel() {
        SwingUtilities.invokeLater(() -> {
            numbersPanel.removeAll();
            for (int num : numbers) {
                JButton numButton = new JButton(String.valueOf(num));

                numButton.setBackground(new Color(70, 130, 180)); // SteelBlue
                numButton.setForeground(Color.WHITE); // White text for contrast
                numButton.setOpaque(true);
                numButton.setBorderPainted(false);

                numButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int value = Integer.parseInt(numButton.getText());
                        if (value <= 30) {
                            generateNumbers(numbers.length);
                            updateNumbersPanel();
                        } else {
                            JOptionPane.showMessageDialog(NumberSorterApp.this,
                                    "Please select a value smaller or equal to 30.",
                                    "Invalid Selection",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
                numbersPanel.add(numButton);
            }
            numbersPanel.revalidate();
            numbersPanel.repaint();
        });
    }

    // Quicksort Implementation with Animation

    /**
     * Starts the sorting process in a new thread to avoid blocking the UI.
     * Toggles the sorting order after completion.
     */
    private void quickSortAndAnimate() {
        quickSort(0, numbers.length - 1);
        sortDescending = !sortDescending;
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = numbers[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            boolean condition = sortDescending ? (numbers[j] > pivot) : (numbers[j] < pivot);
            if (condition) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    private void swap(int i, int j) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
        updateNumbersPanel();
        sleep();
    }

    private void sleep() {
        try {
            // Animation delay to visualize the sort
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberSorterApp().setVisible(true));
    }
}