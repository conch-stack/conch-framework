package com.nabob.conch.lombok;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.nabob.conch.lombok.MyHello")
public class HelloProcessor extends AbstractProcessor {
    private JavacTrees javacTrees; // 获取 JcTree
    private TreeMaker treeMaker; // 构建生成 jcTree
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        System.err.println("这是我的第一人编译注释处理器");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "这是我的处理器");
        javacTrees = JavacTrees.instance(processingEnv);// 语法树
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        super.init(processingEnv);
        this.names = Names.instance(context);

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .flatMap(t -> roundEnv.getElementsAnnotatedWith(t).stream())
                .forEach(t -> {
                    JCTree tree = javacTrees.getTree(t);
                    // 基于访问者设计模式 去修改方法
                    tree.accept(new TreeTranslator() {
                        @Override
                        public void visitMethodDef(JCTree.JCMethodDecl tree) {
//                            System.out.println("hello world");
                            JCTree.JCStatement sysout = treeMaker.Exec(
                                    treeMaker.Apply(
                                            List.nil(),
                                            select("System.out.println"),
                                            List.of(treeMaker.Literal("hello world!")) // 方法中的内容
                                    )
                            );
                            // 覆盖原有的语句块
                            tree.body.stats = tree.body.stats.append(sysout);
                            super.visitMethodDef(tree);
                        }
                    });
                });

        return true;
    }


    private JCTree.JCFieldAccess select(JCTree.JCExpression selected, String expressive) {
        return treeMaker.Select(selected, names.fromString(expressive));
    }

    private JCTree.JCFieldAccess select(String expressive) {
        String[] exps = expressive.split("\\.");
        JCTree.JCFieldAccess access = treeMaker.Select(ident(exps[0]), names.fromString(exps[1]));
        int index = 2;
        while (index < exps.length) {
            access = treeMaker.Select(access, names.fromString(exps[index++]));
        }
        return access;
    }

    private JCTree.JCIdent ident(String name) {
        return treeMaker.Ident(names.fromString(name));
    }
}