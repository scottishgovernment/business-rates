#!/bin/sh
. /etc/profile
/usr/bin/java -Dlogging.config=/opt/business-rates/logback.xml -jar /opt/business-rates/*.jar >> /var/log/business-rates/business-rates.log 2>&1
