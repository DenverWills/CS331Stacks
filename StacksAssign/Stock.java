package StacksAssign;

// Stock class to represent a stock purchase
public class Stock {
    int shares;
    double pricePerShare;

    // Constructor for Stock class
    public Stock(int shares, double pricePerShare) {
        this.shares = shares;
        this.pricePerShare = pricePerShare;
    }

    // Method to calculate the total cost of the stock purchase
    public double getTotalCost() {
        return shares * pricePerShare;
    }
}
