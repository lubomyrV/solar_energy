package com.demo.solarenergy.database;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInit {
	
	@Autowired Sqlite database;

	@PostConstruct
	public void initialize(){
		database.createNewDatabase();
	}
	
}
