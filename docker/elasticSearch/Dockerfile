FROM docker.elastic.co/elasticsearch/elasticsearch:8.9.2
COPY elasticsearch.yml /usr/share/elasticsearch/config/
USER root
RUN chmod -R 777 /usr/share/elasticsearch/config
