package com.my.mall.mapper;

import com.my.mall.model.OmsCompanyAddress;
import com.my.mall.model.OmsCompanyAddressExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsCompanyAddressMapper {
    long countByExample(OmsCompanyAddressExample example);

    int deleteByExample(OmsCompanyAddressExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsCompanyAddress row);

    int insertSelective(OmsCompanyAddress row);

    List<OmsCompanyAddress> selectByExample(OmsCompanyAddressExample example);

    OmsCompanyAddress selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsCompanyAddress row, @Param("example") OmsCompanyAddressExample example);

    int updateByExample(@Param("row") OmsCompanyAddress row, @Param("example") OmsCompanyAddressExample example);

    int updateByPrimaryKeySelective(OmsCompanyAddress row);

    int updateByPrimaryKey(OmsCompanyAddress row);
}