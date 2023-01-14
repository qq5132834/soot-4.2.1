package soot;

import org.junit.Test;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.*;
import java.io.*;
import java.util.*;

/**
 * 参考： https://blog.csdn.net/beswkwangbo/article/details/41242889?spm=1001.2101.3001.6650.9&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-9-41242889-blog-41247429.pc_relevant_multi_platform_whitelistv4&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-9-41242889-blog-41247429.pc_relevant_multi_platform_whitelistv4&utm_relevant_index=16
 *
 * 使用Soot从头创建类文件的示例.
 * “createclass”示例使用Soot创建HelloWorld类文件。
 * 其进展如下：
 * - Create a SootClass <code>HelloWorld</code> extending java.lang.Object.
 * - Create a 'main' method and add it to the class.
 * - Create an empty JimpleBody and add it to the 'main' method.
 * - Add locals and statements to JimpleBody.
 * - Write the result out to a class file.
 */

public class MainTestJunit {

    //类名称
    private final static String className = "HelloWorld_HL";

    /***
     * 将类编译成class文件
     */
    @Test
    public void createclass() throws FileNotFoundException, IOException {
        SootClass sClass;
        SootMethod method;

        // Resolve Dependencies
        Scene.v().loadClassAndSupport("java.lang.Object");
        Scene.v().loadClassAndSupport("java.lang.System");

        // Declare 'public class HelloWorld'
        sClass = new SootClass(className, Modifier.PUBLIC);

        // 'extends Object'
        sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
        Scene.v().addClass(sClass);

        // Create the method, public static void main(String[])
        method = new SootMethod("main",
                Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}),
                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);

        sClass.addMethod(method);

        // Create the method body
        {
            // create empty body
            JimpleBody body = Jimple.v().newBody(method);

            method.setActiveBody(body);
            Chain units = body.getUnits();
            Local arg, tmpRef;

            // Add some locals, java.lang.String l0
            arg = Jimple.v().newLocal("l0", ArrayType.v(RefType.v("java.lang.String"), 1));
            body.getLocals().add(arg);

            // Add locals, java.io.printStream tmpRef
            tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
            body.getLocals().add(tmpRef);

            // add "l0 = @parameter0"
            units.add(Jimple.v().newIdentityStmt(arg,
                    Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"), 1), 0)));

            // add "tmpRef = java.lang.System.out"
            units.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
                    Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));

            // insert "tmpRef.println("Hello world!")"
            {
                SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
                units.add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!"))));
            }

            // insert "return"
            units.add(Jimple.v().newReturnVoidStmt());

        }


        {
            /***
             * 生成.jimple IR文件
             */
            String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_jimple);
            OutputStream streamOut = new FileOutputStream(fileName);
            PrintWriter writerOut = new PrintWriter(
                    new OutputStreamWriter(streamOut));
            Printer.v().printTo(sClass, writerOut);
            writerOut.flush();
            streamOut.close();
        }


        {
            /***
             * 生成.class文件
             */
            String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
            OutputStream streamOut = new JasminOutputStream(
                    new FileOutputStream(fileName));
            PrintWriter writerOut = new PrintWriter(
                    new OutputStreamWriter(streamOut));
            JasminClass jasminClass = new soot.jimple.JasminClass(sClass);
            jasminClass.print(writerOut);
            writerOut.flush();
            streamOut.close();
        }
    }

}