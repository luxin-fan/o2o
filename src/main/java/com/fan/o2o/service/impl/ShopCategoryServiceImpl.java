package com.fan.o2o.service.impl;

import java.util.Date;
import java.util.List;
//import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fan.o2o.dao.ShopCategoryDao;
import com.fan.o2o.dto.ImageHolder;
import com.fan.o2o.dto.ShopCategoryExecution;
import com.fan.o2o.entity.ShopCategory;
import com.fan.o2o.enums.ShopCategoryStateEnum;
import com.fan.o2o.exceptions.ShopCategoryOperationException;
import com.fan.o2o.service.ShopCategoryService;
import com.fan.o2o.util.ImageUtil;
import com.fan.o2o.util.PathUtil;

@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {
	@Autowired
	private ShopCategoryDao shopCategoryDao;

	@Override
	public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {
		return shopCategoryDao.queryShopCategory(shopCategoryCondition);
	}
	
	@Override
	@Transactional
	public ShopCategoryExecution addShopCategory(ShopCategory shopCategory, ImageHolder thumbnail) {
		// 空值判断
		if (shopCategory != null) {
			// 设定默认值
			shopCategory.setCreateDate(new Date());
			shopCategory.setLastEditDate(new Date());
			if (thumbnail != null) {
				// 若上传有图片流，则进行存储操作，并给shopCategory实体类设置上相对路径
				addThumbnail(shopCategory, thumbnail);
			}
			try {
				// 往数据库添加店铺类别信息
				int effectedNum = shopCategoryDao.insertShopCategory(shopCategory);
				if (effectedNum > 0) {
					// 删除店铺类别之前在redis里存储的一切key,for简单实现
					//deleteRedis4ShopCategory();
					return new ShopCategoryExecution(ShopCategoryStateEnum.SUCCESS, shopCategory);
				} else {
					return new ShopCategoryExecution(ShopCategoryStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new ShopCategoryOperationException("添加店铺类别信息失败:" + e.toString());
			}
		} else {
			return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory, ImageHolder thumbnail) {
		// 空值判断，主要判断shopCategoryId不为空
		if (shopCategory.getShopCategoryId() != null && shopCategory.getShopCategoryId() > 0) {
			// 设定默认值
			shopCategory.setLastEditDate(new Date());
			if (thumbnail != null) {
				// 若上传的图片不为空，则先获取之前的图片路径
				ShopCategory tempShopCategory = shopCategoryDao.queryShopCategoryById(shopCategory.getShopCategoryId());
				if (tempShopCategory.getShopCategoryImg() != null) {
					// 若之前图片不为空，则先移除之前的图片
					ImageUtil.deleteFileOrPath(tempShopCategory.getShopCategoryImg());
				}
				// 存储新的图片
				addThumbnail(shopCategory, thumbnail);
			}
			try {
				// 更新数据库信息
				int effectedNum = shopCategoryDao.updateShopCategory(shopCategory);
				if (effectedNum > 0) {
					// 删除店铺类别之前在redis里存储的一切key,for简单实现
					//deleteRedis4ShopCategory();
					return new ShopCategoryExecution(ShopCategoryStateEnum.SUCCESS, shopCategory);
				} else {
					return new ShopCategoryExecution(ShopCategoryStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new ShopCategoryOperationException("更新店铺类别信息失败:" + e.toString());
			}
		} else {
			return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
		}
	}

	/**
	 * 存储图片
	 * 
	 * @param shopCategory
	 * @param thumbnail
	 */
	private void addThumbnail(ShopCategory shopCategory, ImageHolder thumbnail) {
		String dest = PathUtil.getShopCategoryPath();
		String thumbnailAddr = ImageUtil.generateNormalImg(thumbnail, dest);
		shopCategory.setShopCategoryImg(thumbnailAddr);
	}

	/**
	 * 移除跟实体类相关的redis key-value
	 */
	/*private void deleteRedis4ShopCategory() {
		String prefix = SCLISTKEY;
		// 获取跟店铺类别相关的redis key
		Set<String> keySet = jedisKeys.keys(prefix + "*");
		for (String key : keySet) {
			// 逐条删除
			jedisKeys.del(key);
		}
	}*/

	@Override
	public ShopCategory getShopCategoryById(Long shopCategoryId) {
		return shopCategoryDao.queryShopCategoryById(shopCategoryId);
	}
}
