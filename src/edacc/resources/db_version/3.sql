-- -----------------------------------------------------
-- Table `EDACC`.`ParameterGraph`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ParameterGraph` ;

CREATE  TABLE IF NOT EXISTS `ParameterGraph` (
  `idParameterGraph` INT NOT NULL AUTO_INCREMENT ,
  `Solver_idSolver` INT NOT NULL ,
  `serializedGraph` LONGBLOB NOT NULL ,
  INDEX `fk_ParameterGraph_Solver1` (`Solver_idSolver` ASC) ,
  PRIMARY KEY (`idParameterGraph`) ,
  CONSTRAINT `fk_ParameterGraph_Solver1`
    FOREIGN KEY (`Solver_idSolver` )
    REFERENCES `EDACC`.`Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

ALTER TABLE `Parameters` ADD COLUMN `attachToPrevious` TINYINT(1) NULL;