/*
 *  Copyright 2010 The MyBatis Team
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

/**
 * Dao层代码生成器 抽象层
 * @author Jeff Butler
 */
public abstract class AbstractJavaClientGenerator extends AbstractJavaGenerator {

    private boolean requiresXMLGenerator;
    
    public abstract AbstractXmlGenerator getMatchedXMLGenerator();
    
    /**
     * 构造方法
     * @param requiresXMLGenerator
     */
    public AbstractJavaClientGenerator(boolean requiresXMLGenerator) {
        super();
        this.requiresXMLGenerator = requiresXMLGenerator;
    }

    public boolean requiresXMLGenerator() {
        return requiresXMLGenerator;
    }

}