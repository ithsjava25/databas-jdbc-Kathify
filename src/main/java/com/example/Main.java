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

    public static void main(String[] args) {
        new Main().run();
    }

    private final Scanner scanner = new Scanner(System.in);

    private void print(String s) {
        System.out.println(s);
    }

    public void run() {
        // dataSource och repositories
        DataSource ds = new SimpleDriverManagerDataSource(
                System.getProperty("APP_JDBC_URL"),
                System.getProperty("APP_DB_USER"),
                System.getProperty("APP_DB_PASS")
        );

        AccountRepository accountRepo = new JdbcAccountRepository(ds);
        MoonMissionRepository moonRepo = new JdbcMoonMissionRepository(ds);

        print("Username: ");
        String username = scanner.nextLine();
        print("Password: ");
        String password = scanner.nextLine();

        Optional<Account> loggedIn = accountRepo.findByName(username);
        if (loggedIn.isEmpty() || !loggedIn.get().password().equals(password)) {
            print("Invalid username or password");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

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

    private void listMissions(MoonMissionRepository repo) {
        List<MoonMission> missions = repo.findAll();
        for (MoonMission m : missions) {
            System.out.println(m.spacecraft());
        }
    }

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

    private void countMissionsByYear(MoonMissionRepository repo) {
        System.out.print("Year: ");
        int year = Integer.parseInt(scanner.nextLine());
        int count = repo.countByYear(year);
        System.out.println("Number of missions in " + year + ": " + count);
    }

    private void createAccount(AccountRepository repo) {
        System.out.print("First name: ");
        String first = scanner.nextLine();
        System.out.print("Last name: ");
        String last = scanner.nextLine();
        System.out.print("SSN: ");
        String ssn = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String name = first.substring(0, 3) + last.substring(0, 3); // som init.sql
        Account account = new Account(0, name, password, first, last, ssn);
        repo.create(account);
        System.out.println("Account created");
    }

    private void updateAccountPassword(AccountRepository repo) {
        System.out.print("User ID: ");
        long id = Long.parseLong(scanner.nextLine());
        System.out.print("New Password: ");
        String password = scanner.nextLine();
        repo.updatePassword(id, password);
        System.out.println("Password updated");
    }

    private void deleteAccount(AccountRepository repo) {
        System.out.print("User ID: ");
        long id = Long.parseLong(scanner.nextLine());
        repo.delete(id);
        System.out.println("Account deleted");
    }
}