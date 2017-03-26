FROM openjdk:8-jre-alpine

# ENV
ENV TZ="JST-9" \
    LANG="ja_JP.UTF-8" \
    KANBAN_VERSION="0.9.0"

# Add Packages
RUN set -x \
    && apk add --no-cache postgresql-client curl bash

# Workdir
WORKDIR /vss-kanban

# Download JAR
RUN curl -OL https://github.com/nemuzuka/vss-kanban/releases/download/v${KANBAN_VERSION}/vss-kanban-standalone-assembly-${KANBAN_VERSION}-SNAPSHOT.jar && \
    mv vss-kanban-standalone-assembly-${KANBAN_VERSION}-SNAPSHOT.jar kanban.jar

# Download tar (for DB setup)
RUN curl -OL https://github.com/nemuzuka/vss-kanban/archive/v${KANBAN_VERSION}.tar.gz && \
    tar -xf v${KANBAN_VERSION}.tar.gz

ADD start.sh /vss-kanban/start.sh
CMD /vss-kanban/start.sh
