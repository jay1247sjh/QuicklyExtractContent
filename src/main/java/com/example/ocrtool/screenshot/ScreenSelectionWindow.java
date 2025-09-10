package com.example.ocrtool.screenshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

/**
 * ScreenSelectionWindow
 * <p>
 * 全屏透明窗口，用于让用户框选截图区域
 * 注意这里如果继承JWindow可能会因为没有边框和标题栏而被拒绝获取焦点，导致ESC无法生效
 */
public class ScreenSelectionWindow extends JFrame {
    // 鼠标拖拽的起点和终点
    private Point start, end;

    // 用户最终选择的矩形区域
    private Rectangle selection;

    // 用于阻塞主线程，直到用户选择完成
    private final CountDownLatch latch = new CountDownLatch(1);

    // 背景图
    private final BufferedImage background;

    public ScreenSelectionWindow() throws AWTException {
        // 去掉标题栏
        setUndecorated(true);
        // 设置窗口大小为整个虚拟显示器（支持多显示器）
        setBounds(getVirtualBounds());
        // 窗口始终置顶
        setAlwaysOnTop(true);
        // 设置光标为十字准星
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        // 设置JPanel本身颜色为透明，确保不遮挡内容
        setBackground(new Color(0, 0, 0, 0));


        // 截图作为背景
        Robot robot = new Robot();
        // 设为背景
        background = robot.createScreenCapture(getVirtualBounds());

        // 透明背景+自定义绘制图形
        JPanel jPanel = new JPanel() {
            // repaint时触发
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 绘制背景
                drawOverlay((Graphics2D) g);
            }
        };
        // 设置背景透明
        jPanel.setOpaque(false);
        // 设置内容面板
        setContentPane(jPanel);

        // 鼠标事件监听（框选）
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 鼠标按下时记录起点
                start = e.getLocationOnScreen();
                end = start;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 右键鼠标取消选择
                if (SwingUtilities.isRightMouseButton(e)) {
                    selection = null;
                } else {
                    // 根据起点和终点计算矩形区域
                    int x = Math.min(start.x, end.x);
                    int y = Math.min(start.y, end.y);
                    int w = Math.abs(start.x - end.x);
                    int h = Math.abs(start.y - end.y);
                    if (w > 0 && h > 0) {
                        selection = new Rectangle(x, y, w, h);
                    }
                }
                // 取消阻塞，选择完成
                latch.countDown();
                // 关闭窗口
                dispose();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // 鼠标拖动时更新终点
                end = e.getLocationOnScreen();
                repaint();
            }
        };
        // 监听鼠标点击、按下、释放事件
        jPanel.addMouseListener(mouseAdapter);
        // 监听鼠标移动和拖拽事件
        jPanel.addMouseMotionListener(mouseAdapter);

        // 键盘监听（ESC键取消选择）
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                // 用户按ESC则取消
                selection = null;
                // 通知操作完成
                if (latch.getCount() > 0) {
                    latch.countDown();
                }
                // 关闭界面
                dispose();
            }
            return false;
        });
    }

    /**
     * 绘制遮罩区和矩形选区
     */
    private void drawOverlay(Graphics2D graphics2D) {
        // 绘制屏幕截图为背景
        graphics2D.drawImage(background, 0, 0, null);
        // 绘制半透明黑色遮罩
        graphics2D.setColor(new Color(0, 0, 0, 50));
        // 设置黑色遮罩覆盖区域
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        // 用户正在拖拽
        if (start != null && end != null) {
            // getX和getY是相对于父容器左上角的x和y坐标
            int x = Math.min(start.x, end.x) - getX();
            int y = Math.min(start.y, end.y) - getY();
            int width = Math.abs(start.x - end.x);
            int height = Math.abs(start.y - end.y);

            // 绘制白色半透明区域
            graphics2D.setColor(new Color(255, 255, 255, 30));
            graphics2D.fillRect(x, y, width, height);
            // 绘制红色边框
            graphics2D.setColor(Color.RED);
            graphics2D.drawRect(x, y, width, height);
        }
    }

    // 阻塞方法，等待用户选择完成后返回矩形区域
    public Rectangle select() throws InterruptedException {
        // 由于Swing是单线程UI框架，所以这里要把任务放到事件分发现次（EDT）上异步执行，在EDT上显示ScreenSelectionWindow窗口
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            // 强制获取焦点
            requestFocus();
        });
        // 阻塞，直到用户完成操作
        latch.await();
        // 返回矩形区域
        return selection;
    }

    // 获取虚拟屏幕边界
    private static Rectangle getVirtualBounds() {
        // 用来存储虚拟桌面的总范围，初始化为空矩形
        Rectangle bounds = new Rectangle();
        // 获取图形环境
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // 遍历系统中所有物理屏幕（GraphicsDevice代表一个显示器）
        for (GraphicsDevice device : ge.getScreenDevices()) {
            // 每个屏幕可能有多个配置（分辨率/色深/DPI）
            for (GraphicsConfiguration config : device.getConfigurations()) {
                // 将该配置的边界与已有的虚拟范围合并
                bounds = bounds.union(config.getBounds());
            }
        }
        // 返回合并结果
        return bounds;
    }
}
