DROP TABLE IF EXISTS `LockedFiles` ;

CREATE  TABLE IF NOT EXISTS `LockedFiles` (
  `filename` VARCHAR(2048) NOT NULL ,
  `filesystemID` INT NOT NULL ,
  `lastReport` DATETIME NOT NULL )
ENGINE = InnoDB;

DROP TABLE IF EXISTS `InstanceDownloads` ;
DROP TABLE IF EXISTS `SolverDownloads` ;