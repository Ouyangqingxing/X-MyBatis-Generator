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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.mybatis.generator.api.dom.OutputUtilities;

/**
 * Java方法
 * @author Jeff Butler
 */
public class Method extends JavaElement {

    private List<String> bodyLines;

    /**
     * 是否有构造方法
     */
    private boolean constructor;

    /**
     * 返回类型全称
     */
    private FullyQualifiedJavaType returnType;

    private String name;

    /**
     * 参数集合
     */
    private List<Parameter> parameters;

    /**
     * 异常集合
     */
    private List<FullyQualifiedJavaType> exceptions;
    
    private boolean isSynchronized;
    
    private boolean isNative;

    /**
     *  构造方法
     */
    public Method() {
        this("bar");
    }
    
    public Method(String name) {
        super();
        bodyLines = new ArrayList<String>();
        parameters = new ArrayList<Parameter>();
        exceptions = new ArrayList<FullyQualifiedJavaType>();
        this.name = name;
    }
    
    public Method(Method original) {
        super(original);
        bodyLines = new ArrayList<String>();
        parameters = new ArrayList<Parameter>();
        exceptions = new ArrayList<FullyQualifiedJavaType>();
        this.bodyLines.addAll(original.bodyLines);
        this.constructor = original.constructor;
        this.exceptions.addAll(original.exceptions);
        this.name = original.name;
        this.parameters.addAll(original.parameters);
        this.returnType = original.returnType;
        this.isNative = original.isNative;
        this.isSynchronized = original.isSynchronized;
    }
    
    public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public void setBodyLines(List<String> bodyLines) {
		this.bodyLines = bodyLines;
	}

    public List<String> getBodyLines() {
        return bodyLines;
    }

    public void addBodyLine(String line) {
        bodyLines.add(line);
    }

    public void addBodyLine(int index, String line) {
        bodyLines.add(index, line);
    }

    public void addBodyLines(Collection<String> lines) {
        bodyLines.addAll(lines);
    }

    public void addBodyLines(int index, Collection<String> lines) {
        bodyLines.addAll(index, lines);
    }

    /**
     * 核心：生成一个方法
     * @date 2017年10月27日 下午5:04:22
     * @author Qing Luo
     * @param indentLevel
     * @param interfaceMethod
     * @return
     */
    public String getFormattedContent(int indentLevel, boolean interfaceMethod) {
        StringBuilder sb = new StringBuilder();
        addFormattedJavadoc(sb, indentLevel);
        addFormattedAnnotations(sb, indentLevel);
        OutputUtilities.javaIndent(sb, indentLevel);
        if (!interfaceMethod) {
            sb.append(getVisibility().getValue());
            if (isStatic()) {
                sb.append("static "); 
            }
            if (isFinal()) {
                sb.append("final ");
            }
            if (isSynchronized()) {
                sb.append("synchronized ");
            }
            if (isNative()) {
                sb.append("native ");
            } else if (bodyLines.size() == 0) {
                sb.append("abstract ");
            }
        }
        else{
        	sb.append(getVisibility().getValue());
        }
        if (!constructor) {
            if (getReturnType() == null) {
                sb.append("void");
            } else {
                sb.append(getReturnType().getShortName());
            }
            sb.append(' ');
        }
        sb.append(getName());
        sb.append('(');
        boolean comma = false;
        for (Parameter parameter : getParameters()) {
            if (comma) {
                sb.append(", ");
            } else {
                comma = true;
            }
            sb.append(parameter.getFormattedContent());
        }
        sb.append(')');
        if (getExceptions().size() > 0) {
            sb.append(" throws ");
            comma = false;
            for (FullyQualifiedJavaType fqjt : getExceptions()) {
                if (comma) {
                    sb.append(", ");
                } else {
                    comma = true;
                }
                sb.append(fqjt.getShortName());
            }
        }
        if (bodyLines.size() == 0 || isNative()) {
            sb.append(';');
        } else {
            sb.append(" {");
            indentLevel++;
            ListIterator<String> listIter = bodyLines.listIterator();
            while (listIter.hasNext()) {
                String line = listIter.next();
                if (line.startsWith("}")) {
                    indentLevel--;
                }
                OutputUtilities.newLine(sb);
                OutputUtilities.javaIndent(sb, indentLevel);
                sb.append(line);
                if ((line.endsWith("{") && !line.startsWith("switch"))
                        || line.endsWith(":")) {
                    indentLevel++;
                }
                if (line.startsWith("break")) {
                    if (listIter.hasNext()) {
                        String nextLine = listIter.next();
                        if (nextLine.startsWith("}")) {
                            indentLevel++;
                        }
                        listIter.previous();
                    }
                    indentLevel--;
                }
            }
            indentLevel--;
            OutputUtilities.newLine(sb);
            OutputUtilities.javaIndent(sb, indentLevel);
            sb.append('}');
        }
        return sb.toString();
    }

    public boolean isConstructor() {
        return constructor;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public void addParameter(int index, Parameter parameter) {
        parameters.add(index, parameter);
    }

    public FullyQualifiedJavaType getReturnType() {
        return returnType;
    }
    
    public void setReturnType(FullyQualifiedJavaType returnType) {
        this.returnType = returnType;
    }

    public List<FullyQualifiedJavaType> getExceptions() {
        return exceptions;
    }

    public void addException(FullyQualifiedJavaType exception) {
        exceptions.add(exception);
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
    }

    public boolean isNative() {
        return isNative;
    }

    public void setNative(boolean isNative) {
        this.isNative = isNative;
    }
    
}