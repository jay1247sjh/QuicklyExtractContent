package com.example.ocrtool.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 * PathDiagnostic
 * <p>
 * 这个类的主要作用就是用于路径诊断
 * 因为这个程序跑起来需要引入外部资源tessdata和opencv的dll，这个类可以帮你诊断是否已经拥有了相关资源
 */
public class PathDiagnostic {

    private static String productJarParentPath = null;

    /**
     * 获取环境
     */
    private static String getEnv() {
        // 用于读取配置文件
        Properties properties = new Properties();
        try (InputStream input = PathDiagnostic.class.getClassLoader().getResourceAsStream("application.properties")) {
            // 读取配置文件
            properties.load(input);
            // 获取环境属性
            return properties.getProperty("application.env", "dev");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 环境诊断
     */
    public static void printPaths() {
        System.out.println("=== 程序诊断信息 ===");

        System.out.println("当前环境：" + getEnv());

        if (getEnv().equals("dev")) {
            // 当前工作目录
            System.out.println("当前工作目录: " + System.getProperty("user.dir"));

            // 判断语料包是否存在
            File tessdataDir = new File(System.getProperty("user.dir"), "target/classes/tessdata");

            System.out.println("tessdata (开发环境): " + tessdataDir.getAbsolutePath() + " - 存在: " + tessdataDir.exists());

            // 判断OpenCV的DLL是否存在
            File dllDev = new File("lib/opencv_java4120.dll");
            // 加载dll
            System.load(dllDev.getAbsolutePath());

            System.out.println("OpenCV DLL (开发环境): " + dllDev.getAbsolutePath() + " - 存在: " + dllDev.exists());
        } else {
            // jar包路径
            String jarPath = PathDiagnostic.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();

            System.out.println("Jar包路径: " + jarPath);

            // jar包所在位置
            File jarFile = new File(jarPath);
            // 获取父级目录
            File jarDir = jarFile.getParentFile();
            // 后面分发路径的时候使用
            productJarParentPath = jarDir.getAbsolutePath();

            System.out.println("Jar包所在目录: " + productJarParentPath);

            File tessdataPro = new File(jarDir, "tessdata");

            System.out.println("tessdata (jar包生产环境): " + tessdataPro.getAbsolutePath() + " - 存在: " + tessdataPro.exists());

            // 检查OpenCV DLL
            File dllProd = new File(jarDir, "lib/opencv_java4120.dll");
            // 加载dll
            System.load(dllProd.getAbsolutePath());

            System.out.println("OpenCV DLL (jar包生产环境): " + dllProd.getAbsolutePath() + " - 存在: " + dllProd.exists());
        }

        System.out.println("=== 诊断结束 ===");
    }

    /**
     * 获取tessdata路径
     */
    public static String getTessDataPath() throws URISyntaxException {
        if (getEnv().equals("dev")) {
            // 获得类加载器
            ClassLoader classLoader = PathDiagnostic.class.getClassLoader();
            // 获取tessdata文件夹
            URL tessdataURL = classLoader.getResource("tessdata");
            // 获取目标文件夹的绝对路径
            return new File(tessdataURL.toURI()).getAbsolutePath();
        } else {
            return productJarParentPath + File.separator + "tessdata";
        }
    }
}