FROM webratio/nodejs-http-server
VOLUME /tmp
ADD dist /opt/www
ADD run.sh /opt/run.sh
EXPOSE 8080
ENTRYPOINT ["sh","/opt/run.sh"]