/*
 *    Copyright (C) 2013 Codenvy.
 *
 */
package com.codenvy.analytics.server.vew.template;


import com.codenvy.analytics.metrics.InitialValueNotFoundException;
import com.codenvy.analytics.metrics.Metric;
import com.codenvy.analytics.metrics.MetricFactory;
import com.codenvy.analytics.metrics.value.ValueData;

import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Map;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class MetricRow extends AbstractRow {

    private static final String ATTRIBUTE_FORMAT                  = "format";
    private static final String ATTRIBUTE_TYPE                    = "type";
    private static final String ATTRIBUTE_TITLE                   = "title";

    private static final String DEFAULT_FORMAT = "%.0f";

    private final Metric metric;
    private final String format;
    private final String title;

    private MetricRow(Metric metric, String title, String format) {
        super();

        this.metric = metric;
        this.title = title;
        this.format = format;
    }

    /** {@inheritDoc} */
    protected String doRetrieve(Map<String, String> context, int columnNumber) throws IOException {
        switch (columnNumber) {
            case 0:
                return getTitle();
            default:
                try {
                    ValueData valueData = metric.getValue(context);
                    return getAsString(valueData, format);
                } catch (InitialValueNotFoundException e) {
                    return "";
                }
        }
    }

    /** @return {@link #title} */
    public String getTitle() {
        return title;
    }

    /** @return {@link #metric} */
    public Metric getMetric() {
        return metric;
    }

    /** Factory method */
    public static MetricRow initialize(Element element) {
        String formatAttr = element.getAttribute(ATTRIBUTE_FORMAT);

        Metric metric = MetricFactory.createMetric(element.getAttribute(ATTRIBUTE_TYPE));
        String format = formatAttr.isEmpty() ? DEFAULT_FORMAT : formatAttr;
        String title = element.getAttribute(ATTRIBUTE_TITLE);

        return new MetricRow(metric, title, format);
    }
}