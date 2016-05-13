import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by houseyoung on 16/5/11 23:02.
 */
public class GUI {
    // 设置城市数量
    int cityNum = 48;

    // 设置TSP数据文件地址
    String tspData = System.getProperty("user.dir") + "/resources/att48.txt";

    int[] bestTour; // 最佳路径
    int bestLength; // 最佳长度
    private int[] x = new int[cityNum]; // X坐标矩阵
    private int[] y = new int[cityNum]; // Y坐标矩阵

    // 定义界面上的元素
    private JButton start;
    private JPanel jPanel;
    private JPanel displayPanel;
    private JLabel antNum;
    private JLabel generation;
    private JLabel alpha;
    private JLabel beta;
    private JLabel rho;
    private JLabel Q;
    private JLabel deltaTypeLabel;
    private JLabel bestLengthLabel;
    private JTextField antNumText;
    private JTextField QText;
    private JTextField generationText;
    private JTextField alphaText;
    private JTextField betaText;
    private JTextField rhoText;
    private JComboBox deltaTypeComboBox;

    // 判断是否点击过了"开始"按钮
    private Boolean isStarted = false;

    private GUICanvas guiCanvas;

    public GUI() {
        // 初始化显示城市折线图的Panel
        displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());

        // 初始化画城市折线图的Canvas，并将其添加到displayPanel上
        guiCanvas = new GUICanvas();
        displayPanel.add(guiCanvas);

        // 从文件中获取X坐标矩阵、Y坐标矩阵
        try {
            x = ReadFile.getX(cityNum, tspData);
            y = ReadFile.getY(cityNum, tspData);
            for (int i = 0; i < cityNum; i++) {
                x[i] += 30;// X坐标加30以保证可以在界面内显示
                y[i] += 200;// Y坐标加200以保证可以在界面内显示
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 点击"开始"按钮后进行的操作
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 从输入框中获取参数
                    int antNum = Integer.parseInt(antNumText.getText().trim());
                    int generation = Integer.parseInt(generationText.getText().trim());
                    double alpha = Double.parseDouble(alphaText.getText().trim());
                    double beta = Double.parseDouble(betaText.getText().trim());
                    double rho = Double.parseDouble(rhoText.getText().trim());
                    int Q = Integer.parseInt(QText.getText().trim());
                    int deltaType = deltaTypeComboBox.getSelectedIndex();

                    // 执行蚁群算法，并返回最佳长度及最佳路径
                    ACO aco = new ACO(cityNum, antNum, generation, alpha, beta, rho, Q, deltaType);
                    aco.init(tspData);
                    aco.solve();
                    bestTour = aco.getBestTour();
                    bestLength = aco.getBestLength();

                    // 向最佳赋值长度Label赋值
                    bestLengthLabel.setText("最佳长度: " + bestLength);

                    // 向显示城市折线图的Panel上再次添加Canvas
                    displayPanel.add(guiCanvas);

                    isStarted = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    class GUICanvas extends Canvas {
        public GUICanvas() {
            // 设置Canvas背景色
            setBackground(Color.WHITE);
        }

        public void paint(Graphics graphics) {
            try {
                // 画城市位置对应的点
                graphics.setColor(Color.RED);
                for (int i = 0; i < cityNum; i++) {
                    graphics.fillOval(x[i] / 10, y[i] / 10, 5, 5);
                    graphics.drawString(String.valueOf(i + 1), x[i] / 10, y[i] / 10);
                }

                // 若点击过了"开始"按钮，则画城市之间的连线
                if (isStarted == true) {
                    graphics.setColor(Color.BLUE); // 设置城市之间连线的颜色
                    for (int j = 0; j < cityNum - 1; j++) {
                        graphics.drawLine(x[bestTour[j]] / 10, y[bestTour[j]] / 10, x[bestTour[j + 1]] / 10, y[bestTour[j + 1]] / 10);
                    }

                    // 将起始城市及终止城市单独画出
                    graphics.setColor(Color.BLUE); // 设置起始城市的颜色
                    graphics.fillOval(x[bestTour[0]] / 10, y[bestTour[0]] / 10, 6, 6);
                    graphics.setColor(Color.GREEN); // 设置终止城市的颜色
                    graphics.fillOval(x[bestTour[cityNum - 1]] / 10, y[bestTour[cityNum - 1]] / 10, 6, 6);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("蚁群算法解决TSP问题"); // 设置标题
        GUI myGUI = new GUI();

        // 配置放置Panel的Container
        Container container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(myGUI.jPanel, BorderLayout.EAST);
        container.add(myGUI.displayPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1024, 600); // 设置窗口大小
        frame.setVisible(true);
    }
}
