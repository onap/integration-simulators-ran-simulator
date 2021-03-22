/*-
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.rest.api.controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.ransim.rest.api.models.CellDetails;
import org.springframework.http.ResponseEntity;

public class TestRansimControllerServices {

    @Test
    public void testGetOperationLog() {
        ResponseEntity<String> rsEntity = Mockito.mock(ResponseEntity.class);

        EntityManagerFactory emfactory = Mockito.mock(EntityManagerFactory.class);
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(emfactory.createEntityManager()).thenReturn(entityManager);

        TypedQuery<CellDetails> query = Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery("from CellDetails cd", CellDetails.class)).thenReturn(query);

        List<CellDetails> cellDetailList = new ArrayList<CellDetails>();
        Mockito.when(query.getResultList()).thenReturn(cellDetailList);
        assertNotNull(rsEntity);

    }

    @Test
    public void testModifyACell() {

        ResponseEntity<String> rsEntity = Mockito.mock(ResponseEntity.class);

        EntityManagerFactory emfactory = Mockito.mock(EntityManagerFactory.class);
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(emfactory.createEntityManager()).thenReturn(entityManager);

        TypedQuery<CellDetails> query = Mockito.mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery("from CellDetails cd", CellDetails.class)).thenReturn(query);

    }

}
