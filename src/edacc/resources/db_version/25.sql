ALTER TABLE `Experiment` DROP FOREIGN KEY `fk_Experiment_VerifierConfig1`;
ALTER TABLE `Experiment` DROP COLUMN `VerifierConfig_idVerifierConfig`; 
DELETE FROM `VerifierConfig`;
ALTER TABLE `VerifierConfig` ADD COLUMN `Experiment_idExperiment` INT(11) NOT NULL  AFTER `Verifier_idVerifier` , 
  ADD CONSTRAINT `fk_VerifierConfig_Experiment1`
  FOREIGN KEY (`Experiment_idExperiment` )
  REFERENCES `Experiment` (`idExperiment` )
  ON DELETE CASCADE
  ON UPDATE CASCADE
, ADD INDEX `fk_VerifierConfig_Experiment1` (`Experiment_idExperiment` ASC) ;
ALTER TABLE `VerifierConfig` ADD UNIQUE INDEX `Experiment_idExperiment_UNIQUE` (`Experiment_idExperiment` ASC) ;