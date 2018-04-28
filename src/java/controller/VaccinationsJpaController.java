/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
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
import model.MedicalRecord;
import model.Vaccinations;

/**
 *
 * @author Ника
 */
public class VaccinationsJpaController implements Serializable {

    public VaccinationsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vaccinations vaccinations) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            MedicalRecord idMedicalRecord = vaccinations.getIdMedicalRecord();
            if (idMedicalRecord != null) {
                idMedicalRecord = em.getReference(idMedicalRecord.getClass(), idMedicalRecord.getIdMedicalRecord());
                vaccinations.setIdMedicalRecord(idMedicalRecord);
            }
            em.persist(vaccinations);
            if (idMedicalRecord != null) {
                idMedicalRecord.getVaccinationsList().add(vaccinations);
                idMedicalRecord = em.merge(idMedicalRecord);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findVaccinations(vaccinations.getIdVaccinations()) != null) {
                throw new PreexistingEntityException("Vaccinations " + vaccinations + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vaccinations vaccinations) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Vaccinations persistentVaccinations = em.find(Vaccinations.class, vaccinations.getIdVaccinations());
            MedicalRecord idMedicalRecordOld = persistentVaccinations.getIdMedicalRecord();
            MedicalRecord idMedicalRecordNew = vaccinations.getIdMedicalRecord();
            if (idMedicalRecordNew != null) {
                idMedicalRecordNew = em.getReference(idMedicalRecordNew.getClass(), idMedicalRecordNew.getIdMedicalRecord());
                vaccinations.setIdMedicalRecord(idMedicalRecordNew);
            }
            vaccinations = em.merge(vaccinations);
            if (idMedicalRecordOld != null && !idMedicalRecordOld.equals(idMedicalRecordNew)) {
                idMedicalRecordOld.getVaccinationsList().remove(vaccinations);
                idMedicalRecordOld = em.merge(idMedicalRecordOld);
            }
            if (idMedicalRecordNew != null && !idMedicalRecordNew.equals(idMedicalRecordOld)) {
                idMedicalRecordNew.getVaccinationsList().add(vaccinations);
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
                Integer id = vaccinations.getIdVaccinations();
                if (findVaccinations(id) == null) {
                    throw new NonexistentEntityException("The vaccinations with id " + id + " no longer exists.");
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
            Vaccinations vaccinations;
            try {
                vaccinations = em.getReference(Vaccinations.class, id);
                vaccinations.getIdVaccinations();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vaccinations with id " + id + " no longer exists.", enfe);
            }
            MedicalRecord idMedicalRecord = vaccinations.getIdMedicalRecord();
            if (idMedicalRecord != null) {
                idMedicalRecord.getVaccinationsList().remove(vaccinations);
                idMedicalRecord = em.merge(idMedicalRecord);
            }
            em.remove(vaccinations);
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

    public List<Vaccinations> findVaccinationsEntities() {
        return findVaccinationsEntities(true, -1, -1);
    }

    public List<Vaccinations> findVaccinationsEntities(int maxResults, int firstResult) {
        return findVaccinationsEntities(false, maxResults, firstResult);
    }

    private List<Vaccinations> findVaccinationsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vaccinations.class));
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

    public Vaccinations findVaccinations(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vaccinations.class, id);
        } finally {
            em.close();
        }
    }

    public int getVaccinationsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vaccinations> rt = cq.from(Vaccinations.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
