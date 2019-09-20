package feature8;

import java.util.Stack;

/**
 * 用栈模拟一个队列，要求实现队列的两个基本操作：入队、出队。
 *
 * isEmpty()    分配了内存空间，值为空，是一种有值（值==空）
 * “”           分配了内存空间无值，值为空字符串，是相对的空，是一种有值（值=空字串）
 * null         未分配内存空间，无值，是一种无值（值不存在）
 *
 * pop()方法：用于移除这个堆栈的顶部对象，并将该对象作为这个函数的返回值
 *      返回值：方法调用返回再这个堆栈的顶部的对象
 *
 * push()方法：用来压入项到堆栈的顶部
 *     返回值：方法调用返回参数项
 *
 * peek()方法：用于查找在此堆栈顶部的对象，无需从堆栈中取出
 *     返回值：方法调用返回在这个堆栈的顶部的对象
 *
 */
public class StackQueue {
    public static void main(String[] args) {
        StackQueue stackQueue = new StackQueue();
        stackQueue.enQueue(1);
        stackQueue.enQueue(2);
        stackQueue.enQueue(3);
        System.out.println(stackQueue.stackA.pop());
        System.out.println(stackQueue.deQueue());
        System.out.println(stackQueue.deQueue());
        stackQueue.enQueue(4);
        System.out.println(stackQueue.deQueue());
        System.out.println(stackQueue.deQueue());
    }

    private Stack<Integer> stackA = new Stack<Integer>();
    private Stack<Integer> stackB = new Stack<Integer>();

    /**
     * 出队操作
     *
     * @return
     */
    private Integer deQueue() {
        if (stackB.isEmpty()) {
            if (stackA.isEmpty()){
                return null;
            }
            transfer();
        }
        return stackB.pop();
    }

    /**
     * 栈A元素转移到栈B
     */
    private void transfer() {
        while (!stackA.isEmpty()) {
            stackB.push(stackA.pop());
        }
    }

    /**
     * 入队操作
     *
     * @param element 入队的元素
     */
    private void enQueue(int element) {
        stackA.push(element);
    }
}