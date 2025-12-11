package com.example;

import java.util.Scanner;
import java.util.Optional;
import javax.sql.DataSource;
import java.util.List;

import com.example.repository.jdbc.AccountRepository;
import com.example.repository.jdbc.MoonMissionRepository;
import com.example.repository.jdbc.JdbcAccountRepository;
import com.example.repository.jdbc.JdbcMoonMissionRepository;
import com.example.repository.jdbc.SimpleDriverManagerDataSource;

public class Main {

    /**
     * Application entry point that constructs a Main instance and starts its interactive run loop.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        new Main().run();
    }

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints a line of text to standard output.
     *
     * @param s the text to print
     */
    private void print(String s) {
        System.out.println(s);
    }

    /**
     * Run the interactive console application: initialize data sources and repositories, authenticate a user,
     * then present and handle the menu-driven command loop.
     *
     * <p>Initializes a DataSource from system properties {@code APP_JDBC_URL}, {@code APP_DB_USER},
     * and {@code APP_DB_PASS}, and creates JDBC-backed Account and MoonMission repositories. Prompts
     * for username and password on standard input and performs authentication; if authentication
     * fails the method returns. After successful authentication, enters a loop that displays a menu
     * and performs actions such as listing missions, retrieving a mission by ID, counting missions by
     * year, and creating/updating/deleting accounts. The loop exits when the user selects the exit option.
     */
    public void run() {
        // skapa DataSource o repositories
        DataSource ds = new SimpleDriverManagerDataSource(
                System.getProperty("APP_JDBC_URL"),
                System.getProperty("APP_DB_USER"),
                System.getProperty("APP_DB_PASS")
        );

        AccountRepository accountRepo = new JdbcAccountRepository(ds);
        MoonMissionRepository moonRepo = new JdbcMoonMissionRepository(ds);

        // be om användarnamn o lösenord
        print("Username: ");
        String username = scanner.nextLine();
        print("Password: ");
        String password = scanner.nextLine();

        // autentisera användaren
        Optional<Account> loggedIn = accountRepo.findByName(username);
        if (loggedIn.isEmpty() || !loggedIn.get().password().equals(password)) {
            print("Invalid username or password");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            // hantera menyval
            switch (choice) {
                case "1" -> listMissions(moonRepo);
                case "2" -> getMissionById(moonRepo);
                case "3" -> countMissionsByYear(moonRepo);
                case "4" -> createAccount(accountRepo);
                case "5" -> updateAccountPassword(accountRepo);
                case "6" -> deleteAccount(accountRepo);
                case "0" -> running = false;
                default -> print("Invalid choice");
            }
        }
    }

    /**
     * Prints the interactive menu of available user actions and prompts for a choice.
     */
    private void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1) List moon missions");
        System.out.println("2) Get a moon mission by mission_id");
        System.out.println("3) Count missions for a given year");
        System.out.println("4) Create an account");
        System.out.println("5) Update an account password");
        System.out.println("6) Delete an account");
        System.out.println("0) Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Prints the spacecraft name of every moon mission retrieved from the repository.
     *
     * @param repo the repository used to fetch moon missions
     */
    private void listMissions(MoonMissionRepository repo) {
        List<MoonMission> missions = repo.findAll();
        for (MoonMission m : missions) {
            System.out.println(m.spacecraft());
        }
    }

    /**
     * Print detailed information for a moon mission identified by its ID.
     *
     * Prompts the user for a mission ID, retrieves the mission from the repository,
     * and prints the mission's fields or "Mission not found" if no mission exists.
     *
     * @param repo repository used to retrieve moon missions
     */
    private void getMissionById(MoonMissionRepository repo) {
        System.out.print("Mission ID: ");
        long id = Long.parseLong(scanner.nextLine());
        Optional<MoonMission> m = repo.findById(id);
        if (m.isPresent()) {
            MoonMission mission = m.get();
            System.out.println("Mission ID: " + mission.missionId());
            System.out.println("Spacecraft: " + mission.spacecraft());
            System.out.println("Launch Date: " + mission.launchDate());
            System.out.println("Carrier Rocket: " + mission.carrierRocket());
            System.out.println("Operator: " + mission.operator());
            System.out.println("Mission Type: " + mission.missionType());
            System.out.println("Outcome: " + mission.outcome());
        } else {
            System.out.println("Mission not found");
        }
    }

    /**
         * Prompts for a year, counts how many moon missions occurred in that year, and prints the result.
         *
         * @param repo repository used to retrieve the mission count for the specified year
         */
    private void countMissionsByYear(MoonMissionRepository repo) {
        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine());
        int count = repo.countByYear(year);
        System.out.println("Number of missions in " + year + ": " + count);
    }

    /**
     * Prompts the user for personal details, creates a new Account with a generated username, and saves it.
     *
     * Prompts for first name, last name, SSN, and password; generates a username by concatenating the first
     * three characters of the first name and the first three characters of the last name; constructs an
     * Account (id 0) and persists it via the provided repository, then prints a confirmation message.
     */
    private void createAccount(AccountRepository repo) {
        System.out.print("First name: ");
        String first = scanner.nextLine();
        System.out.print("Last name: ");
        String last = scanner.nextLine();
        System.out.print("SSN: ");
        String ssn = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // skapa ett konto med genererat namn
        String name = first.substring(0, 3) + last.substring(0, 3);
        Account account = new Account(0, name, password, first, last, ssn);
        repo.create(account);
        System.out.println("Account created");
    }

    /**
     * Prompts for a user ID and a new password, then updates that account's password using the repository.
     *
     * @param repo repository used to update the account password
     */
    private void updateAccountPassword(AccountRepository repo) {
        System.out.print("User ID: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.print("New Password: ");
        String password = scanner.nextLine();
        repo.updatePassword(id, password);
        System.out.println("Password updated");
    }

    /**
     * Prompts for a user ID, deletes the corresponding account from the repository, and prints a confirmation.
     *
     * @param repo repository used to remove the account by ID
     */
    private void deleteAccount(AccountRepository repo) {
        System.out.print("User ID: ");
        long id = Long.parseLong(scanner.nextLine());
        repo.delete(id);
        System.out.println("Account deleted");
    }
}