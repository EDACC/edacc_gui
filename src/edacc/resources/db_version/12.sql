ALTER TABLE Client ADD (
  `startTimestamp` TIMESTAMP NULL DEFAULT NOW() ,
  `walltime` INT NULL DEFAULT 0
);