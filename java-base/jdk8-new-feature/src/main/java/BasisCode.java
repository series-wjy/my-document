import java.util.Random;

/**
 * 基础
 *
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月16日 13:57:00
 */
public class BasisCode {
    public static void main(String[] args) {
        float sum = 0.0f;
        float c = 0.0f;
        for (int i = 0; i < 20000000; i++) {
            float x = 1.0f;
            float y = x - c;
            float t = sum + y;
            c = (t - sum) - y;
            sum = t;
        }
        System.out.println("sum is " + sum);
    }
}
