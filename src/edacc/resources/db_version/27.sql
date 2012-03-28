-- -----------------------------------------------------
-- Table `Cost`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Cost` ;
CREATE  TABLE IF NOT EXISTS `Cost` (
  `idCost` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`idCost`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `CostBinary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CostBinary` ;
CREATE  TABLE IF NOT EXISTS `CostBinary` (
  `idCostBinary` INT NOT NULL AUTO_INCREMENT ,
  `Solver_idSolver` INT NOT NULL ,
  `Cost_idCost` INT NOT NULL ,
  `binaryName` VARCHAR(255) NOT NULL ,
  `binaryArchive` LONGBLOB NOT NULL ,
  `md5` VARCHAR(255) NOT NULL ,
  `version` VARCHAR(255) NOT NULL ,
  `runCommand` VARCHAR(255) NOT NULL ,
  `runPath` VARCHAR(255) NOT NULL ,
  `parameters` VARCHAR(4096) NOT NULL ,
  PRIMARY KEY (`idCostBinary`) ,
  INDEX `fk_CostBinary_Solver1` (`Solver_idSolver` ASC) ,
  INDEX `fk_CostBinary_Cost1` (`Cost_idCost` ASC) ,
  CONSTRAINT `fk_CostBinary_Solver1`
    FOREIGN KEY (`Solver_idSolver` )
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_CostBinary_Cost1`
    FOREIGN KEY (`Cost_idCost` )
    REFERENCES `Cost` (`idCost` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;