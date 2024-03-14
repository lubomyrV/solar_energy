package com.demo.solarenergy.controller;

import com.demo.solarenergy.database.Sqlite;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Controller
public class WebController {

    @Autowired
    Sqlite database;
    @Autowired
    Environment env;
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/uploadCsv")
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            byte[] bytes = file.getBytes();
            System.out.println(bytes);
            try (
                    BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
                    CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
            ) {
                for (CSVRecord csvRecord : csvParser) {
                    // Accessing Values by Column Index
                    System.out.println("Record No - " + csvRecord.getRecordNumber());
                    if (csvRecord.getRecordNumber() == 1) {
                        continue;
                    }
                    String date_time = csvRecord.get(0);
                    String voltage_v = csvRecord.get(1);
                    String current_a = csvRecord.get(2);
                    System.out.println("---------------");
                    System.out.println(date_time + " " + voltage_v + " " +current_a);
                    // String date = date_time.split("T")[0];
                    try {
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                    // database.insertRecord(date_time, date, voltage_v, current_a);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}