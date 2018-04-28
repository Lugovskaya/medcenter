/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Personal;
import model.Doctor;
import model.Patient;
import model.MedicalRecord;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.User;

/**
 *
 * @author Ника
 */
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws RollbackFailureException, Exception {
        if (user.getMedicalRecordList() == null) {
            user.setMedicalRecordList(new ArrayList<MedicalRecord>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Personal personal = user.getPersonal();
            if (personal != null) {
                personal = em.getReference(personal.getClass(), personal.getIdUser());
                user.setPersonal(personal);
            }
            Doctor doctor = user.getDoctor();
            if (doctor != null) {
                doctor = em.getReference(doctor.getClass(), doctor.getIdUser());
                user.setDoctor(doctor);
            }
            Patient patient = user.getPatient();
            if (patient != null) {
                patient = em.getReference(patient.getClass(), patient.getIdUser());
                user.setPatient(patient);
            }
            List<MedicalRecord> attachedMedicalRecordList = new ArrayList<MedicalRecord>();
            for (MedicalRecord medicalRecordListMedicalRecordToAttach : user.getMedicalRecordList()) {
                medicalRecordListMedicalRecordToAttach = em.getReference(medicalRecordListMedicalRecordToAttach.getClass(), medicalRecordListMedicalRecordToAttach.getIdMedicalRecord());
                attachedMedicalRecordList.add(medicalRecordListMedicalRecordToAttach);
            }
            user.setMedicalRecordList(attachedMedicalRecordList);
            em.persist(user);
            if (personal != null) {
                User oldUserOfPersonal = personal.getUser();
                if (oldUserOfPersonal != null) {
                    oldUserOfPersonal.setPersonal(null);
                    oldUserOfPersonal = em.merge(oldUserOfPersonal);
                }
                personal.setUser(user);
                personal = em.merge(personal);
            }
            if (doctor != null) {
                User oldUserOfDoctor = doctor.getUser();
                if (oldUserOfDoctor != null) {
                    oldUserOfDoctor.setDoctor(null);
                    oldUserOfDoctor = em.merge(oldUserOfDoctor);
                }
                doctor.setUser(user);
                doctor = em.merge(doctor);
            }
            if (patient != null) {
                User oldUserOfPatient = patient.getUser();
                if (oldUserOfPatient != null) {
                    oldUserOfPatient.setPatient(null);
                    oldUserOfPatient = em.merge(oldUserOfPatient);
                }
                patient.setUser(user);
                patient = em.merge(patient);
            }
            for (MedicalRecord medicalRecordListMedicalRecord : user.getMedicalRecordList()) {
                User oldIdUserOfMedicalRecordListMedicalRecord = medicalRecordListMedicalRecord.getIdUser();
                medicalRecordListMedicalRecord.setIdUser(user);
                medicalRecordListMedicalRecord = em.merge(medicalRecordListMedicalRecord);
                if (oldIdUserOfMedicalRecordListMedicalRecord != null) {
                    oldIdUserOfMedicalRecordListMedicalRecord.getMedicalRecordList().remove(medicalRecordListMedicalRecord);
                    oldIdUserOfMedicalRecordListMedicalRecord = em.merge(oldIdUserOfMedicalRecordListMedicalRecord);
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

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getIdUser());
            Personal personalOld = persistentUser.getPersonal();
            Personal personalNew = user.getPersonal();
            Doctor doctorOld = persistentUser.getDoctor();
            Doctor doctorNew = user.getDoctor();
            Patient patientOld = persistentUser.getPatient();
            Patient patientNew = user.getPatient();
            List<MedicalRecord> medicalRecordListOld = persistentUser.getMedicalRecordList();
            List<MedicalRecord> medicalRecordListNew = user.getMedicalRecordList();
            List<String> illegalOrphanMessages = null;
            if (personalOld != null && !personalOld.equals(personalNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Personal " + personalOld + " since its user field is not nullable.");
            }
            if (doctorOld != null && !doctorOld.equals(doctorNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Doctor " + doctorOld + " since its user field is not nullable.");
            }
            if (patientOld != null && !patientOld.equals(patientNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Patient " + patientOld + " since its user field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personalNew != null) {
                personalNew = em.getReference(personalNew.getClass(), personalNew.getIdUser());
                user.setPersonal(personalNew);
            }
            if (doctorNew != null) {
                doctorNew = em.getReference(doctorNew.getClass(), doctorNew.getIdUser());
                user.setDoctor(doctorNew);
            }
            if (patientNew != null) {
                patientNew = em.getReference(patientNew.getClass(), patientNew.getIdUser());
                user.setPatient(patientNew);
            }
            List<MedicalRecord> attachedMedicalRecordListNew = new ArrayList<MedicalRecord>();
            for (MedicalRecord medicalRecordListNewMedicalRecordToAttach : medicalRecordListNew) {
                medicalRecordListNewMedicalRecordToAttach = em.getReference(medicalRecordListNewMedicalRecordToAttach.getClass(), medicalRecordListNewMedicalRecordToAttach.getIdMedicalRecord());
                attachedMedicalRecordListNew.add(medicalRecordListNewMedicalRecordToAttach);
            }
            medicalRecordListNew = attachedMedicalRecordListNew;
            user.setMedicalRecordList(medicalRecordListNew);
            user = em.merge(user);
            if (personalNew != null && !personalNew.equals(personalOld)) {
                User oldUserOfPersonal = personalNew.getUser();
                if (oldUserOfPersonal != null) {
                    oldUserOfPersonal.setPersonal(null);
                    oldUserOfPersonal = em.merge(oldUserOfPersonal);
                }
                personalNew.setUser(user);
                personalNew = em.merge(personalNew);
            }
            if (doctorNew != null && !doctorNew.equals(doctorOld)) {
                User oldUserOfDoctor = doctorNew.getUser();
                if (oldUserOfDoctor != null) {
                    oldUserOfDoctor.setDoctor(null);
                    oldUserOfDoctor = em.merge(oldUserOfDoctor);
                }
                doctorNew.setUser(user);
                doctorNew = em.merge(doctorNew);
            }
            if (patientNew != null && !patientNew.equals(patientOld)) {
                User oldUserOfPatient = patientNew.getUser();
                if (oldUserOfPatient != null) {
                    oldUserOfPatient.setPatient(null);
                    oldUserOfPatient = em.merge(oldUserOfPatient);
                }
                patientNew.setUser(user);
                patientNew = em.merge(patientNew);
            }
            for (MedicalRecord medicalRecordListOldMedicalRecord : medicalRecordListOld) {
                if (!medicalRecordListNew.contains(medicalRecordListOldMedicalRecord)) {
                    medicalRecordListOldMedicalRecord.setIdUser(null);
                    medicalRecordListOldMedicalRecord = em.merge(medicalRecordListOldMedicalRecord);
                }
            }
            for (MedicalRecord medicalRecordListNewMedicalRecord : medicalRecordListNew) {
                if (!medicalRecordListOld.contains(medicalRecordListNewMedicalRecord)) {
                    User oldIdUserOfMedicalRecordListNewMedicalRecord = medicalRecordListNewMedicalRecord.getIdUser();
                    medicalRecordListNewMedicalRecord.setIdUser(user);
                    medicalRecordListNewMedicalRecord = em.merge(medicalRecordListNewMedicalRecord);
                    if (oldIdUserOfMedicalRecordListNewMedicalRecord != null && !oldIdUserOfMedicalRecordListNewMedicalRecord.equals(user)) {
                        oldIdUserOfMedicalRecordListNewMedicalRecord.getMedicalRecordList().remove(medicalRecordListNewMedicalRecord);
                        oldIdUserOfMedicalRecordListNewMedicalRecord = em.merge(oldIdUserOfMedicalRecordListNewMedicalRecord);
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
                Integer id = user.getIdUser();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Personal personalOrphanCheck = user.getPersonal();
            if (personalOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Personal " + personalOrphanCheck + " in its personal field has a non-nullable user field.");
            }
            Doctor doctorOrphanCheck = user.getDoctor();
            if (doctorOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Doctor " + doctorOrphanCheck + " in its doctor field has a non-nullable user field.");
            }
            Patient patientOrphanCheck = user.getPatient();
            if (patientOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Patient " + patientOrphanCheck + " in its patient field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<MedicalRecord> medicalRecordList = user.getMedicalRecordList();
            for (MedicalRecord medicalRecordListMedicalRecord : medicalRecordList) {
                medicalRecordListMedicalRecord.setIdUser(null);
                medicalRecordListMedicalRecord = em.merge(medicalRecordListMedicalRecord);
            }
            em.remove(user);
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

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
