package System;

import java.util.Arrays;

public class travel_best {
    private static int INF = 1000000; // 设置一个较大的值作为无穷大
    // 动态规划求解
    public static int shortestPath(int[][] dist, int start, int[] path) {
        int n = dist.length;
        int[][] dp = new int[1 << n][n]; // dp数组，用于记录已访问过的节点和当前节点
        int[][] prev = new int[1 << n][n]; // prev数组，用于记录前一个节点
        for (int[] row : dp) {
            Arrays.fill(row, INF); // 初始化为无穷大
        }
        dp[1 << start][start] = 0; // 起点到起点的距离为0
        for (int s = 1; s < (1 << n); s += 2) { // s表示已经访问过的节点集合
            if ((s & (1 << start)) == 0) continue; // 起点必须要包含在集合中
            for (int i = 0; i < n; i++) {
                if ((s & (1 << i)) == 0) continue; // i必须要包含在集合中
                for (int j = 0; j < n; j++) {
                    if ((s & (1 << j)) == 0) continue; // j必须要包含在集合中
                    // 根据状态转移方程更新dp数组
                    int cost = dp[s ^ (1 << i)][j] + dist[j][i]; // s ^ (1 << i)表示从s中去掉i
                    if (cost < dp[s][i]) {
                        dp[s][i] = cost;
                        prev[s][i] = j; // 记录前一个节点
                    }
                }
            }
        }

        int ans = INF;
        int end = -1;
        for (int i = 0; i < n; i++) {
            int cost = dp[(1 << n) - 1][i]; // 计算最短路径的长度
            if (cost < ans) {
                ans = cost;
                end = i; // 记录最后一个节点
            }
        }

        int pos = 0;
        int s = (1 << n) - 1; // s表示所有节点都被访问的状态
        while (pos < n) { // 逆向回溯，求解路径
            path[pos++] = end;
            if(s<0||end<0){
                System.out.println("从该点出发不存在一条路径能经过所有点！");
                return -1;
            }
            int next = prev[s][end];
            s ^= (1 << end); // 去掉已经访问过的节点
            end = next;
        }

        return ans;
    }
    public static void run(int[][] dis,int len,int begin) {
        int[] path = new int[len];
        int shortest = shortestPath(dis, begin, path);
        if(shortest==-1)
            return;
        System.out.print(begin+1+"->");
        for(int i=0;i<len;++i){
            System.out.print(path[i]+1);
            if(i!=len-1)
                System.out.print("->");
        }
        System.out.println("\t\t距离为：" + shortest);
    }
}
