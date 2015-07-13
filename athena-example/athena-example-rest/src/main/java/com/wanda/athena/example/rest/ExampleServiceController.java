package com.wanda.athena.example.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wanda.athena.example.service.ExampleService;

@Controller
public class ExampleServiceController {
	@Autowired
	private ExampleService exampleService;

	@RequestMapping(value = "/1/test", method = RequestMethod.GET)
	@ResponseBody
	public String serverStatus() {
		return exampleService.helloworld();
	}

	@RequestMapping(value = "/1/test/{person}:({fields})", method = RequestMethod.GET)
	@ResponseBody
	public String testGetSubFields(@PathVariable String person,
			@PathVariable String fields) {
		return person + "@" + fields;
	}

	@RequestMapping(value = "/1/test/{person}:[{beans}]", method = RequestMethod.GET)
	@ResponseBody
	public String testGetChildBean(@PathVariable String person,
			@PathVariable String beans) {
		return person + "@" + beans;
	}

	@RequestMapping(value = "/1/test/dogs", method = RequestMethod.GET)
	@ResponseBody
	public String testPaging(String limit, String offset) {
		return limit + "@" + offset;
	}

	@RequestMapping(value = "/1/pets/{petId}.json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map getGetPetJson(@PathVariable String petId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		map.put("key1", "value1");

		return map;
	}

	@RequestMapping(value = "/1/pets/{petId}.xml", method = RequestMethod.GET, produces = "application/xml")
	@ResponseBody
	public Map getGetPetXml(@PathVariable String petId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", "value");
		map.put("key1", "value1");

		return map;
	}

	@RequestMapping(value = "/exception", method = RequestMethod.GET)
	@ResponseBody
	public String exception() throws Exception {
		throw new Exception();
	}

	@RequestMapping(value = "/runtime-exception", method = RequestMethod.GET)
	@ResponseBody
	public String runtimeException() {
		throw new RuntimeException();
	}

	@RequestMapping(value = "/throwable", method = RequestMethod.GET)
	@ResponseBody
	public String throwable() throws Throwable {
		throw new Throwable();
	}

	@RequestMapping(value = "/io-exception", method = RequestMethod.GET)
	@ResponseBody
	public String ioException() throws Throwable {
		throw new Throwable();
	}

	public void setExampleService(ExampleService exampleService) {
		this.exampleService = exampleService;
	}

}
