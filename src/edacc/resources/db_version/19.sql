DROP INDEX `name` ON `Instances`;
ALTER TABLE `User` ADD COLUMN `admin` TINYINT(1) NULL;
