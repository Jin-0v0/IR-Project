create database doc_search CHARACTER SET utf8;
use doc_search;

CREATE TABLE `files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `original_file_name` varchar(150) NOT NULL COMMENT '文件原始名字',
  `size` int(10) NOT NULL COMMENT '单位:B,文件大小',
  `store_relative_path` varchar(300) NOT NULL COMMENT '存储路径',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_path` (`store_relative_path`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;