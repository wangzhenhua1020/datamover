$(function() {

	var srcSqlMap = {};

	$.validator.addMethod('positiveInt', function(value, element) {
		return this.optional(element) || (/^[0-9]+$/.test(value) && value > 0);
	}, '只能输入正整数');

	$.validator.addMethod('jsonArray', function(value, element) {
		var flag = false;
		try {
			arr = eval('(' + value + ')');
			if ($.isArray(arr)) {
				flag = true;
			}
		} catch(e) {
		}
		return this.optional(element) || flag;
	}, '只能输入字符串数组JSON');

	$('#configTable').bootstrapTable({
		columns: [ {
			title : '配置名称',
			align : 'center',
			field: 'name'
		}, {
			title : '源库名称',
			align : 'center',
			field: 'srcDsName'
		}, {
			title : '源库SQL类型',
			align : 'center',
			width : 120,
			field: 'srcSqlType',
			formatter: function(value, row, index) {
				return value == 0 ? "查询SQL" : (value == 1 ? "更新SQL" : "未知");
			}
		}, {
			title : '源库SQL',
			align : 'center',
			field: 'srcSql',
			class: 'td-src-sql',
			formatter: function(value, row, index) {
				if (value) {
					srcSqlMap[row.id] = value;
					return '<a href="javascript:void(0);" class="src-sql-view underline-none-link" data-config-id="' + row.id + '">' + value + '</a>';
				}
				return value;
			}
		}, {
			title : '目标库名称',
			align : 'center',
			field: 'destDsName',
			visible: false
		}, {
			title : '目标库表',
			align : 'center',
			field: 'destTable',
			visible: false
		}, {
			title : '目标库表主键',
			align : 'center',
			field: 'primaryKeyListJson',
			visible: false
		}, {
			title : '目标库表删除方式',
			align : 'center',
			field: 'destTableDeleteType',
			visible: false,
			formatter: function(value, row, index) {
				return value == 0 ? "IN" : (value == 1 ? "OR（支持NULL值）" : (value == 2 ? "全部删除" : ""));
			}
		}, {
			title : '超时时间（秒）',
			align : 'center',
			field: 'timeout',
			visible: false
		}, {
			title : '是否单例',
			align : 'center',
			field: 'singleton',
			visible: false,
			formatter: function(value, row, index) {
				return value == 0 ? '否' : '是';
			}
		}, {
			title : '状态',
			align : 'center',
			width : 120,
			field: 'status',
			formatter: function(value, row, index) {
				return value == 0 ? '不可用' : '可用';
			}
		}, {
			title : '功能描述',
			align : 'center',
			field: 'remark',
			formatter: function(value, row, index) {
				return value;
			}
		}, {
			title : '操作',
			align : 'center',
			field: '',
			width: '200',
			class: 'oper',
			formatter: function(value, row, index) {
				return '<a href="javascript:void(0)" class="update-config" data-config-id="' + row.id + '">修改</a> ' +
						'<a href="javascript:void(0)" class="delete-config" data-config-id="' + row.id + '">删除</a> ' +
						'<a href="javascript:void(0)" class="exec-config" data-config-id="' + row.id + '">执行</a> ' +
						'<a href="javascript:void(0)" class="track-result" data-config-id="' + row.id + '">跟踪</a> ';
			}
		} ],
		detailView: true,
		detailFormatter: function(index, row) {
			return '<div class="row"><div class="col-2">目标库名称：</div><div class="col-9">' + (row.destDsName || '') + '</div></div>' +
				'<div class="row"><div class="col-2">目标库表：</div><div class="col-9">' + (row.destTable || '') + '</div></div>' +
				'<div class="row"><div class="col-2">目标库表主键：</div><div class="col-9">' + (row.primaryKeyListJson || '') + '</div></div>' +
				'<div class="row"><div class="col-2">目标库表删除方式：</div><div class="col-9">' +
						((row.destTableDeleteType == 0 ? "IN" : (row.destTableDeleteType == 1 ? "OR（支持NULL值）" : "")) || '') + '</div></div>' +
				'<div class="row"><div class="col-2">超时时间（秒）：</div><div class="col-9">' + (row.timeout || '') + '</div></div>' +
				'<div class="row"><div class="col-2">是否单例：</div><div class="col-9">' + (row.singleton == 0 ? '否' : '是') + '</div></div>' +
				'<div class="row"><div class="col-2">是否单例：</div><div class="col-9">' + (row.singleton == 0 ? '否' : '是') + '</div></div>' +
				'<div class="row"><div class="col-2">是否单例：</div><div class="col-9">' + (row.singleton == 0 ? '否' : '是') + '</div></div>' +
				'<div class="row"><div class="col-2">完成后执行：</div><div class="col-9">' + (row.postAction == 0 ?
						'无' : (row.postAction == 1 ? 'SHELL脚本' : '未知')) + '</div></div>' +
				'<div class="row"><div class="col-2">执行条件：</div><div class="col-9">' + (row.postCondition == 0 ?
						'无条件执行' : (row.postCondition == 1 ? '成功后执行' : ((row.postCondition == 2 ? '失败后执行' : '未知')))) + '</div></div>' +
				'<div class="row"><div class="col-2">ID（URL调用传参）：</div><div class="col-9">' + row.id + '</div></div>';
		},
		locale: 'zh-CN',
		//height: 540,
		pagination: true,
		pageSize: 10,
		pageNumber: 1,
		pageList: [ 10 ],
		sidePagination: 'server',
		method: 'get',
		contentType: 'application/x-www-form-urlencoded',
		url: CONTEXT_PATH + '/config/listByPage',
		ajaxOptions: {
			async: true
		},
		queryParams: function(params) {
			$.extend(params, {
				name: $.trim($('#conditionName').val()),
				srcDsName: $.trim($('#conditionSrcDsName').val()),
				destDsName: $.trim($('#conditionDestDsName').val()),
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
	}).on('click', 'td.oper .exec-config', function() {
		var id = $(this).data('config-id');
		$.ajax({
			type: "GET",
			dataType: "text",
			url: CONTEXT_PATH + "/config/exec",
			data: {
				id: id
			},
			success: function(data, status, xhr) {
				layer.alert('执行命令下发成功', {icon: 1});
			}
		});
	}).on('click', 'td.oper .track-result', function() {
		var id = $(this).data('config-id');
		window.open(CONTEXT_PATH + '/track?configId=' + id, '_self');
	}).on('click', 'td.oper .update-config', function() {
		var id = $(this).data('config-id');
		configModify('update', id);
	}).on('click', 'td.oper .delete-config', function() {
		var id = $(this).data('config-id');
		layer.confirm('确认删除该条记录?',
			{icon: 3, title:'提示'},
			function(index) {
				$.ajax({
					type: 'POST',
					url: CONTEXT_PATH + '/config/delete',
					data: {
						id: id
					},
					dataType: 'text',
					success: function(data, status, xhr) {
						layer.alert('删除成功', {icon: 1}, function(index){
							$('#refreshTable').trigger('click');
							layer.closeAll();
						});
					}
				});
				layer.close(index);
			});
	}).on('click', '.src-sql-view', function() {
		var id = $(this).data('config-id');
		var sql = srcSqlMap[id] || '';
		sqlView(sql, true);
	});

	$('#refreshTable').on('click', function(event, opts) {
		$('#configTable').bootstrapTable('refresh', $.extend({}, opts));
	});

	$('#addConfig').on('click', function() {
		configModify('add');
	});

	$('.condition-form').on('keyup', '.form-control', delayRefresh);

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

	function sqlView(sql, readOnly, callback) {
		var index = layer.open({
			type: 1,
			area: ['1000px', '650px'],
			title: 'SQL查看',
			fixed: true,
			//resize: true,
			//maxmin: true,
			content: '<div id="srcSqlContView" class="sqlviewer"></div>',
			cancel: function(index){
				layer.close(index);
				var val = $('#srcSqlContView').data('editor').getValue();
				$.isFunction(callback) && callback(val);
				return false; 
			}
		});
		layer.full(index);
		var $srcSqlCont = $('#srcSqlContView');
		$srcSqlCont.data('editor',
			CodeMirror($srcSqlCont[0], {
				value: sql,
				mode: 'sql',
				lineWrapping: false,
				theme: 'panda-syntax',
				readOnly: readOnly
			})
		);
		var coder = $srcSqlCont.data('editor');
		coder.setSize('100%', '100%');
		setTimeout(function() {
			coder.refresh();
		}, 400);
	}

	function configModify(modifyType, configId) {
		var winIndex = layer.open({
			type: 1,
			area: ['1200px', '700px'],
			title: '迁移配置',
			//fixed: false,
			resize: true,
			//maxmin: true,
			content:
				'<div class="p-3 container">' +
					'<form id="configForm" class="config-form" onsubmit="return false;">' +
						'<input type="hidden" id="configId" name="id">' +
						'<div class="form-group required row">' +
							'<label for="name" class="col-sm-2 col-form-label text-right">配置名称</label>' +
							'<div class="col-sm-6">' +
								'<input type="text" class="form-control" id="name" name="name">' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="srcDsId" class="col-sm-2 col-form-label text-right">源库</label>' +
							'<div class="col-sm-6">' +
								'<select class="mr-sm-2 custom-select" id="srcDsId" name="srcDsId">' +
								'</select>' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="srcSqlType" class="col-sm-2 col-form-label text-right">源库SQL类型</label>' +
							'<div class="col-sm-6">' +
								'<select class="mr-sm-2 custom-select" id="srcSqlType" name="srcSqlType">' +
									'<option value="1">更新SQL</option>' +
									'<option value="0">查询SQL</option>' +
								'</select>' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="srcSqlCont" class="col-sm-2 col-form-label text-right">源库SQL</label>' +
							'<div class="col-sm-9">' +
								'<div id="srcSqlViewBar" class="sqlviewer-bar text-right">' +
									'<span class="sqlviewer-bar-btn" id="importTemplateBtn"><i class="fa fa-file-code-o"></i> 引用模板</span>' +
									'<span class="sqlviewer-bar-btn" id="parseVarBtn"><i class="fa fa-code"></i> 生成变量</span>' +
									'<span class="sqlviewer-bar-btn" id="maxCoderBtn"><i class="fa fa-search-plus"></i> 全屏</span>' +
								'</div>' +
								'<div id="srcSqlCont" class="sqlviewer"></div>' +
								'<input type="hidden" val="" id="srcSqlHidden" name="srcSql">' +
							'</div>' +
						'</div>' +
						'<div class="form-group row">' +
							'<label for="srcSqlCont" class="col-sm-2 col-form-label text-right">SQL变量</label>' +
							'<div class="col-sm-9">' +
								'<div id="sqlvar-cont" class="border rounded sqlvar-cont">无变量</div>' +
							'</div>' +
						'</div>' +
						'<div id="destPanel">' +
							'<div class="form-group required row">' +
								'<label for="destDsId" class="col-sm-2 col-form-label text-right">目标库</label>' +
								'<div class="col-sm-6">' +
									'<select class="mr-sm-2 custom-select" id="destDsId" name="destDsId">' +
									'</select>' +
								'</div>' +
							'</div>' +
							'<div class="form-group required row">' +
								'<label for="destTable" class="col-sm-2 col-form-label text-right">目标库表</label>' +
								'<div class="col-sm-6">' +
									'<input type="text" class="form-control" id="destTable" name="destTable">' +
								'</div>' +
							'</div>' +
							'<div class="form-group required row">' +
								'<label for="destTableDeleteType" class="col-sm-2 col-form-label text-right">目标库表删除方式</label>' +
								'<div class="col-sm-6">' +
									'<select class="mr-sm-2 custom-select" id="destTableDeleteType" name="destTableDeleteType">' +
										'<option value="0">OR（支持NULL值）</option>' +
										'<option value="1">IN</option>' +
										'<option value="2">全部删除</option>' +
									'</select>' +
								'</div>' +
							'</div>' +
							'<div class="form-group row" id="primaryKeyListJsonRow">' +
								'<label for="primaryKeyListJson" class="col-sm-2 col-form-label text-right">目标库表主键</label>' +
								'<div class="col-sm-6">' +
									'<input type="text" class="form-control" id="primaryKeyListJson" name="primaryKeyListJson">' +
								'</div>' +
							'</div>' +
						'</div>' +
						'<div class="form-group row">' +
							'<label for="timeout" class="col-sm-2 col-form-label text-right">超时时间（秒）</label>' +
							'<div class="col-sm-6">' +
								'<input type="text" class="form-control" id="timeout" name="timeout">' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="singleton" class="col-sm-2 col-form-label text-right">是否单例</label>' +
							'<div class="col-sm-6">' +
								'<select class="mr-sm-2 custom-select" id="singleton" name="singleton">' +
									'<option value="1">是</option>' +
									'<option value="0">否</option>' +
								'</select>' +
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
							'<label for="remark" class="col-sm-2 col-form-label text-right">功能描述</label>' +
							'<div class="col-sm-9">' +
								'<textarea rows="5" class="form-control" id="remark" name="remark"></textarea>' +
							'</div>' +
						'</div>' +
						'<hr></hr>' +
						'<div class="form-group required row">' +
							'<label for="postAction" class="col-sm-2 col-form-label text-right">完成后执行</label>' +
							'<div class="col-sm-6">' +
								'<select class="mr-sm-2 custom-select" id="postAction" name="postAction">' +
									'<option value="0">无</option>' +
									'<option value="1">SHELL脚本</option>' +
								'</select>' +
							'</div>' +
						'</div>' +
						'<div id="postPanel">' +
							'<div class="form-group required row">' +
								'<label for="postCondition" class="col-sm-2 col-form-label text-right">执行条件</label>' +
								'<div class="col-sm-6">' +
									'<select class="mr-sm-2 custom-select" id="postCondition" name="postCondition">' +
										'<option value="0">无条件执行</option>' +
										'<option value="1">成功后执行</option>' +
										'<option value="2">失败后执行</option>' +
									'</select>' +
								'</div>' +
							'</div>' +
							'<div class="form-group required row">' +
								'<label for="postData" class="col-sm-2 col-form-label text-right">SHELL脚本</label>' +
								'<div class="col-sm-9">' +
									'<div id="postDataCont" class="shellviewer"></div>' +
									'<input type="hidden" val="" id="postDataHidden" name="postData">' +
								'</div>' +
							'</div>' +
						'</div>' +
						'<div class="form-group row">' +
							'<div class="col-sm-12 text-center">' +
								'<button id="saveBtn" type="button" class="btn btn-primary">保存</button>' +
								'<button id="cancelBtn" type="button" class="btn btn-light ml-5">取消</button>' +
							'</div>' +
						'</div>' +
						'<div class="overlay"></div>' +
					'</form>' +
				'</div>'
		});
		//layer.full(winIndex);
		var $srcSqlCont = $('#srcSqlCont');
		var editor = CodeMirror($srcSqlCont[0], {
			value: '',
			mode: 'sql',
			lineWrapping: false,
			theme: 'panda-syntax',
			placeholder: '请输入SQL语句',
			readOnly: false
		});
		$srcSqlCont.data('editor', editor);
		editor.setSize('100%', '280px');
		editor.on('blur', function() {
			$('#srcSqlHidden').val(editor.getValue());
			setTimeout(function() {
				var validator = $('#configForm').validate();
				validator.element('#srcSqlHidden');
			});
		});

		var $postDataCont = $('#postDataCont');
		var shellEditor = CodeMirror($postDataCont[0], {
			value: '',
			mode: 'shell',
			lineWrapping: false,
			theme: 'panda-syntax',
			placeholder: '请输入SHELL脚本',
			readOnly: false
		});
		$postDataCont.data('editor', shellEditor);
		shellEditor.setSize('100%', '280px');
		shellEditor.on('blur', function() {
			$('#postDataHidden').val(shellEditor.getValue());
			setTimeout(function() {
				var validator = $('#configForm').validate();
				validator.element('#postDataHidden');
			});
		});

		var validator = $('#configForm').validate({
			ignore: '.ignore',//元素忽略
			rules: {
				name: 'required',
				srcDsId: 'required',
				srcSqlType: 'required',
				srcSql: 'required',
				destDsId: 'required',
				destTable: 'required',
				primaryKeyListJson: 'jsonArray',
				destTableDeleteType: 'required',
				timeout: 'positiveInt',
				singleton: 'required',
				status: 'required',
				postAction: 'required',
				postCondition: 'required',
				postData: 'required'
			},
			messages: {
				name: '名称不能为空',
				srcDsId: '源库不能为空',
				srcSqlType: '源库SQL类型不能为空',
				srcSql: '源库SQL不能为空',
				destDsId: '目标库不能为空',
				destTable: '目标库表不能为空',
				primaryKeyListJson: '目标库表主键格式不正确',
				destTableDeleteType: '目标库表删除方式不能为空',
				timeout: '超时时间必须为正整数',
				singleton: '是否单例不能为空',
				status: '是否可用不能为空',
				postAction: '完成后执行动作不能为空',
				postCondition: '完成后执行条件不能为空',
				postData: 'SHELL脚本不能为空'
			}
		});
		$('#srcSqlType').change(function() {
			var val = $(this).val();
			var ids = ['destDsId', 'destTable', 'primaryKeyListJson', 'destTableDeleteType'];
			if (val == 1) {
				$('#destPanel').hide();
				$.each(ids, function(idx, id) {
					$('#' + id).addClass('ignore');
				});
			} else {
				$('#destPanel').show();
				$.each(ids, function(idx, id) {
					$('#' + id).removeClass('ignore');
				});
			}
		});
		$('#destTableDeleteType').change(function() {
			var val = $(this).val();
			if (val == 2) {//全部删除
				$('#primaryKeyListJson').addClass('ignore');
				$('#primaryKeyListJsonRow').addClass('d-none');
			} else {//根据主键删除
				$('#primaryKeyListJson').removeClass('ignore');
				$('#primaryKeyListJsonRow').removeClass('d-none');
			}
		});
		$('#importTemplateBtn').click(function() {
			var tplIndex = layer.open({
				type: 1,
				area: ['600px', '400px'],
				title: '模板配置',
				fixed: true,
				//resize: true,
				//maxmin: true,
				content:
					'<div class="p-3 container">' +
						'<form id="templateForm" class="template-form" onsubmit="return false;">' +
							'<div class="form-group row">' +
								'<label for="groupId" class="col-sm-2 col-form-label text-right">所属分组</label>' +
								'<div class="col-sm-9">' +
									'<div class="border rounded template-group-tree-cont">' +
										'<ul id="templateGroupTree" class="ztree"></ul>' +
										'<div class="nodata">无模板分组数据</div>' +
									'</div>' +
								'</div>' +
							'</div>' +
							'<div class="form-group row">' +
								'<label for="remark" class="col-sm-2 col-form-label text-right">模板名称</label>' +
								'<div class="col-sm-9">' +
									'<select class="mr-sm-2 custom-select" id="templateSelect" name="templateSelect">' +
									'</select>' +
								'</div>' +
							'</div>' +
							'<div class="form-group row">' +
								'<label for="remark" class="col-sm-2 col-form-label text-right">引入方式</label>' +
								'<div class="col-sm-9">' +
									'<select class="mr-sm-2 custom-select" id="importType">' +
										'<option value="2" selected="selected">末尾追加</value>' +
										'<option value="1">光标处追加</value>' +
										'<option value="0">整体替换</value>' +
									'</select>' +
								'</div>' +
							'</div>' +
							'<div class="form-group row">' +
								'<div class="col-sm-12 text-center">' +
									'<button id="importSubmitBtn" type="button" class="btn btn-primary">引入</button>' +
									'<button id="importCancelBtn" type="button" class="btn btn-light ml-5">取消</button>' +
								'</div>' +
							'</div>' +
							'<div class="overlay"></div>' +
						'</form>' +
					'</div>'
			});
			var validator = $('#templateForm').validate({
				ignore: '.ignore',//元素忽略
				rules: {
					templateSelect: 'required'
				},
				messages: {
					templateSelect: '请选择引入模板'
				}
			});
			$('#importSubmitBtn').click(function() {
				if (!validator.form()) {
					return;
				}
				var $templateSelect = $('#templateSelect'),
					$option = $templateSelect.children('option[value="' + $templateSelect.val() + '"]'),
					sql = $option.data('sql') || '',
					importType = $('#importType').val();
				if (importType == 0) {
					editor.setValue(sql);
				} else if (importType == 1) {
					var val = editor.getValue();
					editor.replaceSelection(val ? '\n' + sql + '\n' : sql);
				} else if (importType == 2) {
					var val = editor.getValue();
					editor.setValue((val ? val  + '\n' : '') + sql);
				} else {
					layer.alert('不支持的引入方式[ ' + importType + ' ]', {icon: 2});
				}
				layer.close(tplIndex);
			});
			$('#importCancelBtn').click(function() {
				layer.close(tplIndex);
			});
			$.ajax({
				type: 'GET',
				url: CONTEXT_PATH + '/template/group/list',
				async: false,
				data: null,
				dataType: 'json',
				success: function(data, status, xhr) {
					if (data.success) {
						var treeData = data.data || [];
						var $ztree = $("#templateGroupTree");
						$.fn.zTree.init($ztree, {
							data: {
								simpleData: {
									enable: true,
									pIdKey: 'parentId'
								}
							},
							view: {
								selectedMulti: false
							},
							callback: {
								onClick: function(event, treeId, treeNode) {
									var treeObj = $.fn.zTree.getZTreeObj('templateGroupTree');
									if (!treeObj) {
										return;
									}
									var arr = treeObj.getSelectedNodes();
									var groupId = arr.length ? arr[0].id : null;
									var $template = $('#templateSelect').empty();
									if (!groupId) {
										return;
									}
									$.ajax({
										type: 'GET',
										url: CONTEXT_PATH + '/template/listByGroup',
										data: {
											groupId: groupId
										},
										dataType: 'json',
										success: function(data, status, xhr) {
											if (data.success) {
												$.each(data.data || [], function(index, o) {
													$('<option/>').data('sql', o.content).val(o.id).text(o.name + '   ' +
															(o.type == 0 ? '（查询SQL）' : '（更新SQL）')).appendTo($template);
												});
												$('#templateForm').validate().element('#templateSelect');
											} else {
												layer.alert(data.message || '加载模板错误', {icon: 2});
											}
										}
									});
								}
							}
						}, treeData);
						if (treeData && treeData.length) {
							$ztree.show();
							$ztree.siblings('.nodata').hide();
						} else {
							$ztree.hide();
							$ztree.siblings('.nodata').show();
						}
					} else {
						layer.alert(data.message || '加载模板分组错误', {icon: 2});
					}
				}
			});
		});
		$('#parseVarBtn').click(function() {
			var editor = $('#srcSqlCont').data('editor'),
				text = editor.getValue(),
				regex = /(?:\$\{([0-9a-zA-Z_]{1,30})\})/g,
				match = null,
				set = {},
				vars = [],
				innerVars = ['CONFIG_ID', 'CONFIG_NAME', 'CONFIG_REMARK', 'CONFIG_INST_ID', 'SRC_DS_NAME', 'SRC_DS_USER', 'DEST_DS_NAME',
					'DEST_DS_USER', 'DEST_DS_TABLE', 'DEST_DS_TABLE_PK_JSON', 'SYSTEM_CURRENT_TIME', 'SYSTEM_CURRENT_TIME_STR',
					'DATETIME_CURRENT_YEAR_START_DATE', 'DATETIME_CURRENT_YEAR_END_DATE', 'DATETIME_LAST_YEAR_START_DATE', 'DATETIME_LAST_YEAR_END_DATE',
					'DATETIME_CURRENT_QUARTER_START_DATE', 'DATETIME_CURRENT_QUARTER_END_DATE', 'DATETIME_LAST_QUARTER_START_DATE', 'DATETIME_LAST_QUARTER_END_DATE',
					'DATETIME_CURRENT_MONTH_START_DATE', 'DATETIME_CURRENT_MONTH_END_DATE', 'DATETIME_LAST_MONTH_START_DATE', 'DATETIME_LAST_MONTH_END_DATE',
					'DATETIME_CURRENT_WEEK_START_DATE', 'DATETIME_CURRENT_WEEK_END_DATE', 'DATETIME_LAST_WEEK_START_DATE', 'DATETIME_LAST_WEEK_END_DATE',
					'DATETIME_CURRENT_HOUR_START_TIME', 'DATETIME_CURRENT_HOUR_END_TIME', 'DATETIME_LAST_HOUR_START_TIME', 'DATETIME_LAST_HOUR_END_TIME'];
			while(match = regex.exec(text)) {
				var varName = match[1];
				if (set[varName]) {//去重
					continue;
				}
				if ($.inArray(varName, innerVars) == -1) {//内置变量不列出
					set[varName] = true;
					vars.push(varName);
				}
			}
			var $container = $('#sqlvar-cont'),
				$existedVars = $container.children('.sqlvar-row');
			if (vars.length) {
				if (!$container.children('.sqlvar-row').length) {
					$container.empty();
				}
				var existedVars = [],
					existedVarsSet = {};
				$existedVars.each(function() {
					var $var = $(this),
						varName = $var.find('.sqlvar-name').val();
					$var.removeClass('sqlvar-error');
					if (!existedVarsSet[varName]) {
						existedVars.push(varName);
						existedVarsSet[varName] = $var;
					} else {
						$var.addClass('sqlvar-error').attr('title', '该变量重复，请手动删除');
					}
				});
				$.each(vars, function(index, varName) {
					var flag = true;
					$.each(existedVars, function(index, existedVar) {
						if (varName == existedVar) {
							existedVarsSet[varName].removeClass('sqlvar-error');
							delete existedVarsSet[varName];
							flag = false;
							return false;
						}
					});
					if (flag) {//不存在则添加
						$container.append(createVarElem({ name: varName }));
					}
				});
				$.each(existedVarsSet, function(varName) {
					$existedVars.each(function() {
						var $var = $(this),
							tmpName = $var.find('.sqlvar-name').val();
						if (varName == tmpName) {
							$var.addClass('sqlvar-error').attr('title', '该变量在当前脚本中不存在，属于多余变量，请手动删除');
							//$var.remove();
							return false;
						}
					});
				});
			} else {
				var $existedVars = $container.children('.sqlvar-row');
				if ($existedVars.length) {
					$existedVars.each(function() {
						$(this).addClass('sqlvar-error').attr('title', '该变量在当前脚本中不存在，属于多余变量，请手动删除');
					});
				} else {
					$container.empty().text('无变量');
				}
			}
		});
		$('#maxCoderBtn').click(function() {
			var editor = $('#srcSqlCont').data('editor'),
				sql = editor.getValue() || '',
				$layer = $('#layui-layer' + winIndex),
				$layerShade = $('#layui-layer-shade' + winIndex);
			$layer.addClass('d-none');
			$layerShade.addClass('d-none');
			sqlView(sql, false, function(sql) {
				$layer.removeClass('d-none');
				$layerShade.removeClass('d-none');
				editor.setValue(sql || '');
				editor.refresh();
				editor.focus();
			});
		});
		$('#sqlvar-cont').on('click', '.sqlvar-remover', function() {
			var $row = $(this).parent(),
				$cont = $row.parent();
			$row.remove();
			if (!$cont.children('.sqlvar-row').length) {
				$cont.empty().text('无变量');
			}
		});
		$('#postAction').change(function() {
			var val = $(this).val();
			var ids = ['postCondition', 'postDataHidden'];
			if (val == 1) {
				$('#postPanel').show();
				$.each(ids, function(idx, id) {
					$('#' + id).removeClass('ignore');
				});
				setTimeout(function() {
					$('#postDataCont').data('editor').refresh();
				}, 500);
			} else {
				$('#postPanel').hide();
				$.each(ids, function(idx, id) {
					$('#' + id).addClass('ignore');
				});
			}
		});
		
		$('#cancelBtn').click(function() {
			layer.close(winIndex);
		});
		$('#saveBtn').click(function() {
			var flag = validator.form();
			if (!flag) {
				return;
			}
			var $sqlVarCont = $('#sqlvar-cont');
			if ($sqlVarCont.find('.sqlvar-error').length) {
				return;
			}
			var reqUrl = '',
				operZh = '';
			if (modifyType == 'update') {
				reqUrl = CONTEXT_PATH + '/config/update';
				operZh = '修改';
			} else if (modifyType == 'add') {
				reqUrl = CONTEXT_PATH + '/config/add';
				operZh = '新增';
			} else {
				layer.alert('不支持的修改类型', {icon: 2});
				return;
			}
			var formData = $('#configForm').serializeArray() || [];
			var varList = [];
			$sqlVarCont.find('.sqlvar-row').each(function() {
				var $row = $(this);
				varList.push({
					id: $row.data('sqlvar-id'),
					name: $row.find('.sqlvar-name').val(),
					value: $row.find('.sqlvar-value').val(),
					remark: $row.find('.sqlvar-remark').val()
				});
			});
			formData.push({
				name: 'vars',
				value: varList
			});
			var submitData = {};
			$.each(formData, function(idx, item) {
				submitData[item.name] = item.value;
			});
			$.ajax({
				type: 'POST',
				url: reqUrl,
				contentType: 'application/json;charset=UTF-8',
				data: JSON.stringify(submitData),
				dataType: 'text',
				success: function(data, status, xhr) {
					layer.alert(operZh + '成功', {icon: 1}, function(index){
						$('#refreshTable').trigger('click');
						layer.closeAll();
					});
				}
			});
		});
		$.ajax({
			type: "GET",
			async: false,
			url: CONTEXT_PATH + "/datasource/list",
			data: null,
			dataType: "json",
			success: function(data, status, xhr) {
				if (data.success && data.data) {
					var $src = $('#srcDsId').empty(),
						$dest = $('#destDsId').empty();
					$.each(data.data, function(idx, ds) {
						$('<option/>').val(ds.id).text(ds.name).appendTo($src);
						$('<option/>').val(ds.id).text(ds.name).appendTo($dest);
					});
				} else {
					layer.alert(data.message || '加载数据源出错', {icon: 2});
				}
			}
		});
		//设置数据
		if (modifyType == 'update') {
			var $overlay = $('#configForm').children('.overlay');
			$overlay.show();
			$.ajax({
				type: "GET",
				url: CONTEXT_PATH + "/config/getDetail",
				data: {
					id: configId
				},
				dataType: "json",
				success: function(data, status, xhr) {
					if (data.success && data.data) {
						setConfigForm(data.data);
					} else {
						layer.alert(data.message || '加载数据源出错', {icon: 2});
					}
				},
				complete: function() {
					$overlay.hide();
				}
			});
		} else {
			setConfigForm();
		}
	}

	function createVarElem(varObj) {
		varObj = varObj || {};
		var $var =
			$('<div class="row sqlvar-row">' +
				'<div class="col-sm-4">变量名<input class="sqlvar-name" type="text"/></div>' +
				'<div class="col-sm-4">变量值<input class="sqlvar-value" type="text"/></div>' +
				'<div class="col-sm-4">备注<input class="sqlvar-remark" type="text"/></div>' +
				'<span class="fa fa-remove sqlvar-remover"></span>' +
			'</div>');
		$var.find('.sqlvar-name').val(varObj.name || '');
		$var.find('.sqlvar-value').val(varObj.value || '');
		$var.find('.sqlvar-remark').val(varObj.remark || '');
		$var.data('sqlvar-id', varObj.id);
		$var.data('sqlvar-type', varObj.type);
		return $var;
	}

	function setConfigForm(configData) {
		configData = configData || {};
		$("#configId").val(configData.id);
		$("#name").val(configData.name);
		setSelectValue($("#srcDsId"), configData.srcDsId);
		setSelectValue($("#srcSqlType"), configData.srcSqlType);
		var editor = $('#srcSqlCont').data('editor');
		editor.setValue(configData.srcSql || '');
		setTimeout(function() {
			editor.refresh();
		}, 500);
		$('#srcSqlHidden').val(configData.srcSql || '');
		setSelectValue($('#destDsId'), configData.destDsId);
		$("#destTable").val(configData.destTable);
		$("#primaryKeyListJson").val(configData.primaryKeyListJson);
		setSelectValue($("#destTableDeleteType"), configData.destTableDeleteType);
		$("#timeout").val(configData.timeout);
		setSelectValue($("#singleton"), configData.singleton);
		setSelectValue($("#status"), configData.status);
		$("#remark").val(configData.remark);
		var $varCont = $('#sqlvar-cont').empty();
		if (configData.vars && configData.vars.length > 0) {
			$.each(configData.vars, function(idx, varObj) {
				$varCont.append(createVarElem(varObj));
			});
		} else {
			$varCont.text('无变量');
		}
		setSelectValue($('#postAction'), configData.postAction);
		setSelectValue($('#postCondition'), configData.postCondition);
		var shellEditor = $('#postDataCont').data('editor');
		shellEditor.setValue(configData.postData || '');
		$('#postDataHidden').val(configData.postData || '');
		//触发事件
		$('#srcSqlType').trigger('change');
		$('#destTableDeleteType').trigger('change');
		$('#postAction').trigger('change');
	}

	function setSelectValue($select, val) {
		if (!$select) {
			return;
		}
		$select.val(val);
		if (!$select.val()) {//默认选中第一个option
			$select.children('option:first').prop('selected', true);
		}
	}

});