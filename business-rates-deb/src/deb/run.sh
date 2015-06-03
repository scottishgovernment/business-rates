#!/bin/sh
. /etc/profile
/usr/bin/java -jar /beta/business-rates-rest-service/*.jar >> /var/log/business-rates-rest-service.log 2>&1
