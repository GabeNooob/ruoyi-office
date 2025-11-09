-- ----------------------------
-- 企业云盘菜单和字典配置
-- ----------------------------

-- ----------------------------
-- 企业云盘相关字典类型
-- ----------------------------

-- 文件类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件类型', 'oa_file_type', 0, '企业云盘的文件类型')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '文件', '0', 'oa_file_type', 0, 'primary', '', '普通文件'),
(2, '文件夹', '1', 'oa_file_type', 0, 'warning', '', '文件夹')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 文件分享类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件分享类型', 'oa_file_share_type', 0, '文件夹的分享类型')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '人员', '0', 'oa_file_share_type', 0, 'primary', '', '分享给指定人员'),
(2, '组织', '1', 'oa_file_share_type', 0, 'success', '', '分享给组织/部门')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 文件分享权限字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件分享权限', 'oa_file_permission', 0, '文件夹的分享权限')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '仅查看', '0', 'oa_file_permission', 0, 'info', '', '只能查看文件'),
(2, '可管理', '1', 'oa_file_permission', 0, 'success', '', '可以管理文件')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 企业云盘菜单权限配置
-- ----------------------------

-- 获取或创建OA协同办公父菜单
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 如果没有OA协同办公菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT 'OA协同办公', '', 1, 20, 0, '/oa', 'ep:briefcase', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = 'OA协同办公');

-- 重新获取OA协同办公菜单ID
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 企业云盘菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('企业云盘', '', 2, 50, @parent_menu_id, 'file', 'ep:folder', 'oa/file/index', 'OaFileManagement', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取企业云盘菜单ID
SET @file_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '企业云盘' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 企业云盘按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询文件', 'oa:file:query', 3, 1, @file_menu_id, '', '', '', 0, 1, 1, 1),
('创建文件夹', 'oa:file:create', 3, 2, @file_menu_id, '', '', '', 0, 1, 1, 1),
('上传文件', 'oa:file:upload', 3, 3, @file_menu_id, '', '', '', 0, 1, 1, 1),
('更新文件', 'oa:file:update', 3, 4, @file_menu_id, '', '', '', 0, 1, 1, 1),
('删除文件', 'oa:file:delete', 3, 5, @file_menu_id, '', '', '', 0, 1, 1, 1),
('导出文件列表', 'oa:file:export', 3, 6, @file_menu_id, '', '', '', 0, 1, 1, 1),
('收藏文件', 'oa:file:favorite', 3, 7, @file_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

COMMIT;

