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
package com.codenvy.eclipse.client.store;


import org.junit.Assert;
import org.junit.Test;

import com.codenvy.eclipse.client.auth.Credentials;

/**
 * {@link InMemoryDataStoreFactory} tests.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStoreFactoryTest {
    @Test(expected = NullPointerException.class)
    public void testGetDataStoreWithNullId() {
        new InMemoryDataStoreFactory().getDataStore(null);
    }

    @Test
    public void testGetDataStoreId() {
        final DataStoreFactory<String, Credentials> dataStoreFactory = new InMemoryDataStoreFactory();

        Assert.assertNotNull(dataStoreFactory.getDataStore("dummy"));
        Assert.assertEquals(dataStoreFactory.getDataStore("equals"), dataStoreFactory.getDataStore("equals"));
        Assert.assertNotEquals(dataStoreFactory.getDataStore("equals"), dataStoreFactory.getDataStore("equals2"));
    }
}
