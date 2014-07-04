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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codenvy.eclipse.client.auth.Credentials;

/**
 * {@link DataStoreFactory} implementation providing {@link DataStore} which stores user credentials in memory.
 * 
 * @author Kevin Pollet
 */
public class InMemoryDataStoreFactory implements DataStoreFactory<String, Credentials> {
    private final ConcurrentMap<String, DataStore<String, Credentials>> dataStores;

    public InMemoryDataStoreFactory() {
        this.dataStores = new ConcurrentHashMap<>();
    }

    @Override
    public DataStore<String, Credentials> getDataStore(String id) {
        checkNotNull(id);

        DataStore<String, Credentials> store = dataStores.get(id);
        if (store == null) {
            final DataStore<String, Credentials> dataStore = new InMemoryDataStore();
            store = dataStores.putIfAbsent(id, dataStore);
            if (store == null) {
                store = dataStore;
            }
        }
        return store;
    }
}
