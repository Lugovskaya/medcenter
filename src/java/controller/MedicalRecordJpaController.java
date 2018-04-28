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
import model.Address;
import model.User;
import model.WorkPlace;
import model.Disease;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.MedicalRecord;
import model.Vaccinations;

/**
 *
 * @author Ника
 */
public class MedicalRecordJpaController implements Serializable {

    public MedicalRecordJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MedicalRecord medicalRecord) throws RollbackFailureException, Exception {
        if (medicalRecord.getDiseaseList() == null) {
            medicalRecord.setDiseaseList(new ArrayList<Disease>());
        }
        if (medicalRecord.getVaccinationsList() == null) {
            medicalRecord.setVaccinationsList(new ArrayList<Vaccinations>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Address idAddress = medicalRecord.getIdAddress();
            if (idAddress != null) {
                idAddress = em.getReference(idAddress.getClass(), idAddress.getIdAddress());
                medicalRecord.setIdAddress(idAddress);
            }
            User idUser = medicalRecord.getIdUser();
            if (idUser != null) {
                idUser = em.getReference(idUser.getClass(), idUser.getIdUser());
                medicalRecord.setIdUser(idUser);
            }
            WorkPlace idWorkPlace = medicalRecord.getIdWorkPlace();
            if (idWorkPlace != null) {
                idWorkPlace = em.getReference(idWorkPlace.getClass(), idWorkPlace.getIdWorkPlace());
                medicalRecord.setIdWorkPlace(idWorkPlace);
            }
            List<Disease> attachedDiseaseList = new ArrayList<Disease>();
            for (Disease diseaseListDiseaseToAttach : medicalRecord.getDiseaseList()) {
                diseaseListDiseaseToAttach = em.getReference(diseaseListDiseaseToAttach.getClass(), diseaseListDiseaseToAttach.getIdDisease());
                attachedDiseaseList.add(diseaseListDiseaseToAttach);
            }
            medicalRecord.setDiseaseList(attachedDiseaseList);
            List<Vaccinations> attachedVaccinationsList = new ArrayList<Vaccinations>();
            for (Vaccinations vaccinationsListVaccinationsToAttach : medicalRecord.getVaccinationsList()) {
                vaccinationsListVaccinationsToAttach = em.getReference(vaccinationsListVaccinationsToAttach.getClass(), vaccinationsListVaccinationsToAttach.getIdVaccinations());
                attachedVaccinationsList.add(vaccinationsListVaccinationsToAttach);
            }
            medicalRecord.setVaccinationsList(attachedVaccinationsList);
            em.persist(medicalRecord);
            if (idAddress != null) {
                idAddress.getMedicalRecordList().add(medicalRecord);
                idAddress = em.merge(idAddress);
            }
            if (idUser != null) {
                idUser.getMedicalRecordList().add(medicalRecord);
                idUser = em.merge(idUser);
            }
            if (idWorkPlace != null) {
                idWorkPlace.getMedicalRecordList().add(medicalRecord);
                idWorkPlace = em.merge(idWorkPlace);
            }
            for (Disease diseaseListDisease : medicalRecord.getDiseaseList()) {
                MedicalRecord oldIdMedicalRecordOfDiseaseListDisease = diseaseListDisease.getIdMedicalRecord();
                diseaseListDisease.setIdMedicalRecord(medicalRecord);
                diseaseListDisease = em.merge(diseaseListDisease);
                if (oldIdMedicalRecordOfDiseaseListDisease != null) {
                    oldIdMedicalRecordOfDiseaseListDisease.getDiseaseList().remove(diseaseListDisease);
                    oldIdMedicalRecordOfDiseaseListDisease = em.merge(oldIdMedicalRecordOfDiseaseListDisease);
                }
            }
            for (Vaccinations vaccinationsListVaccinations : medicalRecord.getVaccinationsList()) {
                MedicalRecord oldIdMedicalRecordOfVaccinationsListVaccinations = vaccinationsListVaccinations.getIdMedicalRecord();
                vaccinationsListVaccinations.setIdMedicalRecord(medicalRecord);
                vaccinationsListVaccinations = em.merge(vaccinationsListVaccinations);
                if (oldIdMedicalRecordOfVaccinationsListVaccinations != null) {
                    oldIdMedicalRecordOfVaccinationsListVaccinations.getVaccinationsList().remove(vaccinationsListVaccinations);
                    oldIdMedicalRecordOfVaccinationsListVaccinations = em.merge(oldIdMedicalRecordOfVaccinationsListVaccinations);
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

    public void edit(MedicalRecord medicalRecord) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            MedicalRecord persistentMedicalRecord = em.find(MedicalRecord.class, medicalRecord.getIdMedicalRecord());
            Address idAddressOld = persistentMedicalRecord.getIdAddress();
            Address idAddressNew = medicalRecord.getIdAddress();
            User idUserOld = persistentMedicalRecord.getIdUser();
            User idUserNew = medicalRecord.getIdUser();
            WorkPlace idWorkPlaceOld = persistentMedicalRecord.getIdWorkPlace();
            WorkPlace idWorkPlaceNew = medicalRecord.getIdWorkPlace();
            List<Disease> diseaseListOld = persistentMedicalRecord.getDiseaseList();
            List<Disease> diseaseListNew = medicalRecord.getDiseaseList();
            List<Vaccinations> vaccinationsListOld = persistentMedicalRecord.getVaccinationsList();
            List<Vaccinations> vaccinationsListNew = medicalRecord.getVaccinationsList();
            if (idAddressNew != null) {
                idAddressNew = em.getReference(idAddressNew.getClass(), idAddressNew.getIdAddress());
                medicalRecord.setIdAddress(idAddressNew);
            }
            if (idUserNew != null) {
                idUserNew = em.getReference(idUserNew.getClass(), idUserNew.getIdUser());
                medicalRecord.setIdUser(idUserNew);
            }
            if (idWorkPlaceNew != null) {
                idWorkPlaceNew = em.getReference(idWorkPlaceNew.getClass(), idWorkPlaceNew.getIdWorkPlace());
                medicalRecord.setIdWorkPlace(idWorkPlaceNew);
            }
            List<Disease> attachedDiseaseListNew = new ArrayList<Disease>();
            for (Disease diseaseListNewDiseaseToAttach : diseaseListNew) {
                diseaseListNewDiseaseToAttach = em.getReference(diseaseListNewDiseaseToAttach.getClass(), diseaseListNewDiseaseToAttach.getIdDisease());
                attachedDiseaseListNew.add(diseaseListNewDiseaseToAttach);
            }
            diseaseListNew = attachedDiseaseListNew;
            medicalRecord.setDiseaseList(diseaseListNew);
            List<Vaccinations> attachedVaccinationsListNew = new ArrayList<Vaccinations>();
            for (Vaccinations vaccinationsListNewVaccinationsToAttach : vaccinationsListNew) {
                vaccinationsListNewVaccinationsToAttach = em.getReference(vaccinationsListNewVaccinationsToAttach.getClass(), vaccinationsListNewVaccinationsToAttach.getIdVaccinations());
                attachedVaccinationsListNew.add(vaccinationsListNewVaccinationsToAttach);
            }
            vaccinationsListNew = attachedVaccinationsListNew;
            medicalRecord.setVaccinationsList(vaccinationsListNew);
            medicalRecord = em.merge(medicalRecord);
            if (idAddressOld != null && !idAddressOld.equals(idAddressNew)) {
                idAddressOld.getMedicalRecordList().remove(medicalRecord);
                idAddressOld = em.merge(idAddressOld);
            }
            if (idAddressNew != null && !idAddressNew.equals(idAddressOld)) {
                idAddressNew.getMedicalRecordList().add(medicalRecord);
                idAddressNew = em.merge(idAddressNew);
            }
            if (idUserOld != null && !idUserOld.equals(idUserNew)) {
                idUserOld.getMedicalRecordList().remove(medicalRecord);
                idUserOld = em.merge(idUserOld);
            }
            if (idUserNew != null && !idUserNew.equals(idUserOld)) {
                idUserNew.getMedicalRecordList().add(medicalRecord);
                idUserNew = em.merge(idUserNew);
            }
            if (idWorkPlaceOld != null && !idWorkPlaceOld.equals(idWorkPlaceNew)) {
                idWorkPlaceOld.getMedicalRecordList().remove(medicalRecord);
                idWorkPlaceOld = em.merge(idWorkPlaceOld);
            }
            if (idWorkPlaceNew != null && !idWorkPlaceNew.equals(idWorkPlaceOld)) {
                idWorkPlaceNew.getMedicalRecordList().add(medicalRecord);
                idWorkPlaceNew = em.merge(idWorkPlaceNew);
            }
            for (Disease diseaseListOldDisease : diseaseListOld) {
                if (!diseaseListNew.contains(diseaseListOldDisease)) {
                    diseaseListOldDisease.setIdMedicalRecord(null);
                    diseaseListOldDisease = em.merge(diseaseListOldDisease);
                }
            }
            for (Disease diseaseListNewDisease : diseaseListNew) {
                if (!diseaseListOld.contains(diseaseListNewDisease)) {
                    MedicalRecord oldIdMedicalRecordOfDiseaseListNewDisease = diseaseListNewDisease.getIdMedicalRecord();
                    diseaseListNewDisease.setIdMedicalRecord(medicalRecord);
                    diseaseListNewDisease = em.merge(diseaseListNewDisease);
                    if (oldIdMedicalRecordOfDiseaseListNewDisease != null && !oldIdMedicalRecordOfDiseaseListNewDisease.equals(medicalRecord)) {
                        oldIdMedicalRecordOfDiseaseListNewDisease.getDiseaseList().remove(diseaseListNewDisease);
                        oldIdMedicalRecordOfDiseaseListNewDisease = em.merge(oldIdMedicalRecordOfDiseaseListNewDisease);
                    }
                }
            }
            for (Vaccinations vaccinationsListOldVaccinations : vaccinationsListOld) {
                if (!vaccinationsListNew.contains(vaccinationsListOldVaccinations)) {
                    vaccinationsListOldVaccinations.setIdMedicalRecord(null);
                    vaccinationsListOldVaccinations = em.merge(vaccinationsListOldVaccinations);
                }
            }
            for (Vaccinations vaccinationsListNewVaccinations : vaccinationsListNew) {
                if (!vaccinationsListOld.contains(vaccinationsListNewVaccinations)) {
                    MedicalRecord oldIdMedicalRecordOfVaccinationsListNewVaccinations = vaccinationsListNewVaccinations.getIdMedicalRecord();
                    vaccinationsListNewVaccinations.setIdMedicalRecord(medicalRecord);
                    vaccinationsListNewVaccinations = em.merge(vaccinationsListNewVaccinations);
                    if (oldIdMedicalRecordOfVaccinationsListNewVaccinations != null && !oldIdMedicalRecordOfVaccinationsListNewVaccinations.equals(medicalRecord)) {
                        oldIdMedicalRecordOfVaccinationsListNewVaccinations.getVaccinationsList().remove(vaccinationsListNewVaccinations);
                        oldIdMedicalRecordOfVaccinationsListNewVaccinations = em.merge(oldIdMedicalRecordOfVaccinationsListNewVaccinations);
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
                Integer id = medicalRecord.getIdMedicalRecord();
                if (findMedicalRecord(id) == null) {
                    throw new NonexistentEntityException("The medicalRecord with id " + id + " no longer exists.");
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
            MedicalRecord medicalRecord;
            try {
                medicalRecord = em.getReference(MedicalRecord.class, id);
                medicalRecord.getIdMedicalRecord();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The medicalRecord with id " + id + " no longer exists.", enfe);
            }
            Address idAddress = medicalRecord.getIdAddress();
            if (idAddress != null) {
                idAddress.getMedicalRecordList().remove(medicalRecord);
                idAddress = em.merge(idAddress);
            }
            User idUser = medicalRecord.getIdUser();
            if (idUser != null) {
                idUser.getMedicalRecordList().remove(medicalRecord);
                idUser = em.merge(idUser);
            }
            WorkPlace idWorkPlace = medicalRecord.getIdWorkPlace();
            if (idWorkPlace != null) {
                idWorkPlace.getMedicalRecordList().remove(medicalRecord);
                idWorkPlace = em.merge(idWorkPlace);
            }
            List<Disease> diseaseList = medicalRecord.getDiseaseList();
            for (Disease diseaseListDisease : diseaseList) {
                diseaseListDisease.setIdMedicalRecord(null);
                diseaseListDisease = em.merge(diseaseListDisease);
            }
            List<Vaccinations> vaccinationsList = medicalRecord.getVaccinationsList();
            for (Vaccinations vaccinationsListVaccinations : vaccinationsList) {
                vaccinationsListVaccinations.setIdMedicalRecord(null);
                vaccinationsListVaccinations = em.merge(vaccinationsListVaccinations);
            }
            em.remove(medicalRecord);
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

    public List<MedicalRecord> findMedicalRecordEntities() {
        return findMedicalRecordEntities(true, -1, -1);
    }

    public List<MedicalRecord> findMedicalRecordEntities(int maxResults, int firstResult) {
        return findMedicalRecordEntities(false, maxResults, firstResult);
    }

    private List<MedicalRecord> findMedicalRecordEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MedicalRecord.class));
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

    public MedicalRecord findMedicalRecord(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MedicalRecord.class, id);
        } finally {
            em.close();
        }
    }

    public int getMedicalRecordCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MedicalRecord> rt = cq.from(MedicalRecord.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
