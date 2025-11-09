-- ----------------------------
-- 企业云盘 - 文件权限表
-- ----------------------------
DROP TABLE IF EXISTS `oa_file_permission`;
CREATE TABLE `oa_file_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `share_type` tinyint NOT NULL COMMENT '分享类型（0人员 1组织）',
  `target_id` bigint NOT NULL COMMENT '目标ID（人员ID或组织ID）',
  `target_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标名称（人员名称或组织名称）',
  `permission` tinyint NOT NULL DEFAULT 0 COMMENT '权限（0仅查看 1可管理）',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_target`(`share_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_file_share_target`(`file_id` ASC, `share_type` ASC, `target_id` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OA协同办公-企业云盘-文件权限表';

-- ----------------------------
-- Records of oa_file_permission
-- ----------------------------
BEGIN;
COMMIT;

