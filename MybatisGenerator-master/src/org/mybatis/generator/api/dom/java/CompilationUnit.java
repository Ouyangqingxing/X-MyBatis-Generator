/*
 *  Copyright 2006 The Apache Software Foundation
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
package org.mybatis.generator.api.dom.java;

import java.util.List;
import java.util.Set;

/**
 * 这个类描述了编译公共方法（类、接口、枚举）
 * @author Jeff Butler
 */
public interface CompilationUnit {
	
    String getFormattedContent();

    Set<FullyQualifiedJavaType> getImportedTypes();
    
    Set<String> getStaticImports();

    FullyQualifiedJavaType getSuperClass();

    boolean isJavaInterface();

    boolean isJavaEnumeration();

    Set<FullyQualifiedJavaType> getSuperInterfaceTypes();

    FullyQualifiedJavaType getType();

    void setType(FullyQualifiedJavaType type);
    
    void addImportedType(FullyQualifiedJavaType importedType);

    void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes);
    
    void addStaticImport(String staticImport);
    
    void addStaticImports(Set<String> staticImports);

    void addFileCommentLine(String commentLine);

    List<String> getFileCommentLines();
    
}