package cn.iocoder.yudao.module.oa.controller.admin.car.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 车辆信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CarRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18149")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "车牌号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("车牌号")
    private String carNo;

    @Schema(description = "车辆名称", example = "赵六")
    @ExcelProperty("车辆名称")
    private String carName;

    @Schema(description = "车型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "车型", converter = DictConvert.class)
    @DictFormat("oa_car_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Long carType;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "分类", converter = DictConvert.class)
    @DictFormat("oa_car_cls") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Long carCls;

    @Schema(description = "品牌型号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("品牌型号")
    private String brand;

    @Schema(description = "车座")
    @ExcelProperty("车座")
    private String seatNum;

    @Schema(description = "裸车价", example = "1610")
    @ExcelProperty("裸车价")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private BigDecimal barePrice;

    @Schema(description = "交强险到期日期")
    @ExcelProperty("交强险到期日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate forceInsuranceDate;

    @Schema(description = "商业险到期日期")
    @ExcelProperty("商业险到期日期")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate businessInsuranceDate;

    @Schema(description = "年检日期")
    @ExcelProperty("年检日期")
    private LocalDate yearCheckDate;

    @Schema(description = "上传照片", example = "https://www.iocoder.cn")
    @ExcelProperty("上传照片")
    private String picUrl;

    @Schema(description = "显示顺序")
    @ExcelProperty("显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}