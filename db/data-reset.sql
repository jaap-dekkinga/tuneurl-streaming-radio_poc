--
-- BSD 3-Clause License
--
-- Copyright (c) 2024, TuneURL Inc.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
--
-- 1. Redistributions of source code must retain the above copyright notice, this
--    list of conditions and the following disclaimer.
--
-- 2. Redistributions in binary form must reproduce the above copyright notice,
--    this list of conditions and the following disclaimer in the documentation
--    and/or other materials provided with the distribution.
--
-- 3. Neither the name of the copyright holder nor the names of its
--    contributors may be used to endorse or promote products derived from
--    this software without specific prior written permission.
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
-- FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
-- DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
-- CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
-- OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--
-- mysqladmin --user=root --password=$PASSWORD drop poctuneurl
-- mysql --user=root --password=$PASSWORD < db/init.sql
-- mysqlshow --user=root --password=$PASSWORD
-- +--------------------+
-- |     Databases      |
-- +--------------------+
-- | information_schema |
-- | mysql              |
-- | performance_schema |
-- | sys                |
-- | poctuneurl         |
-- +--------------------+
--
-- MySQL Script generated by MySQL Workbench
-- Saturday, 29 July, 2023 01:50:07 AM
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema poctuneurl
-- -----------------------------------------------------
-- CREATE SCHEMA IF NOT EXISTS `poctuneurl` ;

-- -----------------------------------------------------
-- Schema poctuneurl
-- -----------------------------------------------------
USE `poctuneurl` ;

-- -----------------------------------------------------
-- Table `poctuneurl`.`sdk_user` SdkUser
-- -----------------------------------------------------
DROP TABLE IF EXISTS `poctuneurl`.`sdk_user` ;

CREATE TABLE IF NOT EXISTS `poctuneurl`.`sdk_user` (
  `su_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `su_email` VARCHAR(254) NOT NULL COMMENT 'User Email',
  `su_fullname` VARCHAR(254) NOT NULL COMMENT 'User Fullname',
  `su_roles` VARCHAR(48) NOT NULL COMMENT 'User Roles',
  `su_ldap` BIGINT NOT NULL COMMENT 'User LDAP ID',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Date Created',
  `su_alias` VARCHAR(16) NULL COMMENT 'User Alias ID',
  PRIMARY KEY (`su_id`)
)
ENGINE = InnoDB
AUTO_INCREMENT=17001001
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'SDK User table';

LOCK TABLES `sdk_user` WRITE;
/*!40000 ALTER TABLE `sdk_user` DISABLE KEYS */;
INSERT INTO `sdk_user` VALUES (17001001,'admin@example.com','admin','ADMIN_ROLE',17001001,'2024-03-13 17:47:07','17001001'),(17001002,'user@example.com','user','USER_ROLE',17001002,'2024-03-04 17:14:56','17001002');
/*!40000 ALTER TABLE `sdk_user` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `poctuneurl`.`ldap_info` LdapInfo
-- -----------------------------------------------------
DROP TABLE IF EXISTS `poctuneurl`.`ldap_info` ;

CREATE TABLE IF NOT EXISTS `poctuneurl`.`ldap_info` (
  `li_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'LDAP ID',
  `li_pwd` VARCHAR(63) NOT NULL COMMENT 'LDAP password',
  `li_uuid` VARCHAR(36) NOT NULL COMMENT 'LDAP UUID',
  `li_status` BIGINT NOT NULL DEFAULT 0 COMMENT 'LDAP status 0=New, 1=Active, 2=Suspended, 3=Deleted',
  PRIMARY KEY (`li_id`)
)
ENGINE = InnoDB
AUTO_INCREMENT=17001001
COMMENT = 'Simulated LDAP User Data';

