#!/bin/sh
exec java ${JAVA_OPTS} \
  -cp /app:/app/WEB-INF/classes:/app/WEB-INF/lib/* \
  org.crp.flowable.aiprediction.AipredictionApplication \
  ${@}
