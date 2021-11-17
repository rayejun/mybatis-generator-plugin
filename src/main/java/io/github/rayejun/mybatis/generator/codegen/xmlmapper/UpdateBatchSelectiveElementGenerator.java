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
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class UpdateBatchSelectiveElementGenerator extends
        AbstractXmlElementGenerator {

    public UpdateBatchSelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", "updateBatchSelective")); //$NON-NLS-1$

        answer.addAttribute(new Attribute("parameterType", "java.util.List")); //$NON-NLS-1$ //$NON-NLS-2$

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement element = new XmlElement("trim");
        element.addAttribute(new Attribute("prefix", "set"));
        element.addAttribute(new Attribute("suffixOverrides", ","));
        answer.addElement(element);

        for (IntrospectedColumn introspectedColumn :
                ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            sb.setLength(0);

            XmlElement columnTrimElement = new XmlElement("trim");
            columnTrimElement.addAttribute(new Attribute("prefix", introspectedColumn.getActualColumnName() + " = case"));
            columnTrimElement.addAttribute(new Attribute("suffix", "end,"));
            element.addElement(columnTrimElement);

            XmlElement columnElement = new XmlElement("foreach");
            columnElement.addAttribute(new Attribute("collection", "list"));
            columnElement.addAttribute(new Attribute("item", "item"));
            columnElement.addAttribute(new Attribute("index", "index"));
            columnTrimElement.addElement(columnElement);

            XmlElement isNotNullElement = new XmlElement("if");
            isNotNullElement.addAttribute(new Attribute("test", "item." + introspectedColumn.getJavaProperty() + " != null"));
            columnElement.addElement(isNotNullElement);
            sb.setLength(0);
            sb.append("when ");

            boolean and = false;
            for (IntrospectedColumn primaryKeyColumn : introspectedTable.getPrimaryKeyColumns()) {
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(primaryKeyColumn));
                if (and) {
                    sb.append("  and ");
                } else {
                    sb.append(" = ");
                    and = true;
                }
                sb.append(MyBatis3FormattingUtilities.getParameterClause(primaryKeyColumn).replace(primaryKeyColumn.getJavaProperty(), "item." + primaryKeyColumn.getJavaProperty()));
            }
            sb.append(" then ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn).replace(introspectedColumn.getJavaProperty(), "item." + introspectedColumn.getJavaProperty()));
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and "); //$NON-NLS-1$
            } else {
                sb.append("where "); //$NON-NLS-1$
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" in "); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));

            XmlElement whereElement = new XmlElement("foreach"); //$NON-NLS-1$
            whereElement.addAttribute(new Attribute("collection", "list")); //$NON-NLS-1$ //$NON-NLS-2$
            whereElement.addAttribute(new Attribute("item", "item")); //$NON-NLS-1$ //$NON-NLS-2$
            whereElement.addAttribute(new Attribute("open", "(")); //$NON-NLS-1$ //$NON-NLS-2$
            whereElement.addAttribute(new Attribute("separator", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            whereElement.addAttribute(new Attribute("close", ")")); //$NON-NLS-1$ //$NON-NLS-2$
            answer.addElement(whereElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn).replace(introspectedColumn.getJavaProperty(), "item." + introspectedColumn.getJavaProperty()));
            whereElement.addElement(new TextElement(sb.toString()));
        }

        parentElement.addElement(answer);
    }
}
