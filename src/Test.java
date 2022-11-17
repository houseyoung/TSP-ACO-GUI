import java.io.IOException;

/**
 * 测试算法运行效果
 * Created by houseyoung on 16/5/11 22:18.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        // 设置TSP数据文件地址
        String tspData = System.getProperty("user.dir") + "/resources/att48.txt";

        // 平均值、最大值、最小值
        double avg = 0;
        int max = 0;
        int min = Integer.MAX_VALUE;

        // 计时器开始
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            // 城市数量, 蚂蚁数量, 迭代次数, Alpha, Beta, Rho, Q, QType
            ACO aco = new ACO(48, 10, 100, 1.0, 10.0, 0.5, 10, 1);
            aco.init(tspData);
            aco.solve();
            avg += aco.getBestLength();
            if (aco.getBestLength() > max) {
                max = aco.getBestLength();
            }
            if (aco.getBestLength() < min) {
                min = aco.getBestLength();
            }
        }
        // 计时器结束
        long endTime = System.currentTimeMillis();

        System.out.println(avg / 10.0 + "\t" + min + "\t" + max + "\t" + (endTime - startTime) / 1000F + "秒");
    }
}
