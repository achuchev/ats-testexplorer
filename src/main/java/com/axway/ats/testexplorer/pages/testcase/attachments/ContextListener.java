/*
 * Copyright 2017 Axway Software
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.testexplorer.pages.testcase.attachments;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.axway.ats.common.filesystem.FileSystemOperationException;
import com.axway.ats.core.filesystem.LocalFileSystemOperations;
import com.axway.ats.core.utils.IoUtils;
import com.axway.ats.core.utils.StringUtils;

public class ContextListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger( ContextListener.class );
    
    //set env var available for the current web app, where all attached filles will be stored
    private static final String ATTACHED_FILES_DIR = "ats-attached-files";
    
    public static String getAttachedFilesDir(){
        
        return ATTACHED_FILES_DIR;
    }

    @Override
    public void contextInitialized(
                                    ServletContextEvent sce ) {

        LocalFileSystemOperations operations = new LocalFileSystemOperations();
        String tomcatDir = System.getenv( "CATALINA_BASE" );
        if( StringUtils.isNullOrEmpty( tomcatDir ) ) {
            tomcatDir = System.getenv( "CATALINA_HOME" );
        }

        if( StringUtils.isNullOrEmpty( tomcatDir ) ) {
            LOG.error( "Neither 'CATALINA_BASE' nor 'CATALINA_HOME' was set. No file would be displayed to the current test case." );
        } else {
            String atsAttachedFiles = IoUtils.normalizeFilePath( tomcatDir + "/" + ATTACHED_FILES_DIR );
            if( !operations.doesFileExist( atsAttachedFiles ) ) {
                try {
                    operations.createDirectory( atsAttachedFiles );
                } catch( FileSystemOperationException fsoe ) {
                    sce.getServletContext().log(
                                                 "Could not create directory for storing ATS attached files at "
                                                 + atsAttachedFiles, fsoe );
                    return;
                }
            }

            // setting folder path to the property
            sce.getServletContext().setAttribute( ATTACHED_FILES_DIR, atsAttachedFiles );

            sce.getServletContext()
               .log( "ATS attached files directory is set to \"" + atsAttachedFiles + "\"." );
        }
    }

    @Override
    public void contextDestroyed(
                                  ServletContextEvent sce ) {}
}
