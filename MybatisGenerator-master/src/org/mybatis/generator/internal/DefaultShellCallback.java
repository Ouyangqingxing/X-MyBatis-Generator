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
package org.mybatis.generator.internal;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.util.StringTokenizer;

import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.exception.ShellException;

/**
 * @author Jeff Butler
 */
public class DefaultShellCallback implements ShellCallback {
	
    private boolean overwrite;

    /**
     *  
     */
    public DefaultShellCallback(boolean overwrite) {
        super();
        this.overwrite = overwrite;
    }

    public File getDirectory(String targetProject, String targetPackage)throws ShellException {
        File project = new File(targetProject);
        if (!project.isDirectory()) {
            throw new ShellException(getString("Warning.9",targetProject));
        }
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }
        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new ShellException(getString("Warning.10",directory.getAbsolutePath()));
            }
        }
        return directory;
    }

    public void refreshProject(String project) {}

    public boolean isMergeSupported() {
        return false;
    }

    public boolean isOverwriteEnabled() {
        return overwrite;
    }

    public String mergeJavaFile(String newFileSource,
            String existingFileFullPath, String[] javadocTags, String fileEncoding)
            throws ShellException {
        throw new UnsupportedOperationException();
    }
    
}