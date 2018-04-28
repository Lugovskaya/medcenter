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
import model.Post;
import model.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Personal;

/**
 *
 * @author Ника
 */
public class PersonalJpaController implements Serializable {

    public PersonalJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Personal personal) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = personal.getUser();
        if (userOrphanCheck != null) {
            Personal oldPersonalOfUser = userOrphanCheck.getPersonal();
            if (oldPersonalOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type Personal whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Post idPost = personal.getIdPost();
            if (idPost != null) {
                idPost = em.getReference(idPost.getClass(), idPost.getIdPost());
                personal.setIdPost(idPost);
            }
            User user = personal.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getIdUser());
                personal.setUser(user);
            }
            em.persist(personal);
            if (idPost != null) {
                idPost.getPersonalList().add(personal);
                idPost = em.merge(idPost);
            }
            if (user != null) {
                user.setPersonal(personal);
                user = em.merge(user);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPersonal(personal.getIdUser()) != null) {
                throw new PreexistingEntityException("Personal " + personal + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Personal personal) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Personal persistentPersonal = em.find(Personal.class, personal.getIdUser());
            Post idPostOld = persistentPersonal.getIdPost();
            Post idPostNew = personal.getIdPost();
            User userOld = persistentPersonal.getUser();
            User userNew = personal.getUser();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                Personal oldPersonalOfUser = userNew.getPersonal();
                if (oldPersonalOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type Personal whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idPostNew != null) {
                idPostNew = em.getReference(idPostNew.getClass(), idPostNew.getIdPost());
                personal.setIdPost(idPostNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getIdUser());
                personal.setUser(userNew);
            }
            personal = em.merge(personal);
            if (idPostOld != null && !idPostOld.equals(idPostNew)) {
                idPostOld.getPersonalList().remove(personal);
                idPostOld = em.merge(idPostOld);
            }
            if (idPostNew != null && !idPostNew.equals(idPostOld)) {
                idPostNew.getPersonalList().add(personal);
                idPostNew = em.merge(idPostNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setPersonal(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setPersonal(personal);
                userNew = em.merge(userNew);
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
                Integer id = personal.getIdUser();
                if (findPersonal(id) == null) {
                    throw new NonexistentEntityException("The personal with id " + id + " no longer exists.");
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
            Personal personal;
            try {
                personal = em.getReference(Personal.class, id);
                personal.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The personal with id " + id + " no longer exists.", enfe);
            }
            Post idPost = personal.getIdPost();
            if (idPost != null) {
                idPost.getPersonalList().remove(personal);
                idPost = em.merge(idPost);
            }
            User user = personal.getUser();
            if (user != null) {
                user.setPersonal(null);
                user = em.merge(user);
            }
            em.remove(personal);
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

    public List<Personal> findPersonalEntities() {
        return findPersonalEntities(true, -1, -1);
    }

    public List<Personal> findPersonalEntities(int maxResults, int firstResult) {
        return findPersonalEntities(false, maxResults, firstResult);
    }

    private List<Personal> findPersonalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Personal.class));
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

    public Personal findPersonal(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Personal.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Personal> rt = cq.from(Personal.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
