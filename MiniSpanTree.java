import java.util.*;

public class MiniSpanTree {

    //Prim专用
    private static int[][] adjMatrix;
    private static int numOfNodes;
    private static int[] adjVertex;//保存顶点
    private static int[] lowCost;//保存相关顶点的权值

    //Kruskal专用
    private static int[][] adjMatrix2;

    public MiniSpanTree(){
        adjMatrix = new int[][]{//0代表和自己相连, 65535表示不可达, Prim专用
                {0, 6, 1, 5, 65535, 65535},
                {6, 0, 5, 65535, 3, 65535},
                {1, 5, 0, 5, 6, 4},
                {5, 65535, 5, 0, 65535, 2},
                {65535, 3, 6, 65535, 0, 6},
                {65535, 65535, 4, 2, 6, 0}
        };

        adjMatrix2 = new int[][]{
        //Kruskal专用, 无重复边， 65535表示不可达 -1重复 注意Kruskal用的矩阵不要有重复边
        //例如 0->2 weight:1 就不需要这个了：2->0 weight:1 否则会出现计算错误
                {0, 6, 1, 5, 65535, 65535},
                {-1, 0, 5, 65535, 3, 65535},
                {-1, -1, 0, 5, 6, 4},
                {-1, 65535, -1, 0, 65535, 2},
                {65535, -1, -1, 65535, 0, 6},
                {65535, 65535, -1, -1, -1, 0}
        };

        numOfNodes = adjMatrix[0].length;
        adjVertex = new int[numOfNodes];
        lowCost = new int[numOfNodes];

        adjVertex[0] = 0;//初始化加入v0顶点
        lowCost[0] = 0;//v0初始化权值为0, v0加入生成树
    }

    public static void primFunc(){

        /*
            Prim具体思想：以顶点为原则
            设图G顶点集合为U，首先任意选择图G中的一点作为起始点a，
            将该点加入集合V，再从集合U-V中找到另一点b使得点b到V中任意一点的权值最小，
            此时将b点也加入集合V；以此类推，现在的集合V={a，b}，再从集合U-V中找到另一点c使得点c到V中任意一点的权值最小，
            此时将c点加入集合V，直至所有顶点全部被加入V，此时就构建出了一颗MST。因为有N个顶点，所以该MST就有N-1条边，
            每一次向集合V中加入一个点，就意味着找到一条MST的边。
        */

        for(int i = 1; i < numOfNodes; i++){
            //初始化数据v0
            lowCost[i] = adjMatrix[0][i];
            adjVertex[i] = 0;
        }

        int currentIndex = 0;//保存当前顶点
        int minCost = 0;

        for(int i = 1; i < numOfNodes; i++){

            minCost = 65535;
            currentIndex = 0;

            for(int j = 1; j < numOfNodes; j++){
                if(lowCost[j] != 0 && lowCost[j] < minCost){
                    minCost = lowCost[j];
                    currentIndex = j;
                }
            }
            //打印权值最小的边
            System.out.println("("+adjVertex[currentIndex]+","+currentIndex+") ");
            lowCost[currentIndex] = 0;//当前节点权值设置为0, 表示已经完成任务

            //把这个顶点加入到adjVertex之中以便下次循环的时候打印
            //为什么需要这个循环，我们如果已经确定了0和1这个顶点，下一个顶点我们需要和0和1两个顶点距离进行比较而不是仅仅比较一个点，选出权重最小的记录
            //因为矩阵中点与点的记录是相互的所以，我们遍历1->6和6->1都可以
            for(int j = 1; j < numOfNodes; j++){
                if(lowCost[j] != 0 && adjMatrix[currentIndex][j] < lowCost[j]){
                    lowCost[j] = adjMatrix[currentIndex][j];
                    adjVertex[j] = currentIndex;
                }
            }
        }
    }

    public static class Edge{//Kruskal

        public int start;
        public int end;
        public int cost;

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
    }

    public static int find(int[] parent, int num){//Kruskal
        while(parent[num] > 0){//注意是while不是if!
            num = parent[num];
        }
        return num;
    }

    public static void kruskalFunc(){
        /*
            Kruskal具体思想：以边为原则
         */
        ArrayList<Edge> edges = new ArrayList<Edge>();
        int[] parent = new int[numOfNodes];//用来判断边与边之间是否形成环路
        //构建边和边集
        for(int i = 0; i < numOfNodes; i++){
            for(int j = i+1; j < numOfNodes; j++){// //j=i+1 例如 0->2 weight:1 就不需要这个了：2->0 weight:1 否则会出现计算错误
                if(adjMatrix[i][j] != 0 && adjMatrix[i][j] != -1 && adjMatrix[i][j] != 65535){
                    Edge edge = new Edge();
                    edge.start = i;
                    edge.end = j;
                    edge.cost = adjMatrix[i][j];
                    edges.add(edge);
                }
            }
        }

        //按照权重从小到大进行排序
        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                if ( e1.cost > e2.cost) {
                    return 1;
                } else if (e1.cost < e2.cost) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for(int i = 0; i < numOfNodes; i++){
            parent[i] = 0;//初始化
        }

        for(int i = 0; i < numOfNodes; i++){
            int n = find(parent, edges.get(i).start);
            int m = find(parent, edges.get(i).end);
            if(n != m){//不构成环路就加入
                parent[n] = m;//parent起点为下标的数据存储为边的最大尾点，证明把一条边加入到生成树中例如 parent[0]=1 既有一条边0—>1 0为起点1为终点
                System.out.println(edges.get(i).start+"->"+edges.get(i).end+" "+"weight:"+edges.get(i).cost);
            }
        }

    }

    public static void main(String[] args){

        new MiniSpanTree();

        System.out.println("Prim Function: ");
        primFunc();

        System.out.println("Kruskal Function: ");
        kruskalFunc();

    }



    public static int[][] getAdjMatrix() {
        return adjMatrix;
    }

    public static void setAdjMatrix(int[][] adjMatrix) {
        MiniSpanTree.adjMatrix = adjMatrix;
    }

    public static int[][] getAdjMatrix2() {
        return adjMatrix2;
    }

    public static void setAdjMatrix2(int[][] adjMatrix2) {
        MiniSpanTree.adjMatrix2 = adjMatrix2;
    }

    public static int getNumOfNodes() {
        return numOfNodes;
    }

    public static void setNumOfNodes(int numOfNodes) {
        MiniSpanTree.numOfNodes = numOfNodes;
    }

    public static int[] getAdjVertex() {
        return adjVertex;
    }

    public static void setAdjVertex(int[] adjVertex) {
        MiniSpanTree.adjVertex = adjVertex;
    }

    public static int[] getLowCost() {
        return lowCost;
    }

    public static void setLowCost(int[] lowCost) {
        MiniSpanTree.lowCost = lowCost;
    }
}
