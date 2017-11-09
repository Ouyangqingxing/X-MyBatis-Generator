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

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * Mapper层 selectById代码生成
 * @author Jeff Butler
 */
public class SelectByPrimaryKeyElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectByPrimaryKeyElementGenerator() {
        super();
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
    	XmlElement answer = new XmlElement("select"); 
        answer.addAttribute(new Attribute("id", introspectedTable.getSelectByPrimaryKeyStatementId())); 
        if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
            answer.addAttribute(new Attribute("resultMap",introspectedTable.getResultMapWithBLOBsId()));
        } else {
            answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
        }
        String parameterType;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterType = introspectedTable.getPrimaryKeyType();
        } else {
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterType = "map"; 
            } else {
            	parameterType = publicPackageName + ".param.SelectByIdParam";
            }
        }
        answer.addAttribute(new Attribute("parameterType", parameterType));
        context.getCommentGenerator().addComment(answer);
        StringBuilder sb = new StringBuilder();
        sb.append("select "); 
        if (stringHasValue(introspectedTable.getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,"); 
        }
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(",")); 
            answer.addElement(getBlobColumnListElement());
        }
        sb.setLength(0);
        sb.append("    from "); 
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("        and "); 
            } else {
                sb.append("        where "); 
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = "); 
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));
        }
        if (context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
    
}