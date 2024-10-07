package StacksAssign;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Stocks extends JFrame {
    // Map to store the stacks of purchases for each stock
    private Map<String, Stack<Stock>> stockMap = new HashMap<>();
    // Map to store the current price for each stock
    private Map<String, Double> currentPrices = new HashMap<>();
    // GUI components
    private JTextArea portfolioDisplay;
    private JTextField sharesField, currentPriceField, sellSharesField;
    private JComboBox<String> stockSelector;
    // Store the currently selected stock
    private String selectedStock = "AAPL";

    // Constructor for the Stocks GUI
    public Stocks() {
        // Initialize 5 different stocks with empty stacks
        stockMap.put("AAPL", new Stack<>());
        stockMap.put("GOOG", new Stack<>());
        stockMap.put("AMZN", new Stack<>());
        stockMap.put("MSFT", new Stack<>());
        stockMap.put("TSLA", new Stack<>());

        // Initialize current prices to 0 for all stocks
        currentPrices.put("AAPL", 0.0);
        currentPrices.put("GOOG", 0.0);
        currentPrices.put("AMZN", 0.0);
        currentPrices.put("MSFT", 0.0);
        currentPrices.put("TSLA", 0.0);

        // Frame settings
        setTitle("Multi-Stock Portfolio Tracker");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a panel for the stock operations (buy/sell/set price)
        JPanel stockPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); 

        // Stock Selector (Drop-down menu to choose a stock)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel stockSelectorLabel = new JLabel("Select Stock:");
        stockPanel.add(stockSelectorLabel, gbc);

        gbc.gridx = 1;
        stockSelector = new JComboBox<>(new String[]{"AAPL", "GOOG", "AMZN", "MSFT", "TSLA"});
        // When the user selects a stock, update the selectedStock variable
        stockSelector.addActionListener(e -> selectedStock = (String) stockSelector.getSelectedItem());
        stockPanel.add(stockSelector, gbc);

        // Purchase section
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel sharesLabel = new JLabel("Shares to Purchase:");
        stockPanel.add(sharesLabel, gbc);

        gbc.gridx = 1;
        sharesField = new JTextField();
        stockPanel.add(sharesField, gbc);

        gbc.gridx = 2;
        JButton purchaseButton = new JButton("Purchase Stock");
        stockPanel.add(purchaseButton, gbc);

        // Sell section
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel sellSharesLabel = new JLabel("Shares to Sell:");
        stockPanel.add(sellSharesLabel, gbc);

        gbc.gridx = 1;
        sellSharesField = new JTextField();
        stockPanel.add(sellSharesField, gbc);

        gbc.gridx = 2;
        JButton sellButton = new JButton("Sell Stock");
        stockPanel.add(sellButton, gbc);

        // Current Price Section (To set the current market price of the stock)
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel currentPriceLabel = new JLabel("Set Current Price per Share:");
        stockPanel.add(currentPriceLabel, gbc);

        gbc.gridx = 1;
        currentPriceField = new JTextField();
        stockPanel.add(currentPriceField, gbc);

        gbc.gridx = 2;
        JButton setPriceButton = new JButton("Set Price");
        stockPanel.add(setPriceButton, gbc);

        // Portfolio display (Text area to show the current state of the portfolio)
        portfolioDisplay = new JTextArea(10, 30);
        portfolioDisplay.setEditable(false); 
        JScrollPane scrollPane = new JScrollPane(portfolioDisplay);

        // Add components to the frame
        add(stockPanel, BorderLayout.NORTH); 
        add(scrollPane, BorderLayout.CENTER); 

        // Event listeners for buttons
        purchaseButton.addActionListener(e -> purchaseStock());  // Purchase stock
        sellButton.addActionListener(e -> sellStock());          // Sell stock
        setPriceButton.addActionListener(e -> setCurrentPrice()); // Set stock price

        updatePortfolioDisplay(); // Update the portfolio display on startup
    }

    // Method to handle purchasing stock
    private void purchaseStock() {
        try {
            int shares = Integer.parseInt(sharesField.getText());
            double currentPrice = currentPrices.get(selectedStock); 
            if (currentPrice > 0) { 
                Stock newStock = new Stock(shares, currentPrice); 
                stockMap.get(selectedStock).push(newStock); 
                sharesField.setText(""); 
                updatePortfolioDisplay(); 
            } else {
                JOptionPane.showMessageDialog(this, "Please set the current price first.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }

    // Method to handle selling stock
    private void sellStock() {
        try {
            int sharesToSell = Integer.parseInt(sellSharesField.getText());
            Stack<Stock> stockStack = stockMap.get(selectedStock); 

            if (currentPrices.get(selectedStock) > 0 && !stockStack.isEmpty()) {
                // Sell the most recent shares first (LIFO), reducing shares from the stack
                while (sharesToSell > 0 && !stockStack.isEmpty()) {
                    Stock lastStock = stockStack.pop(); 
                    if (lastStock.shares > sharesToSell) {
                        lastStock.shares -= sharesToSell; 
                        stockStack.push(lastStock); 
                        sharesToSell = 0;
                    } else {
                        sharesToSell -= lastStock.shares; 
                    }
                }

                if (sharesToSell > 0) {
                    JOptionPane.showMessageDialog(this, "Not enough shares to sell.");
                }

                sellSharesField.setText(""); 
                updatePortfolioDisplay(); 
            } else {
                JOptionPane.showMessageDialog(this, "Please set the current price first.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }

    // Method to set the current price of the selected stock
    private void setCurrentPrice() {
        try {
            double price = Double.parseDouble(currentPriceField.getText());
            currentPrices.put(selectedStock, price); 
            updatePortfolioDisplay(); 
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.");
        }
    }

    // Method to update the portfolio display with the current stock information
    private void updatePortfolioDisplay() {
        StringBuilder portfolioText = new StringBuilder("Current Portfolio:\n");

        double totalCost = 0;
        double totalValue = 0;

        // Loop through each stock to display its purchases and calculate totals
        for (String stock : stockMap.keySet()) {
            Stack<Stock> stockStack = stockMap.get(stock);
            double currentPrice = currentPrices.get(stock);
            portfolioText.append(stock).append(":\n");

            double stockCost = 0;
            double stockValue = 0;

            // Loop through all purchases of the stock
            for (Stock s : stockStack) {
                stockCost += s.getTotalCost(); 
                stockValue += s.shares * currentPrice; 
                portfolioText.append(s.shares).append(" shares at $")
                        .append(s.pricePerShare).append(" per share\n");
            }

            // Display the total cost and current value for the stock
            portfolioText.append("Original Cost: $").append(stockCost).append("\n");
            portfolioText.append("Current Value: $").append(stockValue).append("\n\n");

            totalCost += stockCost;
            totalValue += stockValue;
        }

        // Display the overall total cost and total value
        portfolioText.append("Total Portfolio Cost: $").append(totalCost).append("\n");
        portfolioText.append("Total Portfolio Value: $").append(totalValue).append("\n");

        // Update the portfolio display area
        portfolioDisplay.setText(portfolioText.toString());
    }

    // Main method to run the Stocks application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Stocks stocksApp = new Stocks();
            stocksApp.setVisible(true); // Make the GUI visible
        });
    }
}


