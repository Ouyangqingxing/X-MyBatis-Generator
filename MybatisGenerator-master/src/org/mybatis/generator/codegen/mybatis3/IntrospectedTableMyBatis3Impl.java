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
package org.mybatis.generator.codegen.mybatis3;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.AbstractGenerator;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.MixedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.ObjectFactory;

/**
 * @author Jeff Butler
 */
public class IntrospectedTableMyBatis3Impl extends IntrospectedTable {
	
    protected List<AbstractJavaGenerator> javaModelGenerators;
    protected List<AbstractJavaGenerator> clientGenerators;
    protected List<AbstractJavaGenerator> serviceGenerators;
    protected List<AbstractJavaGenerator> serviceImplGenerators;
    protected List<AbstractJavaGenerator> paramGenerators;
    protected AbstractXmlGenerator xmlMapperGenerator;

    /**
     * 构造方法
     */
    public IntrospectedTableMyBatis3Impl() {
        super(TargetRuntime.MYBATIS3);
        javaModelGenerators = new ArrayList<AbstractJavaGenerator>();
        clientGenerators = new ArrayList<AbstractJavaGenerator>();
        serviceGenerators = new ArrayList<AbstractJavaGenerator>();
        serviceImplGenerators = new ArrayList<AbstractJavaGenerator>();
        paramGenerators = new ArrayList<AbstractJavaGenerator>();
    }

    /**
     * 生成构造器
     */
    @Override
    public void calculateGenerators(List<String> warnings,ProgressCallback progressCallback) {	
    	calculateJavaModelGenerators(warnings, progressCallback);
        AbstractJavaClientGenerator javaClientGenerator = calculateClientGenerators(warnings, progressCallback);
        calculateXmlMapperGenerator(javaClientGenerator, warnings, progressCallback);
        calculateJavaServiceGenerators(warnings, progressCallback);
        calculateJavaServiceImplGenerators(warnings, progressCallback);
        calculateJavaParamGenerators(warnings, progressCallback);
    }

