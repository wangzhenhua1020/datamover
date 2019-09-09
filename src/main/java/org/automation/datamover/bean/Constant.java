package org.automation.datamover.bean;

import java.io.File;

//常量
public class Constant {

	public static final Integer STATUS_AVAILABLE = 1;//可用
	public static final Integer STATUS_DISABLE = 0;//不可用

	public static final Integer STATUS_TASK_READY = 0;//就绪
	public static final Integer STATUS_TASK_SUCCESS = 1;//成功
	public static final Integer STATUS_TASK_FAIL = 2;//失败
	public static final Integer STATUS_TASK_RUNNING = 3;//运行中
	public static final Integer STATUS_TASK_TIMEOUT = 4;//超时

	public static final Integer SINGLETON_NO = 0;//非单例运行
	public static final Integer SINGLETON_YES = 1;//单例运行

	public static final Integer TASK_TRIGGER_TYPE_MANUAL = 0;//手动触发
	public static final Integer TASK_TRIGGER_TYPE_SCHEDULE = 1;//定时触发

	public static final Integer SYSTEM_CODE_ERROR = 0;//系统默认错误代码

	public static final Integer SQL_TYPE_SELECT = 0;//查询SQL
	public static final Integer SQL_TYPE_UPDATE = 1;//更新SQL

	public static final Integer DEST_TABLE_DELETE_TYPE_OR = 0;//数据删除方式（字段or语句，支持字段为空时IS NULL）
	public static final Integer DEST_TABLE_DELETE_TYPE_IN = 1;//数据删除方式（字段in语句）
	public static final Integer DEST_TABLE_DELETE_TYPE_ALL = 2;//全部删除

	public static final Integer VAR_TYPE_CUSTOM = 0;//自定义变量
	public static final Integer VAR_TYPE_INNER = 1;//内置变量

	public static final Integer POST_ACTION_NONE = 0;//完成后不执行任何动作
	public static final Integer POST_ACTION_SHELL = 1;//完成后执行SHELL脚本

	public static final Integer POST_CONDITION_ALWAYS = 0;//无条件执行
	public static final Integer POST_CONDITION_SUCCESS = 1;//成功后执行
	public static final Integer POST_CONDITION_FAIL = 2;//失败后执行

	public static final String SYSTEM_PATH;//系统路径

	static {
		File file = new File("");
		SYSTEM_PATH = file.getAbsolutePath();
	}

}
