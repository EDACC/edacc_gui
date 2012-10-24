-- adapt updated db versions to edacc.sql script
ALTER TABLE SolverConfig CHANGE COLUMN `name` `name` VARCHAR(4096) NOT NULL DEFAULT ""; -- was DEFAULT NULL