package com.example.ocrtool.config;

import com.example.ocrtool.Main;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

/**
 * KeyMapping 类
 * <p>
 * 用于集中管理常见键盘按键的常量映射
 */
public final class KeyMapping {

    // 工具类不允许实例化
    private KeyMapping() {

    }

    // ================== 功能键 ==================
    public static final int CTRL = NativeKeyEvent.VC_CONTROL;
    public static final int SHIFT = NativeKeyEvent.VC_SHIFT;
    public static final int ALT = NativeKeyEvent.VC_ALT;
    public static final int META = NativeKeyEvent.VC_META; // Mac 上对应 Command

    // ================== 字母键 ==================
    public static final int A = NativeKeyEvent.VC_A;
    public static final int B = NativeKeyEvent.VC_B;
    public static final int C = NativeKeyEvent.VC_C;
    public static final int D = NativeKeyEvent.VC_D;
    public static final int E = NativeKeyEvent.VC_E;
    public static final int F = NativeKeyEvent.VC_F;
    public static final int G = NativeKeyEvent.VC_G;
    public static final int H = NativeKeyEvent.VC_H;
    public static final int I = NativeKeyEvent.VC_I;
    public static final int J = NativeKeyEvent.VC_J;
    public static final int K = NativeKeyEvent.VC_K;
    public static final int L = NativeKeyEvent.VC_L;
    public static final int M = NativeKeyEvent.VC_M;
    public static final int N = NativeKeyEvent.VC_N;
    public static final int O = NativeKeyEvent.VC_O;
    public static final int P = NativeKeyEvent.VC_P;
    public static final int Q = NativeKeyEvent.VC_Q;
    public static final int R = NativeKeyEvent.VC_R;
    public static final int S = NativeKeyEvent.VC_S;
    public static final int T = NativeKeyEvent.VC_T;
    public static final int U = NativeKeyEvent.VC_U;
    public static final int V = NativeKeyEvent.VC_V;
    public static final int W = NativeKeyEvent.VC_W;
    public static final int X = NativeKeyEvent.VC_X;
    public static final int Y = NativeKeyEvent.VC_Y;
    public static final int Z = NativeKeyEvent.VC_Z;

    // ================== 数字键（主键盘区） ==================
    public static final int NUM_0 = NativeKeyEvent.VC_0;
    public static final int NUM_1 = NativeKeyEvent.VC_1;
    public static final int NUM_2 = NativeKeyEvent.VC_2;
    public static final int NUM_3 = NativeKeyEvent.VC_3;
    public static final int NUM_4 = NativeKeyEvent.VC_4;
    public static final int NUM_5 = NativeKeyEvent.VC_5;
    public static final int NUM_6 = NativeKeyEvent.VC_6;
    public static final int NUM_7 = NativeKeyEvent.VC_7;
    public static final int NUM_8 = NativeKeyEvent.VC_8;
    public static final int NUM_9 = NativeKeyEvent.VC_9;

    // ================== 功能键 ==================
    public static final int F1 = NativeKeyEvent.VC_F1;
    public static final int F2 = NativeKeyEvent.VC_F2;
    public static final int F3 = NativeKeyEvent.VC_F3;
    public static final int F4 = NativeKeyEvent.VC_F4;
    public static final int F5 = NativeKeyEvent.VC_F5;
    public static final int F6 = NativeKeyEvent.VC_F6;
    public static final int F7 = NativeKeyEvent.VC_F7;
    public static final int F8 = NativeKeyEvent.VC_F8;
    public static final int F9 = NativeKeyEvent.VC_F9;
    public static final int F10 = NativeKeyEvent.VC_F10;
    public static final int F11 = NativeKeyEvent.VC_F11;
    public static final int F12 = NativeKeyEvent.VC_F12;

    // ================== 方向键 ==================
    public static final int UP = NativeKeyEvent.VC_UP;
    public static final int DOWN = NativeKeyEvent.VC_DOWN;
    public static final int LEFT = NativeKeyEvent.VC_LEFT;
    public static final int RIGHT = NativeKeyEvent.VC_RIGHT;

    // ================== 其他常用键 ==================
    public static final int ESCAPE = NativeKeyEvent.VC_ESCAPE;
    public static final int TAB = NativeKeyEvent.VC_TAB;
    public static final int ENTER = NativeKeyEvent.VC_ENTER;
    public static final int BACKSPACE = NativeKeyEvent.VC_BACKSPACE;
    public static final int SPACE = NativeKeyEvent.VC_SPACE;
    public static final int DELETE = NativeKeyEvent.VC_DELETE;
    public static final int INSERT = NativeKeyEvent.VC_INSERT;
    public static final int HOME = NativeKeyEvent.VC_HOME;
    public static final int END = NativeKeyEvent.VC_END;
    public static final int PAGE_UP = NativeKeyEvent.VC_PAGE_UP;
    public static final int PAGE_DOWN = NativeKeyEvent.VC_PAGE_DOWN;

    // 从config文件读取热键配置并转化成快捷键集合
    public static Set<Integer> loadHotkey() {
        try {
            // 创建配置文件对象
            Properties props = new Properties();
            // 从类路径（target/classes/）查找文件并进行加载
            try (InputStream in = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (in != null) {
                    props.load(in);
                }
            }
            // 设置默认热键为CTRL+SHIFT+A
            String hotkeyStr = props.getProperty("screenshot.hotkey", "CTRL+SHIFT+A");
            // 解析热键
            return parseHotkeyStr(hotkeyStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 解析热键
    private static Set<Integer> parseHotkeyStr(String hotkeyStr) {
        // 创建集合用于收集按键
        HashSet<Integer> keySet = new HashSet<>();
        // 如果按键为空则直接
        if (hotkeyStr == null || hotkeyStr.isEmpty()) {
            return keySet;
        }
        // 将字符串按照+号进行切割
        String[] parts = hotkeyStr.toUpperCase().split("\\+");
        // 对每一个按键进行映射处理
        Stream.of(parts).forEach((part) -> {
            try {
                // 通过反射机制获取按键编码
                int keyCode = (int) KeyMapping.class.getField(part.trim()).get(null);
                // 将编码添加到集合
                keySet.add(keyCode);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
        // 将处理好的结果返回
        return keySet;
    }
}
