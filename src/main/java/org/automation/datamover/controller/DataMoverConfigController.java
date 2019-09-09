package org.automation.datamover.controller;

import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.ext.DataMoverConfigDetail;
import org.automation.datamover.bean.req.DataMoverConfigListQO;
import org.automation.datamover.service.DataMoverConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/config")
public class DataMoverConfigController {

	@Autowired
	private DataMoverConfigService dataMoverConfigService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	String configPage(ModelMap model) {
		return "pages/config";
	}

	@RequestMapping(value = "/listByPage", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object listByPage(DataMoverConfigListQO qo) {
		return dataMoverConfigService.listByPage(qo);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object list(DataMoverConfigListQO qo) {
		return dataMoverConfigService.list(qo);
	}

	@RequestMapping(value = "/getDetail", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object getDetail(Integer id) {
		return dataMoverConfigService.getDetail(id);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	void add(@RequestBody DataMoverConfigDetail config) {
		dataMoverConfigService.add(config);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	void update(@RequestBody DataMoverConfigDetail config) {
		dataMoverConfigService.update(config);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	void delete(Integer id) {
		dataMoverConfigService.delete(id);
	}

	@RequestMapping(value = "/exec", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object exec(Integer id) {
		return dataMoverConfigService.exec(id, Constant.TASK_TRIGGER_TYPE_MANUAL);
	}

}
