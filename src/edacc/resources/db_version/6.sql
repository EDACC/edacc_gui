-- FK index exists anyway
DROP INDEX status ON ExperimentResults;

ALTER TABLE `Experiment` ADD COLUMN `countUnprocessedJobs` INT NOT NULL DEFAULT 0;

-- Update existing experiments with their count
UPDATE Experiment SET countUnprocessedJobs = (SELECT COUNT(*) FROM ExperimentResults WHERE status=-1 AND priority>= 0 AND Experiment_idExperiment=idExperiment);

DELIMITER $$

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