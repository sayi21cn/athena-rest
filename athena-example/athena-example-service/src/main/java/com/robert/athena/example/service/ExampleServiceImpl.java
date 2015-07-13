package com.robert.athena.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl implements ExampleService{
	@Value("${test.property}")
	private String word;
	
	public String helloworld() {
		return word;
	}
}
