/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mybatis.generator.codegen;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getSetterMethodName;
import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * Java代码生成器
 * @author Jeff Butler
 */
public abstract class AbstractJavaGenerator extends AbstractGenerator {
    
	public abstract List<CompilationUnit> getCompilationUnits(String generatorName);

    /**
     * 生成字段
     * @date 2017年10月26日 下午3:46:54
     * @author Qing Luo
     * @param introspectedColumn
     * @return
     */
    public Field getJavaBeansField(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);
        context.getCommentGenerator().addFieldComment(field,introspectedTable, introspectedColumn);
        return field;
    }
    
    /**
     * 生成字段的getter方法
     * @date 2017年10月26日 下午3:46:33
     * @author Qing Luo
     * @param introspectedColumn
     * @return
     */
    public Method getJavaBeansGetter(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        method.setName(getGetterMethodName(property, fqjt));
        context.getCommentGenerator().addGetterComment(method,introspectedTable, introspectedColumn);
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(property);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    /**
     * 生成字段的setter方法
     * @date 2017年10月26日 下午3:47:04
     * @author Qing Luo
     * @param introspectedColumn
     * @return
     */
    public Method getJavaBeansSetter(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getSetterMethodName(property));
        method.addParameter(new Parameter(fqjt, property));
        context.getCommentGenerator().addSetterComment(method,introspectedTable, introspectedColumn);
        StringBuilder sb = new StringBuilder();
        if (isTrimStringsEnabled() && introspectedColumn.isStringColumn()) {
            sb.append("this.");
            sb.append(property);
            sb.append(" = ");
            sb.append(property);
            sb.append(" == null ? null : ");
            sb.append(property);
            sb.append(".trim();");
            method.addBodyLine(sb.toString());
        } else {
            sb.append("this.");
            sb.append(property);
            sb.append(" = ");
            sb.append(property);
            sb.append(';');
            method.addBodyLine(sb.toString());
        }
        return method;
    }

    public List<Method> getServiceMethods(){
    	List<Method> result = new ArrayList<Method>();
    	return result;
    }
    
    /**
	 * 生成Java方法
	 * @date 2017年10月26日 下午3:46:04
	 * @author Qing Luo
	 * @param field
	 * @return
	 */
    public static Method getGetter(Field field) {
        Method method = new Method();
        method.setName(getGetterMethodName(field.getName(), field.getType()));
        method.setReturnType(field.getType());
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(field.getName());
        sb.append(';');
        method.addBodyLine(sb.toString());
        return method;
    }
    
    /**
     * 生成默认构造器
     * @date 2017年10月26日 下午3:46:57
     * @author Qing Luo
     * @param topLevelClass
     */
    protected void addDefaultConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        method.addBodyLine("super();");
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }
    
    /**
     * 判断是否可用
     * @date 2017年10月26日 下午3:47:02
     * @author Qing Luo
     * @return
     */
    public boolean isTrimStringsEnabled() {
        Properties properties = context.getJavaModelGeneratorConfiguration().getProperties();
        boolean rc = isTrue(properties.getProperty(PropertyRegistry.MODEL_GENERATOR_TRIM_STRINGS));
        return rc;
    }

    /**
     * 生成基类
     * @date 2017年10月26日 下午3:46:59
     * @author Qing Luo
     * @return
     */
    public String getRootClass() {
        String rootClass = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_CLASS);
        if (rootClass == null) {
            Properties properties = context.getJavaModelGeneratorConfiguration().getProperties();
            rootClass = properties.getProperty(PropertyRegistry.ANY_ROOT_CLASS);
        }
        return rootClass;
    }

}