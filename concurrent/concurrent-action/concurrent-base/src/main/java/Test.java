/**
 * @author wangjiayou
 * @version 1.0.0
 * @date 2022年04月14日 15:57:00
 */
public class Test {

    public static void main(String[] args) {
        label1:
        for (int i = 0; i < 10; i++) {
            System.out.println("i = " + i);
            for (int x = 0; x < 10; x++) {
                System.out.println("x = " + x);
                break label1;
            }
        }
        System.out.println("end executing ......");
    }
}
