package org.automation.datamover.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	String indexPage(ModelMap model) {
		return "index";
	}

	@RequestMapping(value = "/help", method = RequestMethod.GET)
	String helpPage(ModelMap model) {
		return "help";
	}

}
