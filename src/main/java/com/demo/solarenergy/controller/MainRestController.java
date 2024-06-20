package com.demo.solarenergy.controller;

import com.demo.solarenergy.database.Sqlite;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.*;

@RestController
public class MainRestController {
	
	@Autowired
	Sqlite database;

	@GetMapping("/getTotalSumOfEnergy")
	public float getTotalSumOfEnergy() {
		float result = database.getTotalSumWattsHours();
		return result;
	}
	
	@GetMapping("/getSumWattByDates/{date}")
	public String getSumWattByDates(@PathVariable(value = "date") String selectedDate) {
		LocalDate date = LocalDate.parse(selectedDate);
		System.out.println(date);
		Set<String> dates = new HashSet<>();
		LocalDate firstDay = date.withDayOfMonth(1);
		LocalDate lastDay = firstDay.withDayOfMonth(firstDay.getMonth().length(firstDay.isLeapYear()));
		dates.add(lastDay.toString());
		while (firstDay.isBefore(lastDay)) {
			dates.add(firstDay.toString());
			firstDay = firstDay.plusDays(1);
		}
		List<Map<String, Object>> result = database.getSumWattByDates(dates);
		System.out.println(result);

		List<JSONObject> data = new ArrayList<>();
		for (Map<String, Object> resMap :result) {
			data.add(new JSONObject(resMap));
		}
		return data.toString();
	}
	@GetMapping("/getRecordsByDate/{date}")
	public String getRecordsByDate(@PathVariable(value = "date") String selectedDate) {
		List<Map<String, Object>> result = database.getRecordsByDate(selectedDate);
		List<JSONObject> data = new ArrayList<>();
		for (Map<String, Object> resMap :result) {
			data.add(new JSONObject(resMap));
		}
		return data.toString();
	}
	
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
}
