package cn.iocoder.yudao.module.bpm.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants.*;

/**
 * BPM 流程变量工具类
 * 提供统一的流程变量创建方法，确保待办列表能够显示关键业务信息
 * 
 * @author 芋道源码
 */
public class BpmProcessVariableUtils {

    /**
     * 构建单据流程变量
     * 从业务对象中提取属性构建流程变量，支持以下标准字段：
     * - billCode: 单据编号
     * - cause: 事由/说明
     * - 其他字段会按原字段名添加到变量中
     * 
     * @param billObject 业务单据对象
     * @return 流程变量Map
     */
    public static Map<String, Object> buildBillVariables(Object billObject) {
        Map<String, Object> variables = new HashMap<>();
        
        if (billObject == null) {
            return variables;
        }
        
        try {
            Class<?> clazz = billObject.getClass();
            Field[] fields = clazz.getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(billObject);
                
                // 跳过null值
                if (value == null) {
                    continue;
                }
                
                String fieldName = field.getName();
                
                // 标准字段映射
                if ("billCode".equals(fieldName)) {
                    variables.put(BILL_CODE, value.toString());
                } else if ("cause".equals(fieldName)) {
                    variables.put(CAUSE, value.toString());
                }
            }
        } catch (Exception e) {
            // 反射异常时，降级处理，返回空Map
            // 实际项目中可以记录日志
        }
        
        return variables;
    }

    /**
     * 构建单据流程变量（扩展版本）
     * 
     * @param billObject 业务单据对象
     * @param additionalVariables 额外的业务变量
     * @return 流程变量Map
     */
    public static Map<String, Object> buildBillVariables(Object billObject, Map<String, Object> additionalVariables) {
        Map<String, Object> variables = buildBillVariables(billObject);
        if (additionalVariables != null) {
            variables.putAll(additionalVariables);
        }
        return variables;
    }


    /**
     * 从流程变量中获取单据编号
     * 
     * @param variables 流程变量
     * @return 单据编号
     */
    public static String getBillCode(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object billCode = variables.get(BILL_CODE);
        return billCode != null ? billCode.toString() : null;
    }

    /**
     * 从流程变量中获取事由
     * 
     * @param variables 流程变量
     * @return 事由/说明
     */
    public static String getCause(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object cause = variables.get(CAUSE);
        return cause != null ? cause.toString() : null;
    }

}
