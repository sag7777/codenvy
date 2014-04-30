/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.analytics.metrics.projects;

import com.codenvy.analytics.datamodel.ListValueData;
import com.codenvy.analytics.datamodel.LongValueData;
import com.codenvy.analytics.datamodel.ValueData;
import com.codenvy.analytics.datamodel.ValueDataUtil;
import com.codenvy.analytics.metrics.CalculatedMetric;
import com.codenvy.analytics.metrics.Context;
import com.codenvy.analytics.metrics.Expandable;
import com.codenvy.analytics.metrics.Metric;
import com.codenvy.analytics.metrics.MetricType;

import javax.annotation.security.RolesAllowed;

import java.io.IOException;
import java.util.ArrayList;


/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
@RolesAllowed({"system/admin", "system/manager"})
public class ProjectPaasAny extends CalculatedMetric implements Expandable {

    public ProjectPaasAny() {
        super(MetricType.PROJECT_PAAS_ANY, new MetricType[]{MetricType.PROJECT_PAAS_APPFOG,
                                                            MetricType.PROJECT_PAAS_AWS,
                                                            MetricType.PROJECT_PAAS_CLOUDBEES,
                                                            MetricType.PROJECT_PAAS_CLOUDFOUNDRY,
                                                            MetricType.PROJECT_PAAS_GAE,
                                                            MetricType.PROJECT_PAAS_HEROKU,
                                                            MetricType.PROJECT_PAAS_OPENSHIFT,
                                                            MetricType.PROJECT_PAAS_TIER3,
                                                            MetricType.PROJECT_PAAS_MANYMO});

    }

    @Override
    public ValueData getValue(Context context) throws IOException {
        long projectsAnyPaaS = 0;

        for (Metric metric : basedMetric) {
            projectsAnyPaaS += ValueDataUtil.getAsLong(metric, context).getAsLong();
        }

        return new LongValueData(projectsAnyPaaS);
    }

    @Override
    public Class<? extends ValueData> getValueDataClass() {
        return LongValueData.class;
    }

    @Override
    public String getDescription() {
        return "The number of created project with some PaaS defined";
    }
    
    @Override
    public String getExpandedValueField() {
        return PROJECT_ID;
    }
    
    @Override
    public ListValueData getExpandedValue(Context context) throws IOException {
        ListValueData result = ListValueData.DEFAULT;
        
        for (Metric metric: basedMetric) {
            ListValueData expandedValue = ((Expandable) metric).getExpandedValue(context);
            result = (ListValueData) result.union(expandedValue);
        }
                
        if (result.size() > LIMIT) {
            result = result.subList(0, LIMIT);
        }
        
        return result;
    }
}
