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
package org.mybatis.generator.config;

import static org.mybatis.generator.internal.util.StringUtility.composeFullyQualifiedTableName;
import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.XmlFormatter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.PluginAggregator;
import org.mybatis.generator.internal.db.ConnectionFactory;
import org.mybatis.generator.internal.db.DatabaseIntrospector;

/**
 * @author Jeff Butler
 */
public class Context extends PropertyHolder {
	
    private String id;

    private UserConfiguration userConfiguration;
    
    private JDBCConnectionConfiguration jdbcConnectionConfiguration;

    private SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration;

    private JavaTypeResolverConfiguration javaTypeResolverConfiguration;

    private JavaModelGeneratorConfiguration javaModelGeneratorConfiguration;

    private JavaClientGeneratorConfiguration javaClientGeneratorConfiguration;

    private JavaServiceGeneratorConfiguration javaServiceGeneratorConfiguration;
    
    private JavaServiceImplGeneratorConfiguration javaServiceImplGeneratorConfiguration;
    
    private ArrayList<TableConfiguration> tableConfigurations;

    private ModelType defaultModelType;

    private String beginningDelimiter = "\"";

    private String endingDelimiter = "\"";

    private CommentGeneratorConfiguration commentGeneratorConfiguration;

    private CommentGenerator commentGenerator;

    private PluginAggregator pluginAggregator;

    private List<PluginConfiguration> pluginConfigurations;

    private String targetRuntime;

    private String introspectedColumnImpl;

    private Boolean autoDelimitKeywords;
    
    private JavaFormatter javaFormatter;
    
    private XmlFormatter xmlFormatter;

   
    /**
     * 构造方法
     * @param defaultModelType
     */
    public Context(ModelType defaultModelType) {
        super();
        if (defaultModelType == null) {
            this.defaultModelType = ModelType.CONDITIONAL;
        } else {
            this.defaultModelType = defaultModelType;
        }
        tableConfigurations = new ArrayList<TableConfiguration>();
        pluginConfigurations = new ArrayList<PluginConfiguration>();
    }
    
    public UserConfiguration getUserConfiguration() {
		return userConfiguration;
	}

	public void setUserConfiguration(UserConfiguration userConfiguration) {
		this.userConfiguration = userConfiguration;
	}

	public void addTableConfiguration(TableConfiguration tc) {
        tableConfigurations.add(tc);
    }
    
    public JDBCConnectionConfiguration getJdbcConnectionConfiguration() {
        return jdbcConnectionConfiguration;
    }
       
    public JavaModelGeneratorConfiguration getJavaModelGeneratorConfiguration() {
		return javaModelGeneratorConfiguration;
	}

	public void setJavaModelGeneratorConfiguration(
			JavaModelGeneratorConfiguration javaModelGeneratorConfiguration) {
		this.javaModelGeneratorConfiguration = javaModelGeneratorConfiguration;
	}

	public JavaClientGeneratorConfiguration getJavaClientGeneratorConfiguration() {
		return javaClientGeneratorConfiguration;
	}

	public void setJavaClientGeneratorConfiguration(
			JavaClientGeneratorConfiguration javaClientGeneratorConfiguration) {
		this.javaClientGeneratorConfiguration = javaClientGeneratorConfiguration;
	}

	public JavaServiceGeneratorConfiguration getJavaServiceGeneratorConfiguration() {
		return javaServiceGeneratorConfiguration;
	}
    
	public void setJavaServiceGeneratorConfiguration(
			JavaServiceGeneratorConfiguration javaServiceGeneratorConfiguration) {
		this.javaServiceGeneratorConfiguration = javaServiceGeneratorConfiguration;
	}
	
	public JavaServiceImplGeneratorConfiguration getJavaServiceImplGeneratorConfiguration() {
		return javaServiceImplGeneratorConfiguration;
	}
	
	public void setJavaServiceImplGeneratorConfiguration(
			JavaServiceImplGeneratorConfiguration javaServiceImplGeneratorConfiguration) {
		this.javaServiceImplGeneratorConfiguration = javaServiceImplGeneratorConfiguration;
	}
	
