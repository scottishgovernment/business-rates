#!/bin/sh
. /etc/profile
/usr/bin/java -jar /opt/business-rates/*.jar >> /var/log/business-rates/business-rates.log 2>&1
