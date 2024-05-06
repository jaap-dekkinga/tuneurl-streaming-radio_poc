-- MySQL dump 10.13  Distrib 8.0.36, for Linux (x86_64)
--
-- Host: localhost    Database: poctuneurl
-- ------------------------------------------------------
-- Server version	8.0.36-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audio_stream_channel`
--

DROP TABLE IF EXISTS `audio_stream_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audio_stream_channel` (
  `ac_id` bigint NOT NULL AUTO_INCREMENT,
  `ac_channel` bigint NOT NULL,
  `ac_offset` bigint NOT NULL DEFAULT '0',
  `ac_duration` bigint NOT NULL DEFAULT '0',
  `ac_category` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ac_title` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ac_url` varchar(4096) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ac_filename` varchar(384) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ac_popup` mediumtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `ac_status` int unsigned NOT NULL DEFAULT '0',
  `ac_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ac_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ac_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30005013 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Audio Stream Channel';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audio_stream_channel`
--

LOCK TABLES `audio_stream_channel` WRITE;
/*!40000 ALTER TABLE `audio_stream_channel` DISABLE KEYS */;
INSERT INTO `audio_stream_channel` VALUES (30005001,1,2365,5063,'save_page','Prince Pasta Anthony! Commercial Turns 50, Continues To Resonate','https://radio.tmalbon.com/audio/v3/ExampleTurl_Save_NPR_Story.mp3','ExampleTurl_Save_NPR_Story.mp3','hExPHRhYmxlPjx0Ym9keT48dHI+CiAgPHRkPjxzcGFuIGNsYXNzPSJpbnN0cnVjdGlvbnNwYW4iPkxpbms6PC9zcGFuPjwvdGQ+PHRkPjxhIGhyZWY9Imh0dHBzOi8vd3d3Lm5wci5vcmcvMjAxOS8xMS8xOS83ODA5NDkyMDgvcHJpbmNlLXBhc3RhLWFudGhvbnktY29tbWVyY2lhbC10dXJucy01MC1jb250aW51ZXMtdG8tcmVzb25hdGUiIHRhcmdldD0iX2JsYW5rIj5wcmluY2UtcGFzdGEtYW50aG9ueS1jb21tZXJjaWFsLXR1cm5zLTUwLWNvbnRpbnVlcy10by1yZXNvbmF0ZTwvYT48L3RkPgo8L3RyPgo8dHI+CiAgPHRkPjxzcGFuIGNsYXNzPSJpbnN0cnVjdGlvbnNwYW4iPlRpdGxlOjwvc3Bhbj48L3RkPjx0ZD5QcmluY2UgUGFzdGEgJ0FudGhvbnkhJyBDb21tZXJjaWFsIFR1cm5zIDUwLCBDb250aW51ZXMgVG8gUmVzb25hdGU8L3RkPgo8L3RyPgo8dHI+CiAgPHRkPjxzcGFuIGNsYXNzPSJpbnN0cnVjdGlvbnNwYW4iPkRhdGU6PC9zcGFuPjwvdGQ+PHRkPk5PVkVNQkVSIDE5LCAyMDE5IDQ6MjQgUE0gRVQ8L3RkPgo8L3RyPgo8dHI+CiAgPHRkPjxzcGFuIGNsYXNzPSJpbnN0cnVjdGlvbnNwYW4iPkZyb206PC9zcGFuPjwvdGQ+PHRkPkhFQVJEIE9OIEFMTCBUSElOR1MgQ09OU0lERVJFRCBGUk9NIDxhIGhyZWY9Imh0dHBzOi8vd3d3LndidXIub3JnLyIgdGFyZ2V0PSJfYmxhbmsiPldCVVI8L2E+PC90ZD4KPC90cj4KPHRyPgogIDx0ZD48c3BhbiBjbGFzcz0iaW5zdHJ1Y3Rpb25zcGFuIj5CeTo8L3NwYW4+PC90ZD48dGQ+SmFtZXMgQm9sb2duYTwvdGQ+CjwvdHI+Cjx0cj4KICA8dGQ+PHNwYW4gY2xhc3M9Imluc3RydWN0aW9uc3BhbiI+U3Rvcnk6PC9zcGFuPjwvdGQ+PHRkPjx0ZXh0YXJlYSBjbGFzcz0ic3RvcmllcyIgcmVhZG9ubHk9IiI+SW4gMTk2OSwgUHJpbmNlIFBhc3RhIGRlYnV0ZWQgYSBUViBjb21tZXJjaWFsIHNldCBpbiBCb3N0b24ncyBMaXR0bGUgSXRhbHkuIEZpZnR5IHllYXJzIGxhdGVyLCB0aGUgYWQgaXMgaWNvbmljIGluIEJvc3RvbiwgYW5kIGhlbHBlZCBsYXVuY2ggSXRhbGlhbiBBbWVyaWNhbnMgYW5kIHRoZWlyIGZvb2QgaW50byBBbWVyaWNhJ3MgaG9tZXMuPC90ZXh0YXJlYT48L3RkPgo8L3RyPgo8L3Rib2R5PjwvdGFibGU+Cg==',0,'2024-03-13 17:47:07','2024-04-11 10:06:35'),(30005002,2,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005003,3,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005004,4,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005005,5,97178,94000,'save_page','Mae Muller--Better Days (feat. Polo G)--EP','https://radio.tmalbon.com/audio/audio-streams.mp3','audio-streams.mp3','hExPHRhYmxlPjx0Ym9keT48dHI+CiAgPHRkPjxzcGFuIGNsYXNzPSJpbnN0cnVjdGlvbnNwYW4iPkxpbms6PC9zcGFuPjwvdGQ+PHRkPjxhIGhyZWY9Imh0dHBzOi8vbXVzaWMuYXBwbGUuY29tL3VzL3NvbmcvYmV0dGVyLWRheXMtZmVhdC1wb2xvLWcvMTYwNTc2MDQ3NCIgdGFyZ2V0PSJfYmxhbmsiPmJldHRlci1kYXlzLWZlYXQtcG9sby1nLTE2MDU3NjA0NzQ8L2E+PC90ZD4KPC90cj4KPHRyPgogIDx0ZD48c3BhbiBjbGFzcz0iaW5zdHJ1Y3Rpb25zcGFuIj5UaXRsZTo8L3NwYW4+PC90ZD48dGQ+QmV0dGVyIERheXMgKGZlYXQuIFBvbG8gRykgLSBFUDwvdGQ+CjwvdHI+Cjx0cj4KICA8dGQ+PHNwYW4gY2xhc3M9Imluc3RydWN0aW9uc3BhbiI+RGF0ZTo8L3NwYW4+PC90ZD48dGQ+SmFudWFyeSAxOSwgMjAyMjwvdGQ+CjwvdHI+Cjx0cj4KICA8dGQ+PHNwYW4gY2xhc3M9Imluc3RydWN0aW9uc3BhbiI+RnJvbTo8L3NwYW4+PC90ZD48dGQ+QSBDYXBpdG9sIFJlY29yZHMgVUsgLyBFTUkgcmVsZWFzZSwgKGMpIDIwMjIgTmVpa2VkIENvbGxlY3RpdmUgQUIsIHVuZGVyIGV4Y2x1c2l2ZSBsaWNlbnNlIHRvIFVuaXZlcnNhbCBPcGVyYXRpb25zIExpbWl0ZWQ8L3RkPgo8L3RyPgo8dHI+CiAgPHRkPjxzcGFuIGNsYXNzPSJpbnN0cnVjdGlvbnNwYW4iPkJ5Ojwvc3Bhbj48L3RkPjx0ZD5ORUlLRUQsIE1hZSBNdWxsZXIsIEogQmFsdmluPC90ZD4KPC90cj4KPHRyPgogIDx0ZD48c3BhbiBjbGFzcz0iaW5zdHJ1Y3Rpb25zcGFuIj5TdG9yeTo8L3NwYW4+PC90ZD48dGQ+PHRleHRhcmVhIGNsYXNzPSJzdG9yaWVzIiByZWFkb25seT0iIj5Xb3VsZCB5b3UgbGlrZSB0byBoZWFyIHRoZSBjb25jZXJ0IGZyb20gdGhlIHBlcnNwZWN0aXZlIG9mIHN0YW5kaW5nIG5leHQgdG8gaGVyLCB0aGUgZHJ1bW1lciBvZiB0aGUgYmFja2dyb3VuZCBzaW5nZXJzPyBJIGxvdmUgeW91IGd1eXMgc28gbXVjaCBhbmQgSSBsb3ZlIHlvdSBNYWx0YS4gVGhpcyBpcyBteSBsYXN0IHNvbmcuIFNvIHBsZWFzZSBqdXN0IGhhdmUgYSBnb29kIHRpbWUuIEJlIHdpdGggbWUuIEkgTWFlIGFuZCB0aGlzIGlzIGJldHRlciBhbmQgeW91IGNhbid0IG1ha2UgZnJpZW5kcyBwcmF5IGZvciB0aGF0IDMwMCBLIGEgc2hvcnQgcGF1c2Ugb2YgdGhlIHBlcmZvcm1hbmNlLiBXb3VsZCB5b3UgbGlrZSB0byBoZWFyIHRoZSByZXN0IG9mIHRoZSBjb25jZXJ0PyBOb3cgZnJvbSB0aGUgcGVyc3BlY3RpdmUgb2Ygc3RhbmRpbmcgbmV4dCB0byBtZSBNdWxsZXIsIHRoZSBkcnVtbWVyIG9yIHRoZSBiYWNrZ3JvdW5kIHNpbmdlcnM/IE5vLCBhbmQgSSwgaGV5LCBubywgbm8uIEFsbCBJIGhhdmUgdG8gZG8gaXMgc2l0IGFuZCBJIGZvciwgaGV5LCBPbGEsIHNpdCBhcm91bmQsIEknbGwgd2FpdCBmb3IgYmV0dGVyIG9uZSBtb3JlIHRpbWUgc3RheWluZyBpbiB0aGUgc2hhZGUuIEFsbCBJIGRvIGlzIHNpdHRpbmcgYXJvdW5kIGFuZCBJIGZlZWwgYmV0dGVyLiBJIGNhbid0IGNhbiBJIGdldCBhbiBhaXIgaHVtYW4gQWxvbnpvIGFuZCBzaXQgYXJvdW5kIGFuZCB3YWl0IGZvciBiZXR0ZXIgdG9kYXk/IE9rLiBJZiB5b3UgbG92ZSBNYWUgTWlsbGVyLCBhcyBtdWNoIGFzIHdlIGRvLCB0aGVyZSBpcyBhIHF1aWNrIGxpbmsgdG8gZG93bmxvYWQgYmV0dGVyIGRheXMgZnJvbSBNYWUgTXVsbGVyIGZyb20gaVR1bmVzLiBOb3csIHdlIHdhbnRlZCB0byB0aGFuayBCQkMgUGFyYW1vdW50IEtpbmcncyBDb2xsZWdlLCBJIEJDLCBNYWdpYyBCZWFucyBhbmQgd2hpdGUgbGlnaHQgZm9yIHRoZWlyIHN1cHBvcnQgdG8gc2V0IHRoaXMgdXAuIFdlIHdvdWxkIGFwcHJlY2lhdGUgaXQgaWYgeW91IGNvbXBsZXRlIGEgcXVpY2sgc3VydmV5LjwvdGV4dGFyZWE+PC90ZD4KPC90cj4KPC90Ym9keT48L3RhYmxlPgo8IS0tIEhZUEVSTElOSz0iaHR0cHM6Ly9tdXNpYy5hcHBsZS5jb20vdXMvc29uZy9iZXR0ZXItZGF5cy1mZWF0LXBvbG8tZy8xNjA1NzYwNDc0IiAtLT4K',0,'2024-03-13 17:47:07','2024-04-11 10:58:31'),(30005006,6,2025,1020000,'live','TuneUrl full demo','https://radio.tmalbon.com/audio/10240-audio-streams.wav','10240-audio-streams.wav','hExPHA+PC9wPgo8IS0tIEhZUEVSTElOSz0iaHR0cHM6Ly9tdXNpYy5hcHBsZS5jb20vdXMvc29uZy9iZXR0ZXItZGF5cy1mZWF0LXBvbG8tZy8xNjA1NzYwNDc0IiAtLT4=',1,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005007,7,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005008,8,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005009,9,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005010,10,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005011,11,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07'),(30005012,12,0,10,'category','title','url','filename','popup',0,'2024-03-13 17:47:07','2024-03-13 17:47:07');
/*!40000 ALTER TABLE `audio_stream_channel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audio_stream_data`
--

DROP TABLE IF EXISTS `audio_stream_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audio_stream_data` (
  `as_id` bigint NOT NULL AUTO_INCREMENT,
  `as_duration` bigint NOT NULL DEFAULT '0',
  `as_filename` varchar(48) COLLATE utf8mb4_unicode_ci NOT NULL,
  `as_status` int unsigned NOT NULL DEFAULT '0',
  `as_url` varchar(4096) COLLATE utf8mb4_unicode_ci NOT NULL,
  `as_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `as_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`as_id`),
  UNIQUE KEY `uq_audio_stream_data_as_id` (`as_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19003001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Audio Stream Data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audio_stream_data`
--

LOCK TABLES `audio_stream_data` WRITE;
/*!40000 ALTER TABLE `audio_stream_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `audio_stream_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fingerprints`
--

DROP TABLE IF EXISTS `fingerprints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fingerprints` (
  `fp_id` bigint NOT NULL AUTO_INCREMENT,
  `fp_as_id` bigint NOT NULL,
  `fp_offset` int unsigned NOT NULL,
  `fp_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fp_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`fp_id`),
  UNIQUE KEY `uq_fingerprints_fp_as_id_fp_offset` (`fp_as_id`,`fp_offset`),
  CONSTRAINT `fk_fingerprints_fp_as_id` FOREIGN KEY (`fp_as_id`) REFERENCES `audio_stream_data` (`as_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20004001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Audio Stream Fingerprint';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fingerprints`
--

LOCK TABLES `fingerprints` WRITE;
/*!40000 ALTER TABLE `fingerprints` DISABLE KEYS */;
/*!40000 ALTER TABLE `fingerprints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ldap_info`
--

DROP TABLE IF EXISTS `ldap_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ldap_info` (
  `li_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'LDAP ID',
  `li_pwd` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'LDAP password',
  `li_uuid` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'LDAP UUID',
  `li_status` bigint NOT NULL DEFAULT '0' COMMENT 'LDAP status 0=New, 1=Active, 2=Suspended, 3=Deleted',
  PRIMARY KEY (`li_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17001003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Simulated LDAP User Data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ldap_info`
--

LOCK TABLES `ldap_info` WRITE;
/*!40000 ALTER TABLE `ldap_info` DISABLE KEYS */;
INSERT INTO `ldap_info` VALUES (17001001,'hExYWRtaW5AMTIzNCFwYXNzd29yZA==','6c1620a8-c5db-4ad1-a3cc-0bae9164a0d3',1),(17001002,'hExcGFzc3dvcmRAdXNlciExMjM0','a8b03cb0-b293-44b8-b4ec-3e9869c0bcdb',1);
/*!40000 ALTER TABLE `ldap_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sdk_user`
--

DROP TABLE IF EXISTS `sdk_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sdk_user` (
  `su_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `su_email` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'User Email',
  `su_fullname` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'User Fullname',
  `su_roles` varchar(48) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'User Roles',
  `su_ldap` bigint NOT NULL COMMENT 'User LDAP ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Date Created',
  `su_alias` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User Alias ID',
  PRIMARY KEY (`su_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17001003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SDK User table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sdk_user`
--

LOCK TABLES `sdk_user` WRITE;
/*!40000 ALTER TABLE `sdk_user` DISABLE KEYS */;
INSERT INTO `sdk_user` VALUES (17001001,'admin@example.com','admin','ADMIN_ROLE',17001001,'2024-03-13 17:47:07','17001001'),(17001002,'user@example.com','user','USER_ROLE',17001002,'2024-03-04 17:14:56','17001002');
/*!40000 ALTER TABLE `sdk_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session_data`
--

DROP TABLE IF EXISTS `session_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session_data` (
  `sd_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Session ID',
  `sd_ldap_id` bigint NOT NULL COMMENT 'Session LDAP ID',
  `sd_ip_addr` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IP Address',
  `sd_start` datetime NOT NULL COMMENT 'Start Session datetime',
  `sd_end` datetime NOT NULL COMMENT 'End Session datetime',
  PRIMARY KEY (`sd_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18002054 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Session Data for API call rate limiting';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session_data`
--

LOCK TABLES `session_data` WRITE;
/*!40000 ALTER TABLE `session_data` DISABLE KEYS */;
INSERT INTO `session_data` VALUES (18002001,17001001,'172.31.30.201','2024-03-13 17:47:07','2024-03-13 17:47:07'),(18002002,17001002,'172.31.30.201','2024-03-02 07:43:13','2024-03-02 07:43:13'),(18002003,17001002,'158.62.9.76','2024-04-15 09:21:36','2024-04-15 09:21:36'),(18002004,17001002,'158.62.9.76','2024-04-15 09:23:48','2024-04-15 09:23:48'),(18002005,17001002,'158.62.9.76','2024-04-15 09:50:02','2024-04-15 09:50:02'),(18002006,17001002,'158.62.9.76','2024-04-15 11:14:53','2024-04-15 11:14:53'),(18002007,17001002,'158.62.9.76','2024-04-15 11:15:16','2024-04-15 11:15:16'),(18002008,17001002,'158.62.9.76','2024-04-15 11:23:07','2024-04-15 11:23:07'),(18002009,17001002,'158.62.9.76','2024-04-15 11:59:34','2024-04-15 11:59:34'),(18002010,17001002,'158.62.9.76','2024-04-15 12:11:33','2024-04-15 12:11:33'),(18002011,17001002,'158.62.9.76','2024-04-15 12:11:49','2024-04-15 12:11:49'),(18002012,17001002,'158.62.9.76','2024-04-15 12:12:21','2024-04-15 12:12:21'),(18002013,17001002,'158.62.9.76','2024-04-15 12:17:37','2024-04-15 12:17:37'),(18002014,17001002,'158.62.9.76','2024-04-15 12:18:17','2024-04-15 12:18:17'),(18002015,17001002,'158.62.9.76','2024-04-15 12:22:14','2024-04-15 12:22:14'),(18002016,17001002,'158.62.9.76','2024-04-15 12:27:12','2024-04-15 12:27:12'),(18002017,17001002,'158.62.9.76','2024-04-15 12:28:22','2024-04-15 12:28:22'),(18002018,17001002,'175.176.18.5','2024-04-15 12:57:03','2024-04-15 12:57:03'),(18002019,17001002,'112.198.104.214','2024-04-15 13:09:02','2024-04-15 13:09:02'),(18002020,17001002,'175.176.17.195','2024-04-15 13:15:32','2024-04-15 13:15:32'),(18002021,17001002,'158.62.9.76','2024-04-15 13:25:27','2024-04-15 13:25:27'),(18002022,17001002,'158.62.9.76','2024-04-15 13:29:58','2024-04-15 13:29:58'),(18002023,17001002,'158.62.9.76','2024-04-15 13:42:36','2024-04-15 13:42:36'),(18002024,17001002,'158.62.9.76','2024-04-15 13:50:12','2024-04-15 13:50:12'),(18002025,17001002,'158.62.9.76','2024-04-15 13:56:20','2024-04-15 13:56:20'),(18002026,17001002,'158.62.9.76','2024-04-15 13:59:41','2024-04-15 13:59:41'),(18002027,17001002,'158.62.9.76','2024-04-15 13:59:55','2024-04-15 13:59:55'),(18002028,17001002,'158.62.9.76','2024-04-15 20:23:01','2024-04-15 20:23:01'),(18002029,17001002,'158.62.9.76','2024-04-15 21:33:14','2024-04-15 21:33:14'),(18002030,17001002,'158.62.9.76','2024-04-15 21:33:38','2024-04-15 21:33:38'),(18002031,17001002,'158.62.9.76','2024-04-15 23:45:59','2024-04-15 23:45:59'),(18002032,17001002,'158.62.9.76','2024-04-16 00:35:44','2024-04-16 00:35:44'),(18002033,17001002,'158.62.9.76','2024-04-16 00:56:28','2024-04-16 00:56:28'),(18002034,17001002,'158.62.9.76','2024-04-16 02:50:09','2024-04-16 02:50:09'),(18002035,17001002,'158.62.9.76','2024-04-16 03:02:36','2024-04-16 03:02:36'),(18002036,17001002,'158.62.9.76','2024-04-16 03:02:57','2024-04-16 03:02:57'),(18002037,17001002,'158.62.9.76','2024-04-16 03:20:11','2024-04-16 03:20:11'),(18002038,17001002,'158.62.9.76','2024-04-16 03:21:05','2024-04-16 03:21:05'),(18002039,17001002,'158.62.9.76','2024-04-16 03:45:15','2024-04-16 03:45:15'),(18002040,17001002,'158.62.9.76','2024-04-16 03:47:38','2024-04-16 03:47:38'),(18002041,17001002,'158.62.9.76','2024-04-16 04:13:03','2024-04-16 04:13:03'),(18002042,17001002,'158.62.9.76','2024-04-16 04:15:57','2024-04-16 04:15:57'),(18002043,17001002,'158.62.9.76','2024-04-16 04:16:54','2024-04-16 04:16:54'),(18002044,17001002,'158.62.9.76','2024-04-16 05:25:17','2024-04-16 05:25:17'),(18002045,17001002,'158.62.9.76','2024-04-16 05:32:17','2024-04-16 05:32:17'),(18002046,17001002,'158.62.9.76','2024-04-16 05:35:51','2024-04-16 05:35:51'),(18002047,17001002,'158.62.9.76','2024-04-16 09:42:06','2024-04-16 09:42:06'),(18002048,17001002,'158.62.9.76','2024-04-16 09:45:16','2024-04-16 09:45:16'),(18002049,17001002,'158.62.9.76','2024-04-16 11:36:55','2024-04-16 11:36:55'),(18002050,17001002,'158.62.9.76','2024-04-16 11:50:54','2024-04-16 11:50:54'),(18002051,17001002,'158.62.9.76','2024-04-16 11:51:24','2024-04-16 11:51:24'),(18002052,17001002,'158.62.9.76','2024-04-16 11:51:41','2024-04-16 11:51:41'),(18002053,17001002,'158.62.9.76','2024-04-16 20:01:57','2024-04-16 20:01:57');
/*!40000 ALTER TABLE `session_data` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-16 20:09:19
