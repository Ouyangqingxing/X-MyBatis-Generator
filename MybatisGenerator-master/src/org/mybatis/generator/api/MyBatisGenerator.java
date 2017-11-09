/*
 *  Copyright 2005 The Apache Software Foundation
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

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.XmlFileMergerJaxp;

/**
 * 这是MyBatis Generator的核心类。
 * 
 * 一个典型的可执行工具包括以下步骤：
 * 1.创建一个Configuration对象，Configuration可以是xml配置文件的解析结果，也可以在Java里创建。
 * 2.创建一个MyBatisGenerator对象。
 * 3.调用一个generate()方法
 * 
 * @see org.mybatis.generator.config.xml.ConfigurationParser
 * @author Jeff Butler
 */
public class MyBatisGenerator {

    private Configuration configuration;

    private ShellCallback shellCallback;

    /**
     * 生成的Java文件
     */
    private List<GeneratedJavaFile> generatedJavaFiles;

    /**
     * 生成的xml文件
     */
    private List<GeneratedXmlFile> generatedXmlFiles;

    private List<String> warnings;

    private Set<String> projects;

    /**
     * 构造方法
     * @param configuration 配置
     * @param shellCallback 命令回调
     * @param warnings 警告信息
     * @throws InvalidConfigurationException
     */
    public MyBatisGenerator(Configuration configuration, ShellCallback shellCallback,List<String> warnings) throws InvalidConfigurationException {
        super();
        if (configuration == null) {
            throw new IllegalArgumentException(getString("RuntimeError.2"));
        } else {
            this.configuration = configuration;
        }
        if (shellCallback == null) {
            this.shellCallback = new DefaultShellCallback(false);
        } else {
            this.shellCallback = shellCallback;
        }
        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }
        generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        generatedXmlFiles = new ArrayList<GeneratedXmlFile>();
        projects = new HashSet<String>();
        this.configuration.validate();
    }

    /**
     * 这是MyBatis Generator的核心方法。
     * @param callback
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    public void generate(ProgressCallback callback) throws SQLException,IOException, InterruptedException {
        generate(callback, null, null);
    }

    /**
     * 这是MyBatis Generator的核心方法。
     * @param callback
     * @param contextIds
     * @throws InvalidConfigurationException
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    public void generate(ProgressCallback callback, Set<String> contextIds)throws SQLException, IOException, InterruptedException {
        generate(callback, contextIds, null);
    }

    /**
     * 这是MyBatis Generator的核心方法。
     * @param callback
     * @param contextIds
     * @param fullyQualifiedTableNames 完全匹配表名
     * @throws InvalidConfigurationException
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    public void generate(ProgressCallback callback, Set<String> contextIds,Set<String> fullyQualifiedTableNames) 
    		throws SQLException,IOException, InterruptedException {
        if (callback == null) {
            callback = new NullProgressCallback();
        }
        generatedJavaFiles.clear();
        generatedXmlFiles.clear();
        //计算上下文
        List<Context> contextsToRun;
        if (contextIds == null || contextIds.size() == 0) {
            contextsToRun = configuration.getContexts();
        } else {
            contextsToRun = new ArrayList<Context>();
            for (Context context : configuration.getContexts()) {
                if (contextIds.contains(context.getId())) {
                    contextsToRun.add(context);
                }
            }
        }
        //启动自定义的类加载器
        if (configuration.getClassPathEntries().size() > 0) {
            ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }
        //运行introspections
        int totalSteps = 0;
        for (Context context : contextsToRun) {
            totalSteps += context.getIntrospectionSteps();
        }
        callback.introspectionStarted(totalSteps);
        for (Context context : contextsToRun) {
            context.introspectTables(callback, warnings,fullyQualifiedTableNames);
        }
        //运行generates
        totalSteps = 0;
        for (Context context : contextsToRun) {
            totalSteps += context.getGenerationSteps();
        }
        callback.generationStarted(totalSteps);
        for (Context context : contextsToRun) {
            context.generateFiles(callback, generatedJavaFiles,generatedXmlFiles, warnings);
        }
        //输出xml文件
        callback.saveStarted(generatedXmlFiles.size() + generatedJavaFiles.size());
        for (GeneratedXmlFile gxf : generatedXmlFiles) {
        	projects.add(gxf.getTargetProject());
            File targetFile;
            String source;
            try {
                File directory = shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
                targetFile = new File(directory, gxf.getFileName());
                if (targetFile.exists()) {
                    if (gxf.isMergeable()) {
                        source = XmlFileMergerJaxp.getMergedSource(gxf,targetFile);
                    } else if (shellCallback.isOverwriteEnabled()) {
                        source = gxf.getFormattedContent();
                        warnings.add(getString("Warning.11",targetFile.getAbsolutePath()));
                    } else {
                        source = gxf.getFormattedContent();
                        targetFile = getUniqueFileName(directory, gxf.getFileName());
                        warnings.add(getString("Warning.2", targetFile.getAbsolutePath()));
                    }
                } else {
                    source = gxf.getFormattedContent();
                }
            } catch (ShellException e) {
                warnings.add(e.getMessage());
                continue;
            }
            callback.checkCancel();
            callback.startTask(getString("Progress.15", targetFile.getName()));
            writeFile(targetFile, source, "UTF-8");
        }
        //输出Java文件
        for (GeneratedJavaFile gjf : generatedJavaFiles) {
            projects.add(gjf.getTargetProject());
            File targetFile;
            String source;
            try {
                File directory = shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
                targetFile = new File(directory, gjf.getFileName());
                if (targetFile.exists()) {
                    if (shellCallback.isMergeSupported()) {
                        source = shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile
                                .getAbsolutePath(),MergeConstants.OLD_ELEMENT_TAGS,gjf.getFileEncoding());
                    } else if (shellCallback.isOverwriteEnabled()) {
                        source = gjf.getFormattedContent();
                        warnings.add(getString("Warning.11",targetFile.getAbsolutePath()));
                    } else {
                        source = gjf.getFormattedContent();
                        targetFile = getUniqueFileName(directory, gjf.getFileName());
                        warnings.add(getString("Warning.2", targetFile.getAbsolutePath()));
                    }
                } else {
                    source = gjf.getFormattedContent();
                }
                callback.checkCancel();
                callback.startTask(getString("Progress.15", targetFile.getName()));
                writeFile(targetFile, source, gjf.getFileEncoding());
            } catch (ShellException e) {
                warnings.add(e.getMessage());
            }
        }
        for (String project : projects) {
            shellCallback.refreshProject(project);
        }
        callback.done();
    }

    /**
     * 写入文件
     * @param file
     * @param content
     */
    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    /**
     * 获取独一的文件名
     * @date 2017年10月24日 下午2:38:07
     * @author Qing Luo
     * @param directory
     * @param fileName
     * @return
     */
    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);
            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }
        if (answer == null) {
            throw new RuntimeException(getString("RuntimeError.3", directory.getAbsolutePath()));
        }
        return answer;
    }

}