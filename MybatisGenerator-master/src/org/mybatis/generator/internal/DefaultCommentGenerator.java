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

package org.mybatis.generator.internal;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * 注释生成器
 * @author Jeff Butler
 */
public class DefaultCommentGenerator implements CommentGenerator {

    private Properties properties;
    
    private boolean suppressDate;
    
    private boolean suppressAllComments;
    
    /**
     * 当前时间 字符串类型
     */
    private String currentDateStr;
    
    /**
     * 构造方法
     */
    public DefaultCommentGenerator() {
        super();
        properties = new Properties();
        suppressDate = false;
        suppressAllComments = false;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date());
    }

    /**
     * Java文件注释
     */
    public void addJavaFileComment(CompilationUnit compilationUnit,IntrospectedTable introspectedTable) {
    	compilationUnit.addFileCommentLine("/*");
    	String year =  introspectedTable.getContext().getUserConfiguration().getYear();
    	String company =  introspectedTable.getContext().getUserConfiguration().getCompany();
    	
    	compilationUnit.addFileCommentLine(" * Copyright (c) " + year +"," + company );
    	compilationUnit.addFileCommentLine(" * All rights reserved. ");
    	compilationUnit.addFileCommentLine(" */");
    }

    /**
     * Entity类注释、导包
     */
    public void addClassComment(InnerClass innerClass,IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        
        innerClass.addJavaDocLine("import io.swagger.annotations.ApiModelProperty;");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        
        innerClass.addJavaDocLine("/**");
        sb.append(" * 文件名称：");
        sb.append(introspectedTable.getTableConfiguration().getDomainObjectName()+".java");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable() + "表的实体类");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * -------------------------------------------------------");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 当前版本：1.0.0 ");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 作者：" + introspectedTable.getContext().getUserConfiguration().getName());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 完成日期：");
        sb.append(currentDateStr);
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        innerClass.addJavaDocLine(" */");
    }

    /**
     * 从形如" + publicPackageName + ".xxx.yyy.zzz.impl中
     * 获取到" + publicPackageName + ".xxx.yyy.zzz
     * @date 2017年11月7日 下午2:38:09
     * @author Qing Luo
     * @param packageName
     * @return
     */
    private String getPublicPackageName(String packageName){
    	int lastPointIndex = 0;
    	for(int i = 0 ; i < packageName.length() ; i ++){
    		if(packageName.charAt(i) == '.'){
    			lastPointIndex = i;
    		}
    	}
    	String result = packageName.substring(0, lastPointIndex);
    	return result;
    }
    
    /**
     * Entity ServiceImpl Param类注释、导包
     */
    public void addClassComment(String type,InnerClass innerClass,IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
    	String publicPackageName = getPublicPackageName(innerClass.getType().getPackageName());
    	StringBuilder sb = new StringBuilder();  	
    	if("param".equals(type)){
    		innerClass.addJavaDocLine("import io.swagger.annotations.ApiModel;");
    		innerClass.addJavaDocLine("import io.swagger.annotations.ApiModelProperty;");	
    	}else if("serviceImpl".equals(type)){
    		if("true".equals(introspectedTable.getTableConfiguration().getHasPrimaryKey())){
	    		innerClass.addJavaDocLine("import " + getPublicPackageName(publicPackageName) + ".param.SelectByIdParam;");
	    		innerClass.addJavaDocLine("import " + getPublicPackageName(publicPackageName) + ".param.DeleteByIdParam;");
    		}
    		innerClass.addJavaDocLine("import " + getPublicPackageName(publicPackageName) + ".entity." + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ";");
    		innerClass.addJavaDocLine("import " + getPublicPackageName(publicPackageName) + ".service." + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Service;");
    		innerClass.addJavaDocLine("");
    		innerClass.addJavaDocLine("import org.springframework.beans.factory.annotation.Autowired;");
    		innerClass.addJavaDocLine("import com.upsoft.sep.bp.common.log.LogHandler;");
    		innerClass.addJavaDocLine("import com.upsoft.sep.bp.common.entity.Result;");
    		innerClass.addJavaDocLine("import com.upsoft.sep.bp.common.exception.BPException;");
    	}else if("entity".equals(type)){
    		innerClass.addJavaDocLine("import io.swagger.annotations.ApiModelProperty;");
    	}

        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
    	
        innerClass.addJavaDocLine("/**");
        sb.append(" * 文件名称：");
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        if("entity".equals(type)){
            sb.append(domainName +".java");
            innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
            sb.setLength(0);
            sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable() + "表的实体类");
        }else if("serviceImpl".equals(type)){
        	sb.append(domainName + "ServiceImpl.java");
        	innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
            sb.setLength(0);
            sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable() + "表的serviceImpl类"); 
        }else if("param".equals(type)){
        	if(innerClass.getType().getBaseQualifiedName().contains("Delete")){
        		sb.append("DeleteByIdParam.java");
        		innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
                sb.setLength(0);
                sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable() + "表的删除参数");
        	}else if(innerClass.getType().getBaseQualifiedName().contains("Select")){
        		sb.append("SelectByIdParam.java");
        		innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
                sb.setLength(0);
                sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable() + "表的查询参数");
        	}
        }
        
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        
        sb.append(" * -------------------------------------------------------");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        
        sb.append(" * 当前版本：1.0.0 ");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        
        sb.append(" * 作者：" + introspectedTable.getContext().getUserConfiguration().getName());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        
        sb.append(" * 完成日期：");
        sb.append(currentDateStr);
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        innerClass.addJavaDocLine(" */");
        sb.setLength(0);
        
        if("param".equals(type)){
        	if(innerClass.getType().getBaseQualifiedName().contains("Delete")){
        		sb.append("@ApiModel(value=\"" + domainName+"的删除参数" +"\")");    		
        	}else if(innerClass.getType().getBaseQualifiedName().contains("Select")){
        		sb.append("@ApiModel(value=\"" + domainName+"的查询参数" +"\")");
        	}
        	innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
            sb.setLength(0);
        }
    }
    
    /**
     * Entity层属性的注释、注解
     */
    public void addFieldComment(Field field,IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        field.addJavaDocLine("");
        //field.addJavaDocLine("/**");
        field.addJavaDocLine("@ApiModelProperty( value = \"" + introspectedColumn.getRemarks() + "\" ) ");
        //field.addJavaDocLine(" */");
    }
    
    /**
     * Entity层getter方法的注释
     */
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
    }

    /**
     * Entity层setter方法的注释
     */
    public void addSetterComment(Method method,IntrospectedTable introspectedTable,IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
    }
    
    /**
     * mapper层方法的注释
     */
    public void addComment(XmlElement xmlElement) {
        if (suppressAllComments) {
            return;
        }
        String remark = "";
        String operateName = xmlElement.getAttributes().get(0).getValue();
        if(operateName.contains("insert")){
        	remark = "<!-- 新增 -->";
        	xmlElement.addElement(new TextElement( remark ));
        }else if(operateName.contains("delete")){
        	remark = "<!-- 删除 -->";
        	xmlElement.addElement(new TextElement( remark ));
        }else if(operateName.contains("update")){
        	remark = "<!-- 修改 -->";
        	xmlElement.addElement(new TextElement( remark ));
        }else if(operateName.contains("select")){
        	remark = "<!-- 查询 -->";
        	xmlElement.addElement(new TextElement( remark ));
        }        
        StringBuilder sb = new StringBuilder();
        String s = getDateString();
        if (s != null) {
            sb.setLength(0);
            sb.append(" This element was generated on ");
            sb.append(s);
            sb.append('.');
            xmlElement.addElement(new TextElement(sb.toString()));
        }
    }

    /**
     * Dao类注释
     */
    public void addDaoClassComment(Interface interfaze,IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
    	String publicPackageName = getPublicPackageName(interfaze.getType().getPackageName());
    	StringBuilder sb = new StringBuilder();
    	if("true".equals(introspectedTable.getTableConfiguration().getHasPrimaryKey())){
	    	interfaze.addJavaDocLine("import " + publicPackageName + ".param.SelectByIdParam;");
	    	interfaze.addJavaDocLine("import " + publicPackageName + ".param.DeleteByIdParam;");
    	}
    	interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
    	
    	interfaze.addJavaDocLine("/**");
        sb.append(" * 文件名称：");
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        sb.append(domainName + "Dao.java");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable()+"表的Dao类");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * -------------------------------------------------------");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 当前版本：1.0.0 ");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 作者：" + introspectedTable.getContext().getUserConfiguration().getName());
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 完成日期：");
        sb.append(currentDateStr);
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        interfaze.addJavaDocLine(" */");
    }
    
    /**
     * Dao层方法的注释
     */
    public void addGeneralMethodComment(Method method,IntrospectedTable introspectedTable) {
    	String username = introspectedTable.getContext().getUserConfiguration().getName();
    	if (suppressAllComments) {
            return;
        }
        String remark = "";
        String operate = method.getName();
        if(operate.contains("insert")){
        	remark = "新增";
        }else if(operate.contains("delete")){
        	remark = "删除";
        }else if(operate.contains("update")){
        	remark = "修改";
        }else if(operate.contains("select")){
        	remark = "查询";
        }
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * " + remark);
        method.addJavaDocLine(" * @author " + username);
        method.addJavaDocLine(" * @date " + currentDateStr);
        for (Parameter parameter : method.getParameters()) {
        	 method.addJavaDocLine(" * @param " + parameter.getName());
		}
        String returnType = method.getReturnType().toString();
        returnType = returnType.lastIndexOf(".") != -1 ? returnType.substring(returnType.lastIndexOf(".") + 1) : returnType;
        method.addJavaDocLine(" * @return " + returnType);
        method.addJavaDocLine(" */");
    }
    
    /**
     * Service类注释
     */
    public void addServiceClassComment(Interface interfaze,IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
    	String publicPackageName = getPublicPackageName(interfaze.getType().getPackageName());
    	StringBuilder sb = new StringBuilder();
    	interfaze.addJavaDocLine("import io.swagger.annotations.ApiOperation;");
    	interfaze.addJavaDocLine("import javax.ws.rs.Consumes;");
    	interfaze.addJavaDocLine("import javax.ws.rs.POST;");
    	interfaze.addJavaDocLine("import javax.ws.rs.Path;");
    	interfaze.addJavaDocLine("import javax.ws.rs.Produces;");
    	interfaze.addJavaDocLine("");
    	interfaze.addJavaDocLine("import " + publicPackageName + ".entity." + introspectedTable.getFullyQualifiedTable().getDomainObjectName()+ ";");
    	if("true".equals(introspectedTable.getTableConfiguration().getHasPrimaryKey())){
	    	interfaze.addJavaDocLine("import " + publicPackageName + ".param.SelectByIdParam;");
	    	interfaze.addJavaDocLine("import " + publicPackageName + ".param.DeleteByIdParam;");
    	}
    	interfaze.addJavaDocLine("import com.upsoft.sep.bp.common.entity.Result;");
    	interfaze.addJavaDocLine("import com.upsoft.sep.bp.common.util.GlobalRestApiAction;");
    	interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        interfaze.addJavaDocLine("/**");
        sb.append(" * 文件名称：");
        String domainName = introspectedTable.getTableConfiguration().getDomainObjectName();
        sb.append(domainName + "Service.java");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 摘要：" + introspectedTable.getFullyQualifiedTable()+"表的service类" );
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * -------------------------------------------------------");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 当前版本：1.0.0 ");
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 作者：" + introspectedTable.getContext().getUserConfiguration().getName());
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        sb.append(" * 完成日期：");
        sb.append(currentDateStr);
        interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        interfaze.addJavaDocLine(" */");
        sb.setLength(0);
    	sb.append("@Path(\"/ExenvPoint\")");
    	interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
    	sb.append("@Consumes({\"application/json\"})");
    	interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
    	sb.append("@Produces({ \"application/json; charset=UTF-8\" })");
    	interfaze.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
    }
    
    /**
     * 枚举的注释
     */
    public void addEnumComment(InnerEnum innerEnum,IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerEnum.addJavaDocLine("/**");
        innerEnum.addJavaDocLine(" * This enum was generated by MyBatis Generator.");
        sb.append(" * This enum corresponds to the database table ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString());
        addJavadocTag(innerEnum, false);
        innerEnum.addJavaDocLine(" */");
    }
    
    /**
     * 默认情况下添加文档级注释
     */
    public void addRootComment(XmlElement rootElement) {
        return;
    }

    /**
     * 新增配置资源
     */
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }

    /**
     * 新增自定义标签
     */
    protected void addJavadocTag(JavaElement javaElement,boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * 获取格式化的日期字符串
     */
    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else {
            return new Date().toString();
        }
    }
  
    /**
     * 字段注释
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * This field was generated by MyBatis Generator.");
        sb.append(" * This field corresponds to the database table ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        field.addJavaDocLine(sb.toString());
        addJavadocTag(field, false);
        field.addJavaDocLine(" */");
    }
  
}