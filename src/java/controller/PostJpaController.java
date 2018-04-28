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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Doctor;
import model.Post;

/**
 *
 * @author Ника
 */
public class PostJpaController implements Serializable {

    public PostJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Post post) throws RollbackFailureException, Exception {
        if (post.getPersonalList() == null) {
            post.setPersonalList(new ArrayList<Personal>());
        }
        if (post.getDoctorList() == null) {
            post.setDoctorList(new ArrayList<Doctor>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Personal> attachedPersonalList = new ArrayList<Personal>();
            for (Personal personalListPersonalToAttach : post.getPersonalList()) {
                personalListPersonalToAttach = em.getReference(personalListPersonalToAttach.getClass(), personalListPersonalToAttach.getIdUser());
                attachedPersonalList.add(personalListPersonalToAttach);
            }
            post.setPersonalList(attachedPersonalList);
            List<Doctor> attachedDoctorList = new ArrayList<Doctor>();
            for (Doctor doctorListDoctorToAttach : post.getDoctorList()) {
                doctorListDoctorToAttach = em.getReference(doctorListDoctorToAttach.getClass(), doctorListDoctorToAttach.getIdUser());
                attachedDoctorList.add(doctorListDoctorToAttach);
            }
            post.setDoctorList(attachedDoctorList);
            em.persist(post);
            for (Personal personalListPersonal : post.getPersonalList()) {
                Post oldIdPostOfPersonalListPersonal = personalListPersonal.getIdPost();
                personalListPersonal.setIdPost(post);
                personalListPersonal = em.merge(personalListPersonal);
                if (oldIdPostOfPersonalListPersonal != null) {
                    oldIdPostOfPersonalListPersonal.getPersonalList().remove(personalListPersonal);
                    oldIdPostOfPersonalListPersonal = em.merge(oldIdPostOfPersonalListPersonal);
                }
            }
            for (Doctor doctorListDoctor : post.getDoctorList()) {
                Post oldIdPostOfDoctorListDoctor = doctorListDoctor.getIdPost();
                doctorListDoctor.setIdPost(post);
                doctorListDoctor = em.merge(doctorListDoctor);
                if (oldIdPostOfDoctorListDoctor != null) {
                    oldIdPostOfDoctorListDoctor.getDoctorList().remove(doctorListDoctor);
                    oldIdPostOfDoctorListDoctor = em.merge(oldIdPostOfDoctorListDoctor);
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

    public void edit(Post post) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Post persistentPost = em.find(Post.class, post.getIdPost());
            List<Personal> personalListOld = persistentPost.getPersonalList();
            List<Personal> personalListNew = post.getPersonalList();
            List<Doctor> doctorListOld = persistentPost.getDoctorList();
            List<Doctor> doctorListNew = post.getDoctorList();
            List<String> illegalOrphanMessages = null;
            for (Personal personalListOldPersonal : personalListOld) {
                if (!personalListNew.contains(personalListOldPersonal)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Personal " + personalListOldPersonal + " since its idPost field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Personal> attachedPersonalListNew = new ArrayList<Personal>();
            for (Personal personalListNewPersonalToAttach : personalListNew) {
                personalListNewPersonalToAttach = em.getReference(personalListNewPersonalToAttach.getClass(), personalListNewPersonalToAttach.getIdUser());
                attachedPersonalListNew.add(personalListNewPersonalToAttach);
            }
            personalListNew = attachedPersonalListNew;
            post.setPersonalList(personalListNew);
            List<Doctor> attachedDoctorListNew = new ArrayList<Doctor>();
            for (Doctor doctorListNewDoctorToAttach : doctorListNew) {
                doctorListNewDoctorToAttach = em.getReference(doctorListNewDoctorToAttach.getClass(), doctorListNewDoctorToAttach.getIdUser());
                attachedDoctorListNew.add(doctorListNewDoctorToAttach);
            }
            doctorListNew = attachedDoctorListNew;
            post.setDoctorList(doctorListNew);
            post = em.merge(post);
            for (Personal personalListNewPersonal : personalListNew) {
                if (!personalListOld.contains(personalListNewPersonal)) {
                    Post oldIdPostOfPersonalListNewPersonal = personalListNewPersonal.getIdPost();
                    personalListNewPersonal.setIdPost(post);
                    personalListNewPersonal = em.merge(personalListNewPersonal);
                    if (oldIdPostOfPersonalListNewPersonal != null && !oldIdPostOfPersonalListNewPersonal.equals(post)) {
                        oldIdPostOfPersonalListNewPersonal.getPersonalList().remove(personalListNewPersonal);
                        oldIdPostOfPersonalListNewPersonal = em.merge(oldIdPostOfPersonalListNewPersonal);
                    }
                }
            }
            for (Doctor doctorListOldDoctor : doctorListOld) {
                if (!doctorListNew.contains(doctorListOldDoctor)) {
                    doctorListOldDoctor.setIdPost(null);
                    doctorListOldDoctor = em.merge(doctorListOldDoctor);
                }
            }
            for (Doctor doctorListNewDoctor : doctorListNew) {
                if (!doctorListOld.contains(doctorListNewDoctor)) {
                    Post oldIdPostOfDoctorListNewDoctor = doctorListNewDoctor.getIdPost();
                    doctorListNewDoctor.setIdPost(post);
                    doctorListNewDoctor = em.merge(doctorListNewDoctor);
                    if (oldIdPostOfDoctorListNewDoctor != null && !oldIdPostOfDoctorListNewDoctor.equals(post)) {
                        oldIdPostOfDoctorListNewDoctor.getDoctorList().remove(doctorListNewDoctor);
                        oldIdPostOfDoctorListNewDoctor = em.merge(oldIdPostOfDoctorListNewDoctor);
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
                Integer id = post.getIdPost();
                if (findPost(id) == null) {
                    throw new NonexistentEntityException("The post with id " + id + " no longer exists.");
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
            Post post;
            try {
                post = em.getReference(Post.class, id);
                post.getIdPost();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The post with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Personal> personalListOrphanCheck = post.getPersonalList();
            for (Personal personalListOrphanCheckPersonal : personalListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Post (" + post + ") cannot be destroyed since the Personal " + personalListOrphanCheckPersonal + " in its personalList field has a non-nullable idPost field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Doctor> doctorList = post.getDoctorList();
            for (Doctor doctorListDoctor : doctorList) {
                doctorListDoctor.setIdPost(null);
                doctorListDoctor = em.merge(doctorListDoctor);
            }
            em.remove(post);
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

    public List<Post> findPostEntities() {
        return findPostEntities(true, -1, -1);
    }

    public List<Post> findPostEntities(int maxResults, int firstResult) {
        return findPostEntities(false, maxResults, firstResult);
    }

    private List<Post> findPostEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Post.class));
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

    public Post findPost(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Post.class, id);
        } finally {
            em.close();
        }
    }

    public int getPostCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Post> rt = cq.from(Post.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
