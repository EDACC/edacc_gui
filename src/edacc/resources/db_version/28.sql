ALTER TABLE `Experiment` ADD COLUMN `Cost_idCost` INT(11) NULL DEFAULT NULL  AFTER `verifierOutputPreserveLast` , 
  ADD CONSTRAINT `fk_Experiment_Cost1`
  FOREIGN KEY (`Cost_idCost` )
  REFERENCES `Cost` (`idCost` )
  ON DELETE SET NULL
  ON UPDATE CASCADE
, ADD INDEX `fk_Experiment_Cost1` (`Cost_idCost` ASC) ;