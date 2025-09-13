package com.example.ocrtool.hotkey;

import com.example.ocrtool.ocr.OcrHandler;
import com.example.ocrtool.screenshot.ScreenSelectionWindow;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * GlobalHotkeyListener
 * <p>
 * 该类使用JNativeHook实现全局快捷键监听功能
 * 绑定的快捷键是 Ctrl+Shift+A，用于触发截图操作
 */
@Slf4j
public class GlobalHotkeyListener implements NativeKeyListener {
    // GlobalScreen是JNative的入口类
    private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());

    // 按下按键集合
    private final Set<Integer> pressedKeys = new HashSet<>();

    // 目标按键集合
    private final Set<Integer> targetKeys = new HashSet<>();

    public GlobalHotkeyListener(Set<Integer> targetKeys) {
        this.targetKeys.addAll(targetKeys);
    }

    /**
     * 按键释放触发事件
     *
     * @param nativeEvent 键盘事件
     */
    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        // 将按键加入Set集合
        pressedKeys.add(nativeEvent.getKeyCode());
        // 如果已按集合长度大于目标集合长度则清空，避免误触截图
        if (pressedKeys.size() > targetKeys.size()) {
            // 清空集合
            pressedKeys.clear();
        }
        // 如果全部按键都在集合中，则触发快捷键
        if (pressedKeys.containsAll(targetKeys)) {
            ScreenSelectionWindow window = null;
            try {
                // 创建屏幕对象
                window = new ScreenSelectionWindow();
                // 阻塞进程开始截屏
                Rectangle rectangle = window.select();
                // 识别内容
                String context = OcrHandler.identifyContext(rectangle);
                System.out.println(context);
            } catch (AWTException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            pressedKeys.remove(nativeEvent.getKeyCode());
        }
    }

    /**
     * 启动全局快捷键监听
     */
    public static void startHotKeyListener(Set<Integer> hotkey) {
        try {
            // 屏蔽日志
            logger.setLevel(Level.OFF);

            // 根据编码获取快捷键字符串
            String originHotkey = hotkey.stream()
                    .map(NativeKeyEvent::getKeyText)
                    .collect(Collectors.joining("+"));
            // 注册快捷键监听
            GlobalScreen.registerNativeHook();
            // 添加监听对象（只有hotkey才会触发nativeKeyReleased）
            GlobalScreen.addNativeKeyListener(new GlobalHotkeyListener(hotkey));

            log.info("😊全局快捷键监听已启动，您当前的快捷键是:" + originHotkey);
        } catch (NativeHookException e) {
            log.error("🤯全局快捷键监听启动失败", e);
        }
    }

    /**
     * 停止全局快捷键监听
     */
    public static void stopHotKeyListener() {
        try {
            GlobalScreen.unregisterNativeHook();

            log.info("😊全局快捷键监听已停止");
        } catch (NativeHookException e) {
            log.error("🤯停止全局快捷键监听失败", e);
        }
    }
}
