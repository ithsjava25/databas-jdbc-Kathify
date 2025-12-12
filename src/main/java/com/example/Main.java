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
     * Application entry point that creates a Main instance and starts the interactive UI.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        new Main().run();
    }

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the given string to standard output.
     */
    private void print(String s) {
        System.out.println(s);
    }

    /**
     * Start the interactive command-line UI: initialize the data source and repositories, authenticate a user, and process menu-driven commands.
     *
     * <p>Reads JDBC connection properties from system properties `APP_JDBC_URL`, `APP_DB_USER`, and `APP_DB_PASS` to create a DataSource and repository instances. Prompts for username and password on standard input and verifies credentials via the account repository; if authentication fails the method prints an error and returns. After successful authentication, enters a loop that displays a menu and dispatches user choices to actions for listing and querying moon missions, counting missions by year, and creating, updating, or deleting accounts; the loop exits when the user chooses the exit option.</p>
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
     * Prints the interactive menu of available actions and prompts the user to choose an option.
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
     * Print the spacecraft name of every moon mission to standard output.
     *
     * @param repo repository used to fetch moon missions
     */
    private void listMissions(MoonMissionRepository repo) {
        List<MoonMission> missions = repo.findAll();
        for (MoonMission m : missions) {
            System.out.println(m.spacecraft());
        }
    }

    /**
     * Prints details for a moon mission selected by prompting the user for a mission ID.
     *
     * Prompts for a mission ID, looks up the mission in the provided repository, and prints its fields
     * (mission ID, spacecraft, launch date, carrier rocket, operator, mission type, outcome).
     * If no mission is found, prints "Mission not found".
     *
     * @param repo the repository used to retrieve moon missions
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
     * Prompt for a year, count moon missions in that year, and print the result.
     *
     * Prompts the user to enter a year, queries the given repository for the number
     * of moon missions that occurred in that year, and prints the count to standard output.
     */
    private void countMissionsByYear(MoonMissionRepository repo) {
        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine());
        int count = repo.countByYear(year);
        System.out.println("Number of missions in " + year + ": " + count);
    }

    /**
     * Prompts the user for personal details, constructs a new Account with a generated username, and saves it to the repository.
     *
     * The username is generated by concatenating the first three characters of the first name and the first three characters of the last name.
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
     * Prompts for a user ID and new password, updates that account's password via the repository, and prints a confirmation.
     *
     * @param repo repository used to persist the password change
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
     * Deletes an account identified by a user-entered ID.
     *
     * Prompts for a user ID on standard input, removes the matching account via the repository,
     * and prints a confirmation message.
     *
     * @param repo repository used to remove the account
     */
    private void deleteAccount(AccountRepository repo) {
        System.out.print("User ID: ");
        long id = Long.parseLong(scanner.nextLine());
        repo.delete(id);
        System.out.println("Account deleted");
    }
}