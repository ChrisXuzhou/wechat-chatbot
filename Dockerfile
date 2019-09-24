FROM harbor.rokid-inc.com/base_image/java:8
RUN mkdir -p /home/admin/logs/portal-control
COPY target/*.jar /home/admin/portal-control/app.jar
COPY healthclient /home/admin/
RUN chmod 755 /home/admin/healthclient
EXPOSE 8080
ENV TZ=Asia/Shanghai
WORKDIR /home/admin/portal-control
ENTRYPOINT ["java", "-XX:+PrintGCDateStamps", "-XX:+PrintGCDetails", "-Xloggc:/home/admin/logs/app/gc.log", \
"-Xms2048m", "-Xmx2048m", "-XX:MaxPermSize=512m", "-XX:+HeapDumpOnOutOfMemoryError", \
"-XX:HeapDumpPath=/home/admin/logs/app/java.hprof", "-jar", "-Dlogging.path=/home/admin/logs", \
"-Dserver.port=8080", "/home/admin/portal-control/app.jar"]
