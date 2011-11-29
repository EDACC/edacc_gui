SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `User`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `User` ;

CREATE  TABLE IF NOT EXISTS `User` (
  `idUser` INT NOT NULL ,
  `lastname` VARCHAR(255) NOT NULL ,
  `firstname` VARCHAR(255) NOT NULL ,
  `password` VARCHAR(255) NOT NULL ,
  `email` VARCHAR(255) NOT NULL ,
  `postal_address` VARCHAR(255) NULL ,
  `affiliation` VARCHAR(255) NULL ,
  PRIMARY KEY (`idUser`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Solver`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Solver` ;

CREATE  TABLE IF NOT EXISTS `Solver` (
  `idSolver` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `description` TEXT NULL ,
  `code` LONGBLOB NULL ,
  `version` VARCHAR(255) NULL ,
  `authors` VARCHAR(255) NULL ,
  `User_idUser` INT NULL ,
  PRIMARY KEY (`idSolver`) ,
  UNIQUE INDEX `name` (`name` ASC, `version` ASC) ,
  INDEX `fk_Solver_User1` (`User_idUser` ASC) ,
  CONSTRAINT `fk_Solver_User1`
    FOREIGN KEY (`User_idUser` )
    REFERENCES `User` (`idUser` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Parameters`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Parameters` ;

CREATE  TABLE IF NOT EXISTS `Parameters` (
  `idParameter` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `prefix` VARCHAR(255) NULL ,
  `hasValue` TINYINT(1)  NOT NULL ,
  `defaultValue` VARCHAR(4096) NULL ,
  `order` INT NOT NULL ,
  `Solver_idSolver` INT NOT NULL ,
  `mandatory` TINYINT(1)  NULL ,
  `space` TINYINT(1)  NULL ,
  `attachToPrevious` TINYINT(1)  NULL ,
  PRIMARY KEY (`idParameter`) ,
  INDEX `fk_Parameters_Solver` (`Solver_idSolver` ASC) ,
  UNIQUE INDEX `uniqueprefix` (`Solver_idSolver` ASC, `prefix` ASC) ,
  UNIQUE INDEX `uniquename` (`name` ASC, `Solver_idSolver` ASC) ,
  CONSTRAINT `fk_Parameters_Solver`
    FOREIGN KEY (`Solver_idSolver` )
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `BenchmarkType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `BenchmarkType` ;

CREATE  TABLE IF NOT EXISTS `BenchmarkType` (
  `idBenchmarkType` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `User_idUser` INT NOT NULL ,
  PRIMARY KEY (`idBenchmarkType`) ,
  INDEX `fk_BenchmarkType_User1` (`User_idUser` ASC) ,
  CONSTRAINT `fk_BenchmarkType_User1`
    FOREIGN KEY (`User_idUser` )
    REFERENCES `User` (`idUser` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Instances`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Instances` ;

CREATE  TABLE IF NOT EXISTS `Instances` (
  `idInstance` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `instance` LONGBLOB NOT NULL ,
  `md5` VARCHAR(60) NOT NULL ,
  `BenchmarkType_idBenchmarkType` INT NULL ,
  PRIMARY KEY (`idInstance`) ,
  UNIQUE INDEX `name` (`name` ASC) ,
  INDEX `fk_Instances_BenchmarkType1` (`BenchmarkType_idBenchmarkType` ASC) ,
  CONSTRAINT `fk_Instances_BenchmarkType1`
    FOREIGN KEY (`BenchmarkType_idBenchmarkType` )
    REFERENCES `BenchmarkType` (`idBenchmarkType` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Experiment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Experiment` ;

CREATE  TABLE IF NOT EXISTS `Experiment` (
  `idExperiment` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NULL ,
  `description` TEXT NULL ,
  `date` DATE NOT NULL ,
  `configurationExp` TINYINT(1)  NULL ,
  `priority` INT NULL ,
  `active` TINYINT(1)  NOT NULL ,
  `countUnprocessedJobs` INT NOT NULL DEFAULT 0 ,
  `solverOutputPreserveFirst` INT NULL ,
  `solverOutputPreserveLast` INT NULL ,
  `watcherOutputPreserveFirst` INT NULL ,
  `watcherOutputPreserveLast` INT NULL ,
  `verifierOutputPreserveFirst` INT NULL ,
  `verifierOutputPreserveLast` INT NULL ,
  PRIMARY KEY (`idExperiment`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Experiment_has_Instances`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Experiment_has_Instances` ;

CREATE  TABLE IF NOT EXISTS `Experiment_has_Instances` (
  `idEI` INT NOT NULL AUTO_INCREMENT ,
  `Experiment_idExperiment` INT NOT NULL ,
  `Instances_idInstance` INT NOT NULL ,
  PRIMARY KEY (`idEI`) ,
  INDEX `fk_Experiment_has_Instances_Experiment1` (`Experiment_idExperiment` ASC) ,
  INDEX `fk_Experiment_has_Instances_Instances1` (`Instances_idInstance` ASC) ,
  CONSTRAINT `fk_Experiment_has_Instances_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Experiment_has_Instances_Instances1`
    FOREIGN KEY (`Instances_idInstance` )
    REFERENCES `Instances` (`idInstance` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SolverBinaries`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SolverBinaries` ;

CREATE  TABLE IF NOT EXISTS `SolverBinaries` (
  `idSolverBinary` INT NOT NULL AUTO_INCREMENT ,
  `idSolver` INT NOT NULL ,
  `binaryName` VARCHAR(255) NOT NULL ,
  `binaryArchive` LONGBLOB NOT NULL ,
  `md5` VARCHAR(255) NOT NULL ,
  `version` VARCHAR(255) NULL ,
  `runCommand` VARCHAR(255) NULL ,
  `runPath` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`idSolverBinary`) ,
  INDEX `fk_SolverBinaries_1` (`idSolver` ASC) ,
  CONSTRAINT `fk_SolverBinaries_1`
    FOREIGN KEY (`idSolver` )
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SolverConfig`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SolverConfig` ;

CREATE  TABLE IF NOT EXISTS `SolverConfig` (
  `idSolverConfig` INT NOT NULL AUTO_INCREMENT ,
  `Experiment_idExperiment` INT NOT NULL ,
  `SolverBinaries_idSolverBinary` INT NOT NULL ,
  `seed_group` INT NULL DEFAULT 0 ,
  `name` VARCHAR(4096) NOT NULL ,
  `cost` FLOAT NULL ,
  `cost_function` VARCHAR(255) NULL ,
  `parameter_hash` VARCHAR(255) NULL ,
  `hint` VARCHAR(1024) NOT NULL ,
  PRIMARY KEY (`idSolverConfig`) ,
  INDEX `fk_SolverConfig_Experiment1` (`Experiment_idExperiment` ASC) ,
  INDEX `fk_SolverConfig_SolverBinaries1` (`SolverBinaries_idSolverBinary` ASC) ,
  INDEX `parameter_hash_index` (`parameter_hash` ASC) ,
  CONSTRAINT `fk_SolverConfig_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_SolverConfig_SolverBinaries1`
    FOREIGN KEY (`SolverBinaries_idSolverBinary` )
    REFERENCES `SolverBinaries` (`idSolverBinary` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ResultCodes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ResultCodes` ;

CREATE  TABLE IF NOT EXISTS `ResultCodes` (
  `resultCode` INT NOT NULL ,
  `description` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`resultCode`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `StatusCodes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `StatusCodes` ;

CREATE  TABLE IF NOT EXISTS `StatusCodes` (
  `statusCode` INT NOT NULL ,
  `description` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`statusCode`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gridQueue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gridQueue` ;

CREATE  TABLE IF NOT EXISTS `gridQueue` (
  `idgridQueue` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `location` VARCHAR(255) NULL ,
  `numCPUs` INT NOT NULL ,
  `description` TEXT NULL ,
  `numCores` INT NULL ,
  `numThreads` INT NULL ,
  `hyperthreading` TINYINT(1)  NULL ,
  `turboboost` TINYINT(1)  NULL ,
  `CPUName` VARCHAR(128) NULL ,
  `cacheSize` INT NULL ,
  `cpuflags` VARCHAR(1024) NULL ,
  `memory` BIGINT NULL ,
  `cpuinfo` VARCHAR(4096) NULL ,
  `meminfo` VARCHAR(4096) NULL ,
  PRIMARY KEY (`idgridQueue`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Client`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Client` ;

CREATE  TABLE IF NOT EXISTS `Client` (
  `idClient` INT NOT NULL AUTO_INCREMENT ,
  `numCores` INT NULL ,
  `numThreads` INT NULL ,
  `hyperthreading` TINYINT(1)  NULL ,
  `turboboost` TINYINT(1)  NULL ,
  `CPUName` VARCHAR(128) NULL ,
  `cacheSize` INT NULL ,
  `cpuflags` VARCHAR(1024) NULL ,
  `memory` BIGINT NULL ,
  `memoryFree` BIGINT NULL ,
  `cpuinfo` VARCHAR(4096) NULL ,
  `meminfo` VARCHAR(4096) NULL ,
  `message` VARCHAR(4096) NULL ,
  `gridQueue_idgridQueue` INT NOT NULL ,
  `lastReport` TIMESTAMP NULL ,
  `jobs_wait_time` INT NULL ,
  `current_wait_time` INT NULL ,
  `startTimestamp` TIMESTAMP NULL DEFAULT NOW() ,
  `walltime` INT NULL DEFAULT 0 ,
  PRIMARY KEY (`idClient`) ,
  INDEX `fk_Client_gridQueue1` (`gridQueue_idgridQueue` ASC) ,
  CONSTRAINT `fk_Client_gridQueue1`
    FOREIGN KEY (`gridQueue_idgridQueue` )
    REFERENCES `gridQueue` (`idgridQueue` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ExperimentResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ExperimentResults` ;

CREATE  TABLE IF NOT EXISTS `ExperimentResults` (
  `SolverConfig_idSolverConfig` INT NOT NULL ,
  `Experiment_idExperiment` INT NOT NULL ,
  `Instances_idInstance` INT NOT NULL ,
  `idJob` INT NOT NULL AUTO_INCREMENT ,
  `run` INT NOT NULL ,
  `seed` INT NULL ,
  `status` INT NOT NULL ,
  `startTime` DATETIME NULL ,
  `resultTime` FLOAT NULL ,
  `resultCode` INT NOT NULL ,
  `computeQueue` INT NULL ,
  `date_modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  `priority` INT NOT NULL DEFAULT 0 ,
  `computeNode` VARCHAR(255) NULL ,
  `computeNodeIP` VARCHAR(255) NULL ,
  `CPUTimeLimit` INT NULL ,
  `wallClockTimeLimit` INT NULL ,
  `memoryLimit` INT NULL ,
  `stackSizeLimit` INT NULL ,
  `outputSizeLimitFirst` INT NULL ,
  `outputSizeLimitLast` INT NULL ,
  `Client_idClient` INT NULL ,
  `cost` FLOAT NULL ,
  PRIMARY KEY (`idJob`) ,
  INDEX `fk_ExperimentResults_SolverConfig1` (`SolverConfig_idSolverConfig` ASC) ,
  INDEX `fk_ExperimentResults_Experiment1` (`Experiment_idExperiment` ASC) ,
  INDEX `fk_ExperimentResults_Instances1` (`Instances_idInstance` ASC) ,
  INDEX `computeQueue` (`computeQueue` ASC) ,
  INDEX `priority` (`priority` ASC) ,
  INDEX `fk_ExperimentResults_ResultCodes1` (`resultCode` ASC) ,
  INDEX `fk_ExperimentResults_StatusCodes1` (`status` ASC) ,
  INDEX `fk_ExperimentResults_Client1` (`Client_idClient` ASC) ,
  CONSTRAINT `fk_ExperimentResults_SolverConfig1`
    FOREIGN KEY (`SolverConfig_idSolverConfig` )
    REFERENCES `SolverConfig` (`idSolverConfig` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExperimentResults_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExperimentResults_Instances1`
    FOREIGN KEY (`Instances_idInstance` )
    REFERENCES `Instances` (`idInstance` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExperimentResults_ResultCodes1`
    FOREIGN KEY (`resultCode` )
    REFERENCES `ResultCodes` (`resultCode` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExperimentResults_StatusCodes1`
    FOREIGN KEY (`status` )
    REFERENCES `StatusCodes` (`statusCode` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExperimentResults_Client1`
    FOREIGN KEY (`Client_idClient` )
    REFERENCES `Client` (`idClient` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SolverConfig_has_Parameters`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SolverConfig_has_Parameters` ;

CREATE  TABLE IF NOT EXISTS `SolverConfig_has_Parameters` (
  `SolverConfig_idSolverConfig` INT NOT NULL ,
  `Parameters_idParameter` INT NOT NULL ,
  `value` VARCHAR(4096) NULL ,
  PRIMARY KEY (`SolverConfig_idSolverConfig`, `Parameters_idParameter`) ,
  INDEX `fk_SolverConfig_has_Parameters_SolverConfig1` (`SolverConfig_idSolverConfig` ASC) ,
  INDEX `fk_SolverConfig_has_Parameters_Parameters1` (`Parameters_idParameter` ASC) ,
  CONSTRAINT `fk_SolverConfig_has_Parameters_SolverConfig1`
    FOREIGN KEY (`SolverConfig_idSolverConfig` )
    REFERENCES `SolverConfig` (`idSolverConfig` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_SolverConfig_has_Parameters_Parameters1`
    FOREIGN KEY (`Parameters_idParameter` )
    REFERENCES `Parameters` (`idParameter` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Experiment_has_gridQueue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Experiment_has_gridQueue` ;

CREATE  TABLE IF NOT EXISTS `Experiment_has_gridQueue` (
  `Experiment_idExperiment` INT NOT NULL ,
  `gridQueue_idgridQueue` INT NOT NULL ,
  PRIMARY KEY (`Experiment_idExperiment`, `gridQueue_idgridQueue`) ,
  INDEX `fk_Experiment_has_gridQueue_Experiment1` (`Experiment_idExperiment` ASC) ,
  INDEX `fk_Experiment_has_gridQueue_gridQueue1` (`gridQueue_idgridQueue` ASC) ,
  CONSTRAINT `fk_Experiment_has_gridQueue_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Experiment_has_gridQueue_gridQueue1`
    FOREIGN KEY (`gridQueue_idgridQueue` )
    REFERENCES `gridQueue` (`idgridQueue` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `instanceClass`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `instanceClass` ;

CREATE  TABLE IF NOT EXISTS `instanceClass` (
  `idinstanceClass` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NULL ,
  `description` TEXT NULL ,
  `User_idUser` INT NULL ,
  `parent` INT NULL ,
  PRIMARY KEY (`idinstanceClass`) ,
  INDEX `fk_instanceClass_User1` (`User_idUser` ASC) ,
  CONSTRAINT `fk_instanceClass_User1`
    FOREIGN KEY (`User_idUser` )
    REFERENCES `User` (`idUser` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Instances_has_instanceClass`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Instances_has_instanceClass` ;

CREATE  TABLE IF NOT EXISTS `Instances_has_instanceClass` (
  `Instances_idInstance` INT NOT NULL ,
  `instanceClass_idinstanceClass` INT NOT NULL ,
  PRIMARY KEY (`Instances_idInstance`, `instanceClass_idinstanceClass`) ,
  INDEX `fk_Instances_has_instanceClass_Instances1` (`Instances_idInstance` ASC) ,
  INDEX `fk_Instances_has_instanceClass_instanceClass1` (`instanceClass_idinstanceClass` ASC) ,
  CONSTRAINT `fk_Instances_has_instanceClass_Instances1`
    FOREIGN KEY (`Instances_idInstance` )
    REFERENCES `Instances` (`idInstance` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Instances_has_instanceClass_instanceClass1`
    FOREIGN KEY (`instanceClass_idinstanceClass` )
    REFERENCES `instanceClass` (`idinstanceClass` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DBConfiguration`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DBConfiguration` ;

CREATE  TABLE IF NOT EXISTS `DBConfiguration` (
  `id` INT NOT NULL ,
  `competition` TINYINT(1)  NOT NULL ,
  `competitionPhase` INT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PropertyValueType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PropertyValueType` ;

CREATE  TABLE IF NOT EXISTS `PropertyValueType` (
  `name` VARCHAR(255) NOT NULL ,
  `typeClass` BLOB NULL ,
  `isDefault` TINYINT(1)  NULL ,
  `typeClassFileName` VARCHAR(255) NULL ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  PRIMARY KEY (`name`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ComputationMethod`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ComputationMethod` ;

CREATE  TABLE IF NOT EXISTS `ComputationMethod` (
  `idComputationMethod` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NULL ,
  `description` TEXT NULL ,
  `md5` VARCHAR(60) NULL ,
  `binaryName` VARCHAR(255) NULL ,
  `binaryFile` LONGBLOB NULL ,
  PRIMARY KEY (`idComputationMethod`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  UNIQUE INDEX `md5_UNIQUE` (`md5` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Property`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Property` ;

CREATE  TABLE IF NOT EXISTS `Property` (
  `idProperty` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NULL ,
  `description` TEXT NULL ,
  `propertyType` INT NULL ,
  `propertySource` INT NULL ,
  `idComputationMethod` INT NULL ,
  `ComputationMethodParameters` VARCHAR(255) NULL ,
  `propertyValueType` VARCHAR(255) NOT NULL ,
  `isDefault` TINYINT(1)  NULL ,
  `multipleOccourence` TINYINT(1)  NULL ,
  PRIMARY KEY (`idProperty`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) ,
  INDEX `fk_Property_ComputationMethod1` (`idComputationMethod` ASC) ,
  INDEX `fk_Property_PropertyValueType1` (`propertyValueType` ASC) ,
  CONSTRAINT `fk_Property_ComputationMethod1`
    FOREIGN KEY (`idComputationMethod` )
    REFERENCES `ComputationMethod` (`idComputationMethod` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Property_PropertyValueType1`
    FOREIGN KEY (`propertyValueType` )
    REFERENCES `PropertyValueType` (`name` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ExperimentResult_has_Property`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ExperimentResult_has_Property` ;

CREATE  TABLE IF NOT EXISTS `ExperimentResult_has_Property` (
  `idExperimentResult_has_Property` INT NOT NULL AUTO_INCREMENT ,
  `idProperty` INT NOT NULL ,
  `idExperimentResults` INT NOT NULL ,
  PRIMARY KEY (`idExperimentResult_has_Property`) ,
  INDEX `fk_ExperimentResult_has_Property_Property1` (`idProperty` ASC) ,
  INDEX `fk_ExperimentResult_has_Property_ExperimentResults1` (`idExperimentResults` ASC) ,
  CONSTRAINT `fk_ExperimentResult_has_Property_Property1`
    FOREIGN KEY (`idProperty` )
    REFERENCES `Property` (`idProperty` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ExperimentResult_has_Property_ExperimentResults1`
    FOREIGN KEY (`idExperimentResults` )
    REFERENCES `ExperimentResults` (`idJob` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ExperimentResult_has_PropertyValue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ExperimentResult_has_PropertyValue` ;

CREATE  TABLE IF NOT EXISTS `ExperimentResult_has_PropertyValue` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `idExperimentResult_has_Property` INT NOT NULL ,
  `value` TEXT NULL ,
  `order` INT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_ExperimentResult_has_PropertyValue_ExperimentResult_has_Pr1` (`idExperimentResult_has_Property` ASC) ,
  CONSTRAINT `fk_ExperimentResult_has_PropertyValue_ExperimentResult_has_Pr1`
    FOREIGN KEY (`idExperimentResult_has_Property` )
    REFERENCES `ExperimentResult_has_Property` (`idExperimentResult_has_Property` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CompetitionCategory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CompetitionCategory` ;

CREATE  TABLE IF NOT EXISTS `CompetitionCategory` (
  `idCompetitionCategory` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL ,
  `description` TEXT NULL ,
  PRIMARY KEY (`idCompetitionCategory`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Solver_has_CompetitionCategory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Solver_has_CompetitionCategory` ;

CREATE  TABLE IF NOT EXISTS `Solver_has_CompetitionCategory` (
  `Solver_idSolver` INT NOT NULL ,
  `CompetitionCategory_idCompetitionCategory` INT NOT NULL ,
  PRIMARY KEY (`Solver_idSolver`, `CompetitionCategory_idCompetitionCategory`) ,
  INDEX `fk_Solver_has_CompetitionCategory_CompetitionCategory1` (`CompetitionCategory_idCompetitionCategory` ASC) ,
  CONSTRAINT `fk_Solver_has_CompetitionCategory_Solver1`
    FOREIGN KEY (`Solver_idSolver` )
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Solver_has_CompetitionCategory_CompetitionCategory1`
    FOREIGN KEY (`CompetitionCategory_idCompetitionCategory` )
    REFERENCES `CompetitionCategory` (`idCompetitionCategory` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Instance_has_Property`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Instance_has_Property` ;

CREATE  TABLE IF NOT EXISTS `Instance_has_Property` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `idInstance` INT NOT NULL ,
  `idProperty` INT NOT NULL ,
  `value` TEXT NULL ,
  INDEX `fk_Instance_has_InstanceProperty_2` (`idInstance` ASC) ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_Instance_has_Property_Property1` (`idProperty` ASC) ,
  CONSTRAINT `fk_Instance_has_InstanceProperty_2`
    FOREIGN KEY (`idInstance` )
    REFERENCES `Instances` (`idInstance` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Instance_has_Property_Property1`
    FOREIGN KEY (`idProperty` )
    REFERENCES `Property` (`idProperty` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `PropertyRegExp`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PropertyRegExp` ;

CREATE  TABLE IF NOT EXISTS `PropertyRegExp` (
  `idPropertyRegExp` INT NOT NULL AUTO_INCREMENT ,
  `regexpr` VARCHAR(255) NULL ,
  `idProperty` INT NULL ,
  PRIMARY KEY (`idPropertyRegExp`) ,
  INDEX `fk_PropertyRegExp_1` (`idProperty` ASC) ,
  CONSTRAINT `fk_PropertyRegExp_1`
    FOREIGN KEY (`idProperty` )
    REFERENCES `Property` (`idProperty` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ExperimentResultsOutput`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ExperimentResultsOutput` ;

CREATE  TABLE IF NOT EXISTS `ExperimentResultsOutput` (
  `ExperimentResults_idJob` INT NOT NULL ,
  `solverOutput` LONGBLOB NULL ,
  `watcherOutput` LONGBLOB NULL ,
  `launcherOutput` LONGBLOB NULL ,
  `verifierOutput` LONGBLOB NULL ,
  `solverExitCode` INT NULL ,
  `watcherExitCode` INT NULL ,
  `verifierExitCode` INT NULL ,
  PRIMARY KEY (`ExperimentResults_idJob`) ,
  INDEX `fk_ExperimentResultsOutput_ExperimentResults1` (`ExperimentResults_idJob` ASC) ,
  CONSTRAINT `fk_ExperimentResultsOutput_ExperimentResults1`
    FOREIGN KEY (`ExperimentResults_idJob` )
    REFERENCES `ExperimentResults` (`idJob` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Experiment_has_Client`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Experiment_has_Client` ;

CREATE  TABLE IF NOT EXISTS `Experiment_has_Client` (
  `Experiment_idExperiment` INT NOT NULL ,
  `Client_idClient` INT NOT NULL ,
  `numCores` INT NULL ,
  PRIMARY KEY (`Experiment_idExperiment`, `Client_idClient`) ,
  INDEX `fk_Experiment_has_Client_Client1` (`Client_idClient` ASC) ,
  CONSTRAINT `fk_Experiment_has_Client_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Experiment_has_Client_Client1`
    FOREIGN KEY (`Client_idClient` )
    REFERENCES `Client` (`idClient` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `InstanceDownloads`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `InstanceDownloads` ;

CREATE  TABLE IF NOT EXISTS `InstanceDownloads` (
  `idInstance` INT NOT NULL ,
  `filesystemID` INT NOT NULL ,
  `lastReport` DATETIME NOT NULL ,
  PRIMARY KEY (`idInstance`, `filesystemID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SolverDownloads`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SolverDownloads` ;

CREATE  TABLE IF NOT EXISTS `SolverDownloads` (
  `idSolver` INT NOT NULL ,
  `filesystemID` INT NOT NULL ,
  `lastReport` DATETIME NOT NULL ,
  PRIMARY KEY (`idSolver`, `filesystemID`) )
ENGINE = InnoDB;

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
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `Version`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Version` ;

CREATE  TABLE IF NOT EXISTS `Version` (
  `version` INT NOT NULL ,
  `date` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`version`) )
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ConfigurationScenario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ConfigurationScenario` ;

CREATE  TABLE IF NOT EXISTS `ConfigurationScenario` (
  `idConfigurationScenario` INT NOT NULL AUTO_INCREMENT ,
  `SolverBinaries_idSolverBinary` INT NOT NULL ,
  `Experiment_idExperiment` INT NOT NULL ,
  `initial_course_length` INT NULL ,
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

INSERT INTO StatusCodes VALUES
(-6, 'unknown verifier result code'),
(-5, 'launcher crash'),
(-4, 'watcher crash'),
(-3, 'solver crash'),
(-2, 'verifier crash'),
(-1, 'not started'),
(0, 'running'),
(1, 'finished'),
(20, 'terminated by user'),
(21, 'time limit exceeded'),
(22, 'wallclock time limit'),
(23, 'memory limit'),
(-402, 'watcher SIGINT'),
(-403, 'watcher SIGQUIT'),
(-404, 'watcher SIGILL'),
(-406, 'watcher SIGABRT'),
(-408, 'watcher SIGFPE'),
(-409, 'watcher SIGKILL'),
(-411, 'watcher SIGSEGV'),
(-413, 'watcher SIGPIPE'),
(-414, 'watcher SIGALRM'),
(-415, 'watcher SIGTERM'),
(-410, 'watcher SIGUSR1'),
(-416, 'watcher SIGUSR1'),
(-430, 'watcher SIGUSR1'),
(-412, 'watcher SIGUSR2'),
(-417, 'watcher SIGUSR2'),
(-431, 'watcher SIGUSR2');

INSERT INTO ResultCodes VALUES
(0, 'unknown'),
(10, 'UNSAT'),
(11, 'SAT'),
(-21, 'time limit exceeded'),
(-22, 'wall clock limit exceeded'),
(-23, 'memory limit exceeded'),
(-302, 'solver SIGINT'),
(-303, 'solver SIGQUIT'),
(-304, 'solver SIGILL'),
(-306, 'solver SIGABRT'),
(-308, 'solver SIGFPE'),
(-309, 'solver SIGKILL'),
(-311, 'solver SIGSEGV'),
(-313, 'solver SIGPIPE'),
(-314, 'solver SIGALRM'),
(-315, 'solver SIGTERM'),
(-310, 'solver SIGUSR1'),
(-316, 'solver SIGUSR1'),
(-330, 'solver SIGUSR1'),
(-312, 'solver SIGUSR2'),
(-317, 'solver SIGUSR2'),
(-331, 'solver SIGUSR2'),
(-398, 'solver binary not executable'),
(-399, 'solver binary not executable');

DELIMITER $$

DROP TRIGGER IF EXISTS `ExperimentResult_has_PropertyValueUpdateTrigger` $$
CREATE TRIGGER ExperimentResult_has_PropertyValueUpdateTrigger AFTER INSERT ON ExperimentResult_has_PropertyValue
  FOR EACH ROW BEGIN
    UPDATE ExperimentResults SET date_modified = CURRENT_TIMESTAMP WHERE idJob = (SELECT idExperimentResults FROM ExperimentResult_has_Property WHERE idExperimentResult_has_Property = NEW.idExperimentResult_has_Property);
  END;
$$

DROP TRIGGER IF EXISTS `ExperimentResultsDeleteTrigger` $$
CREATE TRIGGER ExperimentResultsDeleteTrigger AFTER DELETE ON ExperimentResults
  FOR EACH ROW BEGIN
    IF OLD.status=-1 AND OLD.priority>=0 THEN UPDATE Experiment SET countUnprocessedJobs = countUnprocessedJobs - 1 WHERE idExperiment=OLD.Experiment_idExperiment; END IF;
  END;
$$

DROP TRIGGER IF EXISTS `ExperimentResultsNewTrigger` $$
CREATE TRIGGER ExperimentResultsNewTrigger AFTER INSERT ON ExperimentResults
  FOR EACH ROW BEGIN
    IF NEW.status=-1 AND new.priority>=0 THEN UPDATE Experiment SET countUnprocessedJobs = countUnprocessedJobs + 1 WHERE idExperiment=NEW.Experiment_idExperiment; END IF;
  END;
$$

DROP TRIGGER IF EXISTS `ExperimentResultsUpdateTrigger` $$
CREATE TRIGGER ExperimentResultsUpdateTrigger AFTER UPDATE ON ExperimentResults
  FOR EACH ROW BEGIN
    IF (OLD.status = -1 AND NEW.status != -1) OR (OLD.priority >= 0 AND NEW.priority < 0 AND OLD.status = -1) THEN
      UPDATE Experiment SET countUnprocessedJobs = countUnprocessedJobs - 1 WHERE idExperiment=OLD.Experiment_idExperiment;
    ELSEIF (OLD.status != -1 AND NEW.status = -1) OR (OLD.priority < 0 AND NEW.priority >= 0 AND NEW.status=-1) THEN 
      UPDATE Experiment SET countUnprocessedJobs = countUnprocessedJobs + 1 WHERE idExperiment=OLD.Experiment_idExperiment;
    END IF;
  END;
$$

DELIMITER ;

-- -----------------------------------------------------
-- Event `MONITOR_JOBS`
-- -----------------------------------------------------
DROP EVENT IF EXISTS MONITOR_JOBS;

-- CREATE EVENT IF NOT EXISTS MONITOR_JOBS
-- ON SCHEDULE EVERY '20' MINUTE
-- DO
-- UPDATE ExperimentResults SET status=-1 WHERE idJob IN (
-- select idJob FROM (SELECT idJob, startTime, Experiment_idExperiment FROM ExperimentResults WHERE status=0) AS ERtmp JOIN Experiment on Experiment.idExperiment = Experiment_idExperiment WHERE TIME_TO_SEC(TIMEDIFF(NOW(), startTime))>CPUTimeLimit*1.3
-- );

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
