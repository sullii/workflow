/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : isocloud_acti

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2019-06-16 13:19:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for employ
-- ----------------------------
DROP TABLE IF EXISTS `employ`;
CREATE TABLE `employ` (
  `id` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL,
  `age` int(11) NOT NULL,
  `brithday` datetime DEFAULT NULL,
  `mobile` varchar(32) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `depNo` varchar(32) NOT NULL,
  `managerId` varchar(32) DEFAULT NULL,
  `position` varchar(32) NOT NULL,
  `depName` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of employ
-- ----------------------------
INSERT INTO `employ` VALUES ('1', '张三', '1', '2019-06-02 14:04:52', '1846469199', '1@qq.com', '001', '2', '研发', '研发部');
INSERT INTO `employ` VALUES ('2', '李四', '15', '2019-05-06 14:05:41', '3', '1@qq.com', '001', '3', '研发经理', '研发部');
INSERT INTO `employ` VALUES ('3', '王五', '16', '2019-06-02 14:06:35', '45165', '1@qq.com', '002', null, '总经理', '董事会');

-- ----------------------------
-- Table structure for leavebill
-- ----------------------------
DROP TABLE IF EXISTS `leavebill`;
CREATE TABLE `leavebill` (
  `id` varchar(32) NOT NULL,
  `empId` varchar(32) NOT NULL,
  `reason` varchar(64) NOT NULL,
  `remark` varchar(64) DEFAULT NULL,
  `creatTime` datetime NOT NULL,
  `finishTime` datetime DEFAULT NULL,
  `status` int(1) DEFAULT '0' COMMENT '状态：0进行中，1完成',
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