    /**
     * 生成Entity层生成器
     * @date 2017年10月26日 下午3:38:00
     * @author Qing Luo
     * @param javaClientGenerator
     * @param warnings
     * @param progressCallback
     */
    protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator,List<String> warnings,
            ProgressCallback progressCallback) {
        if (javaClientGenerator == null) {
            if (context.getSqlMapGeneratorConfiguration() != null) {
                xmlMapperGenerator = new XMLMapperGenerator();
            }
        } else {
            xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();
        }
        initializeAbstractGenerator(xmlMapperGenerator, warnings,progressCallback);
    }

    /**
     * 抽象Dao层生成器
     * @date 2017年10月26日 下午3:37:58
     * @author Qing Luo
     * @param warnings
     * @param progressCallback
     * @return
     */
    protected AbstractJavaClientGenerator calculateClientGenerators(List<String> warnings,ProgressCallback progressCallback) {
        if (!rules.generateJavaClient()) {
            return null;
        }
        AbstractJavaClientGenerator javaGenerator = createJavaClientGenerator();
        if (javaGenerator == null) {
            return null;
        }
        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        clientGenerators.add(javaGenerator);
        return javaGenerator;
    }
    
    /**
     * 生成Dao层生成器
     * @date 2017年10月26日 下午3:37:50
     * @author Qing Luo
     * @return
     */
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }
        String type = context.getJavaClientGeneratorConfiguration().getConfigurationType();
        AbstractJavaClientGenerator javaGenerator;
        if ("XMLMAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new JavaMapperGenerator();
        } else if ("MIXEDMAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new MixedClientGenerator();
        } else if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new AnnotatedClientGenerator();
        } else if ("MAPPER".equalsIgnoreCase(type)) {
            javaGenerator = new JavaMapperGenerator();
        } else {
            javaGenerator = (AbstractJavaClientGenerator) ObjectFactory.createInternalObject(type);
        }
        return javaGenerator;
    }

    /**
     * 生成Entity层生成器
     * @date 2017年10月26日 下午3:37:48
     * @author Qing Luo
     * @param warnings
     * @param progressCallback
     */
    protected void calculateJavaModelGenerators(List<String> warnings,ProgressCallback progressCallback) {
        if (getRules().generateExampleClass()) {
            AbstractJavaGenerator javaGenerator = new ExampleGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
        if (getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
        if (getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
    }

    /**
     * 生成service层生成器
     * @date 2017年10月26日 下午4:10:41
     * @author Qing Luo
     * @param warnings
     * @param progressCallback
     */
    protected void calculateJavaServiceGenerators(List<String> warnings,ProgressCallback progressCallback) {
        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            serviceGenerators.add(javaGenerator);
        }
    }
    
    /**
     * 生成serviceImpl层生成器
     * @date 2017年10月26日 下午4:10:56
     * @author Qing Luo
     * @param warnings
     * @param progressCallback
     */
    protected void calculateJavaServiceImplGenerators(List<String> warnings,ProgressCallback progressCallback) {
        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            serviceImplGenerators.add(javaGenerator);
        }
    }
    
    /**
     * 生成param层生成器
     * @date 2017年11月6日 上午10:40:26
     * @author Qing Luo
     * @param warnings
     * @param progressCallback
     */
    protected void calculateJavaParamGenerators(List<String> warnings,ProgressCallback progressCallback) {
        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings,progressCallback);
            paramGenerators.add(javaGenerator);
        }
    }
    
    /**
     * 初始化抽象生成器
     * @date 2017年10月26日 下午3:37:45
     * @author Qing Luo
     * @param abstractGenerator
     * @param warnings
     * @param progressCallback
     */
    protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings,
            ProgressCallback progressCallback) {
        if (abstractGenerator == null) {
            return;
        }
        abstractGenerator.setContext(context);
        abstractGenerator.setIntrospectedTable(this);
        abstractGenerator.setProgressCallback(progressCallback);
        abstractGenerator.setWarnings(warnings);
    }

    /**
     * 生成Java文件
     */
    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
    	//Java文件集合
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();
        //生成entity层的代码
        for (AbstractJavaGenerator javaGenerator : javaModelGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits("javaModelGenerators");
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                	context.getJavaModelGeneratorConfiguration().getTargetProject(),
                	context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),context.getJavaFormatter());
                answer.add(gjf);
            }
        }
        //生成dao层的代码
        for (AbstractJavaGenerator javaGenerator : clientGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits("clientGenerators");
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                	context.getJavaClientGeneratorConfiguration().getTargetProject(),
                	context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),context.getJavaFormatter());
                answer.add(gjf);
            }
        }        
        //生成service层
        for(AbstractJavaGenerator javaGenerator : serviceGenerators){
        	List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits("serviceGenerators");
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                	context.getJavaServiceGeneratorConfiguration().getTargetProject(),
                	context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),context.getJavaFormatter());
                answer.add(gjf);
            }
        }
        //serviceImpl层代码
        for(AbstractJavaGenerator javaGenerator : serviceImplGenerators){
        	List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits("serviceImplGenerators");
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                	context.getJavaServiceImplGeneratorConfiguration().getTargetProject(),
                	context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),context.getJavaFormatter());
                answer.add(gjf);
            }
        }
        //生成param层代码
        for(AbstractJavaGenerator javaGenerator : paramGenerators){
        	List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits("paramGenerators");
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                	"target",null,context.getJavaFormatter());
                answer.add(gjf);
            }
        }
        return answer;
    }

    /**
     * 生成xml文件
     */
    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
        if (xmlMapperGenerator != null) {
            Document document = xmlMapperGenerator.getDocument();
            GeneratedXmlFile gxf = new GeneratedXmlFile(document,getMyBatis3XmlMapperFileName(),getMyBatis3XmlMapperPackage(),
            	context.getSqlMapGeneratorConfiguration().getTargetProject(),false, context.getXmlFormatter());
            if (context.getPlugins().sqlMapGenerated(gxf, this)) {
                answer.add(gxf);
            }
        }
        return answer;
    }

    /**
     * 当前生成步骤
     */
    @Override
    public int getGenerationSteps() {
        return javaModelGenerators.size() + clientGenerators.size() + (xmlMapperGenerator == null ? 0 : 1);
    }

    /**
     * 是否是Java5
     */
    @Override
    public boolean isJava5Targeted() {
        return true;
    }

    @Override
    public boolean requiresXMLGenerator() {
        AbstractJavaClientGenerator javaClientGenerator = createJavaClientGenerator();
        if (javaClientGenerator == null) {
            return false;
        } else {
            return javaClientGenerator.requiresXMLGenerator();
        }
    }
    
}