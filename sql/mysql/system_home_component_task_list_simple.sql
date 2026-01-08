-- 任务列表组件初始化SQL（精简版）
-- 执行前请根据实际情况调整 id 和 category_id

INSERT INTO `ruoyi-office`.system_home_component
(id, category_id, name, code, component_path, description, preview_image, default_width, default_height, config_schema, status, sort, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES(
  10, 
  2, 
  '任务列表', 
  'workbench_task_list', 
  'dashboard/home/components/taskLists/workbench-task-list.vue', 
  '展示我的单据、待办任务、已办任务、抄送我的四个Tab页签', 
  NULL, 
  24, 
  8, 
  '{"properties": [{"key": "title", "type": "string", "label": "标题文本", "default": "我的任务", "required": false}, {"key": "showTitle", "type": "boolean", "label": "显示标题", "default": true, "required": false}, {"key": "floatingTitle", "type": "boolean", "label": "浮动标题", "default": false, "required": false}, {"key": "titleFontSize", "type": "number", "label": "标题文字大小(px)", "default": 18, "min": 12, "max": 32, "required": false}, {"key": "titleBold", "type": "boolean", "label": "标题文字加粗", "default": true, "required": false}, {"key": "titleColor", "type": "string", "label": "标题文字颜色", "default": "#1F2937", "required": false}, {"key": "titleMarginTop", "type": "number", "label": "标题上边距(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "titleMarginRight", "type": "number", "label": "标题右边距(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "titleMarginBottom", "type": "number", "label": "标题下边距(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "titleMarginLeft", "type": "number", "label": "标题左边距(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "paddingTop", "type": "number", "label": "内边距-上(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "paddingRight", "type": "number", "label": "内边距-右(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "paddingBottom", "type": "number", "label": "内边距-下(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "paddingLeft", "type": "number", "label": "内边距-左(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "marginTop", "type": "number", "label": "外边距-上(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "marginRight", "type": "number", "label": "外边距-右(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "marginBottom", "type": "number", "label": "外边距-下(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "marginLeft", "type": "number", "label": "外边距-左(px)", "default": 0, "min": 0, "max": 100, "required": false}]}', 
  0, 
  10, 
  '1', 
  NOW(), 
  '', 
  NOW(), 
  0, 
  1
);

