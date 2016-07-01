--
-- Add records for each user for the last month to the statistics cache tables
-- Note: If this is run multiple times, the records will be added multiple times!
--

INSERT INTO audit_user_prev
SELECT
    user AS user,
    count(*) AS jobs,
    SUM(cores) AS total_cores,
    TRUNCATE(SUM(IF(done>start, cores*(done-start-suspended)/3600,0)),2) AS core_hours,
    TRUNCATE(SUM(IF(start>qtime, (start-qtime)/3600,0)),2) AS waiting_time,
    FROM_UNIXTIME(done, '%m') as month,
    FROM_UNIXTIME(done, '%Y') as year,
    SUM(IF(jobtype='serial',1,0)) AS serial_jobs,
    SUM(IF(jobtype='parallel',1,0)) AS parallel_jobs,
    TRUNCATE(SUM(IF(jobtype='serial' AND done>start, cores*(done-start-suspended), 0))/3600,2) AS serial_core_hours,
    TRUNCATE(SUM(IF(jobtype='parallel' AND done>start, cores*(done-start-suspended), 0))/3600,2) AS parallel_core_hours
  FROM audit
  WHERE done<UNIX_TIMESTAMP(LAST_DAY(NOW() - INTERVAL 1 MONTH) + INTERVAL 1 DAY) AND
        done>=UNIX_TIMESTAMP(LAST_DAY(NOW() - INTERVAL 2 MONTH) + INTERVAL 1 DAY)
  GROUP BY user, year, month;


INSERT INTO audit_project_prev
SELECT
    account AS project,
    user as user,
    COUNT(*) AS jobs,
    SUM(cores) As total_cores,
    SUM(IF(done>start, cores*(done-start-suspended)/3600,0)) AS core_hours,
    SUM(IF(start>qtime,(start-qtime)/3600,0)) AS waiting_time,
    FROM_UNIXTIME(done, '%m') as month,
    FROM_UNIXTIME(done, '%Y') as year,
    SUM(IF(jobtype='serial',1,0)) AS serial_jobs,
    SUM(IF(jobtype='parallel',1,0)) AS parallel_jobs,
    TRUNCATE(SUM(IF(jobtype='serial' AND done>start, cores*(done-start-suspended), 0))/3600,2) AS serial_core_hours,
    TRUNCATE(SUM(IF(jobtype='parallel' AND done>start, cores*(done-start-suspended), 0))/3600,2) AS parallel_core_hours
  FROM audit
  WHERE done<UNIX_TIMESTAMP(LAST_DAY(NOW() - INTERVAL 1 MONTH) + INTERVAL 1 DAY) AND
        done>=UNIX_TIMESTAMP(LAST_DAY(NOW() - INTERVAL 2 MONTH) + INTERVAL 1 DAY) AND
    ( account LIKE 'nesi%' OR
      account LIKE 'uoa%' OR
      account LIKE 'uoo%' OR
      account LIKE 'massey%' OR
      account LIKE 'landcare%'
    )
  GROUP BY project, user, year, month;


