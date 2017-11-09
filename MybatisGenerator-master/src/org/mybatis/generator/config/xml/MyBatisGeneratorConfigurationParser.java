/*
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
package org.mybatis.generator.config.xml;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.ColumnRenamingRule;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.IgnoredColumn;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaServiceGeneratorConfiguration;
import org.mybatis.generator.config.JavaServiceImplGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyHolder;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.UserConfiguration;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 配置文件解析器，将解析结果放入Properties
 * @author Jeff Butler
 */
public class MyBatisGeneratorConfigurationParser {
    
	/**
	 * 资源：存放解析结果
	 */
	private Properties properties;

	/**
	 * 构造方法
	 * @param properties
	 */
    public MyBatisGeneratorConfigurationParser(Properties properties) {
        super();
        if (properties == null) {
            this.properties = System.getProperties();
        } else {
            this.properties = properties;
        }
    }

    /**
     * 解析配置文件
     * @date 2017年10月26日 上午11:22:31
     * @author Qing Luo
     * @param rootNode 元素
     * @return Configuration
     * @throws XMLParserException
     */
    public Configuration parseConfiguration(Element rootNode)throws XMLParserException {
        Configuration configuration = new Configuration();
        NodeList nodeList = rootNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("properties".equals(childNode.getNodeName())) { 
                parseProperties(configuration, childNode);
            } else if ("classPathEntry".equals(childNode.getNodeName())) { 
                parseClassPathEntry(configuration, childNode);
            } else if ("context".equals(childNode.getNodeName())) { 
                parseContext(configuration, childNode);
            }
        }
        return configuration;
    }

    /**
     * 解析Properties
     * @date 2017年10月26日 上午11:25:53
     * @author Qing Luo
     * @param configuration
     * @param node
     * @throws XMLParserException
     */
    private void parseProperties(Configuration configuration, Node node)throws XMLParserException {
        Properties attributes = parseAttributes(node);
        String resource = attributes.getProperty("resource"); 
        String url = attributes.getProperty("url"); 
        if (!stringHasValue(resource) && !stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14")); 
        }
        if (stringHasValue(resource) && stringHasValue(url)) {
            throw new XMLParserException(getString("RuntimeError.14")); 
        }
        URL resourceUrl;
        try {
            if (stringHasValue(resource)) {
                resourceUrl = ObjectFactory.getResource(resource);
                if (resourceUrl == null) {
                    throw new XMLParserException(getString("RuntimeError.15", resource)); 
                }
            } else {
                resourceUrl = new URL(url);
            }
            InputStream inputStream = resourceUrl.openConnection().getInputStream();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            if (stringHasValue(resource)) {
                throw new XMLParserException(getString("RuntimeError.16", resource)); 
            } else {
                throw new XMLParserException(getString("RuntimeError.17", url)); 
            }
        }
    }

    /**
     * 解析Context
     * @date 2017年10月26日 上午11:27:25
     * @author Qing Luo
     * @param configuration
     * @param node
     */
    private void parseContext(Configuration configuration, Node node) {
        Properties attributes = parseAttributes(node);
        String defaultModelType = attributes.getProperty("defaultModelType");
        String targetRuntime = attributes.getProperty("targetRuntime");
        String introspectedColumnImpl = attributes.getProperty("introspectedColumnImpl"); 
        String id = attributes.getProperty("id");
        ModelType mt = defaultModelType == null ? null : ModelType.getModelType(defaultModelType);
        Context context = new Context(mt);
        context.setId(id);
        if (stringHasValue(introspectedColumnImpl)) {
            context.setIntrospectedColumnImpl(introspectedColumnImpl);
        }
        if (stringHasValue(targetRuntime)) {
            context.setTargetRuntime(targetRuntime);
        }
        configuration.addContext(context);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(context, childNode);
            } else if ("user".equals(childNode.getNodeName())) { 
                parseUser(context, childNode);
            } else if ("plugin".equals(childNode.getNodeName())) { 
                parsePlugin(context, childNode);
            } else if ("commentGenerator".equals(childNode.getNodeName())) { 
                parseCommentGenerator(context, childNode);
            } else if ("jdbcConnection".equals(childNode.getNodeName())) { 
                parseJdbcConnection(context, childNode);
            } else if ("javaModelGenerator".equals(childNode.getNodeName())) { 
                parseJavaModelGenerator(context, childNode);
            } else if ("javaTypeResolver".equals(childNode.getNodeName())) { 
                parseJavaTypeResolver(context, childNode);
            } else if ("sqlMapGenerator".equals(childNode.getNodeName())) { 
                parseSqlMapGenerator(context, childNode);
            } else if ("javaClientGenerator".equals(childNode.getNodeName())) { 
                parseJavaClientGenerator(context, childNode);
            } else if ("javaServiceGenerator".equals(childNode.getNodeName())){
            	parseJavaServiceGenerator(context, childNode);
            } else if ("javaServiceImplGenerator".equals(childNode.getNodeName())){
            	parseJavaServiceImplGenerator(context, childNode);
            }else if ("table".equals(childNode.getNodeName())) { 
                parseTable(context, childNode);
            }
        }
    }

    /**
     * 解析sqlMapGenerator元素
     * @date 2017年10月26日 下午1:48:28
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseSqlMapGenerator(Context context, Node node) {
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage"); 
        String targetProject = attributes.getProperty("targetProject"); 
        sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
        sqlMapGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(sqlMapGeneratorConfiguration, childNode);
            }
        }
    }

    /**
     * 解析table元素
     * @date 2017年10月26日 下午1:50:34
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseTable(Context context, Node node) {
        TableConfiguration tc = new TableConfiguration(context);
        context.addTableConfiguration(tc);
        Properties attributes = parseAttributes(node);
        String catalog = attributes.getProperty("catalog"); 
        String schema = attributes.getProperty("schema"); 
        String tableName = attributes.getProperty("tableName"); 
        String domainObjectName = attributes.getProperty("domainObjectName");
        String hasPrimaryKey = attributes.getProperty("hasPrimaryKey");
        String alias = attributes.getProperty("alias"); 
        String enableInsert = attributes.getProperty("enableInsert"); 
        String enableSelectByPrimaryKey = attributes.getProperty("enableSelectByPrimaryKey"); 
        String enableSelectByExample = attributes.getProperty("enableSelectByExample"); 
        String enableUpdateByPrimaryKey = attributes.getProperty("enableUpdateByPrimaryKey"); 
        String enableDeleteByPrimaryKey = attributes.getProperty("enableDeleteByPrimaryKey"); 
        String enableDeleteByExample = attributes.getProperty("enableDeleteByExample"); 
        String enableCountByExample = attributes.getProperty("enableCountByExample"); 
        String enableUpdateByExample = attributes.getProperty("enableUpdateByExample"); 
        String selectByPrimaryKeyQueryId = attributes.getProperty("selectByPrimaryKeyQueryId"); 
        String selectByExampleQueryId = attributes.getProperty("selectByExampleQueryId"); 
        String modelType = attributes.getProperty("modelType"); 
        String escapeWildcards = attributes.getProperty("escapeWildcards"); 
        String delimitIdentifiers = attributes.getProperty("delimitIdentifiers"); 
        String delimitAllColumns = attributes.getProperty("delimitAllColumns"); 
        if (stringHasValue(catalog)) {
            tc.setCatalog(catalog);
        }
        if (stringHasValue(schema)) {
            tc.setSchema(schema);
        }
        if (stringHasValue(tableName)) {
            tc.setTableName(tableName);
        }
        if (stringHasValue(domainObjectName)) {
            tc.setDomainObjectName(domainObjectName);
        }
        if (stringHasValue(hasPrimaryKey)) {
            tc.setHasPrimaryKey(hasPrimaryKey);
        }
        if (stringHasValue(alias)) {
            tc.setAlias(alias);
        }
        if (stringHasValue(enableInsert)) {
            tc.setInsertStatementEnabled(isTrue(enableInsert));
        }
        if (stringHasValue(enableSelectByPrimaryKey)) {
            tc.setSelectByPrimaryKeyStatementEnabled(
                    isTrue(enableSelectByPrimaryKey));
        }
        if (stringHasValue(enableSelectByExample)) {
            tc.setSelectByExampleStatementEnabled(
                    isTrue(enableSelectByExample));
        }
        if (stringHasValue(enableUpdateByPrimaryKey)) {
            tc.setUpdateByPrimaryKeyStatementEnabled(
                    isTrue(enableUpdateByPrimaryKey));
        }
        if (stringHasValue(enableDeleteByPrimaryKey)) {
            tc.setDeleteByPrimaryKeyStatementEnabled(
                    isTrue(enableDeleteByPrimaryKey));
        }
        if (stringHasValue(enableDeleteByExample)) {
            tc.setDeleteByExampleStatementEnabled(
                    isTrue(enableDeleteByExample));
        }
        if (stringHasValue(enableCountByExample)) {
            tc.setCountByExampleStatementEnabled(
                    isTrue(enableCountByExample));
        }
        if (stringHasValue(enableUpdateByExample)) {
            tc.setUpdateByExampleStatementEnabled(
                    isTrue(enableUpdateByExample));
        }
        if (stringHasValue(selectByPrimaryKeyQueryId)) {
            tc.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
        }
        if (stringHasValue(selectByExampleQueryId)) {
            tc.setSelectByExampleQueryId(selectByExampleQueryId);
        }
        if (stringHasValue(modelType)) {
            tc.setConfiguredModelType(modelType);
        }
        if (stringHasValue(escapeWildcards)) {
            tc.setWildcardEscapingEnabled(isTrue(escapeWildcards));
        }
        if (stringHasValue(delimitIdentifiers)) {
            tc.setDelimitIdentifiers(isTrue(delimitIdentifiers));
        }
        if (stringHasValue(delimitAllColumns)) {
            tc.setAllColumnDelimitingEnabled(isTrue(delimitAllColumns));
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(tc, childNode);
            } else if ("columnOverride".equals(childNode.getNodeName())) { 
                parseColumnOverride(tc, childNode);
            } else if ("ignoreColumn".equals(childNode.getNodeName())) { 
                parseIgnoreColumn(tc, childNode);
            } else if ("generatedKey".equals(childNode.getNodeName())) { 
                parseGeneratedKey(tc, childNode);
            } else if ("columnRenamingRule".equals(childNode.getNodeName())) { 
                parseColumnRenamingRule(tc, childNode);
            }
        }
    }
    
    /**
     * 解析重写字段
     * @date 2017年10月26日 下午1:59:44
     * @author Qing Luo
     * @param tc
     * @param node
     */

    private void parseColumnOverride(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column"); 
        String property = attributes.getProperty("property"); 
        String javaType = attributes.getProperty("javaType"); 
        String jdbcType = attributes.getProperty("jdbcType"); 
        String typeHandler = attributes.getProperty("typeHandler"); 
        String delimitedColumnName = attributes.getProperty("delimitedColumnName"); 
        ColumnOverride co = new ColumnOverride(column);
        if (stringHasValue(property)) {
            co.setJavaProperty(property);
        }
        if (stringHasValue(javaType)) {
            co.setJavaType(javaType);
        }
        if (stringHasValue(jdbcType)) {
            co.setJdbcType(jdbcType);
        }
        if (stringHasValue(typeHandler)) {
            co.setTypeHandler(typeHandler);
        }
        if (stringHasValue(delimitedColumnName)) {
            co.setColumnNameDelimited(isTrue(delimitedColumnName));
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(co, childNode);
            }
        }
        tc.addColumnOverride(co);
    }

   
    /**
     * 解析字段
     * @date 2017年10月26日 下午1:59:58
     * @author Qing Luo
     * @param tc
     * @param node
     */
    private void parseGeneratedKey(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column"); 
        boolean identity = isTrue(attributes.getProperty("identity")); 
        String sqlStatement = attributes.getProperty("sqlStatement"); 
        String type = attributes.getProperty("type"); 
        GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);
        tc.setGeneratedKey(gk);
    }

    
    /**
     * 解析忽略字段
     * @date 2017年10月26日 下午2:00:36
     * @author Qing Luo
     * @param tc
     * @param node
     */
    private void parseIgnoreColumn(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String column = attributes.getProperty("column"); 
        String delimitedColumnName = attributes.getProperty("delimitedColumnName"); 
        IgnoredColumn ic = new IgnoredColumn(column);
        if (stringHasValue(delimitedColumnName)) {
            ic.setColumnNameDelimited(isTrue(delimitedColumnName));
        }
        tc.addIgnoredColumn(ic);
    }

    
    /**
     * 解析字段重命名规则
     * @date 2017年10月26日 下午2:00:39
     * @author Qing Luo
     * @param tc
     * @param node
     */
    private void parseColumnRenamingRule(TableConfiguration tc, Node node) {
        Properties attributes = parseAttributes(node);
        String searchString = attributes.getProperty("searchString"); 
        String replaceString = attributes.getProperty("replaceString"); 
        ColumnRenamingRule crr = new ColumnRenamingRule();
        crr.setSearchString(searchString);
        if (stringHasValue(replaceString)) {
            crr.setReplaceString(replaceString);
        }
        tc.setColumnRenamingRule(crr);
    }

    
    /**
     * 解析Java类型转换器
     * @date 2017年10月26日 下午2:00:42
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseJavaTypeResolver(Context context, Node node) {
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); 
        if (stringHasValue(type)) {
            javaTypeResolverConfiguration.setConfigurationType(type);
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(javaTypeResolverConfiguration, childNode);
            }
        }
    }

    
    /**
     * 解析插件
     * @date 2017年10月26日 下午2:00:45
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parsePlugin(Context context, Node node) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        context.addPluginConfiguration(pluginConfiguration);
        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); 
        pluginConfiguration.setConfigurationType(type);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(pluginConfiguration, childNode);
            }
        }
    }

    /**
     * 解析用户信息
     * @date 2017年11月7日 下午3:41:39
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseUser(Context context, Node node) {
        UserConfiguration userConfiguration = new UserConfiguration();
        Properties attributes = parseAttributes(node);
        String name = attributes.getProperty("name");
        String year = attributes.getProperty("year"); 
        String company = attributes.getProperty("company"); 
        userConfiguration.setName(name);
        userConfiguration.setYear(year);
        userConfiguration.setCompany(company);
        context.setUserConfiguration(userConfiguration);
    }
    
    /**
     * 解析JavaModelGenerator标签（Entity层）
     * @date 2017年10月26日 下午2:00:49
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseJavaModelGenerator(Context context, Node node) {
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
        Properties attributes = parseAttributes(node);
        String targetPackage = attributes.getProperty("targetPackage"); 
        String targetProject = attributes.getProperty("targetProject"); 
        javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
        javaModelGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(javaModelGeneratorConfiguration, childNode);
            }
        }
    }

    
    /**
     * 解析JavaClientGenerator标签（Dao层）
     * @date 2017年10月26日 下午2:00:52
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseJavaClientGenerator(Context context, Node node) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); 
        String targetPackage = attributes.getProperty("targetPackage"); 
        String targetProject = attributes.getProperty("targetProject"); 
        String implementationPackage = attributes.getProperty("implementationPackage"); 
        javaClientGeneratorConfiguration.setConfigurationType(type);
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);
        javaClientGeneratorConfiguration.setImplementationPackage(implementationPackage);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(javaClientGeneratorConfiguration, childNode);
            }
        }
    }

    
    /**
     * 解析JavaServiceGenerator标签（Service层）
     * @date 2017年10月26日 下午2:07:52
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseJavaServiceGenerator(Context context, Node node) {
        JavaServiceGeneratorConfiguration javaServiceGeneratorConfiguration = new JavaServiceGeneratorConfiguration();
        context.setJavaServiceGeneratorConfiguration(javaServiceGeneratorConfiguration);
        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); 
        String targetPackage = attributes.getProperty("targetPackage"); 
        String targetProject = attributes.getProperty("targetProject"); 
        javaServiceGeneratorConfiguration.setConfigurationType(type);
        javaServiceGeneratorConfiguration.setTargetPackage(targetPackage);
        javaServiceGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(javaServiceGeneratorConfiguration, childNode);
            }
        }
    }
    
    /**
     * 解析JavaServiceImplGenerator标签（ServiceImpl层）
     * @date 2017年10月26日 下午2:07:56
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseJavaServiceImplGenerator(Context context, Node node) {
        JavaServiceImplGeneratorConfiguration javaServiceImplGeneratorConfiguration = new JavaServiceImplGeneratorConfiguration();
        context.setJavaServiceImplGeneratorConfiguration(javaServiceImplGeneratorConfiguration);
        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); 
        String targetPackage = attributes.getProperty("targetPackage"); 
        String targetProject = attributes.getProperty("targetProject"); 
        javaServiceImplGeneratorConfiguration.setConfigurationType(type);
        javaServiceImplGeneratorConfiguration.setTargetPackage(targetPackage);
        javaServiceImplGeneratorConfiguration.setTargetProject(targetProject);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(javaServiceImplGeneratorConfiguration, childNode);
            }
        }
    }
    
    /**
     * 解析JDBC连接
     * @date 2017年10月26日 下午2:00:56
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseJdbcConnection(Context context, Node node) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        Properties attributes = parseAttributes(node);
        String driverClass = attributes.getProperty("driverClass"); 
        String connectionURL = attributes.getProperty("connectionURL"); 
        String userId = attributes.getProperty("userId"); 
        String password = attributes.getProperty("password"); 
        jdbcConnectionConfiguration.setDriverClass(driverClass);
        jdbcConnectionConfiguration.setConnectionURL(connectionURL);
        if (stringHasValue(userId)) {
            jdbcConnectionConfiguration.setUserId(userId);
        }
        if (stringHasValue(password)) {
            jdbcConnectionConfiguration.setPassword(password);
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(jdbcConnectionConfiguration, childNode);
            }
        }
    }
    
    /**
     * 解析classPathEntry标签
     * @date 2017年10月26日 下午2:01:01
     * @author Qing Luo
     * @param configuration
     * @param node
     */
    private void parseClassPathEntry(Configuration configuration, Node node) {
        Properties attributes = parseAttributes(node);
        configuration.addClasspathEntry(attributes.getProperty("location")); 
    }
   
    /**
     * 解析property标签
     * @date 2017年10月26日 下午2:01:05
     * @author Qing Luo
     * @param propertyHolder
     * @param node
     */
    private void parseProperty(PropertyHolder propertyHolder, Node node) {
        Properties attributes = parseAttributes(node);
        String name = attributes.getProperty("name"); 
        String value = attributes.getProperty("value"); 
        propertyHolder.addProperty(name, value);
    }

    
    /**
     * 解析Attributes值
     * @date 2017年10月26日 下午2:01:11
     * @author Qing Luo
     * @param node
     * @return
     */
    private Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap nnm = node.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node attribute = nnm.item(i);
            String value = parsePropertyTokens(attribute.getNodeValue());
            attributes.put(attribute.getNodeName(), value);
        }
        return attributes;
    }
    
    /**
     * 解析PropertyTokens
     * @date 2017年10月26日 下午2:01:15
     * @author Qing Luo
     * @param string
     * @return
     */
    private String parsePropertyTokens(String string) {
        final String OPEN = "${"; 
        final String CLOSE = "}"; 
        String newString = string;
        if (newString != null) {
            int start = newString.indexOf(OPEN);
            int end = newString.indexOf(CLOSE);
            while (start > -1 && end > start) {
                String prepend = newString.substring(0, start);
                String append = newString.substring(end + CLOSE.length());
                String propName = newString.substring(start + OPEN.length(),end);
                String propValue = properties.getProperty(propName);
                if (propValue != null) {
                    newString = prepend + propValue + append;
                }
                start = newString.indexOf(OPEN, end);
                end = newString.indexOf(CLOSE, end);
            }
        }
        return newString;
    }

    
    /**
     * 解析commentGenerator标签
     * @date 2017年10月26日 下午2:01:18
     * @author Qing Luo
     * @param context
     * @param node
     */
    private void parseCommentGenerator(Context context, Node node) {
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type"); 
        if (stringHasValue(type)) {
            commentGeneratorConfiguration.setConfigurationType(type);
        }
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if ("property".equals(childNode.getNodeName())) { 
                parseProperty(commentGeneratorConfiguration, childNode);
            }
        }
    }
}
