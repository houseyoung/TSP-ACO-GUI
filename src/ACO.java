/**
 * Created by houseyoung on 16/5/11 19:47.
 */
import java.io.IOException;

public class ACO {
    private Ant[] ants; // 蚂蚁
    private int cityNum; // 城市数量

    private int[] x; // X坐标矩阵
    private int[] y; // Y坐标矩阵
    private double[][] distance; // 距离矩阵
    private double[][] pheromone; // 信息素矩阵

    private int bestLength; // 最佳长度
    private int[] bestTour; // 最佳路径

    private int antNum; // 蚂蚁数量
    private int generation; // 迭代次数
    private double alpha; // 信息素重要程度系数
    private double beta; // 城市间距离重要程度系数
    private double rho; // 信息素残留系数
    private int Q; // 蚂蚁循环一周在经过的路径上所释放的信息素总量
    private int deltaType; // 信息素更新方式模型，0: Ant-quantity; 1: Ant-density; 2: Ant-cycle

    /**
     * 构造方法
     * @param cityNum
     * @param antNum
     * @param generation
     * @param alpha
     * @param beta
     * @param rho
     * @param Q
     */
    public ACO(int cityNum, int antNum, int generation, double alpha, double beta, double rho, int Q, int deltaType) {
        this.cityNum = cityNum;
        this.antNum = antNum;
        this.generation = generation;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.Q = Q;
        this.deltaType = deltaType;

        ants = new Ant[antNum];
    }

    /**
     * 初始化
     * @param filename
     * @throws IOException
     */
    public void init(String filename) throws IOException {
        // 从文件中获取X坐标矩阵、Y坐标矩阵
        x = ReadFile.getX(cityNum, filename);
        y = ReadFile.getY(cityNum, filename);

        // 计算距离矩阵
        getDistance(x, y);

        // 初始化信息素矩阵
        pheromone = new double[cityNum][cityNum];
        double start = 1.0 / ((cityNum - 1) * antNum); // 计算初始信息素数值
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = start;
            }
        }

        // 初始化最佳长度及最佳路径
        bestLength = Integer.MAX_VALUE;
        bestTour = new int[cityNum + 1];

        // 初始化antNum个蚂蚁
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(cityNum);
            ants[i].init(distance, alpha, beta);
        }
    }

    /**
     * 计算距离矩阵
     * @param x
     * @param y
     * @throws IOException
     */
    private void getDistance (int[] x, int[] y) throws IOException {
        // 计算距离矩阵
        distance = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum - 1; i++) {
            distance[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                distance[i][j] = Math.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j])) / 10.0);
                distance[j][i] = distance[i][j];
            }
        }
        distance[cityNum - 1][cityNum - 1] = 0;
    }

    /**
     * 解决TSP问题
     */
    public void solve() {
        // 迭代generation次
        for (int g = 0; g < generation; g++) {
            // 对antNum只蚂蚁分别进行操作
            for (int ant = 0; ant < antNum; ant++) {
                // 为每只蚂蚁分别选择一条路径
                for (int i = 1; i < cityNum; i++) {
                    ants[ant].selectNextCity(pheromone);
                }

                // 把这只蚂蚁起始城市再次加入其禁忌表中，使禁忌表中的城市最终形成一个循环
                ants[ant].getTabu().add(ants[ant].getFirstCity());

                // 若这只蚂蚁走过所有路径的距离比当前的最佳距离小，则覆盖最佳距离及最佳路径
                if (ants[ant].getTourLength() < bestLength) {
                    bestLength = ants[ant].getTourLength();
                    for (int k = 0; k < cityNum + 1; k++) {
                        bestTour[k] = ants[ant].getTabu().get(k).intValue();
                    }
                }

                // 更新这只蚂蚁信息素增量矩阵
                double[][] delta = ants[ant].getDelta();
                for (int i = 0; i < cityNum; i++) {
                    for (int j : ants[ant].getTabu()) {
                        if (deltaType == 0) {
                            delta[i][j] = Q; // Ant-quantity System
                        }
                        if (deltaType == 1) {
                            delta[i][j] = Q / distance[i][j]; // Ant-density System
                        }
                        if (deltaType == 2) {
                            delta[i][j] = Q / ants[ant].getTourLength(); // Ant-cycle System
                        }
                    }
                }
                ants[ant].setDelta(delta);
            }

            // 更新信息素
            updatePheromone();

            // 重新初始化蚂蚁
            for (int i = 0; i < antNum; i++) {
                ants[i].init(distance, alpha, beta);
            }
        }

        // 打印最佳结果
        print();
    }

    /**
     * 更新信息素
     */
    private void updatePheromone() {
        // 按照rho系数保留原有信息素
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = pheromone[i][j] * rho;
            }
        }

        // 按照蚂蚁留下的信息素增量矩阵更新信息素
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                for (int ant = 0; ant < antNum; ant++) {
                    pheromone[i][j] += ants[ant].getDelta()[i][j];
                }
            }
        }
    }

    /**
     * 在控制台中输出最佳长度及最佳路径
     */
    private void print() {
        System.out.println("最佳长度: " + bestLength);
        System.out.print("最佳路径: ");
        for (int i = 0; i < cityNum - 1; i++) {
            System.out.print(bestTour[i] + 1 + "-");
        }
        System.out.println(bestTour[cityNum - 1] + 1);
    }

    /**
     * 输出最佳路径
     * @return
     */
    public int[] getBestTour() {
        return bestTour;
    }

    /**
     * 输出最佳长度
     * @return
     */
    public int getBestLength() {
        return bestLength;
    }

    /**
     * 输出X坐标矩阵
     * @return
     */
    public int[] getX() {
        return x;
    }

    /**
     * 输出Y坐标矩阵
     * @return
     */
    public int[] getY() {
        return y;
    }
}
