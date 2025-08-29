package org.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A graphical application for visualizing the Quicksort algorithm.
 * The user can specify a number of integers to be generated, which are then
 * displayed as buttons. Clicking the "Sort" button animates the sorting process.
 */
public class NumberSorterApp extends JFrame {

    /** Manages switching between the intro and sort screens. */
    private final CardLayout cardLayout;
    /** The main container panel that holds different screens (cards). */
    private final JPanel mainPanel;
    /** A random number generator for creating the initial array. */
    private final Random random = new Random();

    // --- UI Components ---
    /** Text field for user to input the number of items to sort. */
    private JTextField inputField;
    /** Panel that holds the grid of number buttons. */
    private JPanel numbersPanel;
    /** The array of numbers being sorted and visualized. */
    private int[] numbers;
    /** A list to hold references to the JButtons representing the numbers. */
    private final List<JButton> numberButtons = new ArrayList<>();
    /** Toggles the sort order between ascending and descending. */
    private boolean sortDescending = true;

    /**
     * Constructs the main application window and initializes the UI.
     */
    public NumberSorterApp() {
        setupFrame();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createIntroScreen(), "INTRO");
        mainPanel.add(createSortScreen(), "SORT");

        add(mainPanel);
        cardLayout.show(mainPanel, "INTRO");
    }

    /**
     * Configures the main properties of the application window (JFrame).
     */
    private void setupFrame() {
        setTitle("Number Sorter App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Creates the introductory screen where the user specifies the quantity of numbers.
     *
     * @return The fully configured introductory screen as a JPanel.
     */
    private JPanel createIntroScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel("How many numbers to display?");
        inputField = new JTextField(10);
        JButton enterButton = new JButton("Enter");

        enterButton.addActionListener(e -> handleIntroScreenInput());

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
     * Creates the main sorting screen panel.
     * This screen contains the grid of number buttons and control buttons (Sort, Reset).
     *
     * @return The fully configured sorting screen as a JPanel.
     */
    private JPanel createSortScreen() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        numbersPanel = new JPanel(new GridLayout(10, 0, 5, 5));
        panel.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);

        JPanel controlPanel = createControlButtonsPanel();
        panel.add(controlPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the panel containing the control buttons (Sort, Reset).
     *
     * @return A JPanel with the control buttons.
     */
    private JPanel createControlButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        Color buttonColor = new Color(60, 179, 113);

        JButton sortButton = createStyledButton("Sort", buttonColor, e -> new Thread(this::quickSortAndAnimate).start());
        JButton resetButton = createStyledButton("Reset", buttonColor, e -> resetToIntroScreen());

        gbc.gridy = 0;
        buttonsPanel.add(sortButton, gbc);
        gbc.gridy = 1;
        buttonsPanel.add(resetButton, gbc);

        JPanel eastPanel = new JPanel();
        eastPanel.add(buttonsPanel);
        return eastPanel;
    }

    /**
     * Creates and configures a styled JButton.
     *
     * @param text            The text for the button.
     * @param backgroundColor The background color of the button.
     * @param actionListener  The action listener for the button click event.
     * @return A configured JButton.
     */
    private JButton createStyledButton(String text, Color backgroundColor, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    /**
     * Handles the input from the intro screen. It parses the number, validates it,
     * and proceeds to the sort screen or shows an error message.
     */
    private void handleIntroScreenInput() {
        try {
            int count = Integer.parseInt(inputField.getText().trim());
            if (count <= 0) {
                throw new NumberFormatException();
            }
            initializeSortScreen(count);
            cardLayout.show(mainPanel, "SORT");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets the application state and returns to the introductory screen.
     */
    private void resetToIntroScreen() {
        inputField.setText("");
        cardLayout.show(mainPanel, "INTRO");
    }

    /**
     * Initializes the sorting screen with a new set of numbers.
     *
     * @param count The number of numbers to generate and display.
     */
    private void initializeSortScreen(int count) {
        generateNumbers(count);
        rebuildNumbersPanel();
    }

    /**
     * Generates an array of random integers.
     * Ensures at least one number is 30 or less to allow for re-initialization.
     *
     * @param count The number of integers to generate.
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
     * Clears and rebuilds the numbers panel with new buttons based on the current {@code numbers} array.
     */
    private void rebuildNumbersPanel() {
        numbersPanel.removeAll();
        numberButtons.clear();

        numbersPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);

        for (int i = 0; i < numbers.length; i++) {
            gbc.gridx = i / 10;
            gbc.gridy = i % 10;

            JButton numButton = createNumberButton(numbers[i]);
            numberButtons.add(numButton);
            numbersPanel.add(numButton, gbc);
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    /**
     * Creates and configures a JButton for a given number.
     *
     * @param number The number to display on the button.
     * @return A configured JButton.
     */
    private JButton createNumberButton(int number) {
        JButton button = new JButton(String.valueOf(number));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(e -> handleNumberButtonClick(button));
        return button;
    }

    /**
     * Handles the click event for a number button.
     * If the number is 30 or less, it re-initializes the screen with that many numbers.
     * Otherwise, it shows a warning message.
     *
     * @param button The button that was clicked.
     */
    private void handleNumberButtonClick(JButton button) {
        int value = Integer.parseInt(button.getText());
        if (value <= 30) {
            initializeSortScreen(value);
        } else {
            JOptionPane.showMessageDialog(NumberSorterApp.this,
                    "Please select a value smaller or equal to 30.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Updates the text on the number buttons to reflect the current state of the {@code numbers} array.
     * This method is called during the sorting animation and is executed on the Event Dispatch Thread.
     */
    private void updateNumbersPanel() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < numbers.length; i++) {
                numberButtons.get(i).setText(String.valueOf(numbers[i]));
            }
        });
    }

    /**
     * Initiates the Quicksort algorithm in a new thread and toggles the sorting direction for the next run.
     */
    private void quickSortAndAnimate() {
        quickSort(0, numbers.length - 1);
        sortDescending = !sortDescending;
    }

    /**
     * The recursive implementation of the Quicksort algorithm.
     *
     * @param low  The starting index of the sub-array to be sorted.
     * @param high The ending index of the sub-array to be sorted.
     */
    private void quickSort(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }

    /**
     * Partitions the sub-array around a pivot element.
     *
     * @param low  The starting index of the sub-array.
     * @param high The ending index of the sub-array (which is the pivot).
     * @return The final index of the pivot element.
     */
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

    /**
     * Swaps two elements in the {@code numbers} array and triggers a UI update and a brief pause for animation.
     *
     * @param i The index of the first element.
     * @param j The index of the second element.
     */
    private void swap(int i, int j) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
        updateNumbersPanel();
        sleep();
    }

    /**
     * Pauses the execution thread for a short duration to create a visual animation effect.
     */
    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * The main entry point for the application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NumberSorterApp().setVisible(true));
    }
}