package com.my.mall.mapper;

import com.my.mall.model.CmsMemberReport;
import com.my.mall.model.CmsMemberReportExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsMemberReportMapper {
    long countByExample(CmsMemberReportExample example);

    int deleteByExample(CmsMemberReportExample example);

    int insert(CmsMemberReport row);

    int insertSelective(CmsMemberReport row);

    List<CmsMemberReport> selectByExample(CmsMemberReportExample example);

    int updateByExampleSelective(@Param("row") CmsMemberReport row, @Param("example") CmsMemberReportExample example);

    int updateByExample(@Param("row") CmsMemberReport row, @Param("example") CmsMemberReportExample example);
}