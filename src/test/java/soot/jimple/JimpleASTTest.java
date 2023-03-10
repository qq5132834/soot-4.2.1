package soot.jimple;

import org.junit.Test;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.parser.JimpleAST;
import soot.jimple.parser.analysis.DepthFirstAdapter;
import soot.jimple.parser.lexer.Lexer;
import soot.jimple.parser.lexer.LexerException;
import soot.jimple.parser.node.AVirtualNonstaticInvoke;
import soot.jimple.parser.node.PFile;
import soot.jimple.parser.node.Start;
import soot.jimple.parser.parser.Parser;
import soot.jimple.parser.parser.ParserException;

import java.io.*;
import java.util.Iterator;

public class JimpleASTTest {

    final static String jimpleFile = "./sootOutput/HelloWorld.jimple";

    @Test
    public void jimpleASTTest() throws ParserException, IOException, LexerException {
        InputStream aJIS = new FileInputStream(new File(jimpleFile));
        JimpleAST jimpleAST = new JimpleAST(aJIS);

        //TODO 将jimple文件转SootClass
        SootClass sc = new SootClass("HelloWorld");
        sc.setResolvingLevel(1);
        jimpleAST.getSkeleton(sc);
        System.out.println(sc.toString());

        JimpleMethodSource mtdSrc = new JimpleMethodSource(jimpleAST);
        for (Iterator<SootMethod> mtdIt = sc.methodIterator(); mtdIt.hasNext();) {
            SootMethod sm = mtdIt.next();
            sm.setSource(mtdSrc);
        }

    }

    @Test
    public void parseTest() throws ParserException, IOException, LexerException {

        Lexer lexer = new Lexer (new PushbackReader(new FileReader(jimpleFile), 1024));
        Parser parser = new Parser(lexer);
        Start ast = parser.parse();
        //TODO ast中可以遍历全部node节点
        MyInterpreter myInterpreter = new MyInterpreter();
        ast.apply(myInterpreter);
    }

    private class MyInterpreter extends DepthFirstAdapter {

        @Override
        public void inAVirtualNonstaticInvoke(AVirtualNonstaticInvoke node) {
            System.out.println("inAVirtualNonstaticInvoke");
            super.inAVirtualNonstaticInvoke(node);
        }

        @Override
        public void outAVirtualNonstaticInvoke(AVirtualNonstaticInvoke node) {
            System.out.println("outAVirtualNonstaticInvoke");
            super.outAVirtualNonstaticInvoke(node);
        }

        @Override
        public void caseAVirtualNonstaticInvoke(AVirtualNonstaticInvoke node) {
            System.out.println("caseAVirtualNonstaticInvoke");
            super.caseAVirtualNonstaticInvoke(node);
        }
    }
}
