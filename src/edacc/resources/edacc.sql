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
ENGINE = InnoDB
COMMENT = 'Stores user infos in a competition database';


-- -----------------------------------------------------
-- Table `Solver`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Solver` ;

CREATE  TABLE IF NOT EXISTS `Solver` (
  `idSolver` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL COMMENT 'The name of the Solver' ,
  `binaryName` VARCHAR(255) NOT NULL COMMENT 'The name of the binary.' ,
  `binary` LONGBLOB NOT NULL COMMENT 'The binary itself.' ,
  `description` TEXT NULL COMMENT 'Description of the solver.' ,
  `md5` VARCHAR(60) NOT NULL COMMENT 'The md5 checksum of the binary of the solver. The md5 of the solvers within a DB have to be unique. ' ,
  `code` LONGBLOB NULL COMMENT 'An archiv containing the code files of the solvers. ' ,
  `version` VARCHAR(255) NULL COMMENT 'The version of the solver.' ,
  `authors` VARCHAR(255) NULL COMMENT 'The author/s of the solver.' ,
  `User_idUser` INT NULL ,
  PRIMARY KEY (`idSolver`) ,
  UNIQUE INDEX `name` (`name` ASC, `version` ASC) ,
  UNIQUE INDEX `md5` (`md5` ASC) ,
  INDEX `fk_Solver_User1` (`User_idUser` ASC) ,
  CONSTRAINT `fk_Solver_User1`
    FOREIGN KEY (`User_idUser` )
    REFERENCES `User` (`idUser` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_german1_ci
COMMENT = 'All relevent information for a solver.';


-- -----------------------------------------------------
-- Table `Parameters`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Parameters` ;

