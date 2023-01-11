package soot;

import soot.tools.CFGViewer;

public class MainTest {
	
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
	public static void main(String[] args) {
		
		System.out.println("hello soot.");
		
//		sootInfo();
//		sootHelpCmd();
		sootClass();
		sootJava();
//		sootCFG();
	}
	
	private static void doMain(String[] args){
		Main.main(args);
	}
	
	/***
	 * 执行命令：java -cp sootclasses-trunk-jar-with-dependencies-4.1.0.jar soot.Main
	 * 
	 * 输出soot基本信息
	 */
	private static void sootInfo(){
		doMain(new String[]{});
	}
	
	/***
	 * 执行命令：java -cp sootclasses-trunk-jar-with-dependencies-4.1.0.jar soot.Main -h
	 * 
	 * 两种方式输出help信息
	 * 等同命令行： java soot.Main --help
	 */
	private static void sootHelpCmd(){
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
	private static void sootClass(){
		//默认解析class文件
		String[] args = new String[]{"-cp", ".", "-pp", "-process-dir", "./sootOutput/HelloWorld", "-f", "J"};
		doMain(args);
	}
	
	private static void sootJava(){
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
	private static void sootCFG(){
		String[] args = new String[]{"-cp", ".", "-pp", "-process-dir", "./sootOutput/"};
		CFGViewer.main(args);
	}
	
	
	
}
