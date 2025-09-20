package com.example.ocrtool.ui;

import javax.swing.*;

/**
 * GUI图形化工具类
 */
public class GUIUtils {

    /**
     * 将文字内容以GUI的形式展示出来
     */
    public static void contentShow(String content) {
        // 创建文本区域
        JTextArea textArea = new JTextArea(content);
        // 自动换行
        textArea.setLineWrap(true);
        // 单词中间不中断
        textArea.setWrapStyleWord(true);
        // 设置为不可编辑
        textArea.setEditable(false);
        // 自动支持 Ctrl+C / 右键菜单
        JScrollPane scrollPane = new JScrollPane(textArea);
        // 创建可视化窗口
        JFrame frame = new JFrame();
        // 只关闭窗口，不退出程序
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 把滚动面板加到窗口里
        frame.add(scrollPane);
        // 设置尺寸
        frame.setSize(400, 200);
        // 设置为窗口位置为屏幕中央
        frame.setLocationRelativeTo(null);
        // 让窗口可见
        frame.setVisible(true);
    }
}
