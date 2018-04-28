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
import model.Ticket;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Service;

/**
 *
 * @author Ника
 */
public class ServiceJpaController implements Serializable {

    public ServiceJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Service service) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (service.getTicketList() == null) {
            service.setTicketList(new ArrayList<Ticket>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Ticket> attachedTicketList = new ArrayList<Ticket>();
            for (Ticket ticketListTicketToAttach : service.getTicketList()) {
                ticketListTicketToAttach = em.getReference(ticketListTicketToAttach.getClass(), ticketListTicketToAttach.getIdTicket());
                attachedTicketList.add(ticketListTicketToAttach);
            }
            service.setTicketList(attachedTicketList);
            em.persist(service);
            for (Ticket ticketListTicket : service.getTicketList()) {
                Service oldIdServiceOfTicketListTicket = ticketListTicket.getIdService();
                ticketListTicket.setIdService(service);
                ticketListTicket = em.merge(ticketListTicket);
                if (oldIdServiceOfTicketListTicket != null) {
                    oldIdServiceOfTicketListTicket.getTicketList().remove(ticketListTicket);
                    oldIdServiceOfTicketListTicket = em.merge(oldIdServiceOfTicketListTicket);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findService(service.getIdService()) != null) {
                throw new PreexistingEntityException("Service " + service + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Service service) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Service persistentService = em.find(Service.class, service.getIdService());
            List<Ticket> ticketListOld = persistentService.getTicketList();
            List<Ticket> ticketListNew = service.getTicketList();
            List<Ticket> attachedTicketListNew = new ArrayList<Ticket>();
            for (Ticket ticketListNewTicketToAttach : ticketListNew) {
                ticketListNewTicketToAttach = em.getReference(ticketListNewTicketToAttach.getClass(), ticketListNewTicketToAttach.getIdTicket());
                attachedTicketListNew.add(ticketListNewTicketToAttach);
            }
            ticketListNew = attachedTicketListNew;
            service.setTicketList(ticketListNew);
            service = em.merge(service);
            for (Ticket ticketListOldTicket : ticketListOld) {
                if (!ticketListNew.contains(ticketListOldTicket)) {
                    ticketListOldTicket.setIdService(null);
                    ticketListOldTicket = em.merge(ticketListOldTicket);
                }
            }
            for (Ticket ticketListNewTicket : ticketListNew) {
                if (!ticketListOld.contains(ticketListNewTicket)) {
                    Service oldIdServiceOfTicketListNewTicket = ticketListNewTicket.getIdService();
                    ticketListNewTicket.setIdService(service);
                    ticketListNewTicket = em.merge(ticketListNewTicket);
                    if (oldIdServiceOfTicketListNewTicket != null && !oldIdServiceOfTicketListNewTicket.equals(service)) {
                        oldIdServiceOfTicketListNewTicket.getTicketList().remove(ticketListNewTicket);
                        oldIdServiceOfTicketListNewTicket = em.merge(oldIdServiceOfTicketListNewTicket);
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
                Integer id = service.getIdService();
                if (findService(id) == null) {
                    throw new NonexistentEntityException("The service with id " + id + " no longer exists.");
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
            Service service;
            try {
                service = em.getReference(Service.class, id);
                service.getIdService();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The service with id " + id + " no longer exists.", enfe);
            }
            List<Ticket> ticketList = service.getTicketList();
            for (Ticket ticketListTicket : ticketList) {
                ticketListTicket.setIdService(null);
                ticketListTicket = em.merge(ticketListTicket);
            }
            em.remove(service);
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

    public List<Service> findServiceEntities() {
        return findServiceEntities(true, -1, -1);
    }

    public List<Service> findServiceEntities(int maxResults, int firstResult) {
        return findServiceEntities(false, maxResults, firstResult);
    }

    private List<Service> findServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Service.class));
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

    public Service findService(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Service.class, id);
        } finally {
            em.close();
        }
    }

    public int getServiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Service> rt = cq.from(Service.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
