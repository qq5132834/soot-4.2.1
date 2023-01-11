package soot;

public class MainTest {
	
	public static void main(String[] args) {
		
		System.out.println("hello soot.");
		
		sootInfo();
		sootHelpCmd();
		
	}
	
	private static void doMain(String[] args){
		Main.main(args);
	}
	
	/***
	 * 输出soot基本信息
	 */
	private static void sootInfo(){
		doMain(new String[]{});
	}
	
	/***
	 * 两种方式输出help信息
	 */
	private static void sootHelpCmd(){
		doMain(new String[]{"-h"});
		doMain(new String[]{"-help"});
	}

}
