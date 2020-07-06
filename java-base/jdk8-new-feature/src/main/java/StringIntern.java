/**
 * @ClassName StringIntern.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年05月27日 19:22:00
 */
public class StringIntern {
    public static void main(String[] args) {
        String str1 = new StringBuilder("极客").append("时间").toString();
        String intern = str1.intern();
        System.out.println(str1.intern() == str1);

        String str2 = new StringBuilder("极客").toString();
        intern = str2.intern();
        System.out.println(str2.intern() == str2);
    }
}
