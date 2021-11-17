/**
 * Copyright 2006-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rayejun.mybatis.generator.codegen.xmlmapper;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.List;

public class InsertOrUpdateElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public InsertOrUpdateElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", "insertOrUpdate")); //$NON-NLS-1$

        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(
                    introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
        }

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            introspectedTable.getColumn(gk.getColumn()).ifPresent(introspectedColumn -> {
                // if the column is null, then it's a configuration error. The
                // warning has already been reported
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute(
                            "useGeneratedKeys", "true")); //$NON-NLS-1$ //$NON-NLS-2$
                    answer.addAttribute(new Attribute(
                            "keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
                    answer.addAttribute(new Attribute(
                            "keyColumn", introspectedColumn.getActualColumnName())); //$NON-NLS-1$
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            });
        }

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into "); //$NON-NLS-1$
        insertClause.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());

        answer.addElement(new TextElement(insertClause.toString()));
        insertClause.setLength(0);

        StringBuilder valuesClause = new StringBuilder();

        StringBuilder updateClause = new StringBuilder();

        StringBuilder sb = new StringBuilder();

        XmlElement insertTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        insertTrimElement.addAttribute(new Attribute("prefix", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        insertTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$

        XmlElement valuesTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        valuesTrimElement.addAttribute(new Attribute("prefix", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffix", ")")); //$NON-NLS-1$ //$NON-NLS-2$
        valuesTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$

        XmlElement updateTrimElement = new XmlElement("trim"); //$NON-NLS-1$
        updateTrimElement.addAttribute(new Attribute("suffixOverrides", ",")); //$NON-NLS-1$ //$NON-NLS-2$

        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            valuesClause.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            updateClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            updateClause.append(" = ");
            updateClause.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            insertClause.append(", "); //$NON-NLS-1$
            valuesClause.append(", "); //$NON-NLS-1$
            updateClause.append(", "); //$NON-NLS-1$

            if (introspectedColumn.isIdentity()) {
                sb.setLength(0);
                sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null"); //$NON-NLS-1$
                XmlElement insertNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                insertNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                insertNotNullElement.addElement(new TextElement(insertClause.toString()));
                insertTrimElement.addElement(insertNotNullElement);
                insertClause.setLength(0);

                XmlElement valueNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                valueNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                valueNotNullElement.addElement(new TextElement(valuesClause.toString()));
                valuesTrimElement.addElement(valueNotNullElement);
                valuesClause.setLength(0);

                XmlElement updateNotNullElement = new XmlElement("if"); //$NON-NLS-1$
                updateNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
                updateNotNullElement.addElement(new TextElement(updateClause.toString()));
                updateTrimElement.addElement(updateNotNullElement);
                updateClause.setLength(0);

                continue;
            }

            if (insertClause.length() > 80 && i + 1 != columns.size()) {
                insertTrimElement.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);
            }
            if (valuesClause.length() > 80 && i + 1 != columns.size()) {
                valuesTrimElement.addElement(new TextElement(valuesClause.toString()));
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
            if (updateClause.length() > 80 && i + 1 != columns.size()) {
                updateTrimElement.addElement(new TextElement(updateClause.toString()));
                updateClause.setLength(0);
                OutputUtilities.xmlIndent(updateClause, 1);
            }

        }
        insertTrimElement.addElement(new TextElement(insertClause.toString()));
        valuesTrimElement.addElement(new TextElement(valuesClause.toString()));
        updateTrimElement.addElement(new TextElement(updateClause.toString()));

        answer.addElement(insertTrimElement);
        answer.addElement(new TextElement("values"));
        answer.addElement(valuesTrimElement);
        answer.addElement(new TextElement("on duplicate key update "));
        answer.addElement(updateTrimElement);

        parentElement.addElement(answer);
    }
}
