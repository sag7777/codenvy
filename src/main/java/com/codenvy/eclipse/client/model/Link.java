/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The link model class.
 * 
 * @author Kevin Pollet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
    public static final String WEB_LINK_REL_ATTRIBUTE_VALUE = "web url";
    public static final String DOWNLOAD_LINK_REL_ATTRIBUTE_VALUE = "download result";

    public final String        href;
    public final String        rel;
    public final String        produces;
    public final String        consumes;
    public final String        method;

    @JsonCreator
    public Link(@JsonProperty("href") String href,
                @JsonProperty("rel") String rel,
                @JsonProperty("produces") String produces,
                @JsonProperty("consumes") String consumes,
                @JsonProperty("method") String method) {

        this.href = href;
        this.rel = rel;
        this.produces = produces;
        this.consumes = consumes;
        this.method = method;
    }

    @Override
    public String toString() {
        return "Link [href=" + href + ", rel=" + rel + ", produces=" + produces + ", consumes=" + consumes + ", method=" + method + "]";
    }
}
