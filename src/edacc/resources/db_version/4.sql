ALTER TABLE `SolverConfig` ADD COLUMN `cost` FLOAT(11) NULL DEFAULT NULL  AFTER `name` , ADD COLUMN `cost_function` VARCHAR(255) NULL DEFAULT NULL  AFTER `cost` , ADD COLUMN `parameter_hash` VARCHAR(255) NULL DEFAULT NULL  AFTER `cost_function` 
, ADD INDEX `parameter_hash_index` (`parameter_hash` ASC) ;