    public JavaTypeResolverConfiguration getJavaTypeResolverConfiguration() {
        return javaTypeResolverConfiguration;
    }
    
    public SqlMapGeneratorConfiguration getSqlMapGeneratorConfiguration() {
        return sqlMapGeneratorConfiguration;
    }
    
    public void addPluginConfiguration(PluginConfiguration pluginConfiguration) {
        pluginConfigurations.add(pluginConfiguration);
    }
    

    /**
     * 此方法简单验证必须的字段是否已经填充
     */
    public void validate(List<String> errors) {
        if (!stringHasValue(id)) {
            errors.add(getString("ValidationError.16"));
        }
        if (jdbcConnectionConfiguration == null) {
            errors.add(getString("ValidationError.10", id));
        } else {
            jdbcConnectionConfiguration.validate(errors);
        }
        if (javaModelGeneratorConfiguration == null) {
            errors.add(getString("ValidationError.8", id));
        } else {
            javaModelGeneratorConfiguration.validate(errors, id);
        }
        if (javaClientGeneratorConfiguration != null) {
            javaClientGeneratorConfiguration.validate(errors, id);
        }
        if (javaServiceGeneratorConfiguration != null) {
        	javaServiceGeneratorConfiguration.validate(errors, id);
        }
        if (javaServiceImplGeneratorConfiguration != null) {
        	javaServiceImplGeneratorConfiguration.validate(errors, id);
        }
        IntrospectedTable it = null;
        try {
            it = ObjectFactory.createIntrospectedTableForValidation(this);
        } catch (Exception e) {
            errors.add(getString("ValidationError.25", id));
        }
        if (it != null && it.requiresXMLGenerator()) {
            if (sqlMapGeneratorConfiguration == null) {
                errors.add(getString("ValidationError.9", id));
            } else {
                sqlMapGeneratorConfiguration.validate(errors, id);
            }
        }
        if (tableConfigurations.size() == 0) {
            errors.add(getString("ValidationError.3", id));
        } else {
            for (int i = 0; i < tableConfigurations.size(); i++) {
                TableConfiguration tc = tableConfigurations.get(i);
                tc.validate(errors, i);
            }
        }
        for (PluginConfiguration pluginConfiguration : pluginConfigurations) {
            pluginConfiguration.validate(errors, id);
        }
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setJavaTypeResolverConfiguration(
            JavaTypeResolverConfiguration javaTypeResolverConfiguration) {
        this.javaTypeResolverConfiguration = javaTypeResolverConfiguration;
    }
    public void setJdbcConnectionConfiguration(
            JDBCConnectionConfiguration jdbcConnectionConfiguration) {
        this.jdbcConnectionConfiguration = jdbcConnectionConfiguration;
    }
    public void setSqlMapGeneratorConfiguration(
            SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration) {
        this.sqlMapGeneratorConfiguration = sqlMapGeneratorConfiguration;
    }
    public ModelType getDefaultModelType() {
        return defaultModelType;
    }

    
    /**
     * 调用validate方法来验证上下文
     * @return 
     */
    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("context");
        xmlElement.addAttribute(new Attribute("id", id));
        if (defaultModelType != ModelType.CONDITIONAL) {
            xmlElement.addAttribute(new Attribute("defaultModelType", defaultModelType.getModelType()));
        }
        if (stringHasValue(introspectedColumnImpl)) {
            xmlElement.addAttribute(new Attribute("introspectedColumnImpl", introspectedColumnImpl));
        }
        if (stringHasValue(targetRuntime)) {
            xmlElement.addAttribute(new Attribute("targetRuntime", targetRuntime));
        }
        addPropertyXmlElements(xmlElement);
        for (PluginConfiguration pluginConfiguration : pluginConfigurations) {
            xmlElement.addElement(pluginConfiguration.toXmlElement());
        }
        if (commentGeneratorConfiguration != null) {
            xmlElement.addElement(commentGeneratorConfiguration.toXmlElement());
        }
        if (jdbcConnectionConfiguration != null) {
            xmlElement.addElement(jdbcConnectionConfiguration.toXmlElement());
        }
        if (userConfiguration != null) {
            xmlElement.addElement(userConfiguration.toXmlElement());
        }
        if (javaTypeResolverConfiguration != null) {
            xmlElement.addElement(javaTypeResolverConfiguration.toXmlElement());
        }
        if (javaModelGeneratorConfiguration != null) {
            xmlElement.addElement(javaModelGeneratorConfiguration.toXmlElement());
        }
        if (sqlMapGeneratorConfiguration != null) {
            xmlElement.addElement(sqlMapGeneratorConfiguration.toXmlElement());
        }
        if (javaClientGeneratorConfiguration != null) {
            xmlElement.addElement(javaClientGeneratorConfiguration.toXmlElement());
        }
        if (javaServiceGeneratorConfiguration != null){
        	xmlElement.addElement(javaServiceGeneratorConfiguration.toXmlElement());
        }
        if (javaServiceImplGeneratorConfiguration != null){
        	xmlElement.addElement(javaServiceImplGeneratorConfiguration.toXmlElement());
        }
        for (TableConfiguration tableConfiguration : tableConfigurations) {
            xmlElement.addElement(tableConfiguration.toXmlElement());
        }
        return xmlElement;
    }

