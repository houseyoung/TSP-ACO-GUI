/**
 * Created by houseyoung on 16/5/11 19:47.
 */
import java.util.Random;
import java.util.ArrayList;

public class Ant {
    private ArrayList<Integer> tabu; // 禁忌表
    private ArrayList<Integer> allowedCities; // 下一步允许选择的城市
    private double[][] delta; // 信息素增量矩阵
    private double[][] distance; // 距离矩阵
    private double[][] eta; // 能见度矩阵

    private double alpha; // 信息素重要程度系数
    private double beta; // 城市间距离重要程度系数

    private int tourLength; // 路径长度
    private int cityNum; // 城市数量
    private int firstCity; // 起始城市
    private int currentCity; // 当前城市

    /**
     * 构造方法
     * @param cityNum
     */
    public Ant(int cityNum) {
        this.cityNum = cityNum;
        tourLength = 0;
    }

    /**
     * 初始化蚂蚁，随机挑选一个城市作为起始位置
     * @param distance
     * @param alpha
     * @param beta
     */
    public void init(double[][] distance, double alpha, double beta) {
        this.alpha = alpha;
        this.beta = beta;
        this.distance = distance;

        // 初始化禁忌表为空
        tabu = new ArrayList<Integer>();

        // 初始化信息素增量矩阵为0
        // 初始化下一步允许选择的城市为所有城市
        delta = new double[cityNum][cityNum];
        allowedCities = new ArrayList<Integer>();
        for (int i = 0; i < cityNum; i++) {
            allowedCities.add(i);
            for (int j = 0; j < cityNum; j++) {
                delta[i][j] = 0.0;
            }
        }

        // 随机挑选一个城市作为蚂蚁的起始城市
        Random random = new Random();
        firstCity = random.nextInt(cityNum);

        // 从未访问过的城市集合中移除起始城市
        for (Integer integer : allowedCities) {
            if (integer.intValue() == firstCity) {
                allowedCities.remove(integer);
                break;
            }
        }

        // 已访问过的城市集合中添加起始城市
        tabu.add(firstCity);

        // 将当前城市设为起始城市
        currentCity = firstCity;

        // 根据距离矩阵计算能见度矩阵
        eta = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum - 1; i++) {
            eta[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                eta[i][j] = 1.0 / distance[i][j];
                eta[j][i] = eta[i][j];
            }
        }
        eta[cityNum - 1][cityNum - 1] = 0;
    }

    /**
     * 选择下一个城市
     * @param pheromone
     */
    public void selectNextCity(double[][] pheromone) {
        double[] probability = new double[cityNum]; // 转移概率矩阵
        double sum = 0;

        // 计算分母
        for (int i : allowedCities) {
            sum += Math.pow(pheromone[currentCity][i], alpha) * Math.pow(eta[currentCity][i], beta);
        }

        // 计算概率矩阵
        for (int i = 0; i < cityNum; i++) {
            if (allowedCities.contains(i)) {
                probability[i] = (Math.pow(pheromone[currentCity][i], alpha) * Math.pow(eta[currentCity][i], beta)) / sum;
            } else {
                probability[i] = 0;
            }
        }

        // 选择下一个城市(权重随机数算法/轮盘赌)
        int selectCity = 0;

        Random random = new Random();
        double rand = random.nextDouble();
        double sumPs = 0.0;
        for (int i = 0; i < cityNum; i++) {
            sumPs += probability[i];
            if (sumPs >= rand) {
                selectCity = i;
                break;
            }
        }

        // 从允许选择的城市中去掉选中的下一个城市
        for (Integer i : allowedCities) {
            if (i.intValue() == selectCity) {
                allowedCities.remove(i);
                break;
            }
        }

        // 在禁忌表中添加选中的下一个城市
        tabu.add(selectCity);

        // 将当前城市改为选中的下一个城市
        currentCity = selectCity;
    }

    /**
     * 计算路径长度
     * @return
     */
    private int calculateTourLength() {
        int length = 0;

        for (int i = 0; i < cityNum; i++) {
            length += distance[tabu.get(i)][tabu.get(i + 1)];
        }

        return length;
    }


    public ArrayList<Integer> getAllowedCities() {
        return allowedCities;
    }

    public void setAllowedCities(ArrayList<Integer> allowedCities) {
        this.allowedCities = allowedCities;
    }

    public int getTourLength() {
        tourLength = calculateTourLength();
        return tourLength;
    }

    public void setTourLength(int tourLength) {
        this.tourLength = tourLength;
    }

    public int getCityNum() {
        return cityNum;
    }

    public void setCityNum(int cityNum) {
        this.cityNum = cityNum;
    }

    public ArrayList<Integer> getTabu() {
        return tabu;
    }

    public void setTabu(ArrayList<Integer> tabu) {
        this.tabu = tabu;
    }

    public double[][] getDelta() {
        return delta;
    }

    public void setDelta(double[][] delta) {
        this.delta = delta;
    }

    public int getFirstCity() {
        return firstCity;
    }

    public void setFirstCity(int firstCity) {
        this.firstCity = firstCity;
    }
}
