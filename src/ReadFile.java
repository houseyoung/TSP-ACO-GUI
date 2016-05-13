import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 从文件中读取X坐标矩阵、Y坐标矩阵
 * Created by houseyoung on 16/5/12 00:44.
 */
public class ReadFile {
    /**
     * 读取X坐标矩阵
     * @param cityNum
     * @param filename
     * @return
     * @throws IOException
     */
    public static int[] getX (int cityNum, String filename) throws IOException {
        int[] x = new int[cityNum]; // X坐标矩阵
        String str;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

        for (int i = 0; i < cityNum; i++) {
            // 按行从文件中数据，格式为：编号 X坐标 Y坐标
            str = bufferedReader.readLine();
            // 使用空格分隔字符
            String[] strcol = str.split(" ");
            x[i] = Integer.valueOf(strcol[1]); // X坐标
        }

        return x;
    }

    /**
     * 读取Y坐标矩阵
     * @param cityNum
     * @param filename
     * @return
     * @throws IOException
     */
    public static int[] getY (int cityNum, String filename) throws IOException {
        int[] y = new int[cityNum]; // Y坐标矩阵
        String str;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

        for (int i = 0; i < cityNum; i++) {
            // 按行从文件中数据，格式为：编号 X坐标 Y坐标
            str = bufferedReader.readLine();
            // 使用空格分隔字符
            String[] strcol = str.split(" ");
            y[i] = Integer.valueOf(strcol[2]); // Y坐标
        }

        return y;
    }
}
