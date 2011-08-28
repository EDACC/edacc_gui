ALTER TABLE ExperimentResults ADD COLUMN `cost` FLOAT;
ALTER TABLE SolverConfig CHANGE COLUMN `name` `name` VARCHAR(4096);
ALTER TABLE Client CHANGE COLUMN `message` `message` VARCHAR(4096);