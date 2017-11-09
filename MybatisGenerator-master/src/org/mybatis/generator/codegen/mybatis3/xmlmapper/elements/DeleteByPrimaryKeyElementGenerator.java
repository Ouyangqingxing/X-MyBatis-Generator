/*
 *  Copyright 2009 The Apache Software Foundation
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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * Mapper层 deleteById代码生成
 * @author Jeff Butler
 */
public class DeleteByPrimaryKeyElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;
    
    public DeleteByPrimaryKeyElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    /**
     * 从形如" + publicPackageName + ".xxx.yyy.zzz.impl中
     * 获取到" + publicPackageName + ".xxx.yyy.zzz
     * @date 2017年11月7日 下午2:38:09
     * @author Qing Luo
     * @param packageName
     * @return
     */
    private String getPublicPackageName(String packageName){
    	int lastPointIndex = 0;
    	for(int i = 0 ; i < packageName.length() ; i ++){
    		if(packageName.charAt(i) == '.'){
    			lastPointIndex = i;
    		}
    	}
    	String result = packageName.substring(0, lastPointIndex);
    	return result;
    }
    
    @Override
    public void addElements(XmlElement parentElement) {
    	String publicPackageName = getPublicPackageName(this.context.getJavaModelGeneratorConfiguration().getTargetPackage());
        XmlElement answer = new XmlElement("delete"); 
        answer.addAttribute(new Attribute("id", introspectedTable.getDeleteByPrimaryKeyStatementId())); 
        String parameterClass;
        if (!isSimple && introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterClass = introspectedTable.getPrimaryKeyType();
        } else {
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterClass = "map"; 
            } else {
                parameterClass = publicPackageName + ".param.DeleteByIdParam";
            }
        }
        answer.addAttribute(new Attribute("parameterType", parameterClass));
        //添加注释
        context.getCommentGenerator().addComment(answer);
        //代码段
        StringBuilder sb = new StringBuilder();
        sb.append("delete from "); 
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("    and "); 
            } else {
                sb.append("    where "); 
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = "); 
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));
        }
        if (context.getPlugins().sqlMapDeleteByPrimaryKeyElementGenerated(answer,introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
    
}