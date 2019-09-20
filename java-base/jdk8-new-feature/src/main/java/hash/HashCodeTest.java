package hash;

import java.util.HashMap;
import java.util.Map;

class HashCodeTest {

  String name;

  public HashCodeTest(String name) {
    this.name = name;
  }

  public static void main(String[] args) {
    Map<HashCodeTest, String> map = new HashMap<>(4);
    map.put(new HashCodeTest("hello"), "hello");
    String hello = map.get(new HashCodeTest("hello"));
    System.out.println(hello);
  }
}