CREATE  TABLE IF NOT EXISTS `Parameters` (
  `idParameter` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL COMMENT 'User friendly description of the Parameter' ,
  `prefix` VARCHAR(255) NULL COMMENT 'The prefix of the parmater as it is used in the command line of the binary.' ,
  `hasValue` TINYINT(1)  NOT NULL COMMENT 'If the parameter has no value this column should be false. For example parameters like -h dont\'t need a value.' ,
  `value` VARCHAR(255) NULL COMMENT 'A predifined value of a parameter.' ,
  `order` INT NOT NULL COMMENT 'Specifies the order for the parameters, if there exists one. Parameters with the same order represent a sort of order-class and within this class there exists no order. ' ,
  `Solver_idSolver` INT NOT NULL ,
  PRIMARY KEY (`idParameter`) ,
  INDEX `fk_Parameters_Solver` (`Solver_idSolver` ASC) ,
  UNIQUE INDEX `uniqueprefix` (`Solver_idSolver` ASC, `prefix` ASC) ,
  UNIQUE INDEX `uniquename` (`name` ASC, `Solver_idSolver` ASC) ,
  CONSTRAINT `fk_Parameters_Solver`
    FOREIGN KEY (`Solver_idSolver` )
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Information abaout the parameters of a solver. ';


-- -----------------------------------------------------
-- Table `instanceClass`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `instanceClass` ;

CREATE  TABLE IF NOT EXISTS `instanceClass` (
  `idinstanceClass` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NULL COMMENT 'The name of the class. (Unique)' ,
  `description` TEXT NULL COMMENT 'teh description should contain the source-url of the instances.\n' ,
  `source` TINYINT(1)  NOT NULL COMMENT 'tells if the class is a source class. ' ,
  PRIMARY KEY (`idinstanceClass`) ,
  UNIQUE INDEX `name` (`name` ASC) )
ENGINE = InnoDB
COMMENT = 'Enables to manage instances into classes.';


-- -----------------------------------------------------
-- Table `Instances`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Instances` ;

CREATE  TABLE IF NOT EXISTS `Instances` (
  `idInstance` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL COMMENT 'Name of the file containing the instance.' ,
  `instance` LONGBLOB NOT NULL COMMENT 'The instance itself.' ,
  `md5` VARCHAR(60) NOT NULL COMMENT 'The MD5-cheksum of the file.' ,
  `numAtoms` INT NULL COMMENT 'The number of variables.' ,
  `numClauses` INT NULL COMMENT 'Number of clauses.' ,
  `ratio` FLOAT NULL COMMENT 'Number of Variables / number of atoms.' ,
  `maxClauseLength` INT NULL COMMENT 'Length of the longest clause.' ,
  `instanceClass_idinstanceClass` INT NOT NULL COMMENT 'The source class of the instance. ' ,
  PRIMARY KEY (`idInstance`, `instanceClass_idinstanceClass`) ,
  UNIQUE INDEX `name` (`name` ASC) ,
  INDEX `fk_Instances_instanceClass1` (`instanceClass_idinstanceClass` ASC) ,
  CONSTRAINT `fk_Instances_instanceClass1`
    FOREIGN KEY (`instanceClass_idinstanceClass` )
    REFERENCES `instanceClass` (`idinstanceClass` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Contains information about the instances. ';


-- -----------------------------------------------------
-- Table `Experiment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Experiment` ;

CREATE  TABLE IF NOT EXISTS `Experiment` (
  `idExperiment` INT NOT NULL AUTO_INCREMENT ,
  `description` TEXT NULL COMMENT 'Description of the Experiment.' ,
  `date` DATE NOT NULL COMMENT 'The date when it was created.' ,
  `numRuns` INT NOT NULL COMMENT 'Number of independent runs for the same solver and same instance. Usefull when testing stochastic solvers.' ,
  `timeOut` INT NOT NULL COMMENT 'Maximum number of seconds a solver is allowed to run.\n' ,
  `autoGeneratedSeeds` TINYINT(1)  NOT NULL COMMENT 'Specifies if seeds should be generated for the solvers. ' ,
  `name` VARCHAR(255) NULL COMMENT 'Name of experiment.' ,
  `memOut` INT NULL COMMENT 'maximum amount of memeory a solver is allowed to use' ,
  `maxSeed` BIGINT NULL COMMENT 'Maximum number for the seed generating process. ' ,
  PRIMARY KEY (`idExperiment`) )
ENGINE = InnoDB
COMMENT = 'Properties of an experiment.';


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
ENGINE = InnoDB
COMMENT = 'Specifies the instances used in an experiment.';


-- -----------------------------------------------------
-- Table `SolverConfig`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SolverConfig` ;

CREATE  TABLE IF NOT EXISTS `SolverConfig` (
  `idSolverConfig` INT NOT NULL AUTO_INCREMENT ,
  `Solver_idSolver` INT NOT NULL ,
  `Experiment_idExperiment` INT NOT NULL ,
  `seed_group` INT NULL DEFAULT 0 ,
  PRIMARY KEY (`idSolverConfig`) ,
  INDEX `fk_SolverConfig_Solver1` (`Solver_idSolver` ASC) ,
  INDEX `fk_SolverConfig_Experiment1` (`Experiment_idExperiment` ASC) ,
  CONSTRAINT `fk_SolverConfig_Solver1`
    FOREIGN KEY (`Solver_idSolver` )
    REFERENCES `Solver` (`idSolver` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_SolverConfig_Experiment1`
    FOREIGN KEY (`Experiment_idExperiment` )
    REFERENCES `Experiment` (`idExperiment` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Parameter configuration of a solvers of an experiment.';


-- -----------------------------------------------------
-- Table `ExperimentResults`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ExperimentResults` ;

CREATE  TABLE IF NOT EXISTS `ExperimentResults` (
  `idJob` INT NOT NULL AUTO_INCREMENT ,
  `run` INT NOT NULL COMMENT 'The run of a (solver,instance) tupple.' ,
  `status` INT NOT NULL COMMENT 'status of the job\n-2: an error occured\n-1: not started \n0: running\n1: finished normaly by solver\n2: terminated by ulimit maxtime\n3: terminated by ulimit maxmem' ,
  `seed` INT NULL COMMENT 'The seed for the solver. ' ,
  `resultFileName` VARCHAR(255) NOT NULL COMMENT 'the name of the result file of the solver =\n[solverName]_[instanceName]_[solverConfigID]_[run].solver' ,
  `time` FLOAT NULL COMMENT 'The CPU-time the job needed to complete.' ,
  `statusCode` INT NULL COMMENT 'the exit code of the solver' ,
  `SolverConfig_idSolverConfig` INT NOT NULL ,
  `Experiment_idExperiment` INT NOT NULL ,
  `Instances_idInstance` INT NOT NULL ,
  `resultFile` LONGBLOB NULL COMMENT 'contains the stdout and stderr output of the solver. \nresultFileName.solver' ,
  `clientOutput` LONGBLOB NULL COMMENT 'contains the stdout- and stderr-output of the client. Messages like: when and  which job was started can be displayed here.  \nresultFileName.client\n' ,
  `startTime` TIME NULL COMMENT 'The start-time of the job.' ,
  PRIMARY KEY (`idJob`) ,
  INDEX `fk_ExperimentResults_SolverConfig1` (`SolverConfig_idSolverConfig` ASC) ,
  INDEX `fk_ExperimentResults_Experiment1` (`Experiment_idExperiment` ASC) ,
  INDEX `fk_ExperimentResults_Instances1` (`Instances_idInstance` ASC) ,
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
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Stores the jobs of an experiment and their properties.';


-- -----------------------------------------------------
-- Table `SolverConfig_has_Parameters`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SolverConfig_has_Parameters` ;

CREATE  TABLE IF NOT EXISTS `SolverConfig_has_Parameters` (
  `SolverConfig_idSolverConfig` INT NOT NULL ,
  `Parameters_idParameter` INT NOT NULL ,
  `value` VARCHAR(255) NULL COMMENT 'The value of the parameter for this solver configuration. ' ,
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
ENGINE = InnoDB
COMMENT = 'Parameters used for a certain configuration their value';


-- -----------------------------------------------------
-- Table `gridQueue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gridQueue` ;

CREATE  TABLE IF NOT EXISTS `gridQueue` (
  `idgridQueue` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL COMMENT 'The name of the queue.' ,
  `location` VARCHAR(255) NULL COMMENT 'Location of the queue.' ,
  `numNodes` INT NULL COMMENT 'Number of computing nodes/computing systems.' ,
  `numCPUs` INT NOT NULL COMMENT 'Number of available CPUS per node/system.\n' ,
  `walltime` INT NOT NULL COMMENT 'Maximum allowed computing time for a job in hours.\n' ,
  `availNodes` INT NOT NULL COMMENT 'Maximum number of nodes that can be assigned to a single user.' ,
  `maxJobsQueue` INT NULL COMMENT 'Maximum number of nodes in the queue available to all users.' ,
  `genericPBSScript` LONGBLOB NULL COMMENT 'A generic script to be submitted to the queue containg the calls of the client.' ,
  `description` TEXT NULL COMMENT 'Description of the queue.' ,
  PRIMARY KEY (`idgridQueue`) )
ENGINE = InnoDB
COMMENT = 'Contains the properties of a queue.';


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
ENGINE = InnoDB
COMMENT = 'Specifies the gridQueues used for an experiment.';


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
ENGINE = InnoDB
COMMENT = 'Which instance belongs to which class.';


-- -----------------------------------------------------
-- Table `DBConfiguration`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DBConfiguration` ;

-- -----------------------------------------------------
-- FUNCTION `absTime`
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS absTime;

delimiter //
CREATE FUNCTION absTime(time FLOAT) RETURNS INT
BEGIN
  WHILE time < 0 DO
    SET time = time +24*60*60;
  END WHILE;
  RETURN time;
END //
delimiter ;

-- -----------------------------------------------------
-- EVENT `MONITOR_JOBS`
-- -----------------------------------------------------

DROP EVENT IF EXISTS MONITOR_JOBS;

CREATE EVENT MONITOR_JOBS
ON SCHEDULE EVERY '20' MINUTE
DO
UPDATE ExperimentResults SET status=-1 WHERE idJob IN (
SELECT idJob FROM Experiment JOIN (SELECT * FROM ExperimentResults WHERE status=0) AS ERtmp on Experiment.idExperiment = Experiment_idExperiment WHERE absTime(curTime()-startTime)>timeOut*1.5
);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;