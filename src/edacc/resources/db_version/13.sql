ALTER TABLE Experiment ADD (
  `solverOutputPreserveFirst` INT NULL ,
  `solverOutputPreserveLast` INT NULL ,
  `watcherOutputPreserveFirst` INT NULL ,
  `watcherOutputPreserveLast` INT NULL ,
  `verifierOutputPreserveFirst` INT NULL ,
  `verifierOutputPreserveLast` INT NULL
);
ALTER TABLE ExperimentResults DROP outputSizeLimitFirst;
ALTER TABLE ExperimentResults DROP outputSizeLimitLast;