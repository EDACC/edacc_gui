DROP TABLE IF EXISTS `Verifier` ;

CREATE  TABLE IF NOT EXISTS `Verifier` (
  `idVerifier` INT NOT NULL ,
  `name` VARCHAR(255) NOT NULL ,
  `binaryArchive` LONGBLOB NOT NULL ,
  `description` TEXT NOT NULL ,
  `md5` VARCHAR(255) NOT NULL ,
  `runCommand` VARCHAR(255) NULL ,
  `runPath` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`idVerifier`) )
ENGINE = InnoDB;

DROP TABLE IF EXISTS `VerifierConfig` ;

CREATE  TABLE IF NOT EXISTS `VerifierConfig` (
  `idVerifierConfig` INT NOT NULL ,
  `Verifier_idVerifier` INT NOT NULL ,
  PRIMARY KEY (`idVerifierConfig`, `Verifier_idVerifier`) ,
  INDEX `fk_VerifierConfig_Verifier1` (`Verifier_idVerifier` ASC) ,
  CONSTRAINT `fk_VerifierConfig_Verifier1`
    FOREIGN KEY (`Verifier_idVerifier` )
    REFERENCES `Verifier` (`idVerifier` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

DROP TABLE IF EXISTS `VerifierParameter` ;

CREATE  TABLE IF NOT EXISTS `VerifierParameter` (
  `idVerifierParameter` INT NOT NULL ,
  `Verifier_idVerifier` INT NOT NULL ,
  `name` VARCHAR(255) NOT NULL ,
  `prefix` VARCHAR(255) NULL ,
  `hasValue` TINYINT(1) NOT NULL ,
  `defaultValue` VARCHAR(4096) NULL ,
  `order` INT NOT NULL ,
  `mandatory` TINYINT(1) NULL ,
  `space` TINYINT(1) NULL ,
  `attachToPrevious` TINYINT(1) NULL ,
  PRIMARY KEY (`idVerifierParameter`) ,
  INDEX `fk_VerifierParameter_Verifier1` (`Verifier_idVerifier` ASC) ,
  CONSTRAINT `fk_VerifierParameter_Verifier1`
    FOREIGN KEY (`Verifier_idVerifier` )
    REFERENCES `Verifier` (`idVerifier` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

DROP TABLE IF EXISTS `VerifierConfig_has_VerifierParameter` ;

CREATE  TABLE IF NOT EXISTS `VerifierConfig_has_VerifierParameter` (
  `VerifierConfig_idVerifierConfig` INT NOT NULL ,
  `VerifierParameter_idVerifierParameter` INT NOT NULL ,
  `value` VARCHAR(4096) NULL ,
  PRIMARY KEY (`VerifierConfig_idVerifierConfig`, `VerifierParameter_idVerifierParameter`) ,
  INDEX `fk_VerifierConfig_has_VerifierParameter_VerifierParameter1` (`VerifierParameter_idVerifierParameter` ASC) ,
  INDEX `fk_VerifierConfig_has_VerifierParameter_VerifierConfig1` (`VerifierConfig_idVerifierConfig` ASC) ,
  CONSTRAINT `fk_VerifierConfig_has_VerifierParameter_VerifierConfig1`
    FOREIGN KEY (`VerifierConfig_idVerifierConfig` )
    REFERENCES `VerifierConfig` (`idVerifierConfig` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_VerifierConfig_has_VerifierParameter_VerifierParameter1`
    FOREIGN KEY (`VerifierParameter_idVerifierParameter` )
    REFERENCES `VerifierParameter` (`idVerifierParameter` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

ALTER TABLE `Experiment` ADD COLUMN `VerifierConfig_idVerifierConfig` INT(11) NULL DEFAULT NULL  AFTER `verifierOutputPreserveLast` , 
  ADD CONSTRAINT `fk_Experiment_VerifierConfig1`
  FOREIGN KEY (`VerifierConfig_idVerifierConfig` )
  REFERENCES `VerifierConfig` (`idVerifierConfig` )
  ON DELETE CASCADE
  ON UPDATE CASCADE
, ADD INDEX `fk_Experiment_VerifierConfig1` (`VerifierConfig_idVerifierConfig` ASC) ;