LOCK TABLES `ldap_info` WRITE;
/*!40000 ALTER TABLE `ldap_info` DISABLE KEYS */;
INSERT INTO `ldap_info` VALUES (17001001,'hExYWRtaW5AMTIzNCFwYXNzd29yZA==','6c1620a8-c5db-4ad1-a3cc-0bae9164a0d3',1),(17001002,'hExcGFzc3dvcmRAdXNlciExMjM0','a8b03cb0-b293-44b8-b4ec-3e9869c0bcdb',1);
/*!40000 ALTER TABLE `ldap_info` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `poctuneurl`.`session_data` SessionData
-- -----------------------------------------------------
DROP TABLE IF EXISTS `poctuneurl`.`session_data` ;

CREATE TABLE IF NOT EXISTS `poctuneurl`.`session_data` (
  `sd_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Session ID',
  `sd_ldap_id` BIGINT NOT NULL COMMENT 'Session LDAP ID',
  `sd_ip_addr` VARCHAR(45) NOT NULL COMMENT 'IP Address',
  `sd_start` DATETIME NOT NULL COMMENT 'Start Session datetime',
  `sd_end` DATETIME NOT NULL COMMENT 'End Session datetime',
  PRIMARY KEY (`sd_id`)
)
ENGINE = InnoDB
AUTO_INCREMENT=18002001
COMMENT = 'Session Data for API call rate limiting';

LOCK TABLES `session_data` WRITE;
/*!40000 ALTER TABLE `session_data` DISABLE KEYS */;
INSERT INTO `session_data` VALUES (18002001,17001001,'172.31.30.201','2024-03-13 17:47:07','2024-03-13 17:47:07'),(18002002,17001002,'172.31.30.201','2024-03-02 07:43:13','2024-03-02 07:43:13');
/*!40000 ALTER TABLE `session_data` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `poctuneurl`.`audio_stream_data` AudioStreamData
-- -----------------------------------------------------
DROP TABLE IF EXISTS `poctuneurl`.`audio_stream_data` ;

CREATE TABLE IF NOT EXISTS `poctuneurl`.`audio_stream_data` (
  `as_id` bigint NOT NULL AUTO_INCREMENT,
  `as_duration` bigint NOT NULL DEFAULT '0',
  `as_filename` VARCHAR(48) NOT NULL,
  `as_status` INT UNSIGNED NOT NULL DEFAULT '0',
  `as_url` VARCHAR(4096) NOT NULL,
  `as_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `as_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`as_id`))
ENGINE = InnoDB
AUTO_INCREMENT=19003001
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Audio Stream Data';

CREATE UNIQUE INDEX `uq_audio_stream_data_as_id` ON `poctuneurl`.`audio_stream_data` (`as_id` ASC) VISIBLE;

LOCK TABLES `audio_stream_data` WRITE;
/*!40000 ALTER TABLE `audio_stream_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `audio_stream_data` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `poctuneurl`.`fingerprints` FingerPrintsData
-- -----------------------------------------------------
DROP TABLE IF EXISTS `poctuneurl`.`fingerprints` ;

CREATE TABLE IF NOT EXISTS `poctuneurl`.`fingerprints` (
  `fp_id` bigint NOT NULL AUTO_INCREMENT,
  `fp_as_id` bigint NOT NULL,
  `fp_offset` INT UNSIGNED NOT NULL,
  `fp_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fp_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_fingerprints_fp_as_id`
    FOREIGN KEY (`fp_as_id`)
    REFERENCES `poctuneurl`.`audio_stream_data` (`as_id`)
    ON DELETE CASCADE,
  PRIMARY KEY (`fp_id`))
ENGINE = InnoDB
AUTO_INCREMENT=20004001
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Audio Stream Fingerprint';

CREATE UNIQUE INDEX `uq_fingerprints_fp_as_id_fp_offset` ON `poctuneurl`.`fingerprints` (`fp_as_id` ASC, `fp_offset` ASC) VISIBLE;

-- -----------------------------------------------------
-- Table `poctuneurl`.`audio_stream_data` AudioStreamTrainingChannel
-- -----------------------------------------------------
DROP TABLE IF EXISTS `poctuneurl`.`audio_stream_channel` ;

CREATE TABLE IF NOT EXISTS `poctuneurl`.`audio_stream_channel` (
  `ac_id` bigint NOT NULL AUTO_INCREMENT,
  `ac_channel` bigint NOT NULL,
  `ac_offset` bigint NOT NULL DEFAULT '0',
  `ac_duration` bigint NOT NULL DEFAULT '0',
  `ac_category` VARCHAR(64) NOT NULL,
  `ac_title` VARCHAR(512) NOT NULL,
  `ac_url` VARCHAR(4096) NOT NULL,
  `ac_filename` VARCHAR(384) NOT NULL,
  `ac_popup` MEDIUMTEXT NOT NULL,
  `ac_status` INT UNSIGNED NOT NULL DEFAULT '0',
  `ac_created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ac_modified` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ac_id`))
ENGINE = InnoDB
AUTO_INCREMENT=30005001
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = 'Audio Stream Channel';

LOCK TABLES `audio_stream_channel` WRITE;
/*!40000 ALTER TABLE `audio_stream_channel` DISABLE KEYS */;
INSERT INTO `audio_stream_channel` VALUES
  (30005001, 1, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005002, 2, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005003, 3, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005004, 4, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005005, 5, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005006, 6, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005007, 7, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005008, 8, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005009, 9, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005010,10, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005011,11, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),
  (30005012,12, 0,10,"category","title","url","filename","popup",0,'2024-03-13 17:47:07','2024-03-13 17:47:07');
/*!40000 ALTER TABLE `audio_stream_channel` ENABLE KEYS */;
UNLOCK TABLES;

COMMIT ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
