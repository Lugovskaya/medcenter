/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.User;
import model.Ticket;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Patient;

/**
 *
 * @author Ника
 */
public class PatientJpaController implements Serializable {

    public PatientJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Patient patient) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (patient.getTicketList() == null) {
            patient.setTicketList(new ArrayList<Ticket>());
        }
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = patient.getUser();
        if (userOrphanCheck != null) {
            Patient oldPatientOfUser = userOrphanCheck.getPatient();
            if (oldPatientOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type Patient whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = patient.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getIdUser());
                patient.setUser(user);
            }
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : patient.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getIdTicket());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            patient.setTicketList(attachedTicketList);
            em.persist(patient);
            if (user != null) {
                user.setPatient(patient);
                user = em.merge(user);
            }
            for (Ticket ticketListTicket : patient.getTicketList()) {
                Patient oldIdPatientOfTicketListTicket = ticketListTicket.getIdPatient();
                ticketListTicket.setIdPatient(patient);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldIdPatientOfTicketListTicket != null) {
                    oldIdPatientOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldIdPatientOfTicketListTicket = em.merge(oldIdPatientOfTicketListTicket);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPatient(patient.getIdUser()) != null) {
                throw new PreexistingEntityException("Patient " + patient + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Patient patient) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Patient persistentPatient = em.find(Patient.class, patient.getIdUser());
            User userOld = persistentPatient.getUser();
            User userNew = patient.getUser();
            List<Ticket> ticketListOld = persistentPatient.getTicketList();
            List<Ticket> ticketListNew = patient.getTicketList();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                Patient oldPatientOfUser = userNew.getPatient();
                if (oldPatientOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type Patient whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getIdUser());
                patient.setUser(userNew);
            }
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getIdTicket());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            patient.setTicketList(ticketListNew);
            patient = em.merge(patient);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setPatient(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setPatient(patient);
                userNew = em.merge(userNew);
            }
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    ticketListOldTicket.setIdPatient(null);
                    ticketListOldTicket = em.merge(ticketListOldTicket);
                }
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    Patient oldIdPatientOfTicketListNewTicket = ticketListNewTicket.getIdPatient();
                    ticketListNewTicket.setIdPatient(patient);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldIdPatientOfTicketListNewTicket != null && !oldIdPatientOfTicketListNewTicket.equals(patient)) {
                        oldIdPatientOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldIdPatientOfTicketListNewTicket = em.merge(oldIdPatientOfTicketListNewTicket);
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
                Integer id = patient.getIdUser();
                if (findPatient(id) == null) {
                    throw new NonexistentEntityException("The patient with id " + id + " no longer exists.");
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
            Patient patient;
            try {
                patient = em.getReference(Patient.class, id);
                patient.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The patient with id " + id + " no longer exists.", enfe);
            }
            User user = patient.getUser();
            if (user != null) {
                user.setPatient(null);
                user = em.merge(user);
            }
            List<Ticket> ticketList = patient.getTicketList();
            for (Ticket ticketListTicket : ticketList) {
                ticketListTicket.setIdPatient(null);
                ticketListTicket = em.merge(ticketListTicket);
            }
            em.remove(patient);
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

    public List<Patient> findPatientEntities() {
        return findPatientEntities(true, -1, -1);
    }

    public List<Patient> findPatientEntities(int maxResults, int firstResult) {
        return findPatientEntities(false, maxResults, firstResult);
    }

    private List<Patient> findPatientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Patient.class));
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

    public Patient findPatient(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Patient.class, id);
        } finally {
            em.close();
        }
    }

    public int getPatientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Patient> rt = cq.from(Patient.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
