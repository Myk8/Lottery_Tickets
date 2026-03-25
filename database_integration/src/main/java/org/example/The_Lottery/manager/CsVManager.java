package org.example.The_Lottery.manager;


import org.example.The_Lottery.service.LottoService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

//here
public class CsVManager {
    private static final String fileName = "lottery.csv";
    private final LottoService lottoService;

    public CsVManager(LottoService LottoService) {
        this.lottoService = LottoService;
    }

    //Create CSV and add datas
    public void exportCurrentWeekToCsV() throws IOException, SQLException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write("ID,WeekNumber,PickedFirst,PickedSecond,PickedThird,PickedFourth,PickedFifth\n");
            ResultSet rs = lottoService.getAllTicketsResultSet();

            while (rs.next()) {
                writer.write(
                        MessageFormat.format("{0},{1},{2},{3},{4},{5},{6}\n",
                                rs.getInt("ID"),
                                rs.getInt("WeekNumber"),
                                rs.getInt("PickedFirst"),
                                rs.getInt("PickedSecond"),
                                rs.getInt("PickedThird"),
                                rs.getInt("PickedFourth"),
                                rs.getInt("PickedFifth"))
                );
            }
            System.out.printf("Data successfully exported to %s", fileName);
        }
    }
}

