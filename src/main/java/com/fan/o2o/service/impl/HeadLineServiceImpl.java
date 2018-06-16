package com.fan.o2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fan.o2o.dao.HeadLineDao;
import com.fan.o2o.dto.HeadLineExecution;
import com.fan.o2o.dto.ImageHolder;
import com.fan.o2o.entity.HeadLine;
import com.fan.o2o.enums.HeadLineStateEnum;
import com.fan.o2o.exceptions.HeadLineOperationException;
import com.fan.o2o.service.HeadLineService;
import com.fan.o2o.util.ImageUtil;
import com.fan.o2o.util.PathUtil;

@Service
public class HeadLineServiceImpl implements HeadLineService {
	@Autowired
	private HeadLineDao headLineDao;

	@Override
	public List<HeadLine> getHeadLineList(HeadLine headLineCondition) {
		return headLineDao.queryHeadLine(headLineCondition);
	}
	
	@Override
	@Transactional
	public HeadLineExecution addHeadLine(HeadLine headLine, ImageHolder thumbnail) {
		// 空值判断
		if (headLine != null) {
			// 设定默认值
			headLine.setCreateDate(new Date());
			headLine.setLastEditDate(new Date());
			// 若传入的头条图片为非空，则存储图片并在实体类里将图片的相对路径设置上
			if (thumbnail != null) {
				addThumbnail(headLine, thumbnail);
			}
			try {
				// 往数据库里插入头条信息
				int effectedNum = headLineDao.insertHeadLine(headLine);
				if (effectedNum > 0) {
					//deleteRedis4HeadLine();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLine);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new HeadLineOperationException("添加区域信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public HeadLineExecution modifyHeadLine(HeadLine headLine, ImageHolder thumbnail) {
		// 空值判断，主要是判断头条Id是否为空
		if (headLine.getLineId() != null && headLine.getLineId() > 0) {
			// 设定默认值
			headLine.setLastEditDate(new Date());
			if (thumbnail != null) {
				// 若需要修改的图片流不为空，则需要处理现有的图片
				HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLine.getLineId());
				if (tempHeadLine.getLineImg() != null) {
					// 若原来是存储有图片的，则将原先图片删除
					ImageUtil.deleteFileOrPath(tempHeadLine.getLineImg());
				}
				// 添加新的图片，并将新的图片相对路径设置到实体类里
				addThumbnail(headLine, thumbnail);
			}
			try {
				// 更新头条信息
				int effectedNum = headLineDao.updateHeadLine(headLine);
				if (effectedNum > 0) {
					//deleteRedis4HeadLine();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLine);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new HeadLineOperationException("更新头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public HeadLineExecution removeHeadLine(long headLineId) {
		// 空值判断，主要判断头条Id是否为非空
		if (headLineId > 0) {
			try {
				// 根据Id查询头条信息
				HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLineId);
				if (tempHeadLine.getLineImg() != null) {
					// 若头条原先存有图片，则将该图片文件删除
					ImageUtil.deleteFileOrPath(tempHeadLine.getLineImg());
				}
				// 删除数据库里对应的头条信息
				int effectedNum = headLineDao.deleteHeadLine(headLineId);
				if (effectedNum > 0) {
					//deleteRedis4HeadLine();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new HeadLineOperationException("删除头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public HeadLineExecution removeHeadLineList(List<Long> headLineIdList) {
		// 空值判断
		if (headLineIdList != null && headLineIdList.size() > 0) {
			try {
				// 根据传入的id列表获取头条列表
				List<HeadLine> headLineList = headLineDao.queryHeadLineByIds(headLineIdList);
				for (HeadLine headLine : headLineList) {
					// 遍历头条列表，若头条的图片非空，则将图片删除
					if (headLine.getLineImg() != null) {
						ImageUtil.deleteFileOrPath(headLine.getLineImg());
					}
				}
				// 批量删除数据库中的头条信息
				int effectedNum = headLineDao.batchDeleteHeadLine(headLineIdList);
				if (effectedNum > 0) {
					//deleteRedis4HeadLine();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new HeadLineOperationException("删除头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	/**
	 * 存储图片
	 * 
	 * @param headLine
	 * @param thumbnail
	 */
	private void addThumbnail(HeadLine headLine, ImageHolder thumbnail) {
		String dest = PathUtil.getHeadLineImagePath();
		String thumbnailAddr = ImageUtil.generateNormalImg(thumbnail, dest);
		headLine.setLineImg(thumbnailAddr);
	}

	/**
	 * 移除跟实体类相关的redis key-value
	 */
	/*private void deleteRedis4HeadLine() {
		String prefix = HLLISTKEY;
		// 获取跟头条相关的redis key
		Set<String> keySet = jedisKeys.keys(prefix + "*");
		for (String key : keySet) {
			// 逐条删除
			jedisKeys.del(key);
		}
	}*/

}
