package com.example.perfectnavigationassistant;

import com.example.perfectnavigationassistant.pojo.Order;

public class MyUtil {
    //①根据输入的地点数，生成一个一维数组，存放输入的Place
    //②根据一维数组，网络请求生成一个二维数组，存放两两之间的距离distance、或者两两之间的时间
    //③根据二维数组，计算出最好的一条线路：一笔连上所有的点，且路程、或者时间最短
    //④计算③中的结果的时候，可能需要一个一维数组，用于在总路程、或者时间被比较之后，暂时保存最佳线路途经点顺序
    //⑤计算③中的结果的时候，还可能需要一个每一轮都会被重新初始化的一维数组，用于记录哪些个点已经去过了。

    private static int minSum = 2147483647;
    private static int[] result;
    private static int[] temp;//用于临时存放每一种可能的排列，如果下面的sum比原先的minSum小，则将其赋值给result数组
    private static int[] flag;//用于在每一个点选下一个点时，判断是否已经把所有情况考虑结束
    static int type = 0;//所有可能的情况的总数

    public static Order bestOrder(int dist[][]) {

        temp = new int[dist.length];
        result = new int[dist.length - 1];
        flag = new int[dist.length + 1];
        for (int i = 0; i < flag.length; i++) {
            flag[i] = 0;
        }

        int sum = 0;
        for (int i = 1; i < dist.length; i++) {
            //temp数组初始化

            temp[0] = i;
            flag[i] = 1;
            nextLevel(dist, sum + dist[0][i], i, 1, dist.length, temp);
            flag[i] = 0;
        }

        Order order = new Order();
        order.setAmountOfOrders(type);
        order.setMinValue(minSum);
        order.setBestOrder(result);

        //打印结果
        System.out.println("所有可能情况数：" + type);
        type = 0;
        System.out.println("最小值：" + minSum);
        minSum = 2147483647;

        System.out.print("顺序：");
        System.out.print("[");
        for (int i = 0; i < result.length - 1; i++) {
            System.out.print(result[i] + " -> ");
        }
        System.out.println(result[result.length - 1] + "]");


        return order;
    }

    public static void nextLevel(int dist[][], int sum, int i, int selected, int n, int temp[]) {
        int t = 0;
        for (int j = (i + 1) % dist.length; selected < n - 1 && t < n - 1 - selected; j++) {
            j %= dist.length;
            if (j != 0 && j != i && flag[j] == 0) {
                temp[selected] = j;
                t++;
                flag[j] = 1;
                nextLevel(dist, sum + dist[i][j], j, selected + 1, n, temp);
                flag[j] = 0;
            }
        }
        if (selected >= n - 1) {//一种排列顺序计算结束
            type++;
            if (sum < minSum) {
                //改变result数组，temp数组复制给result数组，从下标0开始存储数据
                for (int j = 0; j < n - 1; j++) {
                    result[j] = temp[j];
                }
                minSum = sum;
            }
        }
    }


}
