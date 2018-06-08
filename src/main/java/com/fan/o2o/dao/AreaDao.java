package com.fan.o2o.dao;

import java.util.List;

import com.fan.o2o.entity.Area;

public interface AreaDao {
	/**
	 * 返回地域的列表
	 * @return areaList
	 */
	List<Area> queryArea();
}
