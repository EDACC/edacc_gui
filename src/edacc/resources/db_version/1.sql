ALTER TABLE `ExperimentResultsOutput` CHANGE COLUMN `solverOutput` `solverOutput` LONGBLOB;

-- -----------------------------------------------------
-- Table `Version`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Version` ;

CREATE  TABLE IF NOT EXISTS `Version` (
  `version` INT NOT NULL ,
  `date` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`version`) )
ENGINE = InnoDB;

