class StateTransaction {
    private String state;
    private int totalTransactions;
    private double totalProfit;
    private double totalRevenue; // Add a field for total revenue

    public StateTransaction(String state) {
        this.state = state;
        this.totalTransactions = 0;
        this.totalProfit = 0.0;
        this.totalRevenue = 0.0; // Initialize total revenue
    }

    public void addTransactions(int count, double profit, double revenue) {
        totalTransactions += count;
        totalProfit += profit;
        totalRevenue += revenue; // Update total revenue
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public double getTotalRevenue() {
        return totalRevenue; // Method to get total revenue
    }

    public String getState() {
        return state;
    }
}