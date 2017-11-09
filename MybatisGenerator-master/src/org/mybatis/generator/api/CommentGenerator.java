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
package org.mybatis.generator.api;

import java.util.Properties;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 注释生成器
 * @author Jeff Butler
 */
public interface CommentGenerator {

    /**
     * Adds properties for this instance from any properties configured in the
     * CommentGenerator configuration.
     * 
     * This method will be called before any of the other methods.
     * 
     * @param properties
     *            All properties from the configuration
     */
    void addConfigurationProperties(Properties properties);

    /**
     * This method should add a Javadoc comment to the specified field. The
     * field is related to the specified table and is used to hold the value of
     * the specified column.
     * <p>
     * 
     * <b>Important:</b> This method should add a the nonstandard JavaDoc tag
     * "@mbggenerated" to the comment. Without this tag, the Eclipse based Java
     * merge feature will fail.
     * 
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addFieldComment(Field field,IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn);

    /**
     * 生成字段注释
     * @date 2017年10月27日 下午4:19:50
     * @author Qing Luo
     * @param field
     * @param introspectedTable
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable);

    /**
     * 生成Entity类注释
     * @date 2017年10月27日 下午4:19:44
     * @author Qing Luo
     * @param innerClass
     * @param introspectedTable
     */
    public void addClassComment(InnerClass innerClass,IntrospectedTable introspectedTable);

    /**
     * 生成Entity类注释
     * @date 2017年10月27日 下午4:20:04
     * @author Qing Luo
     * @param innerClass
     * @param introspectedTable
     * @param markAsDoNotDelete
     */
    public void addClassComment(String type,InnerClass innerClass,IntrospectedTable introspectedTable, boolean markAsDoNotDelete);

    /**
     * 生成枚举注释
     * @date 2017年10月27日 下午4:20:02
     * @author Qing Luo
     * @param innerEnum
     * @param introspectedTable
     */
    public void addEnumComment(InnerEnum innerEnum,IntrospectedTable introspectedTable);

    /**
     * 生成Entity getter方法注释
     * @date 2017年10月27日 下午4:20:00
     * @author Qing Luo
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn);

    /**
     * 生成Entity setter方法注释
     * @date 2017年10月27日 下午4:19:58
     * @author Qing Luo
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addSetterComment(Method method,IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn);

    /**
     * 生成构造方法方法注释
     * @date 2017年10月27日 下午4:19:56
     * @author Qing Luo
     * @param method
     * @param introspectedTable
     */
    public void addGeneralMethodComment(Method method,IntrospectedTable introspectedTable);

    /**
     * 生成dao类注释
     * @date 2017年10月27日 下午4:19:54
     * @author Qing Luo
     * @param interfaze
     * @param introspectedTable
     * @param b
     */
    void addDaoClassComment(Interface interfaze,IntrospectedTable introspectedTable, boolean b);
    
    /**
     * 生成service类注释
     * @date 2017年11月7日 上午10:00:17
     * @author Qing Luo
     * @param interfaze
     * @param introspectedTable
     * @param b
     */
    void addServiceClassComment(Interface interfaze,IntrospectedTable introspectedTable, boolean b);
    
    /**
     * This method is called to add a file level comment to a generated java
     * file. This method could be used to add a general file comment (such as a
     * copyright notice). However, note that the Java file merge function in
     * Eclipse does not deal with this comment. If you run the generator
     * repeatedly, you will only retain the comment from the initial run.
     * <p>
     * 
     * The default implementation does nothing.
     * 
     * @param compilationUnit
     */
    public void addJavaFileComment(CompilationUnit compilationUnit,IntrospectedTable introspectedTable);

    /**
     * This method should add a suitable comment as a child element of the
     * specified xmlElement to warn users that the element was generated and is
     * subject to regeneration.
     * 
     * @param xmlElement
     */
    public void addComment(XmlElement xmlElement);

    /**
     * This method is called to add a comment as the first child of the root
     * element. This method could be used to add a general file comment (such as
     * a copyright notice). However, note that the XML file merge function does
     * not deal with this comment. If you run the generator repeatedly, you will
     * only retain the comment from the initial run.
     * <p>
     * 
     * The default implementation does nothing.
     * 
     * @param rootElement
     */
    public void addRootComment(XmlElement rootElement);
	
}