package System;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import static System.travel_best.run;

//针对游客的不同需求，设计一款旅游区景点导游系统。
//        基本功能及要求如下：
//        1、提供用户操作的菜单和界面，实现系统的所有功能。
//        2、旅游区所有景点信息，要求以文件的形式存储（如文本文件），格式自行设计。
//        3、提供对旅游区所有景点信息的查询、编辑功能，可进行查找、添加、删除和修改等操作，修改后的信息需要保存回文件。
//        4、提供旅游区当前景点显示功能，要求能显示游客当前所在景点及所有与游客所在景点相邻景点信息。
//        5、提供查询从旅游区每个景点出发到其他任一景点的最短简单路径及距离的功能。
//        6、提供查询旅游区任意两个景点之间所有简单路径及距离、最短简单路径及距离的功能。
//        7、提供最佳游览路线推荐功能，即确定从某一景点出发，经过景区所有景点（景点可以重复）且距离最短的游览路线。
public class Main {
    static final int max_value=1000000;       //不设置为Integer.MAX_VALUE是为了防止溢出
    static ArrayList<Scenic_Spot> Spots; //景点数组，存储景点的编号和名称
    static int Spots_num;      //景点的数量
    static int[][] Spots_dis;    //矩阵存储各景点之间的距离，如果之间没有直达路线，则为max_value，即Integer类型最大值
    static int tourist_location_num,choice_num;   //游客目前所在的景点编号
    static String id="Mark",pwd="666",address_details= "E:\\data_Struct\\Spots_details.txt",       //id和pwd分别是用户名的账号密码
            address_Map= "E:\\data_Struct\\Spots_Map.txt";   //文件分别存储景点信息和景点距离
    static boolean circle_system,id_right=false;       //circle用于菜单栏的循环      id_right用于管理员身份判断
    static StreamTokenizer in=new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));    //快速输入
    static Scanner input=new Scanner(System.in);             //输入字符串
    static PrintWriter _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));      //快速输出
    public static void main(String[] args)throws Exception{
        read();   //从文件中读取数据
        show();   //系统展示
        while(!circle_system){
            choice_num=get_choice_num();
            switch (choice_num) {
                case 1 -> Spots_Show();          //展示所有的景点
                case 2 -> Individual_details();       //查看某个单独的景点信息
                case 3 -> Location_details();        //查看当前景点的信息以及周边环境信息
                case 4 -> path();         //最短路径查询等
                case 5 -> Best_tour();           //最佳导航路线
                case 6 -> Change_details();          //更改文件数据
                case 7 -> show();               //再次展示系统
                case 8 -> circle_system=true;           //退出系统
                default -> Wrong_num();           //输入的编号有误
            }
        }
        end();                //结束执行
    }
    //主函数
    public static void read() throws Exception {   //读入数据
        Spots=new ArrayList<>();
        Spots_num=0;         //将景点数量先置为0，最后用于统计景点数量
        File Spots_detalis=new File(address_details);
        BufferedReader reader=new BufferedReader(new FileReader(Spots_detalis));
        String s;
        while((s=reader.readLine())!=null){       //从景点信息文件中不断读入数据
            String[] arr=s.split(" ");      //先读入字符串，然后转化为数组，读入的数据都是三个一组
            Scenic_Spot spot=new Scenic_Spot();
            spot.spot_name=arr[0];
            spot.spot_number=Integer.parseInt(arr[1]);   //将数组信息存储到Scenic_Spot里面
            spot.details=arr[2];
            Spots.add(spot);          //将Scenic_Spot当前对象spot的信息添加到Arraylist里面
            Spots_num++;              //存储一个景点，数量就加一
        }
        Spots_dis=new int[Spots_num+1][Spots_num+1];        //创建景点之间距离的数组，用于存储距离，景点编号就是索引值
        for(int i=0;i<=Spots_num;i++){
            for(int j=0;j<=Spots_num;j++)
                Spots_dis[i][j]=max_value;           //先将各景点之间距离置为max_value，表示两者之间的距离无限大，也就是不可达
        }
        File distance=new File(address_Map);          //打开存储距离的文件
        reader=new BufferedReader(new FileReader(distance));
        while((s=reader.readLine())!=null){        //同样是从距离文件中不断读入数据，直到空
            String[] arr=s.split(" ");          //先存入字符串，然后转化为数组
            int a=Integer.parseInt(arr[0]),b=Integer.parseInt(arr[1]),c=Integer.parseInt(arr[2]);   //将得到的字符串转化为整形
            if(a>Spots_num||b>Spots_num){              //如果距离的景点编号溢出景点数量，则表示文件的距离是有问题的，需要进行修改
                System.out.println("文件输入有编号错误，请文件进行进行修改！");
                System.exit(1);                   //异常退出系统
            }
            Spots_dis[a][b]=c;              //无向图邻接矩阵设置距离
            Spots_dis[b][a]=c;
        }
    }
    //读取数据
    public static boolean id_check(String a,String b) {      //用户名和密码判断   用于文件修改数据
        if(id.equals(a)&&pwd.equals(b)){
            id_right=true;        //如果匹配，判断标识“id_right”置为true，本次程序便不再输入用户密码
            return true;
        }
        return false;
    }
    //用户id判断
    public static String get_String() throws IOException {     //得到字符串，如果是streamtokenizer的输入字符串，存在的问题是如果输入的字符串是一串数字，则不会读取，所以使用了相对输入缓慢的Scanner
        return input.next();
    }
    //输入字符串
    public static int get_Integer()throws IOException{       //输入整形，因为从缓冲区得到的是浮点数，进行强转
        in.nextToken();
        return (int)in.nval;
    }
    //输入整形
    public static void Spots_Show(){
        _out.println("以下为该城市旅游景点：");
        int print_count=0;
        for(int i=0;i<Spots_num;i++){
            Scenic_Spot sp=Spots.get(i); //创建临时变量sp从ArrayList中取出景点
            _out.printf("%-3d、%-15s\t",sp.spot_number,sp.spot_name);   //输出所有的景点名字和编号
            print_count++;
            if(print_count%4==0)         //一行四组
                _out.println();
        }
        _out.println();
        _out.flush();
    }
    //展示所有的景点名字
    public static void Individual_details() throws IOException {        //查看某个景点的信息
        _out.println("请输入要查看的景点编号：");_out.flush();
        int n=get_Integer();
        while (n <= 0 || n > Spots_num) {
            _out.println("您输入的编号有误，请重新输入");
            _out.flush();
            n = get_Integer();
        }           //         此循环确保用户输入的编号正确
        Scenic_Spot sp=Spots.get(n-1);          //索引值=景点编号-1；
        _out.println(sp.spot_name+"  编号为："+sp.spot_number+"\n"+sp.details);              //输出编号代表的景区的名字、编号、详细信息
        _out.flush();
    }
    //查看某个景点的信息
    public static void Location_details() throws IOException {
        _out.println("请输入您当前所处的景点位置编号：");_out.flush();
        int n=get_Integer();
        while (n <= 0 || n > Spots_num) { //此循环确保输入的编号正确
            _out.println("您输入的编号有误，请重新输入");
            _out.flush();
            n = get_Integer();
        }
        n--;         //因为景点编号与景点距离的索引值不一样，所以有+-1的区别
        Scenic_Spot sp=Spots.get(n);
        _out.println(sp.spot_name+"  编号为："+sp.spot_number+"\n"+sp.details+"\n其周边景点信息有：");        //输出用户当前的景点信息
        for(int i=0;i<Spots_num;i++){
            if(Spots_dis[n+1][i+1]!=max_value){                                //如果景点之间可达，就是相邻的景点
                _out.println(sp.spot_name+ " ——>  "+Spots.get(i).spot_name+"("+Spots.get(i).spot_number+")    \t距离为："+Spots_dis[n+1][i+1]);     //输出当前景点相邻的景点的信息
            }
        }
        tourist_location_num=n;     //记录游客所在的景点编号
        _out.flush();
    }
    //查看游客当前所处位置景点的信息，以及周边景点的信息
    public static void path() throws IOException {
        boolean check=false;int option = 1;
        while(!check) {
            _out.println("请输入要查询的路径：\n1、两个景点之间的所有简单路径以及最短简单路径\t\t2.任意两个景点之间的最短简单路径");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=3)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        if(option==1)
            all_path();     //查询所有可达路径
        else
            each_path();
    }
    //查询路径
    public static void each_path() throws IOException {
        boolean check=false;int option = 1;
        while(!check) {
            _out.println("请输入要查询的景点编号：");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=Spots_num)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        floyd(option);   //floyd算法
    }
    //查询任意两点之间的最短路径floyd算法前置操作
    public static void Best_tour() throws IOException {
        int option=0;
        boolean check=false;
        while(!check) {     //确保输入合法
            _out.println("请输入您当前所处的景点编号：");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=Spots_num)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");
            }
        }
        int[][] temp_replace=new int[Spots_num][Spots_num];
        for(int i=1;i<=Spots_num;i++){
            if (Spots_num >= 0) System.arraycopy(Spots_dis[i], 1, temp_replace[i - 1], 0, Spots_num);
        }
        run(temp_replace,Spots_num,option-1);     //run是导入的
    }
    //规划最佳旅游路线
    public static void Change_details() throws Exception {
        boolean check=false;
        if(!id_right){         //改变地图信息需要用户权限
            _out.println("请输入用户名和密码：");_out.flush();
            String idcheck=get_String(),pwd_check=get_String();     //输入用户名和密码
            check=id_check(idcheck,pwd_check);         //对id进行判断
            while(!check){
                _out.println("对不起，您输入的用户名或密码有误，请重新输入：");_out.flush();
                idcheck=get_String();pwd_check=get_String();
                check=id_check(idcheck,pwd_check);
            }
        }
        check=false;int option = 1;
        while(!check) {
            _out.println("请输入编码选择功能：\n1、查找\t2、添加\t3、删除\t4、修改");//查找、添加、删除和修改
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=4)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");
            }
        }
        switch (option){         //根据管理员的选择，进行下一项操作
            case 1->query();      //查询
            case 2->create();    //添加
            case 3->delete();      //删除
            case 4->updata();       //修改
        }
        //read();
    }
    //管理员修改数据
    public static void Wrong_num(){        //如果用户输入的数字有问题，便执行
        _out.printf("您输入的“数字”有误，请重新输入。");
        _out.flush();
    }
    //用于菜单栏对用户选择进行判断输出，其实可以没有
    public static int get_choice_num() throws IOException {      //获取用户的需求编号
        _out.println("\n请您根据功能输入相应的编号：");
        _out.flush();
        return get_Integer();
    }
    //菜单栏获取用户的需求编号
    public static void end(){  //结束退出系统时，打印的话语
        _out.println("感谢使用景点导游系统，再见！");
        _out.flush();
        _out.close();
    }
    //结束时输出的语句
    public static void show()  {         //展示系统界面，供用户选择相应的功能
        _out.print("\t\t\t   *********************************************\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t         旅游区景点导游系统           \t   *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *           本次游览城市为：武汉市         \t   *\n");
        _out.print("\t\t\t   *                                     \t   *\n");
        _out.print("\t\t\t   *                                      \t   *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 1  全部景点显示             \t       *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 2  查询某个景点详细信息      \t       *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 3  显示您的当前位置以及周边信息     \t   *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 4  查询最短路径或者距离         \t       *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 5  最佳游览路线                \t   *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 6  管理员功能：对景点信息进行修改   \t   *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 7  展示系统功能                 \t   *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *\t"+" ☆"+" 8  退出系统                   \t   *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *                                           *\n");
        _out.print("\t\t\t   *********************************************\t\n");
        _out.flush();
    }
    //菜单栏展示界面
    public static void query() throws IOException {
        boolean check=false;int option = 1;
        while(!check) {         //循环保证输入合法
            _out.println("请输入编码选择查找的文件：\n1、Spots_details\t2、Spots_Map");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=2)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        if(option==1)
            file_details();    //查询景点数据
        else
            file_Map();         //查询景点之间的距离
    }
    //查询操作
    public static void create() throws Exception {
        boolean check=false;int option = 1;
        while(!check) {        //循环保证用户输入合法
            _out.println("请输入编码选择要更改的文件：\n1、Spots_details\t2、Spots_Map");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=2)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        if(option==1)
            create_Spots_details();        //增加景点数据
        else
            create_Spots_Map();         //增加景点之间距离数据
        read();                //因为增加了数据，所以需要重新读取，对现有数据进行更新
    }
    //增加操作 创建
    public static void create_Spots_details() throws Exception {
        boolean check=false;
        String name=null,details=null;
        while(!check) {       //循环保证数据输入合法
            _out.println("请按照“景点名字” ”景点详细介绍“ 的顺序输入内容（”景点编号“将由系统分配："+(Spots_num+1)+"）：");
            _out.flush();
            name=get_String();
            details=get_String();
            if(check_create_details(name))           //检查输入数据是否合法
                check=true;
            else {
                _out.println("对不起，您输入的信息有误！请重新输入！\n");_out.flush();
            }
        }
        RandomAccessFile rand=new RandomAccessFile(address_details,"rw");
        rand.seek(rand.length());      //将指针移到文件最后
        rand.write(("\n"+name+" "+(++Spots_num)+" "+details).getBytes());           //添加数据
        _out.println("信息添加成功！");_out.flush();
    }
    //创造景点数据
    public static boolean check_create_details(String name){
        for(Scenic_Spot sp:Spots){       //确保新创建的景点是新的，数据中没有
            if(sp.spot_name.equals(name))
                return false;
        }
        return true;
    }
    //判断输入景点数据的合法性
    public static void create_Spots_Map()throws Exception{
        boolean check=false;
        int a = 0,b=0,c=0;
        while(!check) {       //循环保证数据输入合法
            _out.println("请按照 “出发地编号” ”目的地编号“ ”路程“ 的顺序输入内容：");
            _out.flush();
            a=get_Integer();
            b=get_Integer();
            c=get_Integer();int judge=check_create_Map(a,b,c);       //judge检查输入的合法
            if(judge==1)       //合法
                check=true;
            else if(judge==0){        //数据以及存在，更改用户需求，看是否是修改
                _out.println("您输入的路程信息已经存在，距离为："+Spots_dis[a][b]+"。是否需要修改（0、不修改；1、修改）：");_out.flush();
                int choice=get_Integer();
                while(choice!=0&&choice!=1){
                    _out.println("对不起，您输入的数字不符合要求！请重新输入！");_out.flush();
                    choice=get_Integer();
                }
                if(choice==0)
                    return;
                else
                    updata_Map(a,b);   //如果用户选择修改数据，就执行到修改函数
            }
            else {          //输入不合法
                _out.println("对不起，您输入的信息有误！请重新输入！");_out.flush();
            }
        }
        Spots_dis[a][b]=c;    //更新数据
        Spots_dis[b][a]=c;
        RandomAccessFile rand=new RandomAccessFile(address_Map,"rw");
        rand.seek(rand.length());    //指针移动到文件尾部
        rand.write(("\n"+a+" "+b+" "+c).getBytes());         //文件添加数据
        _out.println("信息添加成功！");_out.flush();
    }
    //创造景点距离数据
    public static int check_create_Map(int x,int y,int z){
        if(x>=1&&x<=Spots_num&&y>=1&&y<=Spots_num&&z>0){
            if(Spots_dis[x][y]==max_value)
                return 1;      //数据不存在，所以合法
            else
                return 0;      //已经有了该距离
        }
        return -1;        //输入的信息不合法
    }
    //判断景点距离输入是否合法
    public static void delete() throws Exception {
        boolean check=false;int option = 1;
        while(!check) {     //保证输入编号合法
            _out.println("请输入编码选择删除内容所在的文件：\n1、Spots_details\t2、Spots_Map");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=2)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        if(option==1) {
            file_details();
            boolean check_option1=false;int option1 = 1;
            while(!check_option1) {     //得到要删除的数据编号
                _out.println("请输入要删除的内容编号：");
                _out.flush();
                option1 = get_Integer();
                if(option1>=1&&option1<=Spots_num)
                    check_option1=true;
                else {
                    _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
                }
            }
            real_delete(option1,address_details);    //执行真正的删除操作
        }
        else {
            boolean check_option2=false;int option2 = 1,option_max=file_Map(0); //得到一共有多少组距离数据
            while(!check_option2) {
                _out.println("请输入要删除的内容编号：");
                _out.flush();
                option2 = get_Integer();
                if(option2>=1&&option2<=option_max)
                    check_option2=true;
                else {
                    _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
                }
            }
            real_delete(option2,address_Map);      //执行真正的删除操作
            load_map();         //删除是在本文件中产生的，所以要同步到文件
        }
    }
    //删除操作
    public static void polish(int sp_num){
        int fi=Spots_num+1;
        int[][] replace_dis=new int[fi][fi];   //新建数组存储新数据
        for(int i=1;i<=fi;++i){
            for(int j=1;j<=fi;j++){
                if(Spots_dis[i][j]==max_value||sp_num==i||sp_num==j)  //如果没有距离或者该景点已经删除就跳过
                    continue;
                else {
                    int x=i>sp_num?i-1:i,y=j>sp_num?j-1:j;        //删除景点之后的编号往前移动
                    replace_dis[x][y]=Spots_dis[i][j];        //添加数据
                }
            }
        }
        Spots_dis=replace_dis;       //将数据替换过来
        for(int i=1;i<=Spots_num;++i){             //因为默认不可达是：所以不可达的点置为最大值
            for(int j=1;j<=Spots_num;j++){
                if(Spots_dis[i][j]==0) {
                    Spots_dis[i][j] = max_value;
                }
            }
        }
    }
    //景点删除后，景点距离矩阵也发生改变
    public static void real_delete(int num,String address) throws IOException {
        if(address.equals(address_details)){        //根据传入的地址不同，判断是要删除景点还是删除距离
            Spots.remove(num-1);Spots_num--;          //从ArrayList里面根据索引删除景点，并且景点数量减少
            load_details();          //文件同步景点数据
            polish(num);           //景点删除，距离也要更新
            load_map();          //文件同步距离数据
        }
        else {                 //删除距离
            int count=1;
            outer:
            for(int i=1;i<=Spots_num;i++){
                for(int j=1;j<=Spots_num;j++){
                    if(Spots_dis[i][j]!=max_value&&count++==num){
                        Spots_dis[i][j]=max_value;       //删除距离，就是置为最大值
                        Spots_dis[j][i]=max_value;
                        break outer;         //删除成功之后，直接退出循环
                    }
                }
            }
        }
        _out.println("信息删除成功！");_out.flush();
    }
    //执行真正的删除操作
    public static void updata() throws IOException {
        boolean check=false;int option = 1;
        while(!check) {        //循环保证输入合法
            _out.println("请输入编码选择修改的文件：\n1、Spots_details\t2、Spots_Map");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=2)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        if(option==1) {
            updata_details();       //对景点进行修改
            load_details();         //同步到文件
        }
        else {
            updata_Map();          //对距离进行修改
            load_map();            //同步到文件
        }
        _out.println("信息修改成功！");_out.flush();
    }
    //修改数据操作
    public static void updata_Map(int x,int y) throws IOException {
        _out.println("请输入修改的距离：");_out.flush();
        int n=get_Integer();
        Spots_dis[x][y]=n;      //更新修改后的距离
        Spots_dis[y][x]=n;
    }
    //修改距离，这个修改是“创建”操作里面发现有相同数据，询问是否修改
    public static void updata_Map() throws IOException {
        int sum=file_Map(0);        //得到一共有多少组距离数据
        boolean check=false;int option = 1;
        while(!check) {         //输入合法
            _out.println("请输入修改的编号：");_out.flush();
            option = get_Integer();
            if(option>=1&&option<=sum)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        _out.println("请输入修改后的距离：");_out.flush();
        int s=get_Integer();
        int count=1;
        outer:
        for(int i=1;i<=Spots_num;i++){
            for(int j=1;j<=Spots_num;j++){
                if(Spots_dis[i][j]!=max_value&&count++==option){   //根据编号来判断是否修改
                    Spots_dis[i][j]=s;     //修改数据
                    Spots_dis[j][i]=s;
                    break outer;          //修改后退出
                }
            }
        }
    }
    //修改距离，这个修改是“修改”操作里面的修改。与上面的修改根据形参个数形成重载
    public static void load_map() throws IOException {
        File file = new File(address_Map);
        try {             //此操作先将距离文件里面的数据清空
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");  //写入空
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean first_line=true;
        RandomAccessFile rand=new RandomAccessFile(address_Map,"rw");
        for(int i=1;i<=Spots_num;i++){
            for(int j=1;j<=i;j++){
                if(Spots_dis[j][i]!=max_value){
                    String s = "";
                    if (!first_line)
                        s = s + "\n";        //如果不是第一行的话，就需要先换行再添加数据
                    else
                        first_line = false;
                    s = s + j + " " + i + " " + Spots_dis[j][i];
                    rand.write(s.getBytes());        //逐个写入数据
                }
            }
        }
    }
    //距离同步文件
    public static void load_details() throws IOException {
        File file = new File(address_details);
        try {       //先将景点信息文件里面数据清空处理
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");  //写入空
            fileWriter.flush();   //清空缓冲区
            fileWriter.close();    //关闭
        } catch (IOException e) {
            e.printStackTrace();
        }
        RandomAccessFile rand=new RandomAccessFile(address_details,"rw");
        boolean first_line=true;
        for(int i=0;i<Spots_num;i++){
            String s = "";
            if (!first_line)
                s = s + "\n";      //如果不是第一行，就需要换行操作
            else
                first_line = false;
            Scenic_Spot sp=Spots.get(i);
            s =s+sp.spot_name+" "+(i+1)+" "+sp.details ;
            rand.write(s.getBytes());      //逐行写入数据
        }
    }
    //景点信息同步文件
    public static void updata_details() throws IOException {
        file_details();     //显示景点信息，供用户选择需要修改的编号
        boolean check=false;int option = 1;
        while(!check) {         //编号合法
            _out.println("请输入编码选择修改内容：");
            _out.flush();
            option = get_Integer();
            if(option>=1&&option<=Spots_num)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        check=false;String name = null,details = null;
        while(!check) {       //修改后的景点信息合法
            _out.println("请按照“景点名字” ”景点详细介绍“ 的顺序输入内容：");
            _out.flush();
            name=get_String();
            details=get_String();
            if(check_create_details(name))      //判断景点信息合法性
                check=true;
            else {
                _out.println("对不起，您输入的信息与现有信息重复！请重新输入！\n");_out.flush();
            }
        }
        Scenic_Spot sp=Spots.get(option-1);  //剩下的是进行数据修改
        sp.details=details;
        sp.spot_name=name;
    }
    //更新景点数据
    public static void file_details(){
        int count=1;
        for(int i=0;i<Spots_num;++i){
            Scenic_Spot sp=Spots.get(i);
            _out.println((count++)+"、 "+sp.spot_name+"\t编号："+sp.spot_number+"\t详细信息："+sp.details);_out.flush();
        }
        _out.println("总共有"+" "+Spots_num+" 个景区");_out.flush();
    }
    //展示所有的景点信息
    public static void file_Map(){
        int count=1;
        for(int i=1;i<=Spots_num;i++){
            for(int j=1;j<=Spots_num;j++){
                if(Spots_dis[i][j]!=max_value)
                    _out.println((count++)+"、  "+i+" ——> "+j+"距离为："+Spots_dis[i][j]+"\t\t"+Spots.get(i-1).spot_name+" ——> "+Spots.get(j-1).spot_name);
            }
        }
    }
    //展示所有的距离信息
    public static int file_Map(int num){
        int count=1;
        for(int i=1;i<=Spots_num;i++){
            for(int j=1;j<=Spots_num;j++){
                if(Spots_dis[i][j]!=max_value)
                    _out.println((count++)+"、  "+i+" ——> "+j+"距离为："+Spots_dis[i][j]+"\t\t"+Spots.get(i-1).spot_name+" ——> "+Spots.get(j-1).spot_name);
            }
        }
        return count;
    }
    //展示所有的距离信息，并且返回有多少个距离数据    与上面的形成重载
    public static void all_path() throws IOException {
        boolean check=false;int x = 0,y=0;
        while(!check) {      //输入合法
            _out.println("请输入起点和终点的编号：");
            _out.flush();
            x = get_Integer();
            y = get_Integer();
            if(x>=1&&x<=Spots_num&&y>=1&&y<=Spots_num)
                check=true;
            else {
                _out.println("对不起，您输入的编号有误！请重新输入！");_out.flush();
            }
        }
        paths path=new paths(Spots_dis,x,y,Spots_num);
        path.dfs_false(x);      //回溯＋深搜
    }
    //某两点的所有路径              //回溯算法+深入搜索
    public static void shorted_path(int x,int y)throws IOException{
        _out.println("从“"+Spots.get(x-1).spot_name+"”到”"+Spots.get(y-1).spot_name+"“的最短路径为：");_out.flush();   //从x到y
        Dijkstra(x,y);
    }
    //最短路径
    public static void Dijkstra(int x,int y){  //x代表起点   y代表终点
        int[][] matrix=new int[Spots_num+1][Spots_num+1];   //将数据复制到matrix数组，然后对该数组进行操作
        for(int i=1;i<=Spots_num;i++){
            for(int j=1;j<=Spots_num;j++)
                matrix[i][j]=Spots_dis[i][j];
        }
        //最短路径长度
        int[] shortest = new int[Spots_num+1];
        //判断该点的最短路径是否求出
        boolean[] visited = new boolean[Spots_num+1];
        //存储输出路径
        String[] path = new String[Spots_num+1];
        //初始化输出路径
        for (int i = 1; i <= Spots_num; i++) {
            path[i] = new String(x + "->" + i);
        }
        //初始化源节点
        shortest[x] = 0;
        visited[x] = true;
        for (int i = 1; i <= Spots_num; i++) {
            int min = max_value;
            int index = 0;
            for (int j = 1; j <= Spots_num; j++) {
                    //已经求出最短路径的节点不需要再加入计算并判断加入节点后是否存在更短路径
                    if (!visited[j]  && matrix[x][j] < min) {
                        min = matrix[x][j];          //寻找最小的中间结点
                        index = j;
                    }
            }
            //更新最短路径
            shortest[index] = min;
            visited[index] = true;
            //更新从index跳到其它节点的较短路径
            for (int m = 1; m <=Spots_num; m++) {
                if (!visited[m] && matrix[x][index] + matrix[index][m] < matrix[x][m]) {
                    matrix[x][m] = matrix[x][index] + matrix[index][m];
                    path[m] = path[index] + "->" + m;
                }
            }
        }
        //打印最短路径
        if (shortest[y] == max_value) {    //如果最短路径还是最大值就是不可达
            _out.println( "不可达");_out.flush();
        }
        else {
            _out.println( path[y] + "，最短距离是：" + shortest[y]);_out.flush();
        }
    }
    //迪杰斯特拉算法，求最短路径
    public static void floyd(int x){    //到这个点
        int[][] Graph=new int[Spots_num+1][Spots_num+1],path=new int[Spots_num+1][Spots_num+1];
        for(int i=0;i<=Spots_num;i++){
            for(int j=0;j<=Spots_num;j++){
                Graph[i][j]=Spots_dis[i][j];     //存储最短距离
                path[i][j]=-1;                   //存储最短路径
            }
        }
        for(int i=1;i<=Spots_num;++i){         //遍历中间结点
            for(int j=1;j<=Spots_num;++j){        //遍历起点
                if(i==j)
                    continue;                 //中间结点不能一样
                for(int k=1;k<=Spots_num;++k){       //遍历终点
                    if(j==k||i==k)
                        continue;         //起终点以及中间结点不能一样
                    if(Graph[j][k]>Graph[j][i]+Graph[i][k]){     //判断新的和旧的哪个小
                        Graph[j][k]=Graph[j][i]+Graph[i][k];
                        path[j][k]=i;              //记录中间结点
                    }
                }
            }
        }
        for(int i=1;i<=Spots_num;++i){
            if(i!=x){
                StringBuilder s=new StringBuilder(String.valueOf(i));
                int before=i,mid=path[i][x];
                while(mid!=-1){         //寻找路径，主要是中间路径，-1表示可以直达
                    s.append("——>").append(mid);
                    before=mid;
                    mid=path[before][x];
                }
                s.append("——>").append(x);          //连接字符串
                _out.println(s+"   距离为："+Graph[i][x]);_out.flush();   //输出
            }
        }
    }
    //普利姆算法
}
