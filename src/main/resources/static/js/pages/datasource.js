$(function() {

	$('#datasourceTable').bootstrapTable({
		columns: [ {
			title : '数据源名称',
			align : 'center',
			field: 'name'
		}, {
			title : '类型',
			align : 'center',
			field: 'dbType'
		}, {
			title : 'URL',
			align : 'center',
			field: 'jdbcUrl'
		}, {
			title : '驱动',
			align : 'center',
			field: 'jdbcDriver'
		}, {
			title : '用户名',
			align : 'center',
			field: 'jdbcUsername',
		}, {
			title : '状态',
			align : 'center',
			field: 'status',
			formatter: function(value, row, index) {
				return value == 0 ? '不可用' : '可用';
			}
		}, {
			title : '备注',
			align : 'center',
			field: 'remark'
		}, {
			title : '操作',
			align : 'center',
			field: 'operate',
			class: 'oper',
			formatter: function (value, row, index) {
				return '<a href="javascript:void(0)" class="update-data-source" data-source-id="' + row.id + '">修改</a> ' +
					'<a href="javascript:void(0)" class="delete-data-source" data-source-id="' + row.id + '">删除</a> ';
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
		url: CONTEXT_PATH + '/datasource/listByPage',
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
	}).on('click', 'td.oper .update-data-source', function () {
		var id = $(this).data('source-id');
		datasourceModify('update', id);
	}).on('click', 'td.oper .delete-data-source', function () {
		var id = $(this).data('source-id');
		layer.confirm('确认删除该条记录?', {icon: 3, title:'提示'}, function(index) {
			deleteDatasourceById(id);
			layer.close(index);
		});

	});

	$('#addDatasource').on('click', function() {
		datasourceModify('insert');
	});

	$('#reloadDatasource').on('click', function() {
		$.ajax({
			type: "GET",
			dataType: "text",
			url: CONTEXT_PATH + "/datasource/reload",
			data: null,
			success: function(data, status, xhr) {
				$('#refreshTable').trigger('click');
				layer.alert('重新加载成功', {icon: 1});
			}
		});
	});

	$('#refreshTable').on('click', function() {
		$('#datasourceTable').bootstrapTable('refresh');
	});

	function datasourceModify(type, id) {
		var datasourceinfo = {};
		var datasource_save = {};
		var datasource_update = {};
		var winIndex = layer.open({
			type: 1,
			content:
				'<div class="pt-3 container">' +
				'<form id="datasourceForm" onsubmit="return false;" >' +
				'<div class="form-group required row">' +
				'<label for="name" class="col-sm-2 col-form-label text-right">数据源名称</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="name" name="name">' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="dbType" class="col-sm-2 col-form-label text-right">类型</label>' +
				'<div class="col-sm-6">' +
				'<select class="mr-sm-2 custom-select db-type-select" id="dbType" name="dbType">' +
				'<option value="mysql">MySQL</option>' +
				'<option value="oracle">Oracle</option>' +
				'<option value="gauss" class="d-none">GaussDB</option>' +
				'<option value="sqlserver">SQL server</option>' +
				'<option value="mppdb">MPPDB</option>' +
				'<option value="postgres">PostgreSQL</option>' +
				'</select>' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="jdbcUrl" class="col-sm-2 col-form-label text-right">URL</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="jdbcUrl" name="jdbcUrl">' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="jdbcDriver" class="col-sm-2 col-form-label text-right">驱动</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="jdbcDriver" name="jdbcDriver" value="com.mysql.jdbc.Driver" readonly>' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="jdbcUsername" class="col-sm-2 col-form-label text-right">用户名</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="jdbcUsername" name="jdbcUsername">' +
				'</div>' +
				'</div>' +
				'<div class="form-group required row">' +
				'<label for="jdbcPassword" class="col-sm-2 col-form-label text-right">密码</label>' +
				'<div class="col-sm-6">' +
				'<input type="text" class="form-control" id="jdbcPassword" name="jdbcPassword">' +
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
				'<button id="testBtn" type="button" class="btn btn-light btn-primary ml-3">测试</button>' +
				'<button id="cancelBtn" type="button" class="btn btn-light ml-3">取消</button>' +
				'</div>' +
				'</div>' +
				'</form>' +
				'</div>',
			area: ['1000px', '650px'],
			title: '数据源配置',
			fixed: false,
			resize: false,
			success: function (layero, index) {
				if(type === 'update') {
					$.ajax({
						type: "POST",
						url: CONTEXT_PATH + "/datasource/get" ,
						data: {id: id},
						success: function(data, status, xhr) {
							var result = data.data;
							$("#name").val(result.name);
							$("#dbType").val(result.dbType);
							$("#jdbcUrl").val(result.jdbcUrl);
							$("#jdbcDriver").val(result.jdbcDriver);
							$("#jdbcUsername").val(result.jdbcUsername);
							$("#jdbcPassword").val(result.jdbcPassword);
							$("#status").val(result.status);
							$("#remark").val(result.remark);
							datasourceinfo = {
								driver: result.jdbcDriver,
								url: result.jdbcUrl,
								username: result.jdbcUsername,
								password: result.jdbcPassword
							};
						}
					});
				}
			}
		});

		var validator = $('#datasourceForm').validate({
			ignore: ':hidden',//隐藏元素忽略
			rules: {
				name: 'required',
				dbType: 'required',
				jdbcUrl: 'required',
				jdbcDriver: 'required',
				jdbcUsername: 'required',
				jdbcPassword: 'required',
				status: 'required'
			},
			messages: {
				name: '数据源名称不能为空',
				dbType: '类型不能为空',
				jdbcUrl: 'URL不能为空',
				jdbcDriver: '驱动不能为空',
				jdbcUsername: '用户名不能为空',
				jdbcPassword: '密码不能为空',
				status: '状态不能为空'
			}
		});

		$(".db-type-select").on("change", function () {
			var dbTypeSelect = $(this).val(),
				jdbcDriver = "";
			if (dbTypeSelect === "mysql") {
				jdbcDriver = "com.mysql.jdbc.Driver";
			} else if (dbTypeSelect === "oracle") {
				jdbcDriver = "oracle.jdbc.driver.OracleDriver";
			} else if (dbTypeSelect === "gauss") {
				jdbcDriver = "com.huawei.gauss.jdbc.ZenithDriver";
			}else if (dbTypeSelect === "sqlserver") {
				jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			} else if (dbTypeSelect === "mppdb") {
				jdbcDriver = "org.postgresql.Driver";
			} else if (dbTypeSelect === "postgres") {
				jdbcDriver = "org.postgresql.Driver";
			}

			$("#jdbcDriver").val(jdbcDriver);
		});

		$("#saveBtn").click(function () {
			var flag = validator.form(),
				url = '',
				success_msg = '';
			if (!flag) {
				return;
			}
			if ($('#jdbcUrl').val() != datasourceinfo.url
					|| $('#jdbcDriver').val() != datasourceinfo.driver
					|| $('#jdbcUsername').val() != datasourceinfo.username
					|| $('#jdbcPassword').val() != datasourceinfo.password) {
				layer.alert('请测试通过后再进行保存', {icon: 0});
				return;
			}
			
			var load_data = $("#datasourceForm").serializeArray();
			if (type === 'insert') {
				url = CONTEXT_PATH + "/datasource/add";
				success_msg = '新增成功';
			} else if(type === 'update') {
				url = CONTEXT_PATH + "/datasource/update";
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
		});

		$("#testBtn").click(function () {
			var flag = validator.form();
			if (flag) {
				var loading = layer.load();
				$.ajax({
					type: "POST",
					url: CONTEXT_PATH + "/datasource/test",
					data: $("#datasourceForm").serializeArray(),
					success: function(data, status, xhr) {
						layer.alert("连接成功！", {icon: 1});
						datasourceinfo = {
							url: $('#jdbcUrl').val(),
							driver: $('#jdbcDriver').val(),
							username: $('#jdbcUsername').val(),
							password: $('#jdbcPassword').val()
						};
					},
					complete: function() {
						layer.close(loading);
					}
				});
			}
		});

		$("#cancelBtn").click(function () {
			layer.close(winIndex);
		});
	}

	function deleteDatasourceById(id) {
		$.ajax({
			type: "POST",
			url: CONTEXT_PATH + "/datasource/delete" ,
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