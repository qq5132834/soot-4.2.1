package soot.asm.frontend.employee;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EmployeeOw2AsmTest {

    /***
     * ClassReader 负责解析 .class 文件中的字节码，并将所有字节码传递给 ClassWriter。
     * ClassVisitor: 负责访问.class文件的各个元素，可以解析或者修改.class文件的内容。
     * ClassWriter：继承自 ClassVisitor，它是生成字节码的工具类，负责将修改后的字节码输出为 byte 数组。
     */
    public static void main(String[] args) throws Exception {

        //1.定义ClassReader
//        String sourceClassName = "soot.asm.frontend.employee.Employee";
//        ClassReader classReader = new ClassReader(sourceClassName);

        InputStream inputStream = new FileInputStream(new File("src/test/java/soot/asm/frontend/employee/Employee.class"));
        ClassReader classReader = new ClassReader(inputStream);

        //2.定义ClassWriter
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        //3.定义ClassVisitor
        ClassVisitor classVisitor = new EmployeeClassVisitor(classWriter);

        // 定义classVisitor输入数据,
        // SKIP_DEBUG 如果设置了此标志，则这些属性既不会被解析也不会被访问
        // EXPAND_FRAMES 依次调用ClassVisitor 接口的各个方法
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);

        // 将最终修改的字节码以byte数组形式返回
        byte[] bytes = classWriter.toByteArray();

        String targetClassName = "soot.asm.frontend.employee.Employee$EnhancedByASM";
        Class<?> clazz = new EmployeeClassLoader().defineClassFromClassFile(targetClassName, bytes);
        System.out.println("【EmployeeOw2AsmTest】clazz：" + clazz);

        // 通过文件流写入方式覆盖原先的内容，实现class文件的改写
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\Employee$EnhancedByASM.class");
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

}
