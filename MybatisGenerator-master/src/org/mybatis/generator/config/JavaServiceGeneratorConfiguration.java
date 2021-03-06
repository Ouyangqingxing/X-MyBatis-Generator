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

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * @author Jeff Butler
 */
public class JavaServiceGeneratorConfiguration extends TypedPropertyHolder {
    
	/**
	 * 目标包
	 */
    private String targetPackage;

    /**
     * 目标项目
     */
    private String targetProject;
    
    /**
     * 实现包
     */
    private String implementationPackage;

    /**
     *  
     */
    public JavaServiceGeneratorConfiguration() {
        super();
    }

    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public XmlElement toXmlElement() {
        XmlElement answer = new XmlElement("javaServiceGenerator");
        if (getConfigurationType() != null) {
            answer.addAttribute(new Attribute("type", getConfigurationType()));
        }
        if (targetPackage != null) {
            answer.addAttribute(new Attribute("targetPackage", targetPackage));
        }
        if (targetProject != null) {
            answer.addAttribute(new Attribute("targetProject", targetProject));
        }
        if (implementationPackage != null) {
            answer.addAttribute(new Attribute("implementationPackage", targetProject));
        }
        addPropertyXmlElements(answer);
        return answer;
    }

    public String getImplementationPackage() {
        return implementationPackage;
    }

    public void setImplementationPackage(String implementationPackage) {
        this.implementationPackage = implementationPackage;
    }

    public void validate(List<String> errors, String contextId) {
        if (!stringHasValue(targetProject)) {
            errors.add(getString("ValidationError.2", contextId));
        }
        if (!stringHasValue(targetPackage)) {
            errors.add(getString("ValidationError.12","javaServiceGenerator", contextId));
        }
        if (!stringHasValue(getConfigurationType())) {
            errors.add(getString("ValidationError.20",contextId));
        }
    }
    
}