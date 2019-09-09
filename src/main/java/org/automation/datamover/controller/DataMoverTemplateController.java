package org.automation.datamover.controller;

import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.db.DataMoverTemplateSql;
import org.automation.datamover.bean.req.DataMoverTemplateGroupListQO;
import org.automation.datamover.service.DataMoverTemplateSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/template")
public class DataMoverTemplateController {

	@Autowired
	private DataMoverTemplateSqlService dataMoverTemplateSqlService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	String templatePage(ModelMap model) {
		return "pages/template";
	}

	@RequestMapping(value = "/listByPage", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object listByPage(DataMoverTemplateGroupListQO qo) {
		return dataMoverTemplateSqlService.listByPage(qo);
	}

	@RequestMapping(value = "/listByGroup", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object list(Integer groupId) {
		return dataMoverTemplateSqlService.listByGroup(groupId);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	void add(DataMoverTemplateSql template) {
		dataMoverTemplateSqlService.add(template);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	void update(DataMoverTemplateSql template) {
		dataMoverTemplateSqlService.update(template);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	void delete(Integer id) {
		dataMoverTemplateSqlService.delete(id);
	}

}
