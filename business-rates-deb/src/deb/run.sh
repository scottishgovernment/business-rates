#!/bin/sh -l
java \
  -Dlogging.config=/opt/business-rates/logback.xml \
  -Djava.security.properties=/opt/business-rates/security.properties \
  -jar /opt/business-rates/*.jar \
  >> /var/log/business-rates/business-rates.log 2>&1
