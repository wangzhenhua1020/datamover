$(function() {

	$('#scheduleTable').bootstrapTable({
		columns: [ {
			title : '调度名称',
			align : 'center',
			field: 'name'
		}, {
			title : '配置名称',
			align : 'center',
			field: 'configName'
		}, {
			title : '调度表达式',
			align : 'center',
			field: 'expr'
		}, {
			title : '状态',
			align : 'center',
			width : 120,
			field: 'status',
			formatter: function(value, row, index) {
				return value == 0 ? '不可用' : '可用';
			}
		}, {
			title : '备注',
			align : 'center',
			field: 'remark'
		}, {
			title : '更新时间',
			align : 'center',
			field: 'updateTime',
			formatter: function(value, row, index) {
				return value;
			}
		}, {
			title : '操作',
			align : 'center',
			field: '',
			class: 'oper',
			formatter: function(value, row, index) {
				return '<a href="javascript:void(0)" class="reload-schedule d-none" data-schedule-id="' + row.id + '">重新加载</a> '+
				'<a href="javascript:void(0)" class="update-schedule" data-schedule-id="' + row.id + '">修改</a> ' +
				'<a href="javascript:void(0)" class="delete-schedule" data-schedule-id="' + row.id + '">删除</a> ';
			}
		} ],
		locale: 'zh-CN',
		//height: 540,
		pagination: true,
		pageSize: 10,
		pageNumber: 1,
		pageList: [ 10 ],
		sidePagination: 'server',
		method: 'get',
		contentType: 'application/x-www-form-urlencoded',
		url: CONTEXT_PATH + '/schedule/listByPage',
		ajaxOptions: {
			async: true
		},
		queryParams: function(params) {
			return params;
		},
		responseHandler: function(data) {
			var total = 0, rows = [];
			if (data.success && data.data) {
				total = data.data.total || 0;
				rows = data.data.list || [];
			} else {
				layer.alert(data.message || '请求数据失败', {icon: 2});
			}
			return { total: total, rows: rows };
		}
	}).on('click', 'td.oper .reload-schedule', function() {
		var id = $(this).data('schedule-id');
		$.ajax({
			type: "GET",
			dataType: "text",
			url: CONTEXT_PATH + "/schedule/reload",
			data: {
				scheduleId: id
			},
			success: function(data, status, xhr) {
				$('#refreshTable').trigger('click');
				layer.alert('加载成功', {icon: 1});
			}
		});
	}).on('click', 'td.oper .update-schedule', function () {
		var id = $(this).data('schedule-id');
		scheduleModify('update', id);
	}).on('click', 'td.oper .delete-schedule', function () {
		var id = $(this).data('schedule-id');
		layer.confirm('确认删除该条记录?', {icon: 3, title:'提示'}, function(index) {
			deleteScheduleById(id);
			layer.close(index);
		});

	});

	$('#refreshTable').on('click', function() {
		$('#scheduleTable').bootstrapTable('refresh');
	});

	$('#reloadSchedule').on('click', function() {
		$.ajax({
			type: "GET",
			dataType: "text",
			url: CONTEXT_PATH + "/schedule/reload",
			data: null,
			success: function(data, status, xhr) {
				$('#refreshTable').trigger('click');
				layer.alert('重新加载成功', {icon: 1});
			}
		});
	});
	$("#addSchedule").on('click', function () {
		scheduleModify('insert');
	})

	function scheduleModify(type, id) {
		var winIndex = layer.open({
			type: 1,
			content:
				'<div class="pt-3 container">' +
				'<form id="datasourceForm" onsubmit="return false;" >' +
				'<div class="form-group required row">' +
				'<label for="name" class="col-sm-2 col-form-label text-right">调度名称</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="name" name="name">' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="configId" class="col-sm-2 col-form-label text-right">配置名称</label>' +
				'<div class="col-sm-6">' +
				'<select class="mr-sm-2 custom-select db-type-select" id="configId" name="configId">' +
				'</select>' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="expr" class="col-sm-2 col-form-label text-right">调度表达式</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="expr" name="expr">' +
				'</div>' +
				'<div class="col-sm-1 text-left">' +
				'<i class="fa fa-question-circle cron-expr-help"></i>' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="status" class="col-sm-2 col-form-label text-right">状态</label>' +
				'<div class="col-sm-6">' +
				'<select class="mr-sm-2 custom-select" id="status" name="status">' +
				'<option value="1">可用</option>' +
				'<option value="0">不可用</option>' +
				'</select>' +
				'</div>' +
				'</div>' +
				'<div class="form-group row">' +
				'<label for="remark" class="col-sm-2 col-form-label text-right">备注</label>' +
				'<div class="col-sm-6">' +
					'<textarea rows="5" class="form-control" id="remark" name="remark"> </textarea>' +
				'</div>' +
				'</div>' +
				'<div class="form-group row">' +
				'<div class="col-sm-12 text-center">' +
				'<button id="saveBtn" type="button" class="btn btn-primary">保存</button>' +
				'<button id="cancelBtn" type="button" class="btn btn-light ml-5">取消</button>' +
				'</div>' +
				'</div>' +
				'</form>' +
				'</div>',
			area: ['1000px', '500px'],
			title: '调度配置',
			fixed: false,
			resize: false,
			success: function (layero, index) {
				//查询配置列表
				var getConfig = {
					type: "GET",
					url: CONTEXT_PATH + "/config/list" ,
					data: {status: 1},
					success: function(data, status, xhr) {
						var $select = $("#configId");
						$.each(data.data, function(i, n) {
							$('<option/>').val(n.id).text(n.name).appendTo($select);
						})
						if(type === 'update') {
							$.ajax({
								type: "POST",
								url: CONTEXT_PATH + "/schedule/get" ,
								data: {id: id},
								success: function(data, status, xhr) {
									var result = data.data;
									$("#name").val(result.name);
									$("#configId").val(result.configId);
									$("#expr").val(result.expr);
									$("#status").val(result.status);
									$("#remark").val(result.remark);
								}
							});
						}
					}
				};
				$.ajax(getConfig);
			}
		});

		var validator = $('#datasourceForm').validate({
			ignore: ':hidden',//隐藏元素忽略
			rules: {
				name: 'required',
				configId: 'required',
				expr: 'required',
				status: 'required'
			},
			messages: {
				name: '调度名称不能为空',
				configId: '配置名称不能为空',
				expr: '调度表达式不能为空',
				status: '状态不能为空'
			}
		});

		$(".cron-expr-help").click(function() {
			layer.alert("该属性为Spring Cron表达式，与Quartz Cron表达式类似。" +
					"Quartz Cron表达式共7位，分别是：秒、分、时、月、星期、年，其中年为可选字段。" +
					"这里要特别注意的是Spring Cron为6位，不支持年，其他位含义与Quartz Cron表达式相同。" +
					"例如：“0 0/5 * * * ?”，代表每隔5分钟执行一次。" +
					"由于Quartz Cron资料比Spring Cron更多，建议百度搜索\"Quartz Cron表达式\"做进一步了解。",
				{ icon: 3, maxWidth: 550 });
		});

		$("#saveBtn").click(function () {
			var flag = validator.form(),
				url = '',
				success_msg = '';
			if (flag) {
				var load_data = $("#datasourceForm").serializeArray();
				if (type === 'insert') {
					url = CONTEXT_PATH + "/schedule/add";
					success_msg = '新增成功';
				} else if(type === 'update') {
					url = CONTEXT_PATH + "/schedule/update";
					success_msg = '修改成功';
					load_data.push({"name":"id","value":id});
				}

				$.ajax({
					type: "POST",
					url: url,
					data: load_data,
					success: function(data, status, xhr) {
						layer.alert(success_msg, {icon: 1}, function(index) {
							$('#refreshTable').trigger('click');
							layer.closeAll();
						});
					}
				});
			}
		});

		$("#cancelBtn").click(function () {
			layer.close(winIndex);
		});
	}

	function deleteScheduleById(id) {
		$.ajax({
			type: "POST",
			url: CONTEXT_PATH + "/schedule/delete" ,
			data: {id: id},
			success: function(data, status, xhr) {
				layer.alert('删除成功', {icon: 1}, function(index) {
					$('#refreshTable').trigger('click');
					layer.closeAll();
				});
			}
		});
	}
});