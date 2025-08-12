package cn.iocoder.yudao.module.wms.framework.security.service;

import cn.iocoder.yudao.module.wms.framework.security.process.mq.WmsProcessInstanceStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhouzhognyan
 * @date 2024/7/29
 * @description 单据业务接口
 */

public interface IBillBizService {

    Logger logger = LoggerFactory.getLogger(IBillBizService.class);






    /**
     *  根据单据编号更新流程状态
     * @param message 单据编码
     *
     */
    default void updateFlowDataByKey(WmsProcessInstanceStatusMessage message) {

    }







}
