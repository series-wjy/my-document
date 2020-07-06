package list;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ListTest.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年05月06日 16:02:00
 */
public class ListTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("a");
        list.add("b");
        list.add("b");
        list.add("c");
        list.add("c");
        list.add("d");
        list.add("e");

        for (int i = 0; i < list.size(); i++) {
            String tmp = list.get(i);
            if(tmp.equals("d")) {
                list.remove(i);
            }
        }
        for (String s : list) {
            System.out.println(s);
        }
    }
}
