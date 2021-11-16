package io.github.rayejun.mybatis.generator.plugin;

import io.github.rayejun.mybatis.generator.codegen.javamapper.*;
import io.github.rayejun.mybatis.generator.codegen.xmlmapper.*;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

public class MethodPlusPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        AbstractXmlElementGenerator insertOrUpdateElementGenerator = new InsertOrUpdateElementGenerator(false);
        insertOrUpdateElementGenerator.setContext(context);
        insertOrUpdateElementGenerator.setIntrospectedTable(introspectedTable);
        insertOrUpdateElementGenerator.addElements(document.getRootElement());

        AbstractXmlElementGenerator insertOrUpdateSelectiveElementGenerator = new InsertOrUpdateSelectiveElementGenerator();
        insertOrUpdateSelectiveElementGenerator.setContext(context);
        insertOrUpdateSelectiveElementGenerator.setIntrospectedTable(introspectedTable);
        insertOrUpdateSelectiveElementGenerator.addElements(document.getRootElement());

        AbstractXmlElementGenerator batchInsertElementGenerator = new BatchInsertElementGenerator(false);
        batchInsertElementGenerator.setContext(context);
        batchInsertElementGenerator.setIntrospectedTable(introspectedTable);
        batchInsertElementGenerator.addElements(document.getRootElement());

        AbstractXmlElementGenerator updateBatchElementGenerator = new UpdateBatchElementGenerator();
        updateBatchElementGenerator.setContext(context);
        updateBatchElementGenerator.setIntrospectedTable(introspectedTable);
        updateBatchElementGenerator.addElements(document.getRootElement());

        AbstractXmlElementGenerator updateBatchSelectiveElementGenerator = new UpdateBatchSelectiveElementGenerator();
        updateBatchSelectiveElementGenerator.setContext(context);
        updateBatchSelectiveElementGenerator.setIntrospectedTable(introspectedTable);
        updateBatchSelectiveElementGenerator.addElements(document.getRootElement());
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        AbstractJavaMapperMethodGenerator insertOrUpdateMethodGenerator = new InsertOrUpdateMethodGenerator(false);
        insertOrUpdateMethodGenerator.setContext(context);
        insertOrUpdateMethodGenerator.setIntrospectedTable(introspectedTable);
        insertOrUpdateMethodGenerator.addInterfaceElements(interfaze);

        AbstractJavaMapperMethodGenerator insertOrUpdateSelectiveMethodGenerator = new InsertOrUpdateSelectiveMethodGenerator();
        insertOrUpdateSelectiveMethodGenerator.setContext(context);
        insertOrUpdateSelectiveMethodGenerator.setIntrospectedTable(introspectedTable);
        insertOrUpdateSelectiveMethodGenerator.addInterfaceElements(interfaze);

        AbstractJavaMapperMethodGenerator batchInsertMethodGenerator = new BatchInsertMethodGenerator(false);
        batchInsertMethodGenerator.setContext(context);
        batchInsertMethodGenerator.setIntrospectedTable(introspectedTable);
        batchInsertMethodGenerator.addInterfaceElements(interfaze);

        AbstractJavaMapperMethodGenerator updateBatchMethodGenerator = new UpdateBatchMethodGenerator();
        updateBatchMethodGenerator.setContext(context);
        updateBatchMethodGenerator.setIntrospectedTable(introspectedTable);
        updateBatchMethodGenerator.addInterfaceElements(interfaze);

        AbstractJavaMapperMethodGenerator updateBatchSelectiveMethodGenerator = new UpdateBatchSelectiveMethodGenerator();
        updateBatchSelectiveMethodGenerator.setContext(context);
        updateBatchSelectiveMethodGenerator.setIntrospectedTable(introspectedTable);
        updateBatchSelectiveMethodGenerator.addInterfaceElements(interfaze);
        return super.clientGenerated(interfaze, introspectedTable);
    }
}
