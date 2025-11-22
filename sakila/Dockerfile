FROM mysql:8.0

COPY 01.schema.sql /docker-entrypoint-initdb.d/
COPY 02.data.sql /docker-entrypoint-initdb.d/

ENV MYSQL_ROOT_PASSWORD=q1w2e3r4!
ENV MYSQL_DATABASE=sakila