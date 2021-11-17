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
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.ArrayList;
import java.util.List;

public class BatchInsertElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public BatchInsertElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", "batchInsert")); //$NON-NLS-1$

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                "map"));

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
        insertClause.append(introspectedTable
                .getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(insertClause.toString()));
        insertClause.setLength(0);
        insertClause.append(" ("); //$NON-NLS-1$

        StringBuilder valuesClause = new StringBuilder();

        List<String> valuesClauses = new ArrayList<>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));

            String parameterClause = MyBatis3FormattingUtilities.getParameterClause(introspectedColumn);
            if (i == 0) {
                valuesClause.append("(");
            }
            valuesClause.append(parameterClause.replace(introspectedColumn.getJavaProperty(), "item." + introspectedColumn.getJavaProperty()));
            if (i == columns.size() - 1) {
                insertClause.append(")");
                valuesClause.append(")");
            }
            if (i + 1 < columns.size()) {
                insertClause.append(", "); //$NON-NLS-1$
                valuesClause.append(", "); //$NON-NLS-1$
            }

            if (insertClause.length() > 80 && i + 1 != columns.size()) {
                answer.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);
            }

            if (valuesClause.length() > 80 && i + 1 != columns.size()) {
                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
        }

        valuesClauses.add(valuesClause.toString());

        answer.addElement(new TextElement(insertClause.toString()));
        answer.addElement(new TextElement("values"));

        XmlElement element = new XmlElement("foreach"); //$NON-NLS-1$
        element.addAttribute(new Attribute("collection", "list")); //$NON-NLS-1$ //$NON-NLS-2$
        element.addAttribute(new Attribute("item", "item")); //$NON-NLS-1$ //$NON-NLS-2$
        element.addAttribute(new Attribute("separator", ",")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(element);

        for (String clause : valuesClauses) {
            element.addElement(new TextElement(clause));
        }

        parentElement.addElement(answer);
    }
}
