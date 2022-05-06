package classloader;

public class TestClassLoader {
    public static void main(String[] args) {
        System.out.println(TestClassLoader.class.getResource("ehcache.xml"));
        System.out.println(TestClassLoader.class.getResource("/ehcache.xml"));
        System.out.println();
        System.out.println(TestClassLoader.class.getClassLoader().getResource("ehcache.xml"));
        System.out.println(TestClassLoader.class.getClassLoader().getResource("/ehcache.xml"));
    }
}