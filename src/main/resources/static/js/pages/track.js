$(function() {

	var errorMap = {};

	$('#traceTable').bootstrapTable({
		columns: [ {
			title : '配置名称',
			align : 'center',
			field: 'name'
		}, {
			title : '触发方式',
			align : 'center',
			field: 'triggerType',
			formatter: function(value, row, index) {
				return value == 0 ? "手动触发" : (value == 1 ? "定时触发" : "未知");
			}
		}, {
			title : '状态',
			align : 'center',
			width : 120,
			field: 'status',
			formatter: function(value, row, index) {
				if (value == 0) {
					return '就绪';
				} else if (value == 1) {
					return '成功';
				} else if (value == 2) {
					return '失败';
				} else if (value == 3) {
					return '运行中';
				} else if (value == 4) {
					return '超时';
				} else {
					return '无'
				}
			}
		}, {
			title : '消息',
			align : 'center',
			field: 'message',
			class: 'td-error-message',
			formatter: function(value, row, index) {
				if (value) {
					errorMap[row.id] = value;
					var $a = $('<a/>').addClass('error-message underline-none-link')
						.attr('href', 'javascript:void(0);')
						.attr('data-track-id', row.id)
						.text(value);
					return $a[0].outerHTML;
				}
				return value;
			}
		}, {
			title : '开始时间',
			align : 'center',
			field: 'startTime',
			formatter: function(value, row, index) {
				return value;
			}
		}, {
			title : '结束时间',
			align : 'center',
			field: 'endTime',
			formatter: function(value, row, index) {
				return value;
			}
		}, {
			title : '操作',
			align : 'center',
			field: '',
			width: '120',
			class: 'oper',
			formatter: function(value, row, index) {
				return row.status == 3 ? '<a href="javascript:void(0)" class="stop-task" data-track-id="' + row.id + '">停止</a>' : null;
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
		url: CONTEXT_PATH + '/track/listByPage',
		ajaxOptions: {
			async: true
		},
		queryParams: function(params) {
			var configId = $("#configId").val();
			if (configId) {
				params.configId = configId;
			}
			$.extend(params, {
				configName: $.trim($('#conditionConfigName').val()),
				triggerType: $('#conditionTriggerType').val(),
				status: $('#conditionStatus').val(),
				startTime: $('#conditionStartTime').val(),
				endTime: $('#conditionEndTime').val()
			});
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
	}).on('click', 'td.td-error-message .error-message', function() {
		var id = $(this).data('track-id');
		var error = errorMap[id];
		showError(error);
	}).on('click', 'td.oper .stop-task', function() {
		var id = $(this).data('track-id');
		layer.confirm('该操作可能无法真正停止SQL的执行，但可将任务设置为失败，以便单例的迁移配置再次运行。确定进行该操作?',
			{icon: 3, title:'提示'},
			function(index) {
				$.ajax({
					type: "GET",
					dataType: "text",
					url: CONTEXT_PATH + "/track/stop",
					data: {
						id: id
					},
					success: function(data, status, xhr) {
						layer.alert('停止成功', {icon: 1}, function(index){
							$('#refreshTable').trigger('click');
							layer.close(index);
						});
					}
				});
			});
	});

	$('#refreshTable').on('click', function(event, opts) {
		$('#traceTable').bootstrapTable('refresh', $.extend({}, opts));
	});

	$('#conditionStartTime').datetimepicker({
		language: 'zh-CN'
	});

	$('#conditionEndTime').datetimepicker({
		language: 'zh-CN'
	});

	$('#conditionConfigName').on('keyup', delayRefresh);
	$('.condition-form').on('change', '.custom-select,.dateinput.form-control', delayRefresh);

	setInterval(function() {
		$('#traceTable').bootstrapTable('refresh', {
			silent: true
		});
	}, 30000);

	function delayRefresh() {
		var callee = arguments.callee;
		if (callee.timer) {
			clearTimeout(callee.timer);
		}
		callee.timer = setTimeout(function() {
			$('#refreshTable').trigger('click', [ {
				pageNumber: 1
			} ]);
		}, 500);
	}

	function showError(error) {
		layer.open({
			type: 1,
			area: ['1000px', '650px'],
			title: '信息查看',
			fixed: true,
			//resize: true,
			//maxmin: true,
			content: '<div id="errorContView" class="errorview"></div>'
		});
		var $errorCont = $('#errorContView');
		$errorCont.text($.trim(error || ''));
	}

});