package org.example.The_Lottery.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

public class LottoService {
    //here
    private final Connection connection;
    final int[] currentWinningNumbers;

    public LottoService(Connection connection) {
        this.connection = connection;
        this.currentWinningNumbers = generateWinningNumbers();
    }

    //Create Database
    public void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            try {
                stmt.executeUpdate("DROP TABLE LotteryDraws");

            } catch (SQLException ignore) {
            }

            String createTableSQL = "CREATE TABLE LotteryDraws ("
                    + "ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                    + "WeekNumber INT,"
                    + "PickedFirst INT,"
                    + "PickedSecond INT,"
                    + "PickedThird INT,"
                    + "PickedFourth INT,"
                    + "PickedFifth INT )";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Database created");
        }
    }

    //Add 5 generated numbers from 1 to 90 without repeating the same number
    public void addGeneratedNumbers() throws SQLException {
        LocalDate date = LocalDate.now();
        int weekNumber = date.get(WeekFields.ISO.weekOfWeekBasedYear()); //There are 52 weeks in one year, with this we can tell the correct week we are today .


        Set<Integer> numbers = generateUniqueNumbers(5);
        int[] formedNumbers = numbers.stream().mapToInt(Integer::intValue).toArray();


        String sql = "INSERT INTO LotteryDraws (WeekNumber, PickedFirst, PickedSecond, PickedThird, PickedFourth, PickedFifth) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, weekNumber);
            for (int i = 0; i < 5; i++) {
                stmt.setInt(i + 2, formedNumbers[i]);

            }
            stmt.executeUpdate();
            // System.out.println("New 5 generated numbers has been created");

        }

    }

    //Generate for this week winner numbers.
    public int[] generateWinningNumbers() {
        Random random = new Random();
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 5) {
            numbers.add(random.nextInt(90) + 1);
        }
        return numbers.stream().mapToInt(Integer::intValue).toArray();
    }


    //Generál randomly without repeating from 1-90.
    public Set<Integer> generateUniqueNumbers(int count) {
        Random random = new Random();
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < count) {
            numbers.add(random.nextInt(90) + 1);
        }
        return numbers;
    }

    //Add be manually generated 5 numbers from 1 until 90-ig without repeating.
    public void addManualNumbers(Scanner sc) throws SQLException {

        LocalDate date = LocalDate.now();
        int weekNumber = date.get(WeekFields.ISO.weekOfWeekBasedYear());


        System.out.print("Pick your number:");
        int first = sc.nextInt();
        System.out.print("Next number:");
        int second = sc.nextInt();
        System.out.print("Next  number:");
        int third = sc.nextInt();
        System.out.print("Next  number:");
        int fourth = sc.nextInt();
        System.out.print("Next  number:");
        int fifth = sc.nextInt();


        Set<Integer> setCheck = new HashSet<>();
        setCheck.add(first);
        setCheck.add(second);
        setCheck.add(third);
        setCheck.add(fourth);
        setCheck.add(fifth);

        //If none of them repeating the same number this task does it job
        if (setCheck.size() == 5) {
            int[] checkArray = {first, second, third, fourth, fifth};
            int counter = 0;

            for (int pickedNumbers : checkArray) {

                //check the higher than 0 and lower than 91  numbers
                if (pickedNumbers < 91 && pickedNumbers > 0) {
                    counter++;
                }
            }

            if (counter == 5) {

                String sql = "INSERT INTO LotteryDraws (WeekNumber,PickedFirst,PickedSecond,PickedThird, PickedFourth, PickedFifth)" +
                        "VALUES (?,?,?,?,?,?)";

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, weekNumber);
                    stmt.setInt(2, first);
                    stmt.setInt(3, second);
                    stmt.setInt(4, third);
                    stmt.setInt(5, fourth);
                    stmt.setInt(6, fifth);
                    stmt.executeUpdate();
                    // System.out.println("New 5 numbered has been added manually");
                }
            } else {
                System.out.println("you need enter 1 between 90 :)");
            }
        } else {
            System.out.println("you cant repeat the same numbers");
        }


    }

    //print out the players section from the table
    public void printAllLLotteryTickets() throws SQLException {
        LocalDate date = LocalDate.now();
        int weekNumber = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        String sql = "SELECT * FROM LotteryDraws WHERE WeekNumber =";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql + weekNumber)
        ) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                int week = rs.getInt("WeekNumber");
                int[] playerNumbers = {
                        rs.getInt("PickedFirst"),
                        rs.getInt("PickedSecond"),
                        rs.getInt("PickedThird"),
                        rs.getInt("PickedFourth"),
                        rs.getInt("PickedFifth")
                };

                System.out.printf("ID: %d Week: %d Picked numbers: %s \n", id, week, Arrays.toString(playerNumbers));
                countCorrectGuesses(playerNumbers);

            }
            System.out.printf("---- Winning numbers for week %d  : %s ----\n", weekNumber, Arrays.toString(currentWinningNumbers));
        }
    }

    //We need it for creating the CSV
    public ResultSet getAllTicketsResultSet() throws SQLException {
        LocalDate date = LocalDate.now();
        int weekNumber = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        Statement stmt = connection.createStatement();
        String sql = "SELECT * FROM LotteryDraws WHERE WeekNumber = ";
        return stmt.executeQuery(sql + weekNumber);
    }


    //Counting the correct answers and print they correct answers
    public void countCorrectGuesses(int[] playerNumbers) {
        int counter = 0;
        Set<Integer> winningSet = new HashSet<>();

        for (int cwn : currentWinningNumbers) {
            winningSet.add(cwn);
        }

        System.out.print("The Correct guesses:");

        for (int pn : playerNumbers) {
            if (winningSet.contains(pn)) {
                System.out.printf("[%d]", pn);
                counter++;
            }
        }
        if (counter == 0) {
            System.out.print("none");
        }
        System.out.printf("\nTotal correct: %d/5\n", counter);
        System.out.println("-----------------");
    }


}
