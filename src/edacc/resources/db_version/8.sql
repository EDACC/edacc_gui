-- -----------------------------------------------------
-- Table `Course`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Course` ;

CREATE  TABLE IF NOT EXISTS `Course` (
  `ConfigurationScenario_idConfigurationScenario` INT NOT NULL ,
  `Instances_idInstance` INT NOT NULL ,
  `seed` INT NOT NULL ,
  `order` INT NOT NULL ,
  INDEX `fk_Course_ConfigurationScenario1` (`ConfigurationScenario_idConfigurationScenario` ASC) ,
  INDEX `fk_Course_Instances1` (`Instances_idInstance` ASC) ,
  PRIMARY KEY (`order`, `ConfigurationScenario_idConfigurationScenario`) ,
  CONSTRAINT `fk_Course_ConfigurationScenario1`
    FOREIGN KEY (`ConfigurationScenario_idConfigurationScenario` )
    REFERENCES `ConfigurationScenario` (`idConfigurationScenario` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Course_Instances1`
    FOREIGN KEY (`Instances_idInstance` )
    REFERENCES `Instances` (`idInstance` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

ALTER TABLE `ConfigurationScenario` ADD COLUMN (`initial_course_length` INT NULL);