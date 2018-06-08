package com.fan.o2o.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathUtil {
	private static String seperator = System.getProperty("file.separator");

	private static String winPath;

	private static String linuxPath;

	private static String shopPath;

	private static String headLinePath;

	private static String shopCategoryPath;

	@Value("${win.base.path}")
	public void setWinPath(String winPath) {
		PathUtil.winPath = winPath;
	}

	@Value("${linux.base.path}")
	public void setLinuxPath(String linuxPath) {
		PathUtil.linuxPath = linuxPath;
	}

	@Value("${shop.relevant.path}")
	public void setShopPath(String shopPath) {
		PathUtil.shopPath = shopPath;
	}

	@Value("${headline.relevant.path}")
	public void setHeadLinePath(String headLinePath) {
		PathUtil.headLinePath = headLinePath;
	}

	@Value("${shopcategory.relevant.path}")
	public void setShopCategoryPath(String shopCategoryPath) {
		PathUtil.shopCategoryPath = shopCategoryPath;
	}

	public static String getImgBasePath() {
		String os = System.getProperty("os.name");
		String basePath = "";
		if (os.toLowerCase().startsWith("win")) {
			/**
			 * 为什么不将根路径设置在classpath下，这样就可以不用指定绝对路径？
			 * 一旦将图片保存在根目录底下，项目在重新部署的时候，那些新生成的图片文件都会被删除，除非一开始图片就保存在classpath下，
			 * 但是这个是不可能的，因为随着用户使用，是不断要上传图片的，所以我们要将根路径设置在工程路径之外，以防止被自动删除。这里一般是将图片 保存到另外一台服务器。
			 */
			basePath = winPath;
		} else {
			basePath = linuxPath;
		}
		basePath = basePath.replace("/", seperator);
		return basePath;
	}

	public static String getShopImagePath(long shopId) {
		String imagePath = shopPath + shopId + seperator;
		return imagePath.replace("/", seperator);
	}

	public static String getHeadLineImagePath() {
		return headLinePath.replace("/", seperator);
	}

	public static String getShopCategoryPath() {
		return shopCategoryPath.replace("/", seperator);
	}
}
