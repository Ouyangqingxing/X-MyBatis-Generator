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
package org.mybatis.generator.codegen;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.internal.ObjectFactory;

/**
 * @author Jeff Butler
 */
public class RootClassInfo {

    private static Map<String, RootClassInfo> rootClassInfoMap;
    
    private PropertyDescriptor[] propertyDescriptors;
    
    private String className;
    
    private List<String> warnings;
    
    static {
        rootClassInfoMap = Collections.synchronizedMap(new HashMap<String, RootClassInfo>());
    }

    public static RootClassInfo getInstance(String className,List<String> warnings) {
        RootClassInfo classInfo = rootClassInfoMap.get(className);
        if (classInfo == null) {
            classInfo = new RootClassInfo(className, warnings);
            rootClassInfoMap.put(className, classInfo);
        }
        return classInfo;
    }

    private RootClassInfo(String className, List<String> warnings) {
        super();
        this.className = className;
        this.warnings = warnings;
        if (className == null) {
            return;
        }
        try {
            Class<?> clazz = ObjectFactory.externalClassForName(className);
            BeanInfo bi = Introspector.getBeanInfo(clazz);
            propertyDescriptors = bi.getPropertyDescriptors();
        } catch (Exception e) {
            propertyDescriptors = null;
            warnings.add(getString("Warning.20", className));
        }
    }

    public boolean containsProperty(IntrospectedColumn introspectedColumn) {
        if (propertyDescriptors == null) {
            return false;
        }
        boolean found = false;
        String propertyName = introspectedColumn.getJavaProperty();
        String propertyType = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
            if (propertyDescriptor.getName().equals(propertyName)) {
                if (!propertyDescriptor.getPropertyType().getName().equals(propertyType)) {
                    warnings.add(getString("Warning.21",propertyName, className, propertyType));
                    break;
                }
                if (propertyDescriptor.getReadMethod() == null) {
                    warnings.add(getString("Warning.22",propertyName, className));
                    break;
                }
                if (propertyDescriptor.getWriteMethod() == null) {
                    warnings.add(getString("Warning.23",propertyName, className));
                    break;
                }
                found = true;
                break;
            }
        }
        return found;
    }
    
}