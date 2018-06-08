package com.fan.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fan.o2o.entity.HeadLine;

public interface HeadLineDao {
	/**
	 * 根据传入的查询条件(头条名查询头条)
	 * 
	 * @return
	 */
	List<HeadLine> queryHeadLine(@Param("headLineCondition") HeadLine headLineCondition);

	/**
	 * 根据头条id返回唯一的头条信息
	 * 
	 * @param lineId
	 * @return
	 */
	HeadLineDao queryHeadLineById(long lineId);

	/**
	 * 根据传入的Id列表查询头条信息(供超级管理员选定删除头条的时候用，主要是处理图片)
	 * 
	 * @param lineIdList
	 * @return
	 */
	List<HeadLineDao> queryHeadLineByIds(List<Long> lineIdList);

	/**
	 * 插入头条
	 * 
	 * @param headLine
	 * @return
	 */
	int insertHeadLine(HeadLineDao headLine);

	/**
	 * 更新头条信息
	 * 
	 * @param headLine
	 * @return
	 */
	int updateHeadLine(HeadLineDao headLine);

	/**
	 * 删除头条
	 * 
	 * @param headLineId
	 * @return
	 */
	int deleteHeadLine(long headLineId);

	/**
	 * 
	 * @param lineIdList
	 * @return
	 */
	int batchDeleteHeadLine(List<Long> lineIdList);
}