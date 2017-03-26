#!/bin/bash

if [ ! -e /vss-kanban/.db-setup ]; then
    echo "Start DB migrate"

    for file in `\find ./vss-kanban-${KANBAN_VERSION}/src/main/resources/db/migration -name "*.sql"`; do
      psql -f ${file} -U ${DATABASE_USER} -d ${DATABASE_DBNAME} -h ${DATABASE_HOST}
    done

    touch /vss-kanban/.db-setup
fi

java -jar /vss-kanban/kanban.jar
