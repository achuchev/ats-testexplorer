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
package com.axway.ats.testexplorer.pages.runsByTypeDashboard.run;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.axway.ats.log.autodb.exceptions.DatabaseAccessException;
import com.axway.ats.testexplorer.pages.BasePage;
import com.axway.ats.testexplorer.pages.WelcomePage;
import com.axway.ats.testexplorer.pages.runsByTypeDashboard.home.RunsByTypeDashboardHomePage;

public class RunsByTypeDashboardRunPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private String[]          jsonDatas;

    public RunsByTypeDashboardRunPage( PageParameters parameters ) {

        super( parameters );

        addNavigationLink( WelcomePage.class, new PageParameters(), "Home", null );

        addNavigationLink( RunsByTypeDashboardHomePage.class,
                           new PageParameters(),
                           "Runs by type",
                           parameters.get( "productName" ).toString() + "/"
                                           + parameters.get( "versionName" ).toString() + "/"
                                           + parameters.get( "type" ).toString() );

        AjaxLink<String> modalTooltip = new AjaxLink<String>( "modalTooltip" ) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(
                                 AjaxRequestTarget target ) {

            }
        };
        //        modalTooltip.
        modalTooltip.add( new WebMarkupContainer( "helpButton" ) );

        add( modalTooltip );

        if( !parameters.isEmpty() ) {
            String productName = parameters.get( "productName" ).toString();
            String versionName = parameters.get( "versionName" ).toString();
            String buildType = parameters.get( "type" ).toString();

            String whereClause = "WHERE productName = '" + productName + "' AND versionName = '" + versionName
                                 + "' AND runId IN (SELECT runId FROM tRunMetainfo WHERE name='type' AND value='"
                                 + buildType + "')";

            if( "unspecified".equals( buildType ) ) {
                whereClause = "WHERE productName = '" + productName + "' AND versionName = '" + versionName
                              + "' AND runId NOT IN (SELECT runId FROM tRunMetainfo WHERE name='type')";
            }

            try {
                jsonDatas = new DashboardRunUtils().initData( whereClause, buildType );
            } catch( DatabaseAccessException e ) {
                LOG.error( "Unable to get run and suites data.", e );
                error( "Unable to get run and suites data." );
            }

        }

    }

    @Override
    public void renderHead(
                            IHeaderResponse response ) {

        if( !getPageParameters().isEmpty() ) {
            new DashboardRunUtils().callJavaScript( response, jsonDatas );
        } else {
            String errorScript = ";resize();";
            response.render( OnLoadHeaderItem.forScript( errorScript ) );
        }

    }

    @Override
    public String getPageName() {

        return "Suites";
    }

}
