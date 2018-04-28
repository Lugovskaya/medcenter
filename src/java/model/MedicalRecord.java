/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ника
 */
@Entity
@Table(name = "medical_record")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MedicalRecord.findAll", query = "SELECT m FROM MedicalRecord m")
    , @NamedQuery(name = "MedicalRecord.findByIdMedicalRecord", query = "SELECT m FROM MedicalRecord m WHERE m.idMedicalRecord = :idMedicalRecord")
    , @NamedQuery(name = "MedicalRecord.findByNationality", query = "SELECT m FROM MedicalRecord m WHERE m.nationality = :nationality")
    , @NamedQuery(name = "MedicalRecord.findBySex", query = "SELECT m FROM MedicalRecord m WHERE m.sex = :sex")
    , @NamedQuery(name = "MedicalRecord.findByBday", query = "SELECT m FROM MedicalRecord m WHERE m.bday = :bday")
    , @NamedQuery(name = "MedicalRecord.findByIdBlood", query = "SELECT m FROM MedicalRecord m WHERE m.idBlood = :idBlood")})
public class MedicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_medical_record")
    private Integer idMedicalRecord;
    @Size(max = 50)
    private String nationality;
    @Size(max = 50)
    private String sex;
    @Temporal(TemporalType.TIMESTAMP)
    private Date bday;
    @Column(name = "id_blood")
    private Integer idBlood;
    @OneToMany(mappedBy = "idMedicalRecord")
    private List<Disease> diseaseList;
    @JoinColumn(name = "id_address", referencedColumnName = "id_address")
    @ManyToOne
    private Address idAddress;
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @ManyToOne
    private User idUser;
    @JoinColumn(name = "id_work_place", referencedColumnName = "id_work_place")
    @ManyToOne
    private WorkPlace idWorkPlace;
    @OneToMany(mappedBy = "idMedicalRecord")
    private List<Vaccinations> vaccinationsList;

    public MedicalRecord() {
    }

    public MedicalRecord(Integer idMedicalRecord) {
        this.idMedicalRecord = idMedicalRecord;
    }

    public Integer getIdMedicalRecord() {
        return idMedicalRecord;
    }

    public void setIdMedicalRecord(Integer idMedicalRecord) {
        this.idMedicalRecord = idMedicalRecord;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBday() {
        return bday;
    }

    public void setBday(Date bday) {
        this.bday = bday;
    }

    public Integer getIdBlood() {
        return idBlood;
    }

    public void setIdBlood(Integer idBlood) {
        this.idBlood = idBlood;
    }

    @XmlTransient
    public List<Disease> getDiseaseList() {
        return diseaseList;
    }

    public void setDiseaseList(List<Disease> diseaseList) {
        this.diseaseList = diseaseList;
    }

    public Address getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Address idAddress) {
        this.idAddress = idAddress;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    public WorkPlace getIdWorkPlace() {
        return idWorkPlace;
    }

    public void setIdWorkPlace(WorkPlace idWorkPlace) {
        this.idWorkPlace = idWorkPlace;
    }

    @XmlTransient
    public List<Vaccinations> getVaccinationsList() {
        return vaccinationsList;
    }

    public void setVaccinationsList(List<Vaccinations> vaccinationsList) {
        this.vaccinationsList = vaccinationsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMedicalRecord != null ? idMedicalRecord.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MedicalRecord)) {
            return false;
        }
        MedicalRecord other = (MedicalRecord) object;
        if ((this.idMedicalRecord == null && other.idMedicalRecord != null) || (this.idMedicalRecord != null && !this.idMedicalRecord.equals(other.idMedicalRecord))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.MedicalRecord[ idMedicalRecord=" + idMedicalRecord + " ]";
    }
    
}
