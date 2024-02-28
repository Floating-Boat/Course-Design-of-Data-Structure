package System;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class paths {
    public paths(int[][] G,int start,int end,int len){
        Graph=G;
        count=-1;   //总共路径数量
        index=0;   //记录最短路径的索引值
        this.start=start;
        this.end=end;
        this.G_length=len;
        visit = new boolean[G_length+1];
        path = new ArrayList<>();
        ans=new ArrayList<>();
        min=Integer.MAX_VALUE;
    }
    //有参构造
    static PrintWriter _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));      //快速输出
    private static final int max_value=1000000;       //表示没有路径的数值
    private int[][] Graph ;// 定义一个图，邻接矩阵
    private int G_length,start,end,count ,min,index;// 顶点个数    //起点和终点
    private boolean[] visit ;// visit数组，用于在dfs中记录访问过的顶点信息。
    private ArrayList<Integer> path ;      //存储每条可能的路径  这个会回溯
    private ArrayList<String> ans;         //存储所有的路径
    public void dfs(int u){
        visit[u] = true;   //表示来过
        path.add(u);
        if(u == end) {    //如果这一个符合终点，就输出
            StringBuilder one=new StringBuilder("");
            int len=path.size(),s=0;    //s是路径总长度
            one.append(path.get(0));
            for(int i=1;i<len;++i) {         //遍历输出此路径
                s += Graph[path.get(i - 1)][path.get(i)];
                one.append("->"+path.get(i));
            }
           one.append("   距离为："+s);
            ans.add(one.toString());
            if(s>min){
                min=s;
                index=++count;
            }
        }
        else{
            for (int i = 1; i <= G_length; i++) {
                if(!visit[i] &&i!=u&&Graph[u][i]!=max_value){   //如果有路径且不闭环，也就是没有来过
                    dfs(i);          //递归深搜
                }
            }
        }
        path.remove(path.size()-1);   //退栈
        visit[u] = false;     //修改为该点没有来过   回溯
    }
    //寻找所有可达简单路线
    public void dfs_false(int u){
        dfs(u);
        System.out.println("所有简单路径为：");
        for(String t:ans)
            System.out.println(t);
        System.out.println("最短路径为：");
        System.out.println(ans.get(index));
    }
}

