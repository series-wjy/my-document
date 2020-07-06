package variable

/**
 * @ClassName a.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年05月08日 12:05:00
 */
object a extends FirstClass {
  def sum(numb: Int*) = {
    var res = 0
    for(n <- numb){
      res += n
    }
    res
  }

  def ttt(x : Int) : Int = {
    x * 2
  }

  def m0(y : Int) : Int = {
    y + 2
  }

  val f0 = (z : Int) => z - 1

  override def main(args: Array[String]): Unit = {
    ttt(f0(1))
    ttt(m0(1))
  }
}

