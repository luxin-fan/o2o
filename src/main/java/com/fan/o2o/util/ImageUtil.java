package com.fan.o2o.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fan.o2o.dto.ImageHolder;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtil {
	//private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static String basePath = PathUtil.getImgBasePath();
	
	private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private static final Random r = new Random();

	private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

	/**
	 * 将CommonsMultipartFile类型转换成为File类型，作为处理缩略图的参数
	 * @param cFile
	 * @return
	 * */
	public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile) {
		File newFile = new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);//写入File流
		}
		catch (IllegalStateException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return newFile;
	}

	/**
	 * 处理缩略图（门面照和商品小图）
	 * @param thumbnail 用户传递的文件流
	 * @param targetAddr 文件保存的地址
	 * */
	public static String generateThumbnail(ImageHolder thumbnail, String targetAddr) {
		//用户传递过来的可能有很多重名文件 所以需要内部修改为随机文件名
		String realFileName = getRandomFileName();
		//获得后缀名
		String extension = getFileExtension(thumbnail.getImageName());
		//有时候传递的目录不存在
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr + realFileName + extension;
		logger.debug("current relativeAddr is: " + relativeAddr);
		File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
		logger.debug("current complete addr is: " + PathUtil.getImgBasePath() + relativeAddr);
		//创建缩略图
		try {
			Thumbnails.of(thumbnail.getImage()).size(200, 200).watermark(Positions.BOTTOM_RIGHT,
					ImageIO.read(new File(basePath + "/watermark.jpg")), 0.25f).outputQuality(0.8f).toFile(dest);
		}
		catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		//我们数据库表中有一个字段shopimg就是存储的图片的相对地址，迁移系统后也可以通过getImgBasePath方法拼接出绝对路径
		return relativeAddr;
	}

	/**
	 * 处理详情图，并返回新生成图片的相对值路径
	 * 
	 * @param thumbnail
	 * @param targetAddr
	 * @return
	 */
	public static String generateNormalImg(ImageHolder thumbnail, String targetAddr) {
		// 获取不重复的随机名
		String realFileName = getRandomFileName();
		// 获取文件的扩展名如png,jpg等
		String extension = getFileExtension(thumbnail.getImageName());
		// 如果目标路径不存在，则自动创建
		makeDirPath(targetAddr);
		// 获取文件存储的相对路径(带文件名)
		String relativeAddr = targetAddr + realFileName + extension;
		logger.debug("current relativeAddr is :" + relativeAddr);
		// 获取文件要保存到的目标路径
		File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
		logger.debug("current complete addr is :" + PathUtil.getImgBasePath() + relativeAddr);
		// 调用Thumbnails生成带有水印的图片
		try {
			Thumbnails.of(thumbnail.getImage()).size(337, 640).watermark(Positions.BOTTOM_RIGHT,
					ImageIO.read(new File(basePath + "/watermark.jpg")), 0.25f).outputQuality(0.9f).toFile(dest);
		}
		catch (IOException e) {
			logger.error(e.toString());
			throw new RuntimeException("创建缩图片失败：" + e.toString());
		}
		// 返回图片相对路径地址
		return relativeAddr;
	}

	/**
	 * 创建目标路径所涉及到的目录，即/home/fanlux/image/xxx.jpg
	 * 那么home fanlux image这些文件夹都要自动的创建出来
	 * @param targetAddr
	 * */
	private static void makeDirPath(String targetAddr) {
		String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
		File dirPath = new File(realFileParentPath);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}

	/**
	 * 获得输入文件流的扩展名(.jpg/.png)
	 * @param thumbnail
	 * @return
	 * */
	private static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 生成随机文件名，当前年月日小时分秒+五位随机数
	 * @return
	 * */
	public static String getRandomFileName() {
		//获取随机的五位数(10000-99999)
		int rannum = r.nextInt(89999) + 10000;
		String nowTimeStr = sDateFormat.format(new Date());
		return nowTimeStr + rannum;
	}

	public static void main(String[] args) throws IOException {
		/*	System.out.println(basePath);
			System.out.println(Thread.currentThread().getContextClassLoader());
			System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));*/
		/**
		 * 传入需要处理的文件
		 * arg1则是水印图片的路径，此时需要读取到classpath下的该图片的绝对路径
		 * 由于该方法是线程执行，所以我们可以通过线程逆推到其类加载器，找到资源的路径
		 * arg2为透明度，然后压缩图片，最后定义输出地点
		 * */
		Thumbnails.of(new File("C:/baidu/work/image/kobe.jpg")).size(200, 200).watermark(Positions.BOTTOM_RIGHT,
				ImageIO.read(new File(basePath + "/watermark.jpg")), 0.25f).outputQuality(0.8f).toFile(
						"C:/baidu/work/image/newkobe.jpg");
	}

	/**
	 * storePath是文件的路径还是目录的路径， 如果storePath是文件路径则删除该文件，
	 * 如果storePath是目录路径则删除该目录下的所有文件
	 * 
	 * @param storePath
	 */
	public static void deleteFileOrPath(String storePath) {
		File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
		if (fileOrPath.exists()) {
			if (fileOrPath.isDirectory()) {
				File files[] = fileOrPath.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
			fileOrPath.delete();
		}
	}
}
