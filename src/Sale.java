import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class InputFile {
    private static final String DIRECTORY_PATH = "C:\\Users\\aiden\\IdeaProjects\\Sales\\Sales info"; // Update this path
    private Map<String, StateTransaction> stateTransactions = new HashMap<>();
    private Map<String, PersonSales> personSalesMap = new HashMap<>(); // Map to track person sales
    private List<PersonSales> personSalesList = new ArrayList<>(); // List to keep track of person sales for future use

    public void readDirectory() {
        try (Stream<Path> paths = Files.walk(Paths.get(DIRECTORY_PATH))) {
            paths.filter(Files::isRegularFile)
                    .forEach(this::readFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(Path filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String emailLine = br.readLine(); // Email line
            String nameLine = br.readLine(); // Name
            String addressLine = br.readLine(); // Address line
            String transactionLine = br.readLine(); // Number of transactions line
            String sellerLine = br.readLine(); // Seller line
            String costLine = br.readLine(); // Cost line
            String materialCostLine = br.readLine(); // Material cost line

            String email = emailLine.split(": ")[1].trim(); // Extract email
            int numberOfTransactions = extractNumberOfTransactions(transactionLine);
            String state = extractState(addressLine);

            double cost = extractCost(costLine);
            double materialCost = extractMaterialCost(materialCostLine);

            double totalRevenue = numberOfTransactions * cost; // Calculate total revenue
            double totalMaterialCost = numberOfTransactions * materialCost;
            double totalProfit = totalRevenue - totalMaterialCost;

            // Update the state transactions
            stateTransactions.computeIfAbsent(state, StateTransaction::new)
                    .addTransactions(numberOfTransactions, totalProfit, totalRevenue); // Pass revenue

            // Update person sales
            PersonSales personSales = personSalesMap.computeIfAbsent(email, PersonSales::new);
            personSales.addSales(totalRevenue);
            if (!personSalesList.contains(personSales)) {
                personSalesList.add(personSales); // Add to list if not already present
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath.getFileName());
            e.printStackTrace();
        }
    }

    private double extractMaterialCost(String materialCostLine) {
        String[] parts = materialCostLine.split(":");
        if (parts.length == 2) {
            try {
                return Double.parseDouble(parts[1].trim().replace("$", "")); // Return material cost without the dollar sign
            } catch (NumberFormatException e) {
                System.err.println("Error parsing material cost: " + parts[1].trim());
            }
        }
        return 0.0; // Default to 0.0 if parsing fails
    }

    private int extractNumberOfTransactions(String transactionLine) {
        String[] parts = transactionLine.split(":");
        if (parts.length == 2) {
            try {
                return Integer.parseInt(parts[1].trim()); // Return the number of transactions
            } catch (NumberFormatException e) {
                System.err.println("Error parsing number of transactions: " + parts[1].trim());
            }
        }
        return 0; // Default to 0 if parsing fails
    }

    private String extractState(String addressLine) {
        String[] addressParts = addressLine.split(",");
        if (addressParts.length >= 2) {
            String[] stateZip = addressParts[1].trim().split(" ");
            if (stateZip.length > 0) {
                return stateZip[0]; // Return the state (assumed to be the first part)
            }
        }
        return "State not found"; // Default message if state cannot be found
    }

    private double extractCost(String costLine) {
        String[] parts = costLine.split(":");
        if (parts.length == 2) {
            try {
                return Double.parseDouble(parts[1].trim().replace("$", "")); // Return cost without the dollar sign
            } catch (NumberFormatException e) {
                System.err.println("Error parsing cost: " + parts[1].trim());
            }
        }
        return 0.0; // Default to 0.0 if parsing fails
    }

    public void displayStatesWithMostTransactionsProfitAndSales() {
        String stateWithMostTransactions = null;
        int maxTransactions = 0;

        String stateWithMostProfit = null;
        double maxProfit = 0.0;

        String stateWithHighestSales = null;
        double maxSales = 0.0;

        for (StateTransaction transaction : stateTransactions.values()) {
            if (transaction.getTotalTransactions() > maxTransactions) {
                maxTransactions = transaction.getTotalTransactions();
                stateWithMostTransactions = transaction.getState();
            }
            if (transaction.getTotalProfit() > maxProfit) {
                maxProfit = transaction.getTotalProfit();
                stateWithMostProfit = transaction.getState();
            }
            if (transaction.getTotalRevenue() > maxSales) {
                maxSales = transaction.getTotalRevenue();
                stateWithHighestSales = transaction.getState();
            }
        }

        if (stateWithMostTransactions != null) {
            System.out.println("State with the most transactions: " + stateWithMostTransactions + " with " + maxTransactions + " transactions.");
        } else {
            System.out.println("No transactions found.");
        }

        if (stateWithMostProfit != null) {
            System.out.printf("State with the most profit: %s with a total profit of $%.2f%n", stateWithMostProfit, maxProfit);
        } else {
            System.out.println("No profit found.");
        }

        if (stateWithHighestSales != null) {
            System.out.printf("State with the highest net sales: %s with a total sales amount of $%.2f%n", stateWithHighestSales, maxSales);
        } else {
            System.out.println("No sales found.");
        }
    }

    // Method to find the person with the highest sales
    public void displayPersonWithHighestSales() {
        String highestSalesEmail = null;
        double maxSales = 0.0;

        for (PersonSales person : personSalesMap.values()) {
            if (person.getTotalSales() > maxSales) {
                maxSales = person.getTotalSales();
                highestSalesEmail = person.getEmail();
            }
        }

        if (highestSalesEmail != null) {
            System.out.printf("Person with the highest sales: %s with total sales of $%.2f%n", highestSalesEmail, maxSales);
        } else {
            System.out.println("No sales found.");
        }
    }

    // QuickSort implementation to sort personSalesList from largest to smallest
    public void quickSort(List<PersonSales> salesList, int low, int high) {
        if (low < high) {
            int pi = partition(salesList, low, high);
            quickSort(salesList, low, pi - 1);
            quickSort(salesList, pi + 1, high);
        }
    }

    private int partition(List<PersonSales> salesList, int low, int high) {
        double pivot = salesList.get(high).getTotalSales();
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (salesList.get(j).getTotalSales() > pivot) { // Sort in descending order
                i++;
                swap(salesList, i, j);
            }
        }
        swap(salesList, i + 1, high);
        return i + 1;
    }

    private void swap(List<PersonSales> salesList, int i, int j) {
        PersonSales temp = salesList.get(i);
        salesList.set(i, salesList.get(j));
        salesList.set(j, temp);
    }

    // Method to display all persons sorted by total sales
    public void displayAllPersons() {
        quickSort(personSalesList, 0, personSalesList.size() - 1); // Sort before displaying
        for (PersonSales person : personSalesList) {
            System.out.printf("Email: %s, Total Sales: $%.2f%n", person.getEmail(), person.getTotalSales());
        }
    }

    public static void main(String[] args) {
        InputFile inputFile = new InputFile();
        inputFile.readDirectory(); // Read all files in the directory
        inputFile.displayStatesWithMostTransactionsProfitAndSales(); // Display the results
        inputFile.displayPersonWithHighestSales(); // Display person with the highest sales
        inputFile.displayAllPersons(); // Display all persons for potential searching
    }
}