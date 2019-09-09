package org.automation.datamover.controller;

import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.db.DataMoverDataSource;
import org.automation.datamover.bean.req.DataMoverDataSourceListQO;
import org.automation.datamover.service.DataMoverDataSourceService;
import org.automation.datamover.util.JdbcTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/datasource")
public class DataMoverDataSourceController {

	@Autowired
	private DataMoverDataSourceService dataMoverDataSourceService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	String datasourcePage(ModelMap model) {
		return "pages/datasource";
	}

	@RequestMapping(value = "/listByPage", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object listByPage(DataMoverDataSourceListQO qo) {
		return dataMoverDataSourceService.listByPage(qo);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseWrapper
	@ResponseBody
	Object list(DataMoverDataSourceListQO qo) {
		return dataMoverDataSourceService.list(qo);
	}

	@RequestMapping(value = "/reload", method = RequestMethod.GET)
	@ResponseBody
	void reload() {
		dataMoverDataSourceService.reload();
	}

	@RequestMapping(value = "/get", method = RequestMethod.POST)
	@ResponseWrapper
	@ResponseBody
	Object get(Integer id) {
		return dataMoverDataSourceService.get(id);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	void add(DataMoverDataSource bean) {
		dataMoverDataSourceService.add(bean);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	void update(DataMoverDataSource bean) {
		dataMoverDataSourceService.update(bean);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	void delete(Integer id) {
		dataMoverDataSourceService.delete(id);
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	@ResponseBody
	void test(DataMoverDataSource bean) throws Exception {
		JdbcTestUtil.test(bean.getJdbcDriver(), bean.getJdbcUrl(),
				bean.getJdbcUsername(), bean.getJdbcPassword());
	}

}
