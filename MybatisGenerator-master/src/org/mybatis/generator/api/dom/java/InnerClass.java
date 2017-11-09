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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mybatis.generator.api.dom.OutputUtilities;

/**
 * @author Jeff Butler
 */
public class InnerClass extends JavaElement {
	
	/**
	 * 字段集合
	 * 包括这个字段的name、type、访问控制符、isFinal、isStatic等等
	 * 详见Field、JavaElement类
	 */
    private List<Field> fields;

    private List<InnerClass> innerClasses;

    private List<InnerEnum> innerEnums;

    private FullyQualifiedJavaType superClass;

    /**
     * 类名信息
     * 包括这个类的名字、加上包的全称等等
     */
    private FullyQualifiedJavaType type;

    private Set<FullyQualifiedJavaType> superInterfaceTypes;

    /**
     * 方法集合
     * 包括方法的参数、返回类型、注释、修饰符、异常、构造方法等等
     * 详见Method类
     */
    private List<Method> methods;

    /**
     * 是否抽象
     */
    private boolean isAbstract;
    
    private List<InitializationBlock> initializationBlocks;

    public InnerClass(FullyQualifiedJavaType type) {
        super();
        this.type = type;
        fields = new ArrayList<Field>();
        innerClasses = new ArrayList<InnerClass>();
        innerEnums = new ArrayList<InnerEnum>();
        superInterfaceTypes = new HashSet<FullyQualifiedJavaType>();
        methods = new ArrayList<Method>();
        initializationBlocks = new ArrayList<InitializationBlock>();
    }

    public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public void setInnerClasses(List<InnerClass> innerClasses) {
		this.innerClasses = innerClasses;
	}

	public void setInnerEnums(List<InnerEnum> innerEnums) {
		this.innerEnums = innerEnums;
	}

	public void setType(FullyQualifiedJavaType type) {
		this.type = type;
	}

	public void setSuperInterfaceTypes(
			Set<FullyQualifiedJavaType> superInterfaceTypes) {
		this.superInterfaceTypes = superInterfaceTypes;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	public void setInitializationBlocks(
			List<InitializationBlock> initializationBlocks) {
		this.initializationBlocks = initializationBlocks;
	}

	public InnerClass(String typeName) {
        this(new FullyQualifiedJavaType(typeName));
    }

    public List<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public FullyQualifiedJavaType getSuperClass() {
        return superClass;
    }

    public void setSuperClass(FullyQualifiedJavaType superClass) {
        this.superClass = superClass;
    }

    public void setSuperClass(String superClassType) {
        this.superClass = new FullyQualifiedJavaType(superClassType);
    }

    public List<InnerClass> getInnerClasses() {
        return innerClasses;
    }

    public void addInnerClass(InnerClass innerClass) {
        innerClasses.add(innerClass);
    }

    public List<InnerEnum> getInnerEnums() {
        return innerEnums;
    }

    public void addInnerEnum(InnerEnum innerEnum) {
        innerEnums.add(innerEnum);
    }

    public List<InitializationBlock> getInitializationBlocks() {
        return initializationBlocks;
    }

    public void addInitializationBlock(InitializationBlock initializationBlock) {
        initializationBlocks.add(initializationBlock);
    }

    /**
     * 核心：生成Entity类
     * @date 2017年10月30日 上午10:24:42
     * @author Qing Luo
     * @param indentLevel
     * @return
     */
    public String getFormattedContent(int indentLevel) {
        StringBuilder sb = new StringBuilder();
        addFormattedJavadoc(sb, indentLevel);
        addFormattedAnnotations(sb, indentLevel);
        OutputUtilities.javaIndent(sb, indentLevel);
        sb.append(getVisibility().getValue());
        if (isAbstract()) {
            sb.append("abstract ");
        }
        if (isStatic()) {
            sb.append("static ");
        }
        if (isFinal()) {
            sb.append("final ");
        }
        sb.append("class ");
        sb.append(getType().getShortName());
        if (superClass != null) {
            sb.append(" extends ");
            sb.append(superClass.getShortName());
        }
        if (superInterfaceTypes.size() > 0) {
            sb.append(" implements ");
            boolean comma = false;
            for (FullyQualifiedJavaType fqjt : superInterfaceTypes) {
                if (comma) {
                    sb.append(", ");
                } else {
                    comma = true;
                }
                sb.append(fqjt.getShortName());
            }
        }
        sb.append(" {");
        indentLevel++;
        Iterator<Field> fldIter = fields.iterator();
        while (fldIter.hasNext()) {
            OutputUtilities.newLine(sb);
            Field field = fldIter.next();
            sb.append(field.getFormattedContent(indentLevel));
        }
        if (initializationBlocks.size() > 0) {
            OutputUtilities.newLine(sb);
        }
        Iterator<InitializationBlock> blkIter = initializationBlocks.iterator();
        while (blkIter.hasNext()) {
            OutputUtilities.newLine(sb);
            InitializationBlock initializationBlock = blkIter.next();
            sb.append(initializationBlock.getFormattedContent(indentLevel));
            if (blkIter.hasNext()) {
                OutputUtilities.newLine(sb);
            }
        }
        if (methods.size() > 0) {
            OutputUtilities.newLine(sb);
        }
        Iterator<Method> mtdIter = methods.iterator();
        while (mtdIter.hasNext()) {
            OutputUtilities.newLine(sb);
            Method method = mtdIter.next();
            sb.append(method.getFormattedContent(indentLevel, false));
            if (mtdIter.hasNext()) {
                OutputUtilities.newLine(sb);
            }
        }
        if (innerClasses.size() > 0) {
            OutputUtilities.newLine(sb);
        }
        Iterator<InnerClass> icIter = innerClasses.iterator();
        while (icIter.hasNext()) {
            OutputUtilities.newLine(sb);
            InnerClass innerClass = icIter.next();
            sb.append(innerClass.getFormattedContent(indentLevel));
            if (icIter.hasNext()) {
                OutputUtilities.newLine(sb);
            }
        }
        if (innerEnums.size() > 0) {
            OutputUtilities.newLine(sb);
        }
        Iterator<InnerEnum> ieIter = innerEnums.iterator();
        while (ieIter.hasNext()) {
            OutputUtilities.newLine(sb);
            InnerEnum innerEnum = ieIter.next();
            sb.append(innerEnum.getFormattedContent(indentLevel));
            if (ieIter.hasNext()) {
                OutputUtilities.newLine(sb);
            }
        }
        indentLevel--;
        OutputUtilities.newLine(sb);
        OutputUtilities.javaIndent(sb, indentLevel);
        OutputUtilities.newLine(sb);
        sb.append('}');
        return sb.toString();
    }

    public Set<FullyQualifiedJavaType> getSuperInterfaceTypes() {
        return superInterfaceTypes;
    }

    public void addSuperInterface(FullyQualifiedJavaType superInterface) {
        superInterfaceTypes.add(superInterface);
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void addMethod(Method method) {
        methods.add(method);
    }

    public FullyQualifiedJavaType getType() {
        return type;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbtract) {
        this.isAbstract = isAbtract;
    }

}