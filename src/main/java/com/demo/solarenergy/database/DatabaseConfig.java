package com.demo.solarenergy.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseConfig {
	
	@Autowired Environment env;
	
	@Bean
	public Sqlite sqlite(){
		return new Sqlite(env.getProperty("databaseName"));
	}
	
}
