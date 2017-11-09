/*
 *  Copyright 2009 The Apache Software Foundation
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

package org.mybatis.generator.codegen.mybatis3.javamapper;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.CountByExampleMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.DeleteByExampleMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.DeleteByPrimaryKeyMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.InsertSelectiveMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByExampleWithBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByExampleWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByPrimaryKeyMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByExampleSelectiveMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByExampleWithBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByExampleWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeySelectiveMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeyWithBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.UpdateByPrimaryKeyWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * Dao层代码生成器 实现层
 * @author Jeff Butler
 */
public class JavaMapperGenerator extends AbstractJavaClientGenerator {

    /**
     * 构造方法
     */
    public JavaMapperGenerator() {
        super(true);
    }

    /**
     * 构造方法
     */
    public JavaMapperGenerator(boolean requiresMatchedXMLGenerator) {
        super(requiresMatchedXMLGenerator);
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
     * 核心：获取CompilationUnits
     */
    @Override
    public List<CompilationUnit> getCompilationUnits(String generatorName) {
    	String publicPackageName = getPublicPackageName(this.context.getJavaModelGeneratorConfiguration().getTargetPackage());
    	progressCallback.startTask(getString("Progress.17",introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();
        //通过参数generatorName判断要生成哪层的Java代码
        //从而修改FullyQualifiedJavaType
    	String packageName = publicPackageName + ".dao";
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName() + "Dao";
    	FullyQualifiedJavaType type = new FullyQualifiedJavaType(className , packageName + "." + className ,
    		true,packageName, false,null,new ArrayList<FullyQualifiedJavaType>(), false,false,false);
        
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addDaoClassComment(interfaze, introspectedTable,false);
        commentGenerator.addJavaFileComment(interfaze,introspectedTable);
        
        String rootInterface = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration().getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }
        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }
        
        addSelectByPrimaryKeyMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        if("true".equals(this.introspectedTable.getTableConfiguration().getHasPrimaryKey())){
        	//要生成的表有主键 则生成删改查方法
        	Method selectMethod = interfaze.getMethods().get(0);
	        Method deleteMethod = interfaze.getMethods().get(1);
	        interfaze.setMethods(new ArrayList<Method>());
	        FullyQualifiedJavaType selectIdType = new FullyQualifiedJavaType("SelectByIdParam",publicPackageName + ".param.SelectByIdParam",
	        		true,publicPackageName + ".param", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
	        FullyQualifiedJavaType deleteIdType = new FullyQualifiedJavaType("DeleteByIdParam",publicPackageName + ".param.SelectByIdParam",
	        		true,publicPackageName + ".param", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
	        List<Parameter> selectParameters = new ArrayList<Parameter>();
	        List<Parameter> deleteParameters = new ArrayList<Parameter>();
	        Parameter selectParam = new Parameter(selectIdType, "selectIdParam", false);
	        Parameter deleteIdParam = new Parameter(deleteIdType, "deleteIdParam", false);
	        //重新加入selectById方法
	        selectParameters.add(selectParam);
	        selectMethod.setParameters(selectParameters);
	        interfaze.addMethod(selectMethod);
	        //重新加入deleteById方法
	        deleteParameters.add(deleteIdParam);
	        deleteMethod.setParameters(deleteParameters);
	        interfaze.addMethod(deleteMethod);
	        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        }        
        addInsertSelectiveMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null,introspectedTable)) {
            answer.add(interfaze);
        }
        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }
        return answer;
    }
    
    protected void addCountByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateCountByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new CountByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByExampleMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByExample()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByExampleMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addInsertSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByExampleWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByExampleWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByExampleWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addSelectByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByExampleWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByExampleWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void initializeAndExecuteGenerator(AbstractJavaMapperMethodGenerator methodGenerator,Interface interfaze) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }

    public List<CompilationUnit> getExtraCompilationUnits() {
        return null;
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLMapperGenerator();
    }
    
}