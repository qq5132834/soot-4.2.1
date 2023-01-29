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

public class Jimple2ClassTest {

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
        // Scene是包含全部 SootClass 的容器，Scene.v() 得到一个单例模式的 Scene。
        // 上面一行会load java.lang.Object 类，并且为之创建相应的 SootClass，以及其配套的 SootMethods 和 SootFields。
        // loadClassAndSupport方法也会自动将 Object 所引用的类全部加载。
        Scene.v().loadClassAndSupport("java.lang.System");

        // Declare 'public class HelloWorld'
        // 创建 HelloWorld_HL 的 SootClass，
        sClass = new SootClass(className, Modifier.PUBLIC);

        // 'extends Object'
        //并且设置他的父类是 Object，
        sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
        Scene.v().addClass(sClass); //然后把它添加到 Scene 中

        // Create the method, public static void main(String[])
        // SootClass 添加 method，为之设置参数类型、返回值以及Modifier。
        method = new SootMethod("main",
                Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}),
                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);

        sClass.addMethod(method); //把 SootMethod 添加到 SootClass中

        // Create the method body
        {
            // method 添加代码
            // create empty bodySourceLocator
            JimpleBody body = Jimple.v().newBody(method);  // 不同的 Body 提供不同的中间表示，例如 JimpleBody，BafBody，GrimpBody

            /***
             * Body 包含 3 个重要的特点： chains of locals, traps, units. chain 类似链表便于插入删除元素，
             * locals就是 body 的local variables.
             * unit 是 statement，
             * trap 指明哪个 unit catch 哪个异常。
             *
             * unit 在 Jimple 表示 statement ，在 Baf 中则表示 instruction。
             */
            method.setActiveBody(body); //调用 Jimple 的单例对象获得该 method 的 JimpleBody，然后设置成活跃的
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