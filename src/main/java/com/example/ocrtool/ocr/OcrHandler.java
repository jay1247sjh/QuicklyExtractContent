package com.example.ocrtool.ocr;

import com.example.ocrtool.opencv.ImageOptimizationHandler;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.Size;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

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
            // 从resources加载tessdata目标路径
            URL tessDataUrl = OcrHandler.class.getClassLoader().getResource("tessdata");
            // 根据路径创建文件对象
            File tessDataFolder = new File(tessDataUrl.toURI());
            // 设置训练数据路径
            tesseract.setDatapath(tessDataFolder.getAbsolutePath());
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
            // 图片闭运算
            image.morphClose(new Size(2, 2));
            // 进行OCR识别
            return tesseract.doOCR(image.getBufferedImage());
        } catch (Exception e) {
            // 用于给上层捕获异常
            throw new RuntimeException("😒OCR失败");
        }
    }
}
