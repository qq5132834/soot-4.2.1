package soot;

import org.junit.Test;
import soot.options.Options;
import soot.tools.CFGViewer;
import soot.util.Chain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/***
 * soot提供的分析功能:
 * 	调用图构造
 * 	指针分析
 * 	Def/use chains
 * 	模块驱动的程序内数据流分析
 * 	结合FlowDroid的污染分析
 *
 * 参考文档：
 * 	https://m.isolves.com/e/wap/show.php?classid=49&id=61106&style=0&bclassid=3&cid=34&cpage=2
 * 	https://www.cnblogs.com/xine/p/14511818.html
 *
 */
public class MainCommandTest {

    private void doMain(String[] args){
        Main.main(args);
    }

    /***
     * 执行命令：java -cp sootclasses-trunk-jar-with-dependencies-4.1.0.jar soot.Main
     *
     * 输出soot基本信息
     */
    @Test
    public void sootInfo(){
        doMain(new String[]{});
    }

    /***
     * 执行命令：java -cp sootclasses-trunk-jar-with-dependencies-4.1.0.jar soot.Main -h
     *
     * 两种方式输出help信息
     * 等同命令行： java soot.Main --help
     */
    @Test
    public void sootHelpCmd(){
        doMain(new String[]{"-h"});
        doMain(new String[]{"-help"});
    }

    /***
     * 执行命令：
     * java -cp sootclasses-trunk-jar-with-dependencies-4.1.0.jar soot.Main -cp . -pp -process-dir ./sootOutput/HelloWorld -f J
     *
     * 执行./sootOutput/HelloWorld中的 HelloWorld.class文件
     * 结果默认保存在./sootOutput 目录中
     *
     * -cp .：soot有自己的classpath且默认classpath为空，所以使用的时候需要添加一下当前路径
     * -pp：soot的工作需要类型信息、类的完整层次结构，所以需要java.lang.Object，使用该参数可以自动包含所需的jar文件
     * -process-dir：处理的目录
     * -f J：生成Jimple类型的文件，默认在soot.jar的目录下的sootOutput下，也可以用-d指定输出文件夹S：shimpleG：grimple
     * 默认解析class文件，也可以用-src-prec解析指定类型
     */
    @Test
    public void sootClass(){
        //默认解析class文件
        String[] args = new String[]{"-cp", ".", "-pp", "-process-dir", "./sootOutput/HelloWorld", "-f", "J"};
        doMain(args);
    }

    @Test
    public void sootJava(){
        //指定解析java文件
        String[] args = new String[]{"-cp", ".", "-pp", "-process-dir", "./sootOutput/HelloWorld", "-src-prec", "java", "-f", "J"};
        doMain(args);
    }

    /***
     * 对三地址码（3 address code）以CFG图dot格式输出
     *
     * 执行命令：java -cp sootclasses-trunk-jar-with-dependencies-4.1.0.jar soot.tools.CFGViewer -cp . -pp -process-dir ./sootOutput/
     *
     * 输出
     */
    @Test
    public void sootCFG(){
        String[] args = new String[]{"-cp", ".", "-pp", "-process-dir", "./sootOutput/"};
        CFGViewer.main(args);
    }

    private void init(){
        soot.G.reset();//re-initializes all of soot
        Options.v().set_src_prec(Options.src_prec_class);//设置处理文件的类型,当然默认也是class文件
        Options.v().set_process_dir(Arrays.asList("./sootOutput/HelloWorld"));//处理路径
        Options.v().set_whole_program(true);//开启全局模式
        Options.v().set_prepend_classpath(true);//对应命令行的 -pp
        Options.v().set_output_format(Options.output_format_jimple);//输出jimple文件
        Options options = Options.v();
        Scene.v().loadNecessaryClasses();//加载所有需要的类

    }

    /***
     * 全过程分析
     */
    @Test
    public void sootProcess(){
        init();
        PackManager.v().runPacks();//运行(要有，不然下面没有输出...坑了好久，加上后运行好慢)
        PackManager.v().writeOutput();//输出jimple到sootOutput目录中
    }

    @Test
    public void sootProcess1(){
        init();
        PackManager.v().getPack("jtp").add(new Transform("jtp.TT", new TransformerTest()));
        for (SootClass appClazz : Scene.v().getApplicationClasses()) {
            for (SootMethod method : appClazz.getMethods()) {
                Body body = method.retrieveActiveBody();
                PackManager.v().getPack("jtp").apply(body);
            }
        }//只分析应用类，运行速度明显快了
    }

    @Test
    public void sootProcess2(){
        /***
         https://m.isolves.com/e/wap/show.php?classid=49&id=61106&style=0&bclassid=3&cid=34&cpage=2
         flow analysis framework
         soot自己有个流分析框架，我们要实现的主要流程：

         1.继承自*FlowAnalysis，backword就是BackwardFlowAnalysis<Unit, FlowSet>，forward就是ForwardFlowAnalysis<Unit, FlowSet>
         2.一些抽象的实现：
         3.值域的抽象（FlowSet）：Soot里有一些默认的，如ArrayPackedSet（其实就是课上提到的bitvector），我们也可以自己实现
         4.copy()：其实就是把IN的值给OUT或者OUT给IN （取决于forward或backword）
         5.
         6.merge()：不难理解，就是Transform Function干的事（可以回忆下那两行算法）
         7.flowThrough()：是流分析的核心，brain of analysis处理式子（等式右边是表达式）处理从IN到OUT或者OUT到IN到底发生了什么
         8.protected void flowThrough(FlowSet src, Unit u, FlowSet dest)
         9.我们还需要补充下Soot中Box的概念
         10.
         11.用上面(Unit)u的方法即可得到Box了，如u.getUseBoxes()，u.getDefBoxes()，那么也就不难理解Unit是啥了，上图中的s其实也是一个Unit
         12.我们还要再补充一点点，soot.Local：代表了Jimple中的本地变量
         13.初始化IN和OUT（边界和每个BB的值）：newInitialFlow()，entryInitialFlow()
         14.实现构造函数，且必须要调用doAnalysis
         15.super(graph); super.doAnalysis()；
         16.查看结果：（就在本类里测试，当然也可以将我们这个类加入jtp当中）
         17.OurAnalysis analysis = new OurAnalysis(graph); analysis.getFlowBefore(s);//Unit s analysis.getFlowAfter(s);
         把这些基础的用法都了解，才能在后面更加关注静态分析核心的算法部分（加油）
         */
    }

    private class TransformerTest extends BodyTransformer {
        @Override
        protected void internalTransform(Body body, String s, Map<String, String> map) {
            System.out.println(body.getMethod().getName());//输出下程序方法的名字
        }
    }

    @Test
    public void sootScene(){
        Scene.v().setSootClassPath("C:/Program Files/Java/jdk1.8.0_271/jre/lib/rt.jar");//rt.jar的路径
        Scene.v().extendSootClassPath("./sootOutput/Helloworld/");//classpath的路径
        SootClass sClass = Scene.v().loadClassAndSupport("Helloworld");//.class
        Scene.v().loadNecessaryClasses();//加载必须的类
        List<SootMethod> sMethods = sClass.getMethods();
        Chain<SootField> sFields = sClass.getFields();
        System.out.println("getClasses()");
//        for(SootClass c : Scene.v().getClasses()){
//          System.out.println(c);
//        }
        for(SootMethod m : sMethods){
            System.out.println(m);
        }
//        System.out.println("getDeclaration()");
//        System.out.println(sFields.size());
        for(SootField f : sFields){
            System.out.println(f.getDeclaration());
        }
        System.out.println("In the end.");
    }
}
