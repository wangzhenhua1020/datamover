package org.automation.datamover.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 变量工具
 * 
 * @author 王振华
 * 
 */
public class SqlVarUtil {

	private SqlVarUtil() {}

	/**
	 * 变量查找正则表达式
	 *   格式：${变量名}
	 *   变量名有字母、数字、下划线组成，区分大小写
	 */
	public static final Pattern VAR_NAME_FINDER_PATTERN = Pattern.compile("(?:\\$\\{[ \t]*([0-9a-zA-Z\\_]+)[ \t]*\\})");

	/**
	 * @see String replaceNormalVars(String content, Map<String, Object> vars, boolean valueTrim)
	 */
	public static String replaceVars(String content, Map<String, Object> vars) {
		return replaceVars(content, vars, true);
	}

	/**
	 * 将文本中出现的变量进行替换
	 *   说明：变量区分类型，请将所有可用变量合并成一个Map(在一个任务中变量名一般唯一)，再调用该方法，建议仅调用一次
	 * 
	 * @param content 含有变量的字符串
	 * @param vars 变量集合 key：变量名 value：变量值
	 * @param valueTrim 是否去掉变量值前后的空白字符
	 * @return
	 * @throws Exception
	 */
	public static String replaceVars(String content, Map<String, Object> vars, boolean valueTrim) {
		Matcher varNameMatcher = VAR_NAME_FINDER_PATTERN.matcher(content);
		StringBuilder sb = new StringBuilder();
		int lastIndex = 0;
		while (varNameMatcher.find()) {
			String varName = varNameMatcher.group(1);
			if (vars.containsKey(varName)) {
				Object varValue = vars.get(varName);
				varValue = varValue == null ? "" : varValue;
				if (valueTrim) {
					varValue = varValue.toString().trim();
				}
				sb.append(content.substring(lastIndex, varNameMatcher.start())).append(varValue);
				lastIndex = varNameMatcher.end();
			}
		}
		sb.append(content.substring(lastIndex));
		return sb.toString();
	}

	public static List<String> getVarNames(String content) {
		List<String> list = new ArrayList<>();
		Matcher varNameMatcher = VAR_NAME_FINDER_PATTERN.matcher(content);
		while (varNameMatcher.find()) {
			String varName = varNameMatcher.group(1);
			list.add(varName);
		}
		return list;
	}

}
