package soot;

public class MainTest {
	
	public static void main(String[] args) {
		
		System.out.println("hello soot.");
		
		sootInfo();
		sootHelpCmd();
		
	}
	
	
	/***
	 * 输出soot基本信息
	 */
	private static void sootInfo(){
		Main.main(new String[]{});
	}
	
	/***
	 * 两种方式输出help信息
	 */
	private static void sootHelpCmd(){
		Main.main(new String[]{"-h"});
		Main.main(new String[]{"-help"});
	}

}
