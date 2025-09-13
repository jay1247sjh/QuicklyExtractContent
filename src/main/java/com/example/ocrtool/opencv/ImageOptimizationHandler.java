package com.example.ocrtool.opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * ImageOptimizationHandler
 * <p>
 * 如果图片类型是 3BYTE_BGR（底层就是 BGR），直接用底层字节
 * 如果不是则通过 getRGB -> ARGB int -> 填充 BGR bytes
 */
public class ImageOptimizationHandler {
    // Mat格式对象
    private Mat mat;

    static {
        System.load(System.getProperty("user.dir") + "/lib/opencv_java4120.dll");
    }

    // 构造图片对象
    public ImageOptimizationHandler(BufferedImage bufferedImage) {
        // 转化格式
        this.mat = this.bufferedImageToMat(bufferedImage);
    }

    /**
     * 将BufferedImage转化成OpenCV的Mat格式
     */
    private Mat bufferedImageToMat(BufferedImage bufferedImage) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        // 如果已经是3BYTE_BGR，直接用底层字节
        if (bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            Mat mat = new Mat(height, width, CvType.CV_8UC3);
            mat.put(0, 0, pixels);
            return mat;
        }

        // 创建数组存放像素点
        int[] argbPixels = new int[width * height];
        // 将图像像素按行打包到数组（一个int四字节，每个字节分别代表ARGB）
        bufferedImage.getRGB(0, 0, width, height, argbPixels, 0, width);

        // 为最终放入Mat字节数组分配空间（3字节/像素）
        byte[] pixelsBGR = new byte[width * height * 3];
        // 把RGB放入bytes
        for (int i = 0; i < argbPixels.length; i++) {
            int argb = argbPixels[i];
            int base = i * 3;
            pixelsBGR[base] = (byte) (argb & 0xFF);        // B
            pixelsBGR[base + 1] = (byte) ((argb >> 8) & 0xFF); // G
            pixelsBGR[base + 2] = (byte) ((argb >> 16) & 0xFF);// R
        }

        // 创建一个OpenCV Mat，CV_8UC3表示每个像素3个通道，每通道8bit无符号
        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        mat.put(0, 0, pixelsBGR);
        return mat;
    }

    /**
     * 普通彩色图（BGR三通道） → 灰度图（单通道）
     */
    public void toGray() {
        Mat gray = new Mat();
        // 如果是彩色图则进行转换
        if (this.mat.channels() > 1) {
            // 转化成灰度图
            Imgproc.cvtColor(this.mat, gray, Imgproc.COLOR_BGR2GRAY);
            this.mat = gray;
        }
    }

    /**
     * 高斯模糊去噪
     */
    public void denoise(Size ksize) {
        Mat blurred = new Mat();
        // 去噪
        Imgproc.GaussianBlur(this.mat, blurred, ksize, 0);
        this.mat = blurred;
    }

    /**
     * Otsu二极化
     */
    public void binarize() {
        Mat binary = new Mat();
        // 二极化处理
        Imgproc.threshold(this.mat, binary, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        this.mat = binary;
    }

    /**
     * 去噪点+填补断裂
     */
    public void morphClose(Size size) {
        Mat morphed = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
        Imgproc.morphologyEx(this.mat, morphed, Imgproc.MORPH_CLOSE, kernel);
        this.mat = morphed;
    }

    /**
     * 获取BufferedImage对象
     */
    public BufferedImage getBufferedImage() {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        // 拷贝数据
        mat.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}
