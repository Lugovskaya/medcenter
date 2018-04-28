/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.MedicalRecord;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.WorkPlace;

/**
 *
 * @author Ника
 */
public class WorkPlaceJpaController implements Serializable {

    public WorkPlaceJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkPlace workPlace) throws RollbackFailureException, Exception {
        if (workPlace.getMedicalRecordList() == null) {
            workPlace.setMedicalRecordList(new ArrayList<MedicalRecord>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<MedicalRecord> attachedMedicalRecordList = new ArrayList<MedicalRecord>();
            for (MedicalRecord medicalRecordListMedicalRecordToAttach : workPlace.getMedicalRecordList()) {
                medicalRecordListMedicalRecordToAttach = em.getReference(medicalRecordListMedicalRecordToAttach.getClass(), medicalRecordListMedicalRecordToAttach.getIdMedicalRecord());
                attachedMedicalRecordList.add(medicalRecordListMedicalRecordToAttach);
            }
            workPlace.setMedicalRecordList(attachedMedicalRecordList);
            em.persist(workPlace);
            for (MedicalRecord medicalRecordListMedicalRecord : workPlace.getMedicalRecordList()) {
                WorkPlace oldIdWorkPlaceOfMedicalRecordListMedicalRecord = medicalRecordListMedicalRecord.getIdWorkPlace();
                medicalRecordListMedicalRecord.setIdWorkPlace(workPlace);
                medicalRecordListMedicalRecord = em.merge(medicalRecordListMedicalRecord);
                if (oldIdWorkPlaceOfMedicalRecordListMedicalRecord != null) {
                    oldIdWorkPlaceOfMedicalRecordListMedicalRecord.getMedicalRecordList().remove(medicalRecordListMedicalRecord);
                    oldIdWorkPlaceOfMedicalRecordListMedicalRecord = em.merge(oldIdWorkPlaceOfMedicalRecordListMedicalRecord);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorkPlace workPlace) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WorkPlace persistentWorkPlace = em.find(WorkPlace.class, workPlace.getIdWorkPlace());
            List<MedicalRecord> medicalRecordListOld = persistentWorkPlace.getMedicalRecordList();
            List<MedicalRecord> medicalRecordListNew = workPlace.getMedicalRecordList();
            List<MedicalRecord> attachedMedicalRecordListNew = new ArrayList<MedicalRecord>();
            for (MedicalRecord medicalRecordListNewMedicalRecordToAttach : medicalRecordListNew) {
                medicalRecordListNewMedicalRecordToAttach = em.getReference(medicalRecordListNewMedicalRecordToAttach.getClass(), medicalRecordListNewMedicalRecordToAttach.getIdMedicalRecord());
                attachedMedicalRecordListNew.add(medicalRecordListNewMedicalRecordToAttach);
            }
            medicalRecordListNew = attachedMedicalRecordListNew;
            workPlace.setMedicalRecordList(medicalRecordListNew);
            workPlace = em.merge(workPlace);
            for (MedicalRecord medicalRecordListOldMedicalRecord : medicalRecordListOld) {
                if (!medicalRecordListNew.contains(medicalRecordListOldMedicalRecord)) {
                    medicalRecordListOldMedicalRecord.setIdWorkPlace(null);
                    medicalRecordListOldMedicalRecord = em.merge(medicalRecordListOldMedicalRecord);
                }
            }
            for (MedicalRecord medicalRecordListNewMedicalRecord : medicalRecordListNew) {
                if (!medicalRecordListOld.contains(medicalRecordListNewMedicalRecord)) {
                    WorkPlace oldIdWorkPlaceOfMedicalRecordListNewMedicalRecord = medicalRecordListNewMedicalRecord.getIdWorkPlace();
                    medicalRecordListNewMedicalRecord.setIdWorkPlace(workPlace);
                    medicalRecordListNewMedicalRecord = em.merge(medicalRecordListNewMedicalRecord);
                    if (oldIdWorkPlaceOfMedicalRecordListNewMedicalRecord != null && !oldIdWorkPlaceOfMedicalRecordListNewMedicalRecord.equals(workPlace)) {
                        oldIdWorkPlaceOfMedicalRecordListNewMedicalRecord.getMedicalRecordList().remove(medicalRecordListNewMedicalRecord);
                        oldIdWorkPlaceOfMedicalRecordListNewMedicalRecord = em.merge(oldIdWorkPlaceOfMedicalRecordListNewMedicalRecord);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = workPlace.getIdWorkPlace();
                if (findWorkPlace(id) == null) {
                    throw new NonexistentEntityException("The workPlace with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WorkPlace workPlace;
            try {
                workPlace = em.getReference(WorkPlace.class, id);
                workPlace.getIdWorkPlace();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workPlace with id " + id + " no longer exists.", enfe);
            }
            List<MedicalRecord> medicalRecordList = workPlace.getMedicalRecordList();
            for (MedicalRecord medicalRecordListMedicalRecord : medicalRecordList) {
                medicalRecordListMedicalRecord.setIdWorkPlace(null);
                medicalRecordListMedicalRecord = em.merge(medicalRecordListMedicalRecord);
            }
            em.remove(workPlace);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorkPlace> findWorkPlaceEntities() {
        return findWorkPlaceEntities(true, -1, -1);
    }

    public List<WorkPlace> findWorkPlaceEntities(int maxResults, int firstResult) {
        return findWorkPlaceEntities(false, maxResults, firstResult);
    }

    private List<WorkPlace> findWorkPlaceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkPlace.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public WorkPlace findWorkPlace(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkPlace.class, id);
        } finally {
            em.close();
        }
    }

    public int getWorkPlaceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkPlace> rt = cq.from(WorkPlace.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
