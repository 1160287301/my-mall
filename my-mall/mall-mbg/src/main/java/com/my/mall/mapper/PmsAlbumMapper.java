package com.my.mall.mapper;

import com.my.mall.model.PmsAlbum;
import com.my.mall.model.PmsAlbumExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsAlbumMapper {
    long countByExample(PmsAlbumExample example);

    int deleteByExample(PmsAlbumExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsAlbum row);

    int insertSelective(PmsAlbum row);

    List<PmsAlbum> selectByExample(PmsAlbumExample example);

    PmsAlbum selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsAlbum row, @Param("example") PmsAlbumExample example);

    int updateByExample(@Param("row") PmsAlbum row, @Param("example") PmsAlbumExample example);

    int updateByPrimaryKeySelective(PmsAlbum row);

    int updateByPrimaryKey(PmsAlbum row);
}