    public List<TableConfiguration> getTableConfigurations() {
        return tableConfigurations;
    }

    public String getBeginningDelimiter() {
        return beginningDelimiter;
    }

    public String getEndingDelimiter() {
        return endingDelimiter;
    }

    @Override
    public void addProperty(String name, String value) {
        super.addProperty(name, value);
        if (PropertyRegistry.CONTEXT_BEGINNING_DELIMITER.equals(name)) {
            beginningDelimiter = value;
        } else if (PropertyRegistry.CONTEXT_ENDING_DELIMITER.equals(name)) {
            endingDelimiter = value;
        } else if (PropertyRegistry.CONTEXT_AUTO_DELIMIT_KEYWORDS.equals(name)) {
            if (stringHasValue(value)) {
                autoDelimitKeywords = new Boolean(isTrue(value));
            }
        }
    }

    public CommentGenerator getCommentGenerator() {
        if (commentGenerator == null) {
            commentGenerator = ObjectFactory.createCommentGenerator(this);
        }
        return commentGenerator;
    }

    public JavaFormatter getJavaFormatter() {
        if (javaFormatter == null) {
            javaFormatter = ObjectFactory.createJavaFormatter(this);
        }
        return javaFormatter;
    }
    
    public XmlFormatter getXmlFormatter() {
        if (xmlFormatter == null) {
            xmlFormatter = ObjectFactory.createXmlFormatter(this);
        }
        return xmlFormatter;
    }
    
    public CommentGeneratorConfiguration getCommentGeneratorConfiguration() {
        return commentGeneratorConfiguration;
    }

    public void setCommentGeneratorConfiguration(CommentGeneratorConfiguration commentGeneratorConfiguration) {
        this.commentGeneratorConfiguration = commentGeneratorConfiguration;
    }

    public Plugin getPlugins() {
        return pluginAggregator;
    }

    public String getTargetRuntime() {
        return targetRuntime;
    }

    public void setTargetRuntime(String targetRuntime) {
        this.targetRuntime = targetRuntime;
    }

    public String getIntrospectedColumnImpl() {
        return introspectedColumnImpl;
    }

    public void setIntrospectedColumnImpl(String introspectedColumnImpl) {
        this.introspectedColumnImpl = introspectedColumnImpl;
    }



    // 调用顺序
    // 1. getIntrospectionSteps()
    // 2. introspectTables()
    // 3. getGenerationSteps()
    // 4. generateFiles()
    
    /**
     * 存放数据库中表的所有信息
     */
    private List<IntrospectedTable> introspectedTables;

    public int getIntrospectionSteps() {
        int steps = 0;
        steps++;
        steps += tableConfigurations.size() * 1;
        return steps;
    }

