-- ----------------------------
-- 员工档案管理菜单 SQL
-- ----------------------------

-- 菜单 SQL (注意：parent_id 需要根据实际的 HRM 模块父菜单 ID 进行调整)
-- 假设 HRM 模块的父菜单 ID 为 2000（需要根据实际情况调整）

-- 员工档案管理 - 父菜单
INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案管理', '', 2, 1, 2000, 'employee-archive', 'ant-design:solution-outlined', '', 0, true, true, true, '1', NOW(), '1', NOW(), false);

-- 获取刚插入的父菜单ID（MySQL 8.0+）
SET @parent_menu_id = LAST_INSERT_ID();

-- 员工档案管理 - 列表页面
INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案列表', 'hrm:employee-archive:query', 2, 1, @parent_menu_id, 'list', '', 'hrm/employee-archive/list/index', 0, true, true, true, '1', NOW(), '1', NOW(), false);

-- 员工档案管理 - 详情页面
INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案详情', 'hrm:employee-archive:query', 2, 2, @parent_menu_id, 'info', '', 'hrm/employee-archive/info/index', 0, false, true, true, '1', NOW(), '1', NOW(), false);

-- 员工档案管理 - 按钮权限
INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案查询', 'hrm:employee-archive:query', 3, 1, @parent_menu_id, '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), false);

INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案创建', 'hrm:employee-archive:create', 3, 2, @parent_menu_id, '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), false);

INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案更新', 'hrm:employee-archive:update', 3, 3, @parent_menu_id, '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), false);

INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案删除', 'hrm:employee-archive:delete', 3, 4, @parent_menu_id, '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), false);

INSERT INTO system_menu(name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES ('员工档案导出', 'hrm:employee-archive:export', 3, 5, @parent_menu_id, '', '', '', 0, true, true, true, '1', NOW(), '1', NOW(), false);

-- ----------------------------
-- 字典数据 SQL
-- ----------------------------

-- 血型字典
INSERT INTO system_dict_type(name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
VALUES ('血型', 'hrm_blood_type', 0, '员工血型', '1', NOW(), '1', NOW(), false, NULL);

SET @dict_type_id = 'hrm_blood_type';

INSERT INTO system_dict_data(dict_type, label, value, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(@dict_type_id, 'A型', '1', 1, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, 'B型', '2', 2, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, 'AB型', '3', 3, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, 'O型', '4', 4, 0, 'default', '', '', '1', NOW(), '1', NOW(), false);

-- 人员状态字典
INSERT INTO system_dict_type(name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
VALUES ('人员状态', 'hrm_employee_status', 0, '员工人员状态', '1', NOW(), '1', NOW(), false, NULL);

SET @dict_type_id = 'hrm_employee_status';

INSERT INTO system_dict_data(dict_type, label, value, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(@dict_type_id, '正式', '1', 1, 0, 'success', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '试用期', '2', 2, 0, 'warning', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '实习生', '3', 3, 0, 'info', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '兼职', '4', 4, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '零时工', '5', 5, 0, 'default', '', '', '1', NOW(), '1', NOW(), false);

-- 职务字典
INSERT INTO system_dict_type(name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
VALUES ('职务', 'hrm_job_position', 0, '员工职务', '1', NOW(), '1', NOW(), false, NULL);

SET @dict_type_id = 'hrm_job_position';

INSERT INTO system_dict_data(dict_type, label, value, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(@dict_type_id, '总经理', '1', 1, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '副总经理', '2', 2, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '部门经理', '3', 3, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '副部门经理', '4', 4, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '主管', '5', 5, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '副主管', '6', 6, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '专员', '7', 7, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '助理', '8', 8, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '其他', '9', 9, 0, 'default', '', '', '1', NOW(), '1', NOW(), false);

-- 文化程度字典
INSERT INTO system_dict_type(name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
VALUES ('文化程度', 'hrm_education', 0, '员工文化程度', '1', NOW(), '1', NOW(), false, NULL);

SET @dict_type_id = 'hrm_education';

INSERT INTO system_dict_data(dict_type, label, value, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(@dict_type_id, '小学', '1', 1, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '初中', '2', 2, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '高中', '3', 3, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '中专', '4', 4, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '大专', '5', 5, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '本科', '6', 6, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '硕士', '7', 7, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '博士', '8', 8, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '其他', '9', 9, 0, 'default', '', '', '1', NOW(), '1', NOW(), false);

