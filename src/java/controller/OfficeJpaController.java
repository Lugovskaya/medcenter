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
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Doctor;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Office;

/**
 *
 * @author Ника
 */
public class OfficeJpaController implements Serializable {

    public OfficeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Office office) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (office.getDoctorList() == null) {
            office.setDoctorList(new ArrayList<Doctor>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Doctor> attachedDoctorList = new ArrayList<Doctor>();
            for (Doctor doctorListDoctorToAttach : office.getDoctorList()) {
                doctorListDoctorToAttach = em.getReference(doctorListDoctorToAttach.getClass(), doctorListDoctorToAttach.getIdUser());
                attachedDoctorList.add(doctorListDoctorToAttach);
            }
            office.setDoctorList(attachedDoctorList);
            em.persist(office);
            for (Doctor doctorListDoctor : office.getDoctorList()) {
                Office oldIdOfficeOfDoctorListDoctor = doctorListDoctor.getIdOffice();
                doctorListDoctor.setIdOffice(office);
                doctorListDoctor = em.merge(doctorListDoctor);
                if (oldIdOfficeOfDoctorListDoctor != null) {
                    oldIdOfficeOfDoctorListDoctor.getDoctorList().remove(doctorListDoctor);
                    oldIdOfficeOfDoctorListDoctor = em.merge(oldIdOfficeOfDoctorListDoctor);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findOffice(office.getNumberOffice()) != null) {
                throw new PreexistingEntityException("Office " + office + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Office office) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Office persistentOffice = em.find(Office.class, office.getNumberOffice());
            List<Doctor> doctorListOld = persistentOffice.getDoctorList();
            List<Doctor> doctorListNew = office.getDoctorList();
            List<Doctor> attachedDoctorListNew = new ArrayList<Doctor>();
            for (Doctor doctorListNewDoctorToAttach : doctorListNew) {
                doctorListNewDoctorToAttach = em.getReference(doctorListNewDoctorToAttach.getClass(), doctorListNewDoctorToAttach.getIdUser());
                attachedDoctorListNew.add(doctorListNewDoctorToAttach);
            }
            doctorListNew = attachedDoctorListNew;
            office.setDoctorList(doctorListNew);
            office = em.merge(office);
            for (Doctor doctorListOldDoctor : doctorListOld) {
                if (!doctorListNew.contains(doctorListOldDoctor)) {
                    doctorListOldDoctor.setIdOffice(null);
                    doctorListOldDoctor = em.merge(doctorListOldDoctor);
                }
            }
            for (Doctor doctorListNewDoctor : doctorListNew) {
                if (!doctorListOld.contains(doctorListNewDoctor)) {
                    Office oldIdOfficeOfDoctorListNewDoctor = doctorListNewDoctor.getIdOffice();
                    doctorListNewDoctor.setIdOffice(office);
                    doctorListNewDoctor = em.merge(doctorListNewDoctor);
                    if (oldIdOfficeOfDoctorListNewDoctor != null && !oldIdOfficeOfDoctorListNewDoctor.equals(office)) {
                        oldIdOfficeOfDoctorListNewDoctor.getDoctorList().remove(doctorListNewDoctor);
                        oldIdOfficeOfDoctorListNewDoctor = em.merge(oldIdOfficeOfDoctorListNewDoctor);
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
                Integer id = office.getNumberOffice();
                if (findOffice(id) == null) {
                    throw new NonexistentEntityException("The office with id " + id + " no longer exists.");
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
            Office office;
            try {
                office = em.getReference(Office.class, id);
                office.getNumberOffice();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The office with id " + id + " no longer exists.", enfe);
            }
            List<Doctor> doctorList = office.getDoctorList();
            for (Doctor doctorListDoctor : doctorList) {
                doctorListDoctor.setIdOffice(null);
                doctorListDoctor = em.merge(doctorListDoctor);
            }
            em.remove(office);
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

    public List<Office> findOfficeEntities() {
        return findOfficeEntities(true, -1, -1);
    }

    public List<Office> findOfficeEntities(int maxResults, int firstResult) {
        return findOfficeEntities(false, maxResults, firstResult);
    }

    private List<Office> findOfficeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Office.class));
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

    public Office findOffice(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Office.class, id);
        } finally {
            em.close();
        }
    }

    public int getOfficeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Office> rt = cq.from(Office.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
