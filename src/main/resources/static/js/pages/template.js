$(function() {

	var sqlMap = {};
	var pageData = {};

	$('#templateTable').bootstrapTable({
		columns: [ {
			title : '模板名称',
			align : 'center',
			field: 'name'
		}, {
			title : '所属分组',
			align : 'center',
			field: 'groupName'
		}, {
			title : '模板类型',
			align : 'center',
			width : 120,
			field: 'type',
			formatter: function(value, row, index) {
				if (value == 0) {
					return '查询SQL';
				} else if (value == 1) {
					return '更新SQL';
				} else {
					return '未知'
				}
			}
		}, {
			title : 'SQL',
			align : 'center',
			field: 'content',
			class: 'td-sql',
			formatter: function(value, row, index) {
				if (value) {
					sqlMap[row.id] = value;
					return '<a href="javascript:void(0);" class="sql-view underline-none-link" data-template-id="' + row.id + '">' + value + '</a>';
				}
				return value;
			}
		}, {
			title : '功能描述',
			align : 'center',
			field: 'remark',
			formatter: function(value, row, index) {
				return value;
			}
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
			width: '200',
			class: 'oper',
			formatter: function(value, row, index) {
				return '<a href="javascript:void(0)" class="update-template" data-template-id="' + row.id + '">修改</a> ' +
						'<a href="javascript:void(0)" class="delete-template" data-template-id="' + row.id + '">删除</a> '
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
		url: null,//CONTEXT_PATH + '/template/listByPage',
		ajaxOptions: {
			async: true
		},
		queryParams: function(params) {
			var groupPath = $("#groupPath").val();
			if (groupPath) {
				params.groupPath = groupPath;
			}
			params.name = $('#conditionName').val();
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
			pageData = {};
			$.each(rows, function(idx, row) {
				pageData[row.id] = row;
			});
			return { total: total, rows: rows };
		}
	}).on('click', '.sql-view', function() {
		var id = $(this).data('template-id');
		var sql = sqlMap[id] || '';
		sqlView(sql, true);
	}).on('click', 'td.oper .update-template', function() {
		var id = $(this).data('template-id');
		templateModify('update', pageData[id]);
	}).on('click', 'td.oper .delete-template', function() {
		var id = $(this).data('template-id');
		layer.confirm('确认删除该条记录?',
			{icon: 3, title:'提示'},
			function(index) {
				$.ajax({
					type: 'POST',
					url: CONTEXT_PATH + '/template/delete',
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
	});

	$('#refreshTable').on('click', function(event, opts) {
		$('#templateTable').bootstrapTable('refresh', $.extend({}, opts));
	});

	$('.condition-form').on('keyup', '.form-control', delayRefresh);

	$('#addGroupBtn').click(function() {
		var node = getSelectedGroupTreeNode();
		if (node) {
			groupModify('add', { parentId: node.id });
		} else {
			layer.confirm('未选中任何节点，新增节点将作为根节点，是否继续？',
				{icon: 3, title:'提示'},
				function(index) {
					groupModify('add');
					layer.close(index);
				});
		}
	});

	$('#updateGroupBtn').click(function() {
		var node = getSelectedGroupTreeNode();
		if (node) {
			groupModify('update', node);
		} else {
			layer.alert('请选择被修改节点', {icon: 2});
		}
	});

	$('#deleteGroupBtn').click(function() {
		var node = getSelectedGroupTreeNode();
		if (node) {
			var id = node.id;
			layer.confirm('确认删除该条记录?',
				{icon: 3, title:'提示'},
				function(index) {
					$.ajax({
						type: 'POST',
						url: CONTEXT_PATH + '/template/group/delete',
						data: {
							id: id
						},
						dataType: 'text',
						success: function(data, status, xhr) {
							layer.alert('删除成功', {icon: 1}, function(index){
								reloadGroupTree();
								layer.closeAll();
							});
						}
					});
					layer.close(index);
				});
		} else {
			layer.alert('请选择被删除节点', {icon: 2});
		}
	});

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

	function getSelectedGroupTreeNode() {
		var treeObj = $.fn.zTree.getZTreeObj('templateGroupTree');
		if (!treeObj) {
			return;
		}
		var nodes = treeObj.getSelectedNodes();
		if (nodes && nodes.length) {
			return nodes[0];
		}
		return null;
	}

	function groupModify(modifyType, groupData) {
		var winIndex = layer.open({
			type: 1,
			area: ['1000px', '335px'],
			title: '模板分组',
			fixed: true,
			//resize: true,
			//maxmin: true,
			content:
				'<div class="p-3 container">' +
					'<form id="groupForm" class="group-form" onsubmit="return false;">' +
						'<input type="hidden" id="groupId" name="id">' +
						'<input type="hidden" id="parentId" name="parentId">' +
						'<div class="form-group required row">' +
							'<label for="name" class="col-sm-2 col-form-label text-right">分组名称</label>' +
							'<div class="col-sm-6">' +
								'<input type="text" class="form-control" id="name" name="name">' +
							'</div>' +
						'</div>' +
						'<div class="form-group row">' +
							'<label for="remark" class="col-sm-2 col-form-label text-right">备注</label>' +
							'<div class="col-sm-9">' +
								'<textarea rows="5" class="form-control" id="remark" name="remark"></textarea>' +
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
		var validator = $('#groupForm').validate({
			ignore: '.ignore',//元素忽略
			rules: {
				name: 'required'
			},
			messages: {
				name: '名称不能为空'
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
			var reqUrl = '',
				operZh = '';
			if (modifyType == 'update') {
				reqUrl = CONTEXT_PATH + '/template/group/update';
				operZh = '修改';
			} else if (modifyType == 'add') {
				reqUrl = CONTEXT_PATH + '/template/group/add';
				operZh = '新增';
			} else {
				layer.alert('不支持的修改类型', {icon: 2});
				return;
			}
			$.ajax({
				type: 'POST',
				url: reqUrl,
				contentType: 'application/x-www-form-urlencoded',
				data: $('#groupForm').serializeArray(),
				dataType: 'text',
				success: function(data, status, xhr) {
					layer.alert(operZh + '成功', {icon: 1}, function(index){
						reloadGroupTree();
						layer.closeAll();
					});
				}
			});
		});
		setGroupForm(groupData);
	}

	function setGroupForm(groupData) {
		groupData = groupData || {};
		$("#groupId").val(groupData.id);
		$("#name").val(groupData.name);
		$("#parentId").val(groupData.parentId);
		$("#remark").val(groupData.remark);
	}

	function reloadGroupTree() {
		$.ajax({
			type: 'GET',
			url: CONTEXT_PATH + '/template/group/list',
			data: null,
			dataType: 'json',
			success: function(data, status, xhr) {
				if (data.success) {
					var treeObj = $.fn.zTree.getZTreeObj('templateGroupTree');
					if (!treeObj) {
						return;
					}
					var allNodes = treeObj.getNodes();
					for (var i = allNodes.length - 1; i >= 0; i--) {
						treeObj.removeNode(allNodes[i]);
					}
					treeObj.addNodes(null, data.data || []);
					reloadTable();
				} else {
					layer.alert(data.message || '加载模板分组错误', {icon: 2});
				}
			}
		});
	}

	function reloadTable() {
		var node = getSelectedGroupTreeNode();
		$('#groupPath').val(node ? node.path : '');
		$('#templateTable').bootstrapTable('refreshOptions', {
			url: CONTEXT_PATH + '/template/listByPage'
		})
	}

	$('#addTemplate').click(function() {
		var node = getSelectedGroupTreeNode();
		templateModify('add', { groupId: node ? node.id : null });
	});

	function sqlView(sql, readOnly, callback) {
		var index = layer.open({
			type: 1,
			area: ['1000px', '650px'],
			title: 'SQL查看',
			fixed: true,
			//resize: true,
			//maxmin: true,
			content: '<div id="sqlContView" class="sqlviewer"></div>',
			cancel: function(index){
				layer.close(index);
				var val = $('#sqlContView').data('editor').getValue();
				$.isFunction(callback) && callback(val);
				return false; 
			}
		});
		layer.full(index);
		var $sqlCont = $('#sqlContView');
		$sqlCont.data('editor',
			CodeMirror($sqlCont[0], {
				value: sql,
				mode: 'sql',
				lineWrapping: false,
				theme: 'panda-syntax',
				readOnly: readOnly
			})
		);
		var coder = $sqlCont.data('editor');
		coder.setSize('100%', '100%');
		setTimeout(function() {
			coder.refresh();
		}, 400);
	}

	function templateModify(modifyType, templateData) {
		var winIndex = layer.open({
			type: 1,
			area: ['1200px', '700px'],
			title: '模板配置',
			//fixed: false,
			resize: true,
			//maxmin: true,
			content:
				'<div class="p-3 container">' +
					'<form id="templateForm" class="template-form" onsubmit="return false;">' +
						'<input type="hidden" id="templateId" name="id">' +
						'<div class="form-group required row">' +
							'<label for="name" class="col-sm-2 col-form-label text-right">模板名称</label>' +
							'<div class="col-sm-6">' +
								'<input type="text" class="form-control" id="name" name="name">' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="groupId" class="col-sm-2 col-form-label text-right">所属分组</label>' +
							'<div class="col-sm-6">' +
								'<div class="border rounded template-group-tree-cont">' +
									'<ul id="templateGroupTree4Modify" class="ztree"></ul>' +
									'<div class="nodata">无模板分组数据</div>' +
								'</div>' +
								'<input id="templateGroupId" type="hidden" name="groupId">' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="sqlType" class="col-sm-2 col-form-label text-right">模板类型</label>' +
							'<div class="col-sm-6">' +
								'<select class="mr-sm-2 custom-select" id="sqlType" name="type">' +
									'<option value="1">更新SQL</option>' +
									'<option value="0">查询SQL</option>' +
								'</select>' +
							'</div>' +
						'</div>' +
						'<div class="form-group required row">' +
							'<label for="sqlCont" class="col-sm-2 col-form-label text-right">SQL</label>' +
							'<div class="col-sm-9">' +
								'<div id="sqlViewBar" class="sqlviewer-bar text-right">' +
									'<span class="sqlviewer-bar-btn" id="maxCoderBtn"><i class="fa fa-search-plus"></i> 全屏</span>' +
								'</div>' +
								'<div id="sqlCont" class="sqlviewer"></div>' +
								'<input type="hidden" val="" id="contentHidden" name="content">' +
							'</div>' +
						'</div>' +
						'<div class="form-group row">' +
							'<label for="remark" class="col-sm-2 col-form-label text-right">功能描述</label>' +
							'<div class="col-sm-9">' +
								'<textarea rows="5" class="form-control" id="remark" name="remark"></textarea>' +
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
		var $sqlCont = $('#sqlCont');
		var editor = CodeMirror($sqlCont[0], {
			value: '',
			mode: 'sql',
			lineWrapping: false,
			theme: 'panda-syntax',
			placeholder: '请输入SQL语句',
			readOnly: false
		});
		$sqlCont.data('editor', editor);
		editor.setSize('100%', '280px');
		editor.on('blur', function() {
			$('#contentHidden').val(editor.getValue());
			setTimeout(function() {
				var validator = $('#templateForm').validate();
				validator.element('#contentHidden');
			});
		});
		var validator = $('#templateForm').validate({
			ignore: '.ignore',//元素忽略
			rules: {
				name: 'required',
				groupId: 'required',
				type: 'required',
				content: 'required'
			},
			messages: {
				name: '名称不能为空',
				groupId: '模板分组不能为空',
				type: 'SQL类型不能为空',
				content: 'SQL不能为空'
			},
			highlight: function(element, errorClass, validClass) {
				var $element = $(element),
					$treeCont = $element.siblings('.template-group-tree-cont');
				if ($treeCont.length) {
					$treeCont.addClass('is-invalid').removeClass('is-valid');
				}
				$element.addClass('is-invalid').removeClass('is-valid');
			},
			unhighlight: function(element, errorClass, validClass) {
				var $element = $(element),
					$treeCont = $element.siblings('.template-group-tree-cont');
				if ($treeCont.length) {
					$treeCont.addClass('is-valid').removeClass('is-invalid');
				}
				$element.addClass('is-valid').removeClass('is-invalid');
			}
		});
		$('#maxCoderBtn').click(function() {
			var editor = $('#sqlCont').data('editor'),
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
		$('#cancelBtn').click(function() {
			layer.close(winIndex);
		});
		$('#saveBtn').click(function() {
			var flag = validator.form();
			if (!flag) {
				return;
			}
			var reqUrl = '',
				operZh = '';
			if (modifyType == 'update') {
				reqUrl = CONTEXT_PATH + '/template/update';
				operZh = '修改';
			} else if (modifyType == 'add') {
				reqUrl = CONTEXT_PATH + '/template/add';
				operZh = '新增';
			} else {
				layer.alert('不支持的修改类型', {icon: 2});
				return;
			}
			$.ajax({
				type: 'POST',
				url: reqUrl,
				contentType: 'application/x-www-form-urlencoded',
				data: $('#templateForm').serializeArray(),
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
			type: 'GET',
			url: CONTEXT_PATH + '/template/group/list',
			async: false,
			data: null,
			dataType: 'json',
			success: function(data, status, xhr) {
				if (data.success) {
					var treeData = data.data || [];
					var $ztree = $("#templateGroupTree4Modify");
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
								var treeObj = $.fn.zTree.getZTreeObj('templateGroupTree4Modify');
								if (!treeObj) {
									return;
								}
								var arr = treeObj.getSelectedNodes();
								$("#templateGroupId").val(arr.length ? arr[0].id : null);
								var validator = $('#templateForm').validate();
								validator.element('#templateGroupId');
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
		//设置数据
		setTemplateForm(templateData);
	}

	function setTemplateForm(templateData) {
		templateData = templateData || {};
		$("#templateId").val(templateData.id);
		$("#name").val(templateData.name);
		$("#templateGroupId").val(templateData.groupId);
		var treeObj = $.fn.zTree.getZTreeObj('templateGroupTree4Modify');
		if (!treeObj) {
			return;
		}
		if (templateData.groupId) {
			var groupNode = treeObj.getNodeByParam('id', templateData.groupId);
			if (groupNode) {
				treeObj.expandNode(groupNode.getParentNode(), true);
				treeObj.selectNode(groupNode);
			}
		}
		setSelectValue($("#sqlType"), templateData.srcDsId);
		var editor = $('#sqlCont').data('editor');
		editor.setValue(templateData.content || '');
		setTimeout(function() {
			editor.refresh();
		}, 500);
		$('#contentHidden').val(templateData.content || '');
		$("#remark").val(templateData.remark);
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

	$.fn.zTree.init($("#templateGroupTree"), {
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
				reloadTable();
			}
		}
	}, []);
	reloadGroupTree();

});