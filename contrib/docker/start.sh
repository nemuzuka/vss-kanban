#!/bin/bash

if [ ! -e /vss-kanban/.db-setup ]; then

    until psql -h ${DATABASE_HOST}  -d ${DATABASE_DBNAME} -U ${DATABASE_USER} -c '\l'; do
      >&2 echo "Postgres is unavailable - sleeping"
      sleep 1
    done

    echo "Start DB migrate"
    for file in `\find ./vss-kanban-${KANBAN_VERSION}/src/main/resources/db/migration -name "*.sql"`; do
      echo "Exec SQL: ${file}"
      psql -f ${file} -U ${DATABASE_USER} -d ${DATABASE_DBNAME} -h ${DATABASE_HOST}
    done

    touch /vss-kanban/.db-setup
    echo "DB migrate done!"
fi

java -jar /vss-kanban/kanban.jar
