/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.api.client.maps;

import java.util.ArrayList;

import org.drools.workbench.services.verifier.api.client.AnalyzerConfigurationMock;
import org.drools.workbench.services.verifier.api.client.index.keys.Key;
import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKey;
import org.drools.workbench.services.verifier.api.client.maps.util.HasKeys;
import org.drools.workbench.services.verifier.api.client.maps.util.HasUUID;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UpdatableInspectorListTest {

    private UpdatableInspectorList<HasUUID, Item> list;
    private AnalyzerConfiguration configuration;


    @Before
    public void setUp() throws
                        Exception {

        configuration = new AnalyzerConfigurationMock();

        list = new UpdatableInspectorList<>( new InspectorFactory<HasUUID, Item>( configuration ) {

            @Override
            public HasUUID make( final Item item ) {
                return new HasUUID() {
                    @Override
                    public UUIDKey getUuidKey() {
                        return mock( UUIDKey.class );
                    }
                };
            }
        },
                                             configuration );

    }

    @Test
    public void add() throws
                      Exception {
        final ArrayList<Item> updates = new ArrayList<>();

        updates.add( new Item() );

        list.update( updates );

        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void reAdd() throws
                        Exception {
        final ArrayList<Item> updates = new ArrayList<>();

        updates.add( new Item() );

        list.update( updates );
        assertEquals( 1,
                      list.size() );

        list.update( updates );
        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void reAddNew() throws
                           Exception {
        final ArrayList<Item> updates = new ArrayList<>();

        updates.add( new Item() );

        list.update( updates );
        assertEquals( 1,
                      list.size() );

        updates.add( new Item() );
        list.update( updates );
        assertEquals( 2,
                      list.size() );
    }

    @Test
    public void remove() throws
                         Exception {
        final ArrayList<Item> updates = new ArrayList<>();

        updates.add( new Item() );
        final Item removeMe = new Item();
        updates.add( removeMe );

        list.update( updates );
        assertEquals( 2,
                      list.size() );

        updates.remove( removeMe );
        list.update( updates );
        assertEquals( 1,
                      list.size() );
    }

    private class Item
            implements HasKeys {

        private UUIDKey uuidKey = configuration.getUUID( this );

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}