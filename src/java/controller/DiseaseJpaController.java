/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import model.Disease;
import model.MedicalRecord;

/**
 *
 * @author Ника
 */
public class DiseaseJpaController implements Serializable {

    public DiseaseJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Disease disease) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            MedicalRecord idMedicalRecord = disease.getIdMedicalRecord();
            if (idMedicalRecord != null) {
                idMedicalRecord = em.getReference(idMedicalRecord.getClass(), idMedicalRecord.getIdMedicalRecord());
                disease.setIdMedicalRecord(idMedicalRecord);
            }
            em.persist(disease);
            if (idMedicalRecord != null) {
                idMedicalRecord.getDiseaseList().add(disease);
                idMedicalRecord = em.merge(idMedicalRecord);
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

    public void edit(Disease disease) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Disease persistentDisease = em.find(Disease.class, disease.getIdDisease());
            MedicalRecord idMedicalRecordOld = persistentDisease.getIdMedicalRecord();
            MedicalRecord idMedicalRecordNew = disease.getIdMedicalRecord();
            if (idMedicalRecordNew != null) {
                idMedicalRecordNew = em.getReference(idMedicalRecordNew.getClass(), idMedicalRecordNew.getIdMedicalRecord());
                disease.setIdMedicalRecord(idMedicalRecordNew);
            }
            disease = em.merge(disease);
            if (idMedicalRecordOld != null && !idMedicalRecordOld.equals(idMedicalRecordNew)) {
                idMedicalRecordOld.getDiseaseList().remove(disease);
                idMedicalRecordOld = em.merge(idMedicalRecordOld);
            }
            if (idMedicalRecordNew != null && !idMedicalRecordNew.equals(idMedicalRecordOld)) {
                idMedicalRecordNew.getDiseaseList().add(disease);
                idMedicalRecordNew = em.merge(idMedicalRecordNew);
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
                Integer id = disease.getIdDisease();
                if (findDisease(id) == null) {
                    throw new NonexistentEntityException("The disease with id " + id + " no longer exists.");
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
            Disease disease;
            try {
                disease = em.getReference(Disease.class, id);
                disease.getIdDisease();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The disease with id " + id + " no longer exists.", enfe);
            }
            MedicalRecord idMedicalRecord = disease.getIdMedicalRecord();
            if (idMedicalRecord != null) {
                idMedicalRecord.getDiseaseList().remove(disease);
                idMedicalRecord = em.merge(idMedicalRecord);
            }
            em.remove(disease);
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

    public List<Disease> findDiseaseEntities() {
        return findDiseaseEntities(true, -1, -1);
    }

    public List<Disease> findDiseaseEntities(int maxResults, int firstResult) {
        return findDiseaseEntities(false, maxResults, firstResult);
    }

    private List<Disease> findDiseaseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Disease.class));
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

    public Disease findDisease(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Disease.class, id);
        } finally {
            em.close();
        }
    }

    public int getDiseaseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Disease> rt = cq.from(Disease.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
