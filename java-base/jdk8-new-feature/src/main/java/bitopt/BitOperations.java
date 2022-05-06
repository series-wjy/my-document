package bitopt;

/**
 * 位运算示例
 * @Author wangjiayou
 * @Version 1.0.0
 * @Create 2020年09月14日 09:12:00
 */
public class BitOperations {
    public static void main(String[] args) {
        int t = (Integer.MAX_VALUE - 128) >> 31;
        System.out.println("1 >> 31 = " + t);
        int z = ~0;
        System.out.println("~t = " + z);
        int x = z & 128;
        System.out.println("z & 128 = " + x);
        System.out.println(Integer.parseUnsignedInt("01111111111111111111111111111111", 2));
        System.out.println(Integer.toBinaryString(-1));

        System.out.println(2147483647 + 1);
    }
}
