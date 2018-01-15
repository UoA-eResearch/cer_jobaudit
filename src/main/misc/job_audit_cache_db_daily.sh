#!/usr/bin/env bash

# Run this script through cron every night shortly after midnight
# e.g. 5 0 * * * [PATH]/job_audit_cache_db_daily.sh

mysql -u root -p[MYSQL_PASSWORD] pandora_audit < [PATH]/job_audit_cache_db_daily.sql
