package com.robert.athena.rest.container.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.portlet.handler.SimpleMappingExceptionResolver;

import com.robert.athena.rest.container.AthenaRestMainServer;
import com.robert.athena.rest.container.admin.bean.BaseResult;
import com.robert.athena.rest.container.admin.bean.ReturnStatus;

@Controller
public class AdminController {

	private static final Log log = LogFactory.getLog(AdminController.class);

	private boolean serviceAvailable = true;

	@RequestMapping(value = "/admin/server-status", method = RequestMethod.GET)
	@ResponseBody
	public String serverStatus() {
		return AthenaRestMainServer.containerServer.status().toString();
	}

	@RequestMapping(value = "/admin/service-status", method = RequestMethod.GET)
	@ResponseBody
	public String checkService(HttpServletResponse response) {
		if (serviceAvailable)
			response.setStatus(200);
		else
			response.setStatus(503);

		return "Current Service Status: " + (serviceAvailable ? "on" : "off");
	}

	@RequestMapping(value = "/admin/switch200", method = RequestMethod.GET)
	@ResponseBody
	public String turnOn(HttpServletResponse response) {
		serviceAvailable = true;

		return "Service Status: on";
	}

	@RequestMapping(value = "/admin/switch503", method = RequestMethod.GET)
	@ResponseBody
	public String turnOff(HttpServletResponse response) {
		serviceAvailable = false;

		return "Service Status: off";
	}

	@RequestMapping(value = "/admin/error-spring-exception")
	@ResponseBody
	public String errorSpringException(HttpServletRequest request,
			HttpServletResponse response) {
		Exception e = (Exception) request
				.getAttribute(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);

		log.error("AthenaRestFramework throws Exception: ", e);

		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

		BaseResult br = new BaseResult();
		br.setStatus(ReturnStatus.SERVER_ERROR.getValue());
		br.setMessage(e.getClass().getName() + ": " + e.getMessage());

		return JSONObject.fromObject(br).toString();
	}

	@RequestMapping(value = "/admin/error-spring-runtime-exception")
	@ResponseBody
	public String errorSpringRuntimeException(HttpServletRequest request,
			HttpServletResponse response) {
		Exception e = (Exception) request
				.getAttribute(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);

		log.error("AthenaRestFramework throws RuntimeException: ", e);

		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

		BaseResult br = new BaseResult();
		br.setStatus(ReturnStatus.SERVER_ERROR.getValue());
		br.setMessage(e.getClass().getName() + ": " + e.getMessage());

		return JSONObject.fromObject(br).toString();
	}

	@RequestMapping(value = "/admin/error-spring-default")
	@ResponseBody
	public String errorSpringDefault(HttpServletRequest request,
			HttpServletResponse response) {
		Exception e = (Exception) request
				.getAttribute(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);

		log.error("AthenaRestFramework throws Unknown Exception: ", e);

		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

		BaseResult br = new BaseResult();
		br.setStatus(ReturnStatus.SERVER_ERROR.getValue());
		br.setMessage(e.getClass().getName() + ": " + e.getMessage());

		return JSONObject.fromObject(br).toString();
	}

}
