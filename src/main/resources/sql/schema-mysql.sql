-- 模板分组
DROP TABLE IF EXISTS `data_mover_template_group`;
CREATE TABLE `data_mover_template_group` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL COMMENT '分组名称',
  `parent_id` int(11) COMMENT '父节点ID，根节点为空',
  `path` varchar(255) COMMENT '节点路径',
  `order_index` int(11) COMMENT '同级节点顺序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(2048) COMMENT '分组备注'
);
CREATE INDEX `data_mover_template_group_idx1` on `data_mover_template_group`(`create_time`);
CREATE INDEX `data_mover_template_group_idx2` on `data_mover_template_group`(`update_time`);

-- SQL模板
DROP TABLE IF EXISTS `data_mover_template_sql`;
CREATE TABLE `data_mover_template_sql` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `group_id` int(11) NOT NULL COMMENT '分组ID',
  `type` int(1) NOT NULL COMMENT 'SQL类型: 0 查询SQL; 1 更新SQL',
  `content` text DEFAULT NULL COMMENT 'SQL',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(2048) COMMENT '模板备注'
);
CREATE INDEX `data_mover_template_sql_idx1` on `data_mover_template_sql`(`create_time`);
CREATE INDEX `data_mover_template_sql_idx2` on `data_mover_template_sql`(`update_time`);

-- 数据源配置
DROP TABLE IF EXISTS `data_mover_datasource`;
CREATE TABLE `data_mover_datasource` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `db_type` varchar(50) NOT NULL COMMENT '数据库类型',
  `jdbc_url` varchar(1024) NOT NULL COMMENT 'JDBC连接字符串',
  `jdbc_driver` varchar(255) NOT NULL COMMENT 'JDBC驱动',
  `jdbc_username` varchar(50) NOT NULL COMMENT 'JDBC用户名',
  `jdbc_password` varchar(50) NOT NULL COMMENT 'JDBC密码',
  `test_sql` varchar(1024) DEFAULT NULL COMMENT '测试SQL',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '配置状态：1 可用; 0 不可用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(2048) COMMENT '数据源说明'
);
CREATE INDEX `data_mover_datasource_idx1` on `data_mover_datasource`(`create_time`);
CREATE INDEX `data_mover_datasource_idx2` on `data_mover_datasource`(`update_time`);

-- 数据迁移配置
DROP TABLE IF EXISTS `data_mover_config`;
CREATE TABLE `data_mover_config` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `src_ds_id` int(11) NOT NULL COMMENT '数据源ID',
  `src_sql_type` int(1) NOT NULL DEFAULT 0 COMMENT 'SQL类型: 0 查询SQL; 1 更新SQL',
  `src_sql` text NOT NULL COMMENT '数据查询SQL',
  `dest_ds_id` int(11) COMMENT '目标数据源ID',
  `dest_table` varchar(255) COMMENT '目标table',
  `dest_table_delete_type` int(1) COMMENT '目标表清空数据方式：0 or(值为NULL时采用IS NULL方式); 1 in; 2 全部删除',
  `primary_key_list_json` varchar(1024) COMMENT '主键数组JSON',
  `timeout` int(11) COMMENT '超时时间（秒）',
  `singleton` int(1) NOT NULL DEFAULT 1 COMMENT '配置状态：1 单例运行; 0 非单例运行',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '配置状态：1 可用; 0 不可用',
  `post_action` int(1) NOT NULL DEFAULT 0 COMMENT '后续动作：0 无; 1 执行shell脚本',
  `post_condition` int(1) NOT NULL DEFAULT 0 COMMENT '后续动作执行条件：0 无条件执行; 1 成功后执行; 2 失败后执行',
  `post_data` text COMMENT '后续动作详细数据',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(2048) COMMENT '接口说明'
);
CREATE INDEX `data_mover_config_idx1` on `data_mover_config`(`create_time`);
CREATE INDEX `data_mover_config_idx2` on `data_mover_config`(`update_time`);

-- 数据迁移配置变量
DROP TABLE IF EXISTS `data_mover_config_var`;
CREATE TABLE `data_mover_config_var` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL COMMENT '变量名称',
  `config_id` int(11) NOT NULL COMMENT '配置ID',
  `type` int(1) NOT NULL COMMENT '变量类型: 0 自定义变量; 1 内置变量',
  `value` varchar(255) DEFAULT NULL COMMENT '变量值',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(2048) COMMENT '变量备注'
);
CREATE INDEX `data_mover_config_var_idx1` on `data_mover_config_var`(`update_time`);
CREATE INDEX `data_mover_config_var_idx2` on `data_mover_config_var`(`config_id`);

-- 数据迁移配置运行实例
DROP TABLE IF EXISTS `data_mover_inst`;
CREATE TABLE `data_mover_inst` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `config_id` int(11) NOT NULL COMMENT '配置ID',
  `trigger_type` int(1) NOT NULL COMMENT '触发方式：0 手动触发; 1 定时触发',
  `status` int(1) NOT NULL DEFAULT -1 COMMENT '本次更新状态：0 无; 1 成功; 2 失败; 3 进行中; 4 超时; 其他值 未知',
  `message` text COMMENT '更新日志',
  `data` text COMMENT '程序运行数据',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);
CREATE INDEX `data_mover_inst_idx1` on `data_mover_inst`(`create_time`);
CREATE INDEX `data_mover_inst_idx2` on `data_mover_inst`(`config_id`);

-- 数据迁移配置调度配置
DROP TABLE IF EXISTS `data_mover_schedule`;
CREATE TABLE `data_mover_schedule` (
  `id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `config_id` int(11) NOT NULL COMMENT '配置ID',
  `expr` varchar(255) NOT NULL COMMENT '调度表达式',
  `status` int(1) NOT NULL DEFAULT 1 COMMENT '配置状态：1 可用; 0 不可用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(2048) COMMENT '调度说明'
);
CREATE INDEX `data_mover_schedule_idx1` on `data_mover_schedule`(`create_time`);
CREATE INDEX `data_mover_schedule_idx2` on `data_mover_schedule`(`update_time`);
CREATE INDEX `data_mover_schedule_idx3` on `data_mover_schedule`(`config_id`);