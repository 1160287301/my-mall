package com.my.mall.mapper;

import com.my.mall.model.OmsOrderReturnApply;
import com.my.mall.model.OmsOrderReturnApplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsOrderReturnApplyMapper {
    long countByExample(OmsOrderReturnApplyExample example);

    int deleteByExample(OmsOrderReturnApplyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderReturnApply row);

    int insertSelective(OmsOrderReturnApply row);

    List<OmsOrderReturnApply> selectByExample(OmsOrderReturnApplyExample example);

    OmsOrderReturnApply selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderReturnApply row, @Param("example") OmsOrderReturnApplyExample example);

    int updateByExample(@Param("row") OmsOrderReturnApply row, @Param("example") OmsOrderReturnApplyExample example);

    int updateByPrimaryKeySelective(OmsOrderReturnApply row);

    int updateByPrimaryKey(OmsOrderReturnApply row);
}