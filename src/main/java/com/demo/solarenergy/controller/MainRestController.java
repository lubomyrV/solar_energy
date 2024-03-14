package com.demo.solarenergy.controller;

import com.demo.solarenergy.database.Sqlite;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.*;

@RestController
public class MainRestController {
	
	@Autowired
	Sqlite database;
/*
	@GetMapping("/checkUnsyncData")
	public boolean checkUnsyncData() {
		List<Map<String, Object>> result = database.getRecordsToSyncSF();
		System.out.println("checkUnsyncData" +result);
		return result.isEmpty();
	}

	@GetMapping("/syncSfData")
	public boolean syncSfData() {
		List<Map<String, Object>> result = database.getRecordsToSyncSF();
		System.out.println("syncSfData" +result);

		return result.isEmpty();
	}
	*/

	@GetMapping("/getTotalSumOfEnergy")
	public float getTotalSumOfEnergy() {
		float result = database.getTotalSumWattsHours();
		return result;
	}
	
	@PostMapping("/getSumWattByDates")
	public String getSumWattByDates(String selectedDate) {
		LocalDate date = LocalDate.parse(selectedDate);
		Set<String> dates = new HashSet<>();
		LocalDate firstDay = date.withDayOfMonth(1);
		LocalDate lastDay = firstDay.withDayOfMonth(firstDay.getMonth().length(firstDay.isLeapYear()));
		dates.add(lastDay.toString());
		while (firstDay.isBefore(lastDay)) {
			dates.add(firstDay.toString());
			firstDay = firstDay.plusDays(1);
		}
		List<Map<String, Object>> result = database.getSumWattByDates(dates);
		List<JSONObject> data = new ArrayList<>();
		for (Map<String, Object> resMap :result) {
			data.add(new JSONObject(resMap));
		}
		return data.toString();
	}
	@PostMapping("/getRecordsByDate")
	public String getRecordsByDate(String selectedDate) {
		List<Map<String, Object>> result = database.getRecordsByDate(selectedDate);
		List<JSONObject> data = new ArrayList<>();
		for (Map<String, Object> resMap :result) {
			data.add(new JSONObject(resMap));
		}
		return data.toString();
	}
	
}
