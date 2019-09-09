package org.automation.datamover.controller;

import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.db.DataMoverTemplateGroup;
import org.automation.datamover.service.DataMoverTemplateGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/template/group")
public class DataMoverTemplateGroupController {

	@Autowired
	private DataMoverTemplateGroupService dataMoverTemplateGroupService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object list() {
		return dataMoverTemplateGroupService.list();
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	void add(DataMoverTemplateGroup group) {
		dataMoverTemplateGroupService.add(group);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	void update(DataMoverTemplateGroup group) {
		dataMoverTemplateGroupService.update(group);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	void delete(Integer id) {
		dataMoverTemplateGroupService.delete(id);
	}

}
