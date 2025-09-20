package com.example.ocrtool.ocr;

import com.example.ocrtool.opencv.ImageOptimizationHandler;
import com.example.ocrtool.utils.PathDiagnostic;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.Size;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * OcrHandler
 */
@Slf4j
public class OcrHandler {

    private static final ITesseract tesseract;

    static {
        // 创建OCR对象
        tesseract = new Tesseract();
        try {
            // 设置训练数据路径
            tesseract.setDatapath(PathDiagnostic.getTessDataPath());
            // 设置中文
            tesseract.setLanguage("chi_sim");
        } catch (Exception e) {
            throw new RuntimeException("😭初始化OCR失败");
        }
    }

    /**
     * 识别内容
     */
    public static String identifyContext(Rectangle rectangle) throws AWTException {
        Robot robot = new Robot();
        try {
            // 创建屏幕捕获对象
            BufferedImage captureImage = robot.createScreenCapture(rectangle);
            // 创建图片优化对象
            ImageOptimizationHandler image = new ImageOptimizationHandler(captureImage);
            // 转化为灰度图
            image.toGray();
            // 高斯去噪
            image.denoise(new Size(3, 3));
            // 进行OCR识别
            return tesseract.doOCR(captureImage);
        } catch (Exception e) {
            e.printStackTrace();
            // 用于给上层捕获异常
            throw new RuntimeException("😒OCR失败");
        }
    }
}
