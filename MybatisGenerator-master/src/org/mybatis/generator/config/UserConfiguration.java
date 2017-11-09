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

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 用户信息 配置
 * @author Jason
 */
public class UserConfiguration extends PropertyHolder {

    private String name;
    
    private String year;
    
    private String company;
    
    public UserConfiguration() {
        super();
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("user");
        xmlElement.addAttribute(new Attribute("name", name));
        xmlElement.addAttribute(new Attribute("year", year));
        xmlElement.addAttribute(new Attribute("company", company));
        addPropertyXmlElements(xmlElement);
        return xmlElement;
    }
    
}