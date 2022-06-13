package com.example.perfectnavigationassistant;

import com.example.perfectnavigationassistant.pojo.Order;
import com.example.perfectnavigationassistant.pojo.Place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Test {

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

    public static void main(String[] args) {
        int dist[][] = {
                {1, 2, 3, 4, 5, 6},
                {7, 8, 10, 9, 11, 12},
                {13, 14, 15, 21, 23, 18},
                {19, 20, 16, 22, 17, 24},
                {25, 26, 27, 28, 29, 30},
                {31, 32, 33, 34, 35, 36}};
        int dist2[][] = {
                {1, 2, 3, 4, 5, 6},
                {7, 8, 9, 10, 11, 12},
                {13, 14, 15, 16, 17, 18},
                {19, 20, 21, 22, 23, 24},
                {25, 26, 27, 28, 29, 30},
                {31, 32, 33, 34, 35, 36}};
        int dist3[][] = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15, 17, 16, 18, 19, 20},
                {21, 22, 23, 24, 25, 27, 26, 28, 29, 30},
                {31, 32, 33, 34, 35, 37, 36, 38, 39, 40},
                {41, 42, 43, 44, 45, 47, 58, 48, 49, 50},
                {51, 52, 53, 54, 55, 57, 56, 46, 59, 60},
                {61, 62, 63, 64, 65, 67, 66, 68, 69, 70},
                {71, 72, 73, 74, 75, 77, 76, 78, 79, 80},
                {81, 82, 83, 84, 85, 87, 86, 88, 89, 90},
                {91, 92, 93, 94, 95, 97, 96, 98, 99, 100},
        };
        Order order = bestOrder(dist);
        System.out.println(order);
        Order order1 = bestOrder(dist2);
        System.out.println(order1);
        Order order2 = bestOrder(dist);
        System.out.println(order2);
        Order order3 = bestOrder(dist3);
        System.out.println(order3);
        //new Test().getDistance(new Place(null, null, null, "116.481028", "39.989643"), new Place(null, null, null, "116.434446", "39.90816"));
    }

    public void getDistance(Place placeA, Place placeB) {
        String uri = "https://restapi.amap.com/v3/direction/driving?" +
                "key=" + "6d61a5918c86aaaeb6802c14133a8519" + "&" +
                "origin=" + placeA.getLongitude() + "," + placeA.getLatitude() + "&" +
                "destination=" + placeB.getLongitude() + "," + placeB.getLatitude() + "&" +
                "strategy=" + 0 + "&" +
                "output=" + "json" + "&"
                + "extensions=" + "all";

        System.out.println(uri);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("执行子线程");
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(uri);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    System.out.println(response.toString());

                    //将json字符串解析出来
//                    JSONObject jsonObject = JSONObject.parseObject(response.toString(), JSONObject.class);
//                    JSONArray pois = jsonObject.getJSONArray("pois");
//                    JSONObject poi = pois.getJSONObject(0);
//
//                    String name = poi.getString("name");
//                    String location = poi.getString("location");
//                    String poiid = poi.getString("id");
//
//                    System.out.println("name : " + name + " , " + "location : " + location + " , " + "poiid : " + poiid);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("子线程执行结束");
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("继续执行主线程");
    }

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

    public static void test1() {
        long time1 = System.currentTimeMillis();

        int s = 0;
        for (int i = 0; i < 1000000; i++) {
            s += i;
        }
        System.out.println(s);

        long time2 = System.currentTimeMillis();

        System.out.println("总耗时：" + (time2 - time1) + "毫秒");
    }
}


