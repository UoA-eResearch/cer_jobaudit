#!/usr/bin/env bash

# Run this script via cron every first day of the month shortly after midnight
# e.g. 15 0 1 * * [PATH]/job_audit_cache_db_monthly.sh

mysql -u root -p[MYSQL_PASSWORD] pandora_audit < [PATH]/job_audit_cache_db_monthly.sql