    /**
     * 
     * @param callback
     * @param warnings
     * @param fullyQualifiedTableNames
     * @throws SQLException
     * @throws InterruptedException
     */
    public void introspectTables(ProgressCallback callback,List<String> warnings, Set<String> fullyQualifiedTableNames)
            throws SQLException, InterruptedException {
        introspectedTables = new ArrayList<IntrospectedTable>();
        JavaTypeResolver javaTypeResolver = ObjectFactory.createJavaTypeResolver(this, warnings);
        Connection connection = null;
        try {
            callback.startTask(getString("Progress.0"));
            connection = getConnection();
            DatabaseIntrospector databaseIntrospector = new DatabaseIntrospector(this, connection.getMetaData(), javaTypeResolver, warnings);
            for (TableConfiguration tc : tableConfigurations) {
                String tableName = composeFullyQualifiedTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName(), '.');
                if (fullyQualifiedTableNames != null && fullyQualifiedTableNames.size() > 0) {
                    if (!fullyQualifiedTableNames.contains(tableName)) {
                        continue;
                    }
                }
                if (!tc.areAnyStatementsEnabled()) {
                    warnings.add(getString("Warning.0", tableName));
                    continue;
                }
                callback.startTask(getString("Progress.1", tableName));
                List<IntrospectedTable> tables = databaseIntrospector.introspectTables(tc);
                if (tables != null) {
                    introspectedTables.addAll(tables);
                }
                callback.checkCancel();
            }
        } finally {
            closeConnection(connection);
        }
    }

    public int getGenerationSteps() {
        int steps = 0;
        if (introspectedTables != null) {
            for (IntrospectedTable introspectedTable : introspectedTables) {
                steps += introspectedTable.getGenerationSteps();
            }
        }
        return steps;
    }

   
    /**
     * 生成文件
     * @date 2017年10月24日 下午3:52:16
     * @author Qing Luo
     * @param callback
     * @param generatedJavaFiles
     * @param generatedXmlFiles
     * @param warnings
     * @throws InterruptedException
     */
    public void generateFiles(ProgressCallback callback,List<GeneratedJavaFile> generatedJavaFiles,
            List<GeneratedXmlFile> generatedXmlFiles, List<String> warnings) throws InterruptedException {
        pluginAggregator = new PluginAggregator();
        for (PluginConfiguration pluginConfiguration : pluginConfigurations) {
            Plugin plugin = ObjectFactory.createPlugin(this,pluginConfiguration);
            if (plugin.validate(warnings)) {
                pluginAggregator.addPlugin(plugin);
            } else {
                warnings.add(getString("Warning.24",pluginConfiguration.getConfigurationType(), id));
            }
        }
        if (introspectedTables != null) {
            for (IntrospectedTable introspectedTable : introspectedTables) {
                callback.checkCancel();
                introspectedTable.initialize();
                introspectedTable.calculateGenerators(warnings, callback);
                //赋予Java文件 和 xml文件
                generatedJavaFiles.addAll(introspectedTable.getGeneratedJavaFiles());
                generatedXmlFiles.addAll(introspectedTable.getGeneratedXmlFiles());
                //这两个值为空
                generatedJavaFiles.addAll(pluginAggregator.contextGenerateAdditionalJavaFiles(introspectedTable));
                generatedXmlFiles.addAll(pluginAggregator.contextGenerateAdditionalXmlFiles(introspectedTable));
            }
        }
        generatedJavaFiles.addAll(pluginAggregator.contextGenerateAdditionalJavaFiles());
        generatedXmlFiles.addAll(pluginAggregator.contextGenerateAdditionalXmlFiles());
    }

    private Connection getConnection() throws SQLException {
        Connection connection = ConnectionFactory.getInstance().getConnection(jdbcConnectionConfiguration);
        return connection;
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {}
        }
    }

    public boolean autoDelimitKeywords() {
        return autoDelimitKeywords != null && autoDelimitKeywords.booleanValue();
    }
    
}