package org.example.The_Lottery;

import org.example.The_Lottery.manager.CsVManager;
import org.example.The_Lottery.service.LottoService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LottoApp {
    private static final String dbURL = "jdbc:derby:memory:myDB;create=true";
//here
    public static void main() {
        try (Scanner sc = new Scanner(System.in)) {
            //DERBY join database
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            try (Connection conn = DriverManager.getConnection(dbURL)) {
                LottoService lottoService = new LottoService(conn);
                lottoService.initializeDatabase();

                CsVManager csVManager = new CsVManager(lottoService);

                boolean running = true;

                while (running) {
                    menu();
                    try {
                        int option = sc.nextInt();
                        switch (option) {
                            //Menu console options
                            case 1:
                                System.out.println("\n*********All lottery tickets*********");
                                lottoService.addGeneratedNumbers();
                                lottoService.printAllLLotteryTickets();
                                break;
                            case 2:
                                System.out.println("******************");
                                lottoService.addManualNumbers(sc);
                                lottoService.printAllLLotteryTickets();
                                break;
                            case 3:
                                csVManager.exportCurrentWeekToCsV(); //CSV
                                break;
                            case 4:
                                running = false;
                                System.out.println("Thank you for using my program!");
                                break;
                            default:
                                System.out.println("enter a number between (1 to 4)!");
                                break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("please enter a number next time!");
                        sc.nextLine(); // Clear invalid input
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }


    }

    //Menu instruction
    public static void menu() {
        System.out.println("\n=== Welcome to the Lottery 5 Program  (1-90) ===");
        System.out.println("Press 1 to generate 5 numbers for you automatically");
        System.out.println("Press 2 to manually you pick 5 numbers by your choice without repeating the same number");
        System.out.println("Press 3 to create a CSV");
        System.out.println("Press 4 to quit from the menu");
        System.out.print("Chose your option:");
    }
}
