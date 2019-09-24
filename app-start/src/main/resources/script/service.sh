#!/usr/bin/env bash

APP_NAME=app

PORT=8080

BASE_PATH=$(cd `dirname $0`; pwd)

PIDFILE=${BASE_PATH}/${APP_NAME}.pid

LOG_PATH=/home/admin/logs/${APP_NAME}

SERVICE_FILE=${BASE_PATH}/${APP_NAME}.jar

ENABLE_TLS="-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"

JVM_OPTIONS="-XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:${LOG_PATH}/gc.logger -Xms500m -Xmx1G -XX:+UseG1GC -XX:SurvivorRatio=6 -XX:MaxGCPauseMillis=400 -XX:G1ReservePercent=15 -XX:InitiatingHeapOccupancyPercent=40 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_PATH}/java.hprof"

start() {

  if [ -f ${PIDFILE} ] && kill -0 $(cat ${PIDFILE}); then
    echo "${APP_NAME} already running."
    exit 0
  fi

  echo "Starting ${APP_NAME}…"

  cd ${BASE_PATH}

  nohup java ${JVM_OPTIONS} -jar ${ENABLE_TLS} -Dlogging.path=${LOG_PATH} -Dserver.port=${PORT} ${SERVICE_FILE} > /dev/null 2>&1 &

  echo $! > "${PIDFILE}"

  echo "${APP_NAME} started."
}

stop() {

  if [ ! -f ${PIDFILE} ] || ! kill -0 $(cat ${PIDFILE}); then
    echo "${APP_NAME} not running."
    exit 0
  fi

  echo "Stopping ${APP_NAME}…"

  kill -9 $(cat ${PIDFILE}) && rm -f ${PIDFILE}

  echo "${APP_NAME} stopped."
}

status() {

  echo "Checking ${APP_NAME}..."

  if [ -f ${PIDFILE} ]; then

    PID=$(cat ${PIDFILE})

    if [ -z "$(ps axf | grep ${PID} | grep -v grep)" ]; then

        echo "The process appears to be dead but pidfile still exists."

        exit 0

    else

        RESPONSE=`curl -XGET http://localhost:${PORT}/health?pretty`

        if [ $? == 0 ]; then

          echo "${APP_NAME} is Running, the PID is ${PID}."

          echo -e "${RESPONSE}"

        else

          echo "${APP_NAME} not running.The process still exists, but api not access."

          exit 0

        fi

    fi
  else

    echo "${APP_NAME} not running."

    exit 0

  fi
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    start
    ;;
  status)
    status
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status}"
esac
