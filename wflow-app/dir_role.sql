-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.0.96-community-nt-log - MySQL Community Edition (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
-- Dumping data for table jwdb.dir_role: ~4 rows (approximately)
/*!40000 ALTER TABLE `dir_role` DISABLE KEYS */;
INSERT INTO `dir_role` (`id`, `name`, `description`) VALUES
	('ROLE_MANAGER', 'Manager', 'Manager Apps'),
	('ROLE_MONITORING', 'Monitoring', 'Monitoring Apps'),
/*!40000 ALTER TABLE `dir_role` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
