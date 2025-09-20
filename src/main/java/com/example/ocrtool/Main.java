package com.example.ocrtool;

import com.example.ocrtool.config.KeyMapping;
import com.example.ocrtool.hotkey.GlobalHotkeyListener;
import com.example.ocrtool.utils.PathDiagnostic;

import java.net.URISyntaxException;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        // 添加路径诊断
        PathDiagnostic.printPaths();
        
        // 获取快捷键编码
        Set<Integer> hotkey = KeyMapping.loadHotkey();
        // 注册快捷键
        GlobalHotkeyListener.startHotKeyListener(hotkey);
    }
}
