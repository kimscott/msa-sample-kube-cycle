FROM webratio/nodejs-http-server
VOLUME /tmp
ARG VUE_APP_API_HOST
ENV VUE_APP_API_HOST=$VUE_APP_API_HOST
ADD dist /opt/www
ADD run.sh /opt/run.sh
EXPOSE 8080
ENTRYPOINT ["sh","/opt/rnun.sh"]