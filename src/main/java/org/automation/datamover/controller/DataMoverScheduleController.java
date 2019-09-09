package org.automation.datamover.controller;

import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.db.DataMoverSchedule;
import org.automation.datamover.bean.req.PageForm;
import org.automation.datamover.service.DataMoverScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/schedule")
public class DataMoverScheduleController {

	@Autowired
	private DataMoverScheduleService dataMoverScheduleService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	String schedulePage(ModelMap model) {
		return "pages/schedule";
	}

	@RequestMapping(value = "/listByPage", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object listByPage(PageForm pageForm) {
		return dataMoverScheduleService.listByPage(pageForm);
	}

	@RequestMapping(value = "/reload", method = RequestMethod.GET)
	@ResponseBody
	void reload(@RequestParam(required = false) Integer scheduleId) {
		if (scheduleId == null) {
			dataMoverScheduleService.reload();
		} else {
			dataMoverScheduleService.reload(scheduleId);
		}
	}

	@RequestMapping(value = "/get", method = RequestMethod.POST)
	@ResponseWrapper
	@ResponseBody
	Object getScheduleById(Integer id) {
		return dataMoverScheduleService.get(id);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	void addSchedule(DataMoverSchedule bean) {
		dataMoverScheduleService.add(bean);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	void updateSchedule(DataMoverSchedule bean) {
		dataMoverScheduleService.update(bean);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	void deleteSchedule(Integer id) {
		dataMoverScheduleService.delete(id);
	}

}
