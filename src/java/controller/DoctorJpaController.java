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
import model.Office;
import model.Post;
import model.Timetable;
import model.User;
import model.Ticket;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Doctor;

/**
 *
 * @author Ника
 */
public class DoctorJpaController implements Serializable {

    public DoctorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doctor doctor) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (doctor.getTicketList() == null) {
            doctor.setTicketList(new ArrayList<Ticket>());
        }
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = doctor.getUser();
        if (userOrphanCheck != null) {
            Doctor oldDoctorOfUser = userOrphanCheck.getDoctor();
            if (oldDoctorOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type Doctor whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Office idOffice = doctor.getIdOffice();
            if (idOffice != null) {
                idOffice = em.getReference(idOffice.getClass(), idOffice.getNumberOffice());
                doctor.setIdOffice(idOffice);
            }
            Post idPost = doctor.getIdPost();
            if (idPost != null) {
                idPost = em.getReference(idPost.getClass(), idPost.getIdPost());
                doctor.setIdPost(idPost);
            }
            Timetable idTimetable = doctor.getIdTimetable();
            if (idTimetable != null) {
                idTimetable = em.getReference(idTimetable.getClass(), idTimetable.getIdTimetable());
                doctor.setIdTimetable(idTimetable);
            }
            User user = doctor.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getIdUser());
                doctor.setUser(user);
            }
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : doctor.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getIdTicket());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            doctor.setTicketList(attachedTicketList);
            em.persist(doctor);
            if (idOffice != null) {
                idOffice.getDoctorList().add(doctor);
                idOffice = em.merge(idOffice);
            }
            if (idPost != null) {
                idPost.getDoctorList().add(doctor);
                idPost = em.merge(idPost);
            }
            if (idTimetable != null) {
                idTimetable.getDoctorList().add(doctor);
                idTimetable = em.merge(idTimetable);
            }
            if (user != null) {
                user.setDoctor(doctor);
                user = em.merge(user);
            }
            for (Ticket ticketListTicket : doctor.getTicketList()) {
                Doctor oldIdDoctorOfTicketListTicket = ticketListTicket.getIdDoctor();
                ticketListTicket.setIdDoctor(doctor);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldIdDoctorOfTicketListTicket != null) {
                    oldIdDoctorOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldIdDoctorOfTicketListTicket = em.merge(oldIdDoctorOfTicketListTicket);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDoctor(doctor.getIdUser()) != null) {
                throw new PreexistingEntityException("Doctor " + doctor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doctor doctor) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Doctor persistentDoctor = em.find(Doctor.class, doctor.getIdUser());
            Office idOfficeOld = persistentDoctor.getIdOffice();
            Office idOfficeNew = doctor.getIdOffice();
            Post idPostOld = persistentDoctor.getIdPost();
            Post idPostNew = doctor.getIdPost();
            Timetable idTimetableOld = persistentDoctor.getIdTimetable();
            Timetable idTimetableNew = doctor.getIdTimetable();
            User userOld = persistentDoctor.getUser();
            User userNew = doctor.getUser();
            List<Ticket> ticketListOld = persistentDoctor.getTicketList();
            List<Ticket> ticketListNew = doctor.getTicketList();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                Doctor oldDoctorOfUser = userNew.getDoctor();
                if (oldDoctorOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type Doctor whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idOfficeNew != null) {
                idOfficeNew = em.getReference(idOfficeNew.getClass(), idOfficeNew.getNumberOffice());
                doctor.setIdOffice(idOfficeNew);
            }
            if (idPostNew != null) {
                idPostNew = em.getReference(idPostNew.getClass(), idPostNew.getIdPost());
                doctor.setIdPost(idPostNew);
            }
            if (idTimetableNew != null) {
                idTimetableNew = em.getReference(idTimetableNew.getClass(), idTimetableNew.getIdTimetable());
                doctor.setIdTimetable(idTimetableNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getIdUser());
                doctor.setUser(userNew);
            }
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getIdTicket());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            doctor.setTicketList(ticketListNew);
            doctor = em.merge(doctor);
            if (idOfficeOld != null && !idOfficeOld.equals(idOfficeNew)) {
                idOfficeOld.getDoctorList().remove(doctor);
                idOfficeOld = em.merge(idOfficeOld);
            }
            if (idOfficeNew != null && !idOfficeNew.equals(idOfficeOld)) {
                idOfficeNew.getDoctorList().add(doctor);
                idOfficeNew = em.merge(idOfficeNew);
            }
            if (idPostOld != null && !idPostOld.equals(idPostNew)) {
                idPostOld.getDoctorList().remove(doctor);
                idPostOld = em.merge(idPostOld);
            }
            if (idPostNew != null && !idPostNew.equals(idPostOld)) {
                idPostNew.getDoctorList().add(doctor);
                idPostNew = em.merge(idPostNew);
            }
            if (idTimetableOld != null && !idTimetableOld.equals(idTimetableNew)) {
                idTimetableOld.getDoctorList().remove(doctor);
                idTimetableOld = em.merge(idTimetableOld);
            }
            if (idTimetableNew != null && !idTimetableNew.equals(idTimetableOld)) {
                idTimetableNew.getDoctorList().add(doctor);
                idTimetableNew = em.merge(idTimetableNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setDoctor(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setDoctor(doctor);
                userNew = em.merge(userNew);
            }
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    ticketListOldTicket.setIdDoctor(null);
                    ticketListOldTicket = em.merge(ticketListOldTicket);
                }
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    Doctor oldIdDoctorOfTicketListNewTicket = ticketListNewTicket.getIdDoctor();
                    ticketListNewTicket.setIdDoctor(doctor);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldIdDoctorOfTicketListNewTicket != null && !oldIdDoctorOfTicketListNewTicket.equals(doctor)) {
                        oldIdDoctorOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldIdDoctorOfTicketListNewTicket = em.merge(oldIdDoctorOfTicketListNewTicket);
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
                Integer id = doctor.getIdUser();
                if (findDoctor(id) == null) {
                    throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.");
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
            Doctor doctor;
            try {
                doctor = em.getReference(Doctor.class, id);
                doctor.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.", enfe);
            }
            Office idOffice = doctor.getIdOffice();
            if (idOffice != null) {
                idOffice.getDoctorList().remove(doctor);
                idOffice = em.merge(idOffice);
            }
            Post idPost = doctor.getIdPost();
            if (idPost != null) {
                idPost.getDoctorList().remove(doctor);
                idPost = em.merge(idPost);
            }
            Timetable idTimetable = doctor.getIdTimetable();
            if (idTimetable != null) {
                idTimetable.getDoctorList().remove(doctor);
                idTimetable = em.merge(idTimetable);
            }
            User user = doctor.getUser();
            if (user != null) {
                user.setDoctor(null);
                user = em.merge(user);
            }
            List<Ticket> ticketList = doctor.getTicketList();
            for (Ticket ticketListTicket : ticketList) {
                ticketListTicket.setIdDoctor(null);
                ticketListTicket = em.merge(ticketListTicket);
            }
            em.remove(doctor);
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

    public List<Doctor> findDoctorEntities() {
        return findDoctorEntities(true, -1, -1);
    }

    public List<Doctor> findDoctorEntities(int maxResults, int firstResult) {
        return findDoctorEntities(false, maxResults, firstResult);
    }

    private List<Doctor> findDoctorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doctor.class));
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

    public Doctor findDoctor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctor.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoctorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doctor> rt = cq.from(Doctor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
