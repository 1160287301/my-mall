package com.my.mall.mapper;

import com.my.mall.model.CmsSubjectCategory;
import com.my.mall.model.CmsSubjectCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsSubjectCategoryMapper {
    long countByExample(CmsSubjectCategoryExample example);

    int deleteByExample(CmsSubjectCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsSubjectCategory row);

    int insertSelective(CmsSubjectCategory row);

    List<CmsSubjectCategory> selectByExample(CmsSubjectCategoryExample example);

    CmsSubjectCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsSubjectCategory row, @Param("example") CmsSubjectCategoryExample example);

    int updateByExample(@Param("row") CmsSubjectCategory row, @Param("example") CmsSubjectCategoryExample example);

    int updateByPrimaryKeySelective(CmsSubjectCategory row);

    int updateByPrimaryKey(CmsSubjectCategory row);
}