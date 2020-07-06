package variable

/**
 * @ClassName FirstClass.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年05月08日 11:22:00
 */
class FirstClass extends App {

  object variableDefined extends App {
    val name = "tom"

    var age = 18;

    val name2 : String = "jack"
  }
}

object variableDefined extends App {
  val name = "tom"

  var age = 18;

  val name2: String = "jack"
}

object AppTest {
  def main(args: Array[String]) {
    variableDefined.name
  }

}