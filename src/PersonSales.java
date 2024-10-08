class PersonSales {
    private String email;
    private double totalSales;

    public PersonSales(String email) {
        this.email = email;
        this.totalSales = 0.0;
    }

    public void addSales(double sales) {
        totalSales += sales;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Email: " + email + ", Total Sales: $" + String.format("%.2f", totalSales);
    }
}