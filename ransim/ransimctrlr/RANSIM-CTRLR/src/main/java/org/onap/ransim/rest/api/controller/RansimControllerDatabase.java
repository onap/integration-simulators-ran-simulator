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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.OperationLog;

public class RansimControllerDatabase {
    
    static Logger log = Logger.getLogger(RansimControllerDatabase.class
            .getName());
    
    /**
     * Gets the CellDetail from the database.
     * 
     * @param nodeId Node Id of the cell(primary key)
     * @return Returns the cell with mentioned node ID.
     */
    CellDetails getCellDetail(String nodeId){
        EntityManagerFactory emfactory = Persistence
                .createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        CellDetails currentCell = null;
        
        try{
            currentCell = entitymanager.find(CellDetails.class, nodeId);
        }catch(Exception e){
            log.info("Exception in getCellDetail: " + e);
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
        }
        finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return currentCell;
        
    }
    
    /**
     * 
     * @param serverId
     * @return
     */
    static NetconfServers getNetconfServer(String serverId){
        EntityManagerFactory emfactory = Persistence
                .createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        NetconfServers ns = null;
        
        try{
            ns = entitymanager.find(NetconfServers.class, serverId);
        }catch(Exception e){
            log.info("Exception in getCellDetail: " + e);
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
        }
        finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return ns;
        
    }
    CellNeighbor getCellNeighbor(String nodeId){
        EntityManagerFactory emfactory = Persistence
                .createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        CellNeighbor ns = null;
        
        try{
            ns = entitymanager.find(CellNeighbor.class, nodeId);
        }catch(Exception e){
            log.info("Exception in getCellDetail: " + e);
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
        }
        finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return ns;
        
    }
    
    void deleteCellDetails(CellDetails deleteCelldetail){
        EntityManagerFactory emfactory = Persistence
                .createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        
        
        
        try{
            if (deleteCelldetail.getServerId() != null) {
                entitymanager.getTransaction().begin();
                log.info("inside NetconfServers handling ....");
                NetconfServers ns = entitymanager.find(NetconfServers.class, deleteCelldetail.getServerId());
                ns.getCells().remove(deleteCelldetail);
                entitymanager.merge(ns);
                entitymanager.flush();
                entitymanager.getTransaction().commit();
                
            }
            entitymanager.getTransaction().begin();
            CellDetails cd = entitymanager.find(CellDetails.class, deleteCelldetail.getNodeId());
            entitymanager.remove(cd);
            entitymanager.flush();
            entitymanager.getTransaction().commit();
            
        }catch(Exception e){
            log.info("Exception in getCellDetail: " + e);
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
        }
        finally {
            entitymanager.close();
            emfactory.close();
        } 
    }
    
    void deleteCellNeighbor(CellNeighbor deleteCellNeighbor){
        EntityManagerFactory emfactory = Persistence
                .createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        
        entitymanager.getTransaction().begin();
        
        try{
            log.info("inside delete cel neighbor");
            CellNeighbor cn = entitymanager.find(CellNeighbor.class, deleteCellNeighbor.getNodeId());
            entitymanager.remove(cn);
            entitymanager.flush();
            entitymanager.getTransaction().commit();
            log.info("removed cell neighbor from database");
            
        }catch(Exception e){
            log.info("Exception in deleteCellNeighbor: " + e);
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
        }
        finally {
            entitymanager.close();
            emfactory.close();
        } 
    }
    
    static void mergeCellDetails(CellDetails cellDetail){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        try{
            entitymanager.getTransaction().begin();
            entitymanager.merge(cellDetail);
            log.info("updated in database....");
            entitymanager.flush();
            entitymanager.getTransaction().commit();
        }catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
    }
    
    void mergeNeighborDetails(NeighborDetails neighborDetails){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        try{
            entitymanager.getTransaction().begin();                      
            entitymanager.merge(neighborDetails);
            log.info("updated in database....");
            entitymanager.flush();
            entitymanager.getTransaction().commit();
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
    }
    
    void mergeNetconfServers(NetconfServers netconfServers){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        try{
            entitymanager.getTransaction().begin();                      
            entitymanager.merge(netconfServers);
            log.info("updated in database....");
            entitymanager.flush();
            entitymanager.getTransaction().commit();
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
    }
    
    void mergeCellNeighbor(CellNeighbor cellNeighbor){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        try{
            entitymanager.getTransaction().begin();
            log.info("updated in database....");
            entitymanager.merge(cellNeighbor);
            entitymanager.flush();
            entitymanager.getTransaction().commit(); 
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }       
    }
    
    List<CellDetails> getCellDetailsList(){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        List<CellDetails> cds = new ArrayList<CellDetails>();
        try{
            entitymanager.getTransaction().begin();
            log.info("updated in database....");
            Query query = entitymanager.createQuery("from CellDetails cd", CellDetails.class);
            cds = query.getResultList();
            entitymanager.flush();
            entitymanager.getTransaction().commit(); 
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return cds;
    }
    
    List<CellDetails> getCellsWithNoServerIds(){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        List<CellDetails> cds = new ArrayList<CellDetails>();
        try{
            entitymanager.getTransaction().begin();
            log.info("getCellswithNoServerIds: updated in database....");            
            TypedQuery<CellDetails> query = entitymanager.createQuery(
                    "SELECT n FROM CellDetails WHERE n.serverId is null", CellDetails.class);
            cds = query.getResultList();
            entitymanager.flush();
            entitymanager.getTransaction().commit(); 
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return cds;
    }
    
    List<CellDetails> getCellsWithCollisionOrConfusion(){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        List<CellDetails> cds = new ArrayList<CellDetails>();
        try{
            entitymanager.getTransaction().begin();
            log.info("getCellsWithCollisionOrConfusion: updated in database....");            
            Query query = entitymanager
                    .createQuery(
                            "from CellDetails cd where cd.pciCollisionDetected=true or cd.pciConfusionDetected=true",
                            CellDetails.class);
            cds = query.getResultList();
            entitymanager.flush();
            entitymanager.getTransaction().commit(); 
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return cds;
    }
    
    List<OperationLog> getOperationLogList(){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        List<OperationLog> ols = new ArrayList<OperationLog>();
        try{
            entitymanager.getTransaction().begin();
            log.info("updated in database....");
            Query query = entitymanager.createQuery("from OperationLog ol", OperationLog.class);
            ols = query.getResultList();
            entitymanager.flush();
            entitymanager.getTransaction().commit(); 
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return ols;
    }
    
    List<NetconfServers> getNetconfServersList(){
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        List<NetconfServers> cnl = new ArrayList<NetconfServers>();
        try{
            log.info("updated in database....");
            Query query = entitymanager.createQuery("from NetconfServers ns", NetconfServers.class);
            cnl = query.getResultList();
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
        
        return cnl;
    }
    
    List<CellNeighbor> getCellNeighborList() {
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        List<CellNeighbor> cellNeighborList = new ArrayList<CellNeighbor>();
        try {
            entitymanager.getTransaction().begin();
            TypedQuery<CellNeighbor> query = entitymanager.createQuery("from CellNeighbor cn", CellNeighbor.class);
            cellNeighborList = query.getResultList();
            entitymanager.flush();
            entitymanager.getTransaction().commit(); 
        } catch (Exception eu) {
            if (entitymanager.getTransaction().isActive()) {
                entitymanager.getTransaction().rollback();
            }
            log.info("Exception:", eu);
        } finally {
            entitymanager.close();
            emfactory.close();
        }
        return cellNeighborList;
    }
    
}
