package soot.jimple;

import org.junit.Test;
import soot.SootClass;
import soot.jimple.parser.JimpleAST;
import soot.jimple.parser.analysis.DepthFirstAdapter;
import soot.jimple.parser.lexer.Lexer;
import soot.jimple.parser.lexer.LexerException;
import soot.jimple.parser.node.AVirtualNonstaticInvoke;
import soot.jimple.parser.node.Start;
import soot.jimple.parser.parser.Parser;
import soot.jimple.parser.parser.ParserException;

import java.io.*;

public class JimpleASTTest {

    final static String jimpleFile = "./sootOutput/HelloWorld.jimple";

    @Test
    public void jimpleASTTest() throws ParserException, IOException, LexerException {
        InputStream aJIS = new FileInputStream(new File(jimpleFile));
        JimpleAST jimpleAST = new JimpleAST(aJIS);
        SootClass sc = new SootClass("HelloWorld");
        jimpleAST.getSkeleton(sc);
    }

    @Test
    public void parseTest() throws ParserException, IOException, LexerException {

        Lexer lexer = new Lexer (new PushbackReader(new FileReader(jimpleFile), 1024));
        Parser parser = new Parser(lexer);
        Start ast = parser.parse();

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
