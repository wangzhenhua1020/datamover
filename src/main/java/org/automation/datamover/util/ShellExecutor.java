package org.automation.datamover.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.automation.datamover.bean.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 本地Shell执行
 */
public class ShellExecutor implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

	private static String topFileName = ".localshell";

	private static String charset = "UTF-8";

	private String shellId;

	private String cmdDetail;

	private Map<String, String> envs = new HashMap<>();

	private Callback callback;

	public ShellExecutor(String shellId, String cmdDetail, Map<String, String> envs, Callback callback) {
		Assert.isTrue(shellId != null && !shellId.trim().isEmpty(), "任务ID不能为空");
		Assert.isTrue(cmdDetail != null && !cmdDetail.trim().isEmpty(), "脚本内容不能为空");
		this.shellId = shellId.trim();
		this.cmdDetail = cmdDetail;
		if (envs != null) {
			this.envs.putAll(envs);
		}
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			ShellResult result = exec(ready());
			if (callback != null) {
				if (result.success) {
					callback.onSuccess(result.rightOutput);
				} else {
					callback.onError(result.rightOutput, result.errorOutput);
				}
			}
		} catch (Exception e) {
			logger.error("执行本地脚本错误：" + e.getMessage(), e);
			if (callback != null) {
				callback.onException(e);
			}
		}
	}

	private String[] getEnvs(Map<String, String> subenvs) {
		this.envs.putAll(subenvs);
		String[] result = new String[this.envs.size()];
		int index = 0;
		for(Map.Entry<String, String> entry: this.envs.entrySet()) {
			result[index++] = entry.getKey() + "=" + (entry.getValue() != null ? entry.getValue() : "");
		}
		return result;
	}

	/**
	 * 创建可执行的Shell文件
	 * @throws IOException 
	 */
	private File ready() throws IOException {
		//创建父级目录
		File topParent = new File(topFileName);
		if (!createDirectory(topParent)) {
			throw new IOException("无法创建目录：" + topParent.getAbsolutePath());
		}
		String dateStr = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
		File dateParent = new File(topParent, dateStr);
		if (!createDirectory(dateParent)) {
			throw new IOException("无法创建目录：" + dateParent.getAbsolutePath());
		}
		File shellParent = new File(dateParent, shellId);
		if (!createDirectory(shellParent)) {
			throw new IOException("无法创建目录：" + shellParent.getAbsolutePath());
		}
		//创建文件
		File shellFile = new File(shellParent, shellId + ".sh");
		if (!createFile(shellFile)) {
			throw new IOException("无法创建shell文件：" + shellFile.getAbsolutePath());
		}
		//赋可写权限
		shellFile.setWritable(true, true);
		FileWriter writer = null;
		try {
			writer = new FileWriter(shellFile);
			writer.write(cmdDetail);
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		//赋执行权限
		shellFile.setExecutable(true);
		return shellFile;
	}

	private ShellResult exec(File file) throws IOException {
		BufferedReader outputReader = null;
		BufferedReader errorReader = null;
		Process process = null;
		StringBuilder outputSb = new StringBuilder();
		StringBuilder errorSb = new StringBuilder();
		int exitValue = 0;
		try {
			process = Runtime.getRuntime().exec(file.getAbsolutePath(), getEnvs(getPathEnvs(file)));
			String line = null;
			outputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
			while ((line = outputReader.readLine()) != null) {
				if (outputSb.length() > 0) {
					outputSb.append("\n");
				}
				outputSb.append(line);
			}
			errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));
			while ((line = errorReader.readLine()) != null) {
				if (errorSb.length() > 0) {
					errorSb.append("\n");
				}
				errorSb.append(line);
			}
		} finally {
			if (outputReader != null) {
				try {
					outputReader.close();
				} catch (IOException e) {
					logger.error("本地SHELL执行结束后，关闭流异常：" + e.getMessage(), e);
				}
			}
			if (errorReader != null) {
				try {
					errorReader.close();
				} catch (IOException e) {
					logger.error("本地SHELL执行结束后，关闭错误流异常：" + e.getMessage(), e);
				}
			}
			if (process != null) {
				try {
					exitValue = process.waitFor();
				} catch (Exception e) {
					logger.error("process获取本地shell返回值(exitcode)异常: " + e.getMessage(), e);
				}
				process.destroy();
			}
		}

		ShellResult result = new ShellResult();
		boolean hasError = false;//是否有错误
		int errorSbLength = errorSb.toString().trim().length();//错误流trim后的length
		if (exitValue == 0) {//退出码正常
			hasError = false;
			if (errorSbLength != 0) {//错误流存在输出
				logger.warn("脚本退出码为0（正常），但错误流中存在错误数据：" + errorSb.toString() + "，系统认为该脚本执行失败");
				hasError = true;
			}
		} else {//退出码异常
			hasError = true;
			if (errorSbLength != 0) {//错误流存在输出
				errorSb.append("\n");
			}
			errorSb.append("脚本退出码非0：" + exitValue);
		}
		result.success = !hasError;
		if (hasError) {
			result.errorOutput = errorSb.toString();
			result.rightOutput = outputSb.toString();
		} else {
			result.errorOutput = null;
			result.rightOutput = outputSb.toString();
		}

		FileWriter writer = null;
		try {
			File logFile = new File(file.getParentFile(), shellId + ".out");
			if (!createFile(logFile)) {
				throw new IOException("无法创建out文件：" + logFile.getAbsolutePath());
			}
			writer = new FileWriter(logFile);
			if (hasError) {
				writer.write("################################exitValue################################\n" + exitValue + "\n\n\n");
				writer.write("################################错误流输出################################\n" + result.errorOutput + "\n\n\n");
				writer.write("################################正确流输出################################\n" + result.rightOutput);
			} else {
				writer.write(result.rightOutput);
			}
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return result;
	}

	/**
	 * 新建文件夹
	 */
	private static boolean createDirectory(File dir) {
		if (dir == null) {
			return false;
		}
		if (!dir.exists()) {//文件不存在
			if (dir.mkdirs()) {
				logger.info("目录创建成功：" + dir.getAbsolutePath());
				return true;
			} else {
				logger.error("目录创建失败：" + dir.getAbsolutePath());
			}
		} else {
			if (dir.isDirectory()) {
				logger.info("目录已存在，无需创建：" + dir.getAbsolutePath());
				return true;
			} else {
				logger.error("目录名称已被其他类型占用：" + dir.getAbsolutePath());
			}
		}
		return false;
	}

	/**
	 * 新建文件
	 * @throws IOException 
	 */
	private static boolean createFile(File file) throws IOException {
		if (file == null) {
			return false;
		}
		if (!file.exists()) {//文件不存在
			if (file.createNewFile()) {
				logger.info("文件创建成功：" + file.getAbsolutePath());
				return true;
			} else {
				logger.error("文件创建失败：" + file.getAbsolutePath());
			}
		} else {
			if (file.isFile()) {
				logger.info("文件已存在，无需创建：" + file.getAbsolutePath());
				return true;
			} else {
				logger.error("文件名称已被其他类型占用：" + file.getAbsolutePath());
			}
		}
		return false;
	}

	/**
	 * 获取系统路径相关的环境变量
	 */
	private static Map<String, String> getPathEnvs(File file) {
		Map<String, String> envs = new HashMap<>();
		envs.put("WORK_DIR", file.getParentFile().getAbsolutePath());
		envs.put("SYSTEM_DIR", Constant.SYSTEM_PATH);
		return envs;
	}

	private static class ShellResult {
		boolean success;
		String errorOutput;
		String rightOutput;
	}

	/**
	 * 回调
	 */
	public static interface Callback {

		void onSuccess(String output);

		void onError(String output, String errorMessage);

		void onException(Exception e);

	}

}
