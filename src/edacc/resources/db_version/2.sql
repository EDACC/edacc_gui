-- -----------------------------------------------------
-- Table `ConfigurationScenario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ConfigurationScenario` ;

CREATE  TABLE IF NOT EXISTS `ConfigurationScenario` (
  `idConfigurationScenario` INT NOT NULL AUTO_INCREMENT ,
  `SolverBinaries_idSolverBinary` INT NOT NULL ,
  `Experiment_idExperiment` INT NOT NULL ,
  PRIMARY KEY (`idConfigurationScenario`) ,
  INDEX `fk_ConfigurationScenario_SolverBinaries1` (`SolverBinaries_idSolverBinary` ASC) ,
  INDEX `fk_ConfigurationScenario_Experiment1` (`Experiment_idExperiment` ASC) ,
  CONSTRAINT `fk_ConfigurationScenario_SolverBinaries1`
    FOREIGN KEY (`SolverBinaries_idSolverBinary` )
    REFERENCES `SolverBinaries` (`idSolverBinary` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ConfigurationScenario_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ConfigurationScenario_has_Parameters`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ConfigurationScenario_has_Parameters` ;

CREATE  TABLE IF NOT EXISTS `ConfigurationScenario_has_Parameters` (
  `ConfigurationScenario_idConfigurationScenario` INT NOT NULL ,
  `Parameters_idParameter` INT NOT NULL ,
  `configurable` TINYINT(1)  NOT NULL ,
  `fixedValue` VARCHAR(4096) NULL ,
  PRIMARY KEY (`ConfigurationScenario_idConfigurationScenario`, `Parameters_idParameter`) ,
  INDEX `fk_ConfigurationScenario_has_Parameters_Parameters1` (`Parameters_idParameter` ASC) ,
  INDEX `fk_ConfigurationScenario_has_Parameters_ConfigurationScenario1` (`ConfigurationScenario_idConfigurationScenario` ASC) ,
  CONSTRAINT `fk_ConfigurationScenario_has_Parameters_ConfigurationScenario1`
    FOREIGN KEY (`ConfigurationScenario_idConfigurationScenario` )
    REFERENCES `ConfigurationScenario` (`idConfigurationScenario` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ConfigurationScenario_has_Parameters_Parameters1`
    FOREIGN KEY (`Parameters_idParameter` )
    REFERENCES `Parameters` (`idParameter` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;
