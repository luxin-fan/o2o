package com.fan.o2o.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fan.o2o.entity.Area;
import com.fan.o2o.service.AreaService;

@Controller
//spring容器中的一个controller
@RequestMapping("/superadmin")
//路由相关，如果调用controller下的相关方法，必须在上面所写的路径下
public class AreaController {
	Logger logger = LoggerFactory.getLogger(AreaController.class);

	@Autowired
	private AreaService areaService;

	//superadmin目录下的方法路径
	/**
	 * get和post区别：
	 * post相对安全，get传参都会反映到url上
	 * */
	@RequestMapping(value = "/listarea", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listArea() {
		logger.info("===start===");
		long startTime = System.currentTimeMillis();
		Map<String, Object> modelMap = new HashMap<String, Object>(); //不可重複
		List<Area> list = new ArrayList<Area>(); //访问service层返回的列表
		try {
			list = areaService.getAreaList();
			//由于超管界面用到的easyUI框架，rows表示返回的集合，total表示总数
			modelMap.put("rows", list);
			modelMap.put("total", list.size());
		}
		catch (Exception e) {
			e.printStackTrace();
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
		}
		logger.error("test error!");
		long endTime = System.currentTimeMillis();
		logger.debug("costTime:[{}ms]", endTime - startTime);
		logger.info("===end===");
		return modelMap;
	}
}
