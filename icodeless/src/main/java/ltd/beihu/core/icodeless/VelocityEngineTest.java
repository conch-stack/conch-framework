package ltd.beihu.core.icodeless;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Adam
 * @since 2020/3/29
 */
public class VelocityEngineTest {

    public static void main(String[] args) throws IOException {
        VelocityEngine ve = new VelocityEngine();

        ve.setProperty(org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        // 初始化
        ve.init();

        // 导入模板
        Template template = ve.getTemplate("vtl/domain.java.vtl");

        VelocityContext ctx = new VelocityContext();
        ctx.put("package", "ltd.beihu.core.icodeless.template");
        ctx.put("className", "Test");
        ctx.put("Object", "Value");
        StringWriter sw = new StringWriter();
        template.merge(ctx, sw);
        String r = sw.toString();
        System.out.println(r);
        File file = new File("/Users/zhengjinzhou/project/beihu-framework/icodeless/src/main/java/ltd/beihu/core/icodeless/template/TestDomain.java");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(r);
        fileWriter.flush();
        fileWriter.close();
    }
}
