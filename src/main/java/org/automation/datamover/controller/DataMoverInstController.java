package org.automation.datamover.controller;

import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.req.DataMoverInstListQO;
import org.automation.datamover.service.DataMoverInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/track")
public class DataMoverInstController {

	@Autowired
	private DataMoverInstService dataMoverInstService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	String configInstPage(ModelMap model,
			@RequestParam(value = "configId", required = false) Integer configId) {
		model.addAttribute("configId", configId);
		return "pages/track";
	}

	@RequestMapping(value = "/listByPage", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object listByPage(DataMoverInstListQO qo) {
		return dataMoverInstService.listByPage(qo);
	}

	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	@ResponseBody
	void stop(Integer id) {
		dataMoverInstService.stop(id);
	}

}
