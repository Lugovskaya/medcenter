/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ника
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Disease.findAll", query = "SELECT d FROM Disease d")
    , @NamedQuery(name = "Disease.findByIdDisease", query = "SELECT d FROM Disease d WHERE d.idDisease = :idDisease")
    , @NamedQuery(name = "Disease.findByDate", query = "SELECT d FROM Disease d WHERE d.date = :date")
    , @NamedQuery(name = "Disease.findByDiagnosis", query = "SELECT d FROM Disease d WHERE d.diagnosis = :diagnosis")
    , @NamedQuery(name = "Disease.findByStatus", query = "SELECT d FROM Disease d WHERE d.status = :status")
    , @NamedQuery(name = "Disease.findByNote", query = "SELECT d FROM Disease d WHERE d.note = :note")})
public class Disease implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_disease")
    private Integer idDisease;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Size(max = 50)
    private String diagnosis;
    @Size(max = 50)
    private String status;
    @Size(max = 500)
    private String note;
    @JoinColumn(name = "id_medical_record", referencedColumnName = "id_medical_record")
    @ManyToOne
    private MedicalRecord idMedicalRecord;

    public Disease() {
    }

    public Disease(Integer idDisease) {
        this.idDisease = idDisease;
    }

    public Integer getIdDisease() {
        return idDisease;
    }

    public void setIdDisease(Integer idDisease) {
        this.idDisease = idDisease;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MedicalRecord getIdMedicalRecord() {
        return idMedicalRecord;
    }

    public void setIdMedicalRecord(MedicalRecord idMedicalRecord) {
        this.idMedicalRecord = idMedicalRecord;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDisease != null ? idDisease.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Disease)) {
            return false;
        }
        Disease other = (Disease) object;
        if ((this.idDisease == null && other.idDisease != null) || (this.idDisease != null && !this.idDisease.equals(other.idDisease))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Disease[ idDisease=" + idDisease + " ]";
    }
    
}
