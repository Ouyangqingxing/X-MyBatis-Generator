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
package org.mybatis.generator.codegen.mybatis3.model;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;

/**
 * Entity/Service/ServiceImpl 层代码生成器 实现层
 * @author Jeff Butler
 */
public class BaseRecordGenerator extends AbstractJavaGenerator {
	
    public BaseRecordGenerator() {
        super();
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
    
    @Override
    public List<CompilationUnit> getCompilationUnits(String generatorName) {
    	String publicPackageName = getPublicPackageName(this.context.getJavaModelGeneratorConfiguration().getTargetPackage());
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.8", table.toString()));
        //通过参数generatorName判断要生成哪层的Java代码
        List<CompilationUnit> result = null;
        if(generatorName.contains("javaModelGenerators")){
        	result = getDaoCompilationUnits(publicPackageName);
        }else if(generatorName.contains("serviceGenerators")){
        	result = getServiceCompilationUnits(publicPackageName);
        }else if(generatorName.contains("serviceImplGenerators")){
        	result = getServiceImplCompilationUnits(publicPackageName);
        }else{
        	result = getParamCompilationUnits(publicPackageName);
        }
        return result;
    }

    /**
     * entity层代码生成
     * @date 2017年10月30日 下午3:19:08
     * @author Qing Luo
     * @return
     */
    private List<CompilationUnit> getDaoCompilationUnits(String publicPackageName){
    	Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();
    	String packageName = publicPackageName + ".entity";
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName();
    	FullyQualifiedJavaType type = new FullyQualifiedJavaType(className , packageName + "." + className , true,
    			packageName, false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	
    	TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass,introspectedTable);
        commentGenerator.addClassComment("entity",topLevelClass, introspectedTable,false);
        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();
        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);
            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }
        String rootClass = getRootClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings).containsProperty(introspectedColumn)) {
                continue;
            }
            Field field = getJavaBeansField(introspectedColumn);
            if (plugins.modelFieldGenerated(field, topLevelClass,introspectedColumn, introspectedTable,Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }
            Method method = getJavaBeansGetter(introspectedColumn);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,introspectedColumn, introspectedTable,Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }
            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,introspectedColumn, introspectedTable,Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
        }
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }
    
    /**
     * service层代码生成
     * @date 2017年10月30日 下午3:19:13
     * @author Qing Luo
     * @return
     */
    private List<CompilationUnit> getServiceCompilationUnits(String publicPackageName){
        String username = this.context.getUserConfiguration().getName();
    	CommentGenerator commentGenerator = context.getCommentGenerator();
    	String packageName = publicPackageName + ".service";
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName() + "Service";
    	FullyQualifiedJavaType type = new FullyQualifiedJavaType(className , packageName + "." + className , true,
    			packageName, false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);

    	Interface interfaze = new Interface(type);
    	interfaze.setVisibility(JavaVisibility.PUBLIC);
    	commentGenerator.addServiceClassComment(interfaze, introspectedTable,false);
        commentGenerator.addJavaFileComment(interfaze,introspectedTable);
        //添加方法 如果该表拥有主键，则生成增删改查方法，否则只生成新增方法
        interfaze.addMethod(getServiceAddMethod(publicPackageName,username));
        if("true".equals(this.introspectedTable.getTableConfiguration().getHasPrimaryKey())){
        	interfaze.addMethod(getServiceDeleteMethod(publicPackageName,username));
            interfaze.addMethod(getServiceUpdateMethod(publicPackageName,username));
            interfaze.addMethod(getServiceSelectMethod(publicPackageName,username));
        }
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(interfaze);
        return answer;
    }

    /**
     * service层 新增数据方法
     * @date 2017年11月6日 下午5:38:23
     * @author Qing Luo
     * @return
     */
    @SuppressWarnings("deprecation")
	private Method getServiceAddMethod(String publicPackageName,String username){    
    	FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(this.introspectedTable.getTableConfiguration().getDomainObjectName(),
    			publicPackageName + ".entity."+this.introspectedTable.getTableConfiguration().getDomainObjectName(),
        		true,publicPackageName + ".entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
 
    	Method insertMethod = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("public static final String TAG = \"/" + this.introspectedTable.getTableConfiguration().getDomainObjectName() + "\";");
    	javaDocLines.add("");
    	javaDocLines.add("/**");
    	javaDocLines.add(" * 新增");
    	javaDocLines.add(" * @date " + new Date().toLocaleString());
    	javaDocLines.add(" * @author " + username);
    	javaDocLines.add(" * @return");
    	javaDocLines.add(" */");
    	javaDocLines.add("@POST");
    	javaDocLines.add("@Path(GlobalRestApiAction.ADD+\"/" + this.introspectedTable.getTableConfiguration().getDomainObjectName() + "\")");
    	javaDocLines.add("@ApiOperation(hidden = false, value = \"新增\", notes = \"参数{id:'1'}\", tags = TAG)");
    	insertMethod.setJavaDocLines(javaDocLines);
    	
    	insertMethod.setVisibility(JavaVisibility.PUBLIC);
    	insertMethod.setName("add"+ this.introspectedTable.getTableConfiguration().getDomainObjectName());
    	insertMethod.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, this.introspectedTable.getTableConfiguration().getDomainObjectName(), false);
    	parameters.add(param);
    	insertMethod.setParameters(parameters);
    	
    	return insertMethod;
    }
    
    /**
     * service层 更新数据方法
     * @date 2017年11月6日 下午5:40:59
     * @author Qing Luo
     * @return
     */
    @SuppressWarnings("deprecation")
	private Method getServiceUpdateMethod(String publicPackageName,String username){
    	FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(this.introspectedTable.getTableConfiguration().getDomainObjectName(),
    			publicPackageName + ".entity."+this.introspectedTable.getTableConfiguration().getDomainObjectName(),
        		true,publicPackageName + ".entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
 
    	Method updateMethod = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("/**");
    	javaDocLines.add(" * 修改 根据id");
    	javaDocLines.add(" * @date " + new Date().toLocaleString());
    	javaDocLines.add(" * @author " + username);
    	javaDocLines.add(" * @return");
    	javaDocLines.add(" */");
    	javaDocLines.add("@POST");
    	javaDocLines.add("@Path(GlobalRestApiAction.MOD+\"/" 
    			+ this.introspectedTable.getTableConfiguration().getDomainObjectName()+"ById" + "\")");
    	javaDocLines.add("@ApiOperation(hidden = false, value = \"修改 根据id\", notes = \"参数{id:'1'}\", tags = TAG)");
    	updateMethod.setJavaDocLines(javaDocLines);
    	
    	updateMethod.setVisibility(JavaVisibility.PUBLIC);
    	updateMethod.setName("update"+ this.introspectedTable.getTableConfiguration().getDomainObjectName() + "ById");
    	updateMethod.setReturnType(returnType);
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, this.introspectedTable.getTableConfiguration().getDomainObjectName(), false);
    	parameters.add(param);
    	updateMethod.setParameters(parameters);
    	
    	return updateMethod;
    }
    
    /**
     * service层 删除数据方法
     * @date 2017年11月6日 下午5:40:57
     * @author Qing Luo
     * @return
     */
    @SuppressWarnings("deprecation")
	private Method getServiceDeleteMethod(String publicPackageName,String username){    	
    	FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("DeleteByIdParam",publicPackageName + ".param.DeleteByIdParam",
        		true,publicPackageName + ".param", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	Method deleteMethod = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("/**");
    	javaDocLines.add(" * 删除 根据id");
    	javaDocLines.add(" * @date " + new Date().toLocaleString());
    	javaDocLines.add(" * @author " + username);
    	javaDocLines.add(" * @param 删除参数");
    	javaDocLines.add(" * @return");
    	javaDocLines.add(" */");
    	javaDocLines.add("@POST");
    	javaDocLines.add("@Path(GlobalRestApiAction.DEL+\"/" + 
    			this.introspectedTable.getTableConfiguration().getDomainObjectName() +"ById" + "\")");
    	javaDocLines.add("@ApiOperation(hidden = false, value = \"删除 根据id\", notes = \"参数{id:'1'}\", tags = TAG)");
    	deleteMethod.setJavaDocLines(javaDocLines);
    	
    	deleteMethod.setVisibility(JavaVisibility.PUBLIC);
    	deleteMethod.setName("delete"+ this.introspectedTable.getTableConfiguration().getDomainObjectName() + "ById");
    	deleteMethod.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, "deleteByIdParam", false);
    	parameters.add(param);
    	deleteMethod.setParameters(parameters);
    	return deleteMethod;
    }
    
    /**
     * service层 查询数据方法
     * @date 2017年11月6日 下午5:40:54
     * @author Qing Luo
     * @return
     */
    @SuppressWarnings("deprecation")
	private Method getServiceSelectMethod(String publicPackageName,String username){
    	FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("SelectByIdParam",publicPackageName + ".param.SelectByIdParam",
        		true,publicPackageName + ".param", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	Method selectMethod = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("/**");
    	javaDocLines.add(" * 查询 根据id");
    	javaDocLines.add(" * @date " + new Date().toLocaleString());
    	javaDocLines.add(" * @author " + username);
    	javaDocLines.add(" * @param 查询参数");
    	javaDocLines.add(" * @return");
    	javaDocLines.add(" */");
    	javaDocLines.add("@POST");
    	javaDocLines.add("@Path(GlobalRestApiAction.GET+\"/" + 
    			this.introspectedTable.getTableConfiguration().getDomainObjectName() +"ById" + "\")");
    	javaDocLines.add("@ApiOperation(hidden = false, value = \"查询 根据id\", notes = \"参数{id:'1'}\", tags = TAG)");
    	selectMethod.setJavaDocLines(javaDocLines);
    	
    	selectMethod.setVisibility(JavaVisibility.PUBLIC);
    	selectMethod.setName("get"+ this.introspectedTable.getTableConfiguration().getDomainObjectName() + "ById");
    	selectMethod.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, "selectByIdParam", false);
    	parameters.add(param);
    	selectMethod.setParameters(parameters);
    	return selectMethod;
    }
    
    /**
     * serviceImpl层代码生成
     * @date 2017年10月30日 下午3:19:15
     * @author Qing Luo
     * @return
     */
	private List<CompilationUnit> getServiceImplCompilationUnits(String publicPackageName){
	    CommentGenerator commentGenerator = context.getCommentGenerator();
		String packageName = publicPackageName + ".service.impl";
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName() + "ServiceImpl";
    	String superPackageName = publicPackageName + ".service.impl";
    	String superClassName = this.introspectedTable.getTableConfiguration().getDomainObjectName() + "Service";
    	FullyQualifiedJavaType type = new FullyQualifiedJavaType(className , packageName + "." + className , true,
    			packageName, false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType superClassType = new FullyQualifiedJavaType(
    			superClassName, superPackageName + "." + superClassName , true,
    			superPackageName, false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
		TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        Set<FullyQualifiedJavaType> superInterfaceTypes = new TreeSet<FullyQualifiedJavaType>();
        superInterfaceTypes.add(superClassType);
        topLevelClass.setSuperInterfaceTypes(superInterfaceTypes);
        commentGenerator.addJavaFileComment(topLevelClass,introspectedTable);
        commentGenerator.addClassComment("serviceImpl",topLevelClass, introspectedTable,false);
        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);
            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }
        String daoName = this.introspectedTable.getTableConfiguration().getDomainObjectName() + "Dao";
    	String first = daoName.charAt(0) + "";
    	first = first.toLowerCase();
    	daoName = first + daoName.substring(1);
        //加入成员变量
        Field DaoField = getDaoField(daoName,publicPackageName);
    	topLevelClass.addField(DaoField);
        topLevelClass.addImportedType(DaoField.getType());
        //加入方法 如果该表拥有主键，则生成增删改查方法，否则只生成新增方法
        topLevelClass.addMethod(getServiceImplAddMethod(daoName,publicPackageName));
        if("true".equals(this.introspectedTable.getTableConfiguration().getHasPrimaryKey())){
	        topLevelClass.addMethod(getServiceImplDeleteMethod(daoName,publicPackageName));
	        topLevelClass.addMethod(getServiceImplUpdateMethod(daoName,publicPackageName));
	        topLevelClass.addMethod(getServiceImplSeleceMethod(daoName,publicPackageName));
        }
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        return answer;
	}
	
	/**
	 * 获取成员变量Dao
	 * @date 2017年11月7日 上午10:52:24
	 * @author Qing Luo
	 * @return
	 */
	private Field getDaoField(String daoName,String publicPackageName){
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(this.introspectedTable.getTableConfiguration().getDomainObjectName()+"Dao" , 
				publicPackageName + ".dao."+ this.introspectedTable.getTableConfiguration().getDomainObjectName() +"Dao", true,
				publicPackageName + ".dao", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
		Field field = new Field();
    	field.setVisibility(JavaVisibility.PRIVATE);	
    	field.setName(daoName);
    	
    	field.setType(type);
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("");
    	javaDocLines.add("LogHandler LOG = LogHandler.getLogHandler("
    			+ this.introspectedTable.getTableConfiguration().getDomainObjectName()
    			+ "ServiceImpl.class);");
    	javaDocLines.add("");
    	javaDocLines.add("@Autowired");
    	field.setJavaDocLines(javaDocLines);
    	return field;
	}
	
	/**
	 * serviceImpl层 新增方法
	 * @date 2017年11月7日 上午11:20:12
	 * @author Qing Luo
	 * @return
	 */
	private Method getServiceImplAddMethod(String daoName,String publicPackageName){
		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(this.introspectedTable.getTableConfiguration().getDomainObjectName(),
				publicPackageName + ".entity."+this.introspectedTable.getTableConfiguration().getDomainObjectName(),
        		true,publicPackageName + ".entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName();
    	Method method = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("@Override");
    	method.setJavaDocLines(javaDocLines);
    	method.setVisibility(JavaVisibility.PUBLIC);
    	method.setName("add"+ className );
    	method.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, "record" , false);
    	parameters.add(param);
    	method.setParameters(parameters);
    	
		List<String> getIdBodyLines = new ArrayList<String>();
		getIdBodyLines.add("Result<String> result = new Result<String>();");
		getIdBodyLines.add("try {");
		getIdBodyLines.add("//1.新增数据");
		getIdBodyLines.add(daoName + ".insert(record);");
		getIdBodyLines.add("");
		getIdBodyLines.add("result.setMsg(\"新增成功\");");
		getIdBodyLines.add("result.setStatus(Result.STATUS_SUCCES);");
		getIdBodyLines.add("} catch (Exception e) {");
		getIdBodyLines.add("LOG.error(\"新增失败!\",e);");
		getIdBodyLines.add("throw new BPException(\"\",\"新增失败!\");");
		getIdBodyLines.add("}");
		getIdBodyLines.add("return result;");
		method.setBodyLines(getIdBodyLines);
		return method;
	}
	
	/**
	 * serviceImpl层 删除方法
	 * @date 2017年11月7日 上午11:20:08
	 * @author Qing Luo
	 * @return
	 */
	private Method getServiceImplDeleteMethod(String daoName,String publicPackageName){
		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("DeleteByIdParam",publicPackageName + ".param.DeleteByIdParam",
        		true,publicPackageName + ".param", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName();
    	Method method = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("@Override");
    	method.setJavaDocLines(javaDocLines);
    	method.setVisibility(JavaVisibility.PUBLIC);
    	method.setName("delete"+ className + "ById");
    	method.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, "deleteByIdParam" , false);
    	parameters.add(param);
    	method.setParameters(parameters);
    	
		List<String> getIdBodyLines = new ArrayList<String>();
		getIdBodyLines.add("Result<String> result = new Result<String>();");
		getIdBodyLines.add("try {");
		getIdBodyLines.add("//1.删除数据");
		getIdBodyLines.add(daoName + ".deleteById(deleteByIdParam);");
		getIdBodyLines.add("");
		getIdBodyLines.add("result.setMsg(\"删除成功\");");
		getIdBodyLines.add("result.setStatus(Result.STATUS_SUCCES);");
		getIdBodyLines.add("} catch (Exception e) {");
		getIdBodyLines.add("LOG.error(\"删除失败!\",e);");
		getIdBodyLines.add("throw new BPException(\"\",\"删除失败!\");");
		getIdBodyLines.add("}");
		getIdBodyLines.add("return result;");
		method.setBodyLines(getIdBodyLines);
		return method;
	}
	
	/**
	 * serviceImpl层 更新方法
	 * @date 2017年11月7日 上午11:20:06
	 * @author Qing Luo
	 * @return
	 */
	private Method getServiceImplUpdateMethod(String daoName,String publicPackageName){
		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(this.introspectedTable.getTableConfiguration().getDomainObjectName(),
				publicPackageName + ".entity."+this.introspectedTable.getTableConfiguration().getDomainObjectName(),
        		true,publicPackageName + ".entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName();
    	Method method = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("@Override");
    	method.setJavaDocLines(javaDocLines);
    	method.setVisibility(JavaVisibility.PUBLIC);
    	method.setName("update"+ className + "ById" );
    	method.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, "record" , false);
    	parameters.add(param);
    	method.setParameters(parameters);
    	
		List<String> getIdBodyLines = new ArrayList<String>();
		getIdBodyLines.add("Result<String> result = new Result<String>();");
		getIdBodyLines.add("try {");
		getIdBodyLines.add("//1.修改数据");
		getIdBodyLines.add(daoName + ".updateById(record);");
		getIdBodyLines.add("");
		getIdBodyLines.add("result.setMsg(\"修改成功\");");
		getIdBodyLines.add("result.setStatus(Result.STATUS_SUCCES);");
		getIdBodyLines.add("} catch (Exception e) {");
		getIdBodyLines.add("LOG.error(\"修改失败!\",e);");
		getIdBodyLines.add("throw new BPException(\"\",\"修改失败!\");");
		getIdBodyLines.add("}");
		getIdBodyLines.add("return result;");
		method.setBodyLines(getIdBodyLines);
		return method;
	}
	
	/**
	 * serviceImpl层 查询方法
	 * @date 2017年11月7日 上午11:19:52
	 * @author Qing Luo
	 * @return
	 */
	private Method getServiceImplSeleceMethod(String daoName,String publicPackageName){
		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("SelectByIdParam",publicPackageName + ".param.SelectByIdParam",
        		true,publicPackageName + ".param", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Result","com.upsoft.sep.bp.common.entity.Result",
    			true,"com.upsoft.sep.bp.common.entity", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	String className = this.introspectedTable.getTableConfiguration().getDomainObjectName();
    	Method method = new Method();
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("@Override");
    	method.setJavaDocLines(javaDocLines);
    	method.setVisibility(JavaVisibility.PUBLIC);
    	method.setName("get"+ className + "ById" );
    	method.setReturnType(returnType);
    	
    	List<Parameter> parameters = new ArrayList<Parameter>();
    	Parameter param = new Parameter(paramType, "selectIdParam" , false);
    	parameters.add(param);
    	method.setParameters(parameters);
    	
		List<String> getIdBodyLines = new ArrayList<String>();
		getIdBodyLines.add("Result<" + className + "> result = new Result<" + className + ">();");
		getIdBodyLines.add("try {");
		getIdBodyLines.add("//1.查询数据");
		getIdBodyLines.add(className + " data = "+daoName + ".selectById(selectIdParam);");
		getIdBodyLines.add("");
		getIdBodyLines.add("result.setData(data);");
		getIdBodyLines.add("result.setMsg(\"查询成功\");");
		getIdBodyLines.add("result.setStatus(Result.STATUS_SUCCES);");
		getIdBodyLines.add("} catch (Exception e) {");
		getIdBodyLines.add("LOG.error(\"查询失败!\",e);");
		getIdBodyLines.add("throw new BPException(\"\",\"查询失败!\");");
		getIdBodyLines.add("}");
		getIdBodyLines.add("return result;");
		method.setBodyLines(getIdBodyLines);
		return method;
	}
	
	/**
	 * param层代码生成
	 * @date 2017年11月6日 上午11:03:09
	 * @author Qing Luo
	 * @return
	 */
	private List<CompilationUnit> getParamCompilationUnits(String publicPackageName){
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
    	String packageName = publicPackageName + ".param";
    	//要生成的param类的类名
    	String deleteParamClassName = "DeleteByIdParam";
    	String selectParamClassName = "SelectByIdParam";
    	FullyQualifiedJavaType deleteByIdType = new FullyQualifiedJavaType(deleteParamClassName,packageName+"."+deleteParamClassName,
    		true,packageName, false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	FullyQualifiedJavaType selectByIdType = new FullyQualifiedJavaType(selectParamClassName,packageName+"."+selectParamClassName, 
    		true,packageName, false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
    	
        if (context.getPlugins().modelBaseRecordClassGenerated(getTopLevelClass(deleteByIdType), introspectedTable)) {
            answer.add(getTopLevelClass(deleteByIdType));
        }
        if (context.getPlugins().modelBaseRecordClassGenerated(getTopLevelClass(selectByIdType), introspectedTable)) {
            answer.add(getTopLevelClass(selectByIdType));
        }
        return answer;
    }
	
	/**
	 * param层代码 获取TopLevelClass
	 * @date 2017年11月6日 上午11:25:18
	 * @author Qing Luo
	 * @param type
	 * @return
	 */
	private TopLevelClass getTopLevelClass(FullyQualifiedJavaType type){
		FullyQualifiedJavaType stringType = new FullyQualifiedJavaType("String","java.lang.String",
        		true,"java.lang", false,null, new ArrayList<FullyQualifiedJavaType>(), false,false,false);
		CommentGenerator commentGenerator = context.getCommentGenerator();
		TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass,introspectedTable);
        commentGenerator.addClassComment("param",topLevelClass, introspectedTable,false);
        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);
            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }
        //加入成员变量	依次设置访问权限符、变量名、类型、注释
    	Field field = new Field();
    	field.setVisibility(JavaVisibility.PRIVATE);
    	field.setName("id");
    	field.setType(stringType);
    	List<String> javaDocLines = new ArrayList<String>();
    	javaDocLines.add("");
    	javaDocLines.add("@ApiModelProperty( value = \"id\" )");
    	field.setJavaDocLines(javaDocLines);
    	topLevelClass.addField(field);
        topLevelClass.addImportedType(field.getType());
        //加入方法 依次设置方法名、返回类型、参数、访问权限符、代码段
        //getId方法
        Method getIdMethod = new Method();
        getIdMethod.setName("getId");
        getIdMethod.setReturnType(stringType);
        getIdMethod.setVisibility(JavaVisibility.PUBLIC);
        List<String> getIdBodyLines = new ArrayList<String>();
        getIdBodyLines.add("return id;");
        getIdMethod.setBodyLines(getIdBodyLines);
        //setId方法
        Method setIdMethod = new Method();
        setIdMethod.setName("setId");
        setIdMethod.setVisibility(JavaVisibility.PUBLIC);
        List<Parameter> parameters = new ArrayList<Parameter>();
        Parameter param = new Parameter(stringType, "id", false);
        parameters.add(param);
        setIdMethod.setParameters(parameters);
        List<String> setIdBodyLines = new ArrayList<String>();
        setIdBodyLines.add("this.id = id;");
        setIdMethod.setBodyLines(setIdBodyLines);
        
        topLevelClass.addMethod(getIdMethod);
        topLevelClass.addMethod(setIdMethod);
         
        return topLevelClass;
	}
	
    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }
        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass() && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass() && introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        List<IntrospectedColumn> constructorColumns = includeBLOBColumns() ? introspectedTable.getAllColumns() :introspectedTable.getNonBLOBColumns();
        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty()));
        }
        StringBuilder sb = new StringBuilder();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super(");
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", ");
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
            }
            sb.append(");");
            method.addBodyLine(sb.toString());
        }
        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.setLength(0);
            sb.append("this.");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = ");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }
        topLevelClass.addMethod(method);
    }
    
    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                        .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }
        return introspectedColumns;
    }
    
}