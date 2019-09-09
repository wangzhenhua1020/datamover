#!/bin/bash

# 进入脚本所在目录
TEMPPATH="$0"
while [ -h "$TEMPPATH"  ]; do
	MAINPATH="$( cd -P "$( dirname "$TEMPPATH"  )" && pwd  )"
	TEMPPATH="$(readlink "$TEMPPATH")"
	[[ $TEMPPATH != /*  ]] && TEMPPATH="$MAINPATH/$TEMPPATH"
done
MAINPATH="$( cd -P "$( dirname "$TEMPPATH"  )" && pwd  )"


ps -ef | grep "$MAINPATH/datamover" | grep -v grep | awk '{print $2}' | xargs kill > /dev/null 2>&1

