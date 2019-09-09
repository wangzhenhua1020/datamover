#!/bin/bash

# export JAVA_HOME=/opt/java/jdk1.8.0_72
# export CLASSPAHT=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
# export PATH=${JAVA_HOME}/bin:${PATH}

# 进入脚本所在目录
TEMPPATH="$0"
while [ -h "$TEMPPATH"  ]; do
	MAINPATH="$( cd -P "$( dirname "$TEMPPATH"  )" && pwd  )"
	TEMPPATH="$(readlink "$TEMPPATH")"
	[[ $TEMPPATH != /*  ]] && TEMPPATH="$MAINPATH/$TEMPPATH"
done
MAINPATH="$( cd -P "$( dirname "$TEMPPATH"  )" && pwd  )"

# 清空nohup日志
# rm -f $MAINPATH/logs/*.log
find $MAINPATH/logs -name "*.log" -mtime +1 | xargs rm -f

# spring.datasource.primary 为程序数据存储库
# spring.datasource.initialization-mode 是否初始化数据库：always: 始终执行初始化；embedded: 只初始化内存数据库（默认值）；never: 不执行初始化
# datamover.thread.scheduler.pool-size 同时执行的任务线程个数
nohup java -Xms4096m -Xmx8192m -Xmn1024m -server -XX:+HeapDumpOnOutOfMemoryError \
	-jar "$MAINPATH/datamover-0.0.1-SNAPSHOT.jar" \
	--server.port=8087 \
	--server.servlet.context-path="/datamover" \
	--spring.datasource.primary.url="jdbc:mysql://127.0.0.1:3306/datamover?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull&useSSL=false&autoReconnect=true" \
	--spring.datasource.primary.username=root \
	--spring.datasource.primary.password=root \
	--spring.datasource.primary.driver-class-name=com.mysql.jdbc.Driver \
	--spring.datasource.primary.initialization-mode=never \
	--spring.datasource.primary.initial-size=5 \
	--spring.datasource.primary.min-idle=1 \
	--spring.datasource.primary.max-active=50 \
	--spring.datasource.primary.max-wait=5000 \
	--spring.datasource.primary.time-between-eviction-runs-millis=60000 \
	--spring.datasource.primary.min-evictable-idle-time-millis=300000 \
	--spring.datasource.primary.validation-query=SELECT 1 FROM DUAL \
	--spring.datasource.primary.test-while-idle=true \
	--spring.datasource.primary.test-on-borrow=false \
	--spring.datasource.primary.test-on-return=false \
	--spring.datasource.primary.break-after-acquire-failure=true \
	--spring.datasource.common.max-wait=60000 \
	--datamover.dest.group-count.delete=999 \
	--datamover.dest.group-count.insert=500 \
	--datamover.thread.scheduler.pool-size=100 \
	> /dev/null 2>&1 &
