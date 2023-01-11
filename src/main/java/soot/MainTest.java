package soot;

public class MainTest {
	
	/***
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("hello soot.");
		
//		sootInfo();
//		sootHelpCmd();
		sootClass();
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
		String[] args = new String[]{"-cp", ".", "-pp", "-process-dir", "c:/Users/51328/Desktop/soot/HelloWorld", "-f", "J"};
		doMain(args);
	}
}
