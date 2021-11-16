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
package io.github.rayejun.mybatis.generator.codegen.javamapper;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Set;
import java.util.TreeSet;

public class UpdateBatchMethodGenerator extends
        AbstractJavaMapperMethodGenerator {

    public UpdateBatchMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        FullyQualifiedJavaType parameterType;

        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            parameterType = FullyQualifiedJavaType.getNewListInstance();
            parameterType.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType()));
        } else {
            parameterType = FullyQualifiedJavaType.getNewListInstance();
            parameterType.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        }

        importedTypes.add(parameterType);

        Method method = new Method("updateBatch");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(parameterType, "list")); //$NON-NLS-1$

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        addMapperAnnotations(method);

        if (context.getPlugins()
                .clientUpdateByPrimaryKeySelectiveMethodGenerated(method,
                        interfaze, introspectedTable)) {
            addExtraImports(interfaze);
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    public void addMapperAnnotations(Method method) {
        // extension point for subclasses
    }

    public void addExtraImports(Interface interfaze) {
        // extension point for subclasses
    }
}
