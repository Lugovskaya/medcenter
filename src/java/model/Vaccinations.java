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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ника
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vaccinations.findAll", query = "SELECT v FROM Vaccinations v")
    , @NamedQuery(name = "Vaccinations.findByIdVaccinations", query = "SELECT v FROM Vaccinations v WHERE v.idVaccinations = :idVaccinations")
    , @NamedQuery(name = "Vaccinations.findByNameVaccinations", query = "SELECT v FROM Vaccinations v WHERE v.nameVaccinations = :nameVaccinations")
    , @NamedQuery(name = "Vaccinations.findByDate", query = "SELECT v FROM Vaccinations v WHERE v.date = :date")})
public class Vaccinations implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_vaccinations")
    private Integer idVaccinations;
    @Size(max = 50)
    @Column(name = "name_vaccinations")
    private String nameVaccinations;
    @Temporal(TemporalType.DATE)
    private Date date;
    @JoinColumn(name = "id_medical_record", referencedColumnName = "id_medical_record")
    @ManyToOne
    private MedicalRecord idMedicalRecord;

    public Vaccinations() {
    }

    public Vaccinations(Integer idVaccinations) {
        this.idVaccinations = idVaccinations;
    }

    public Integer getIdVaccinations() {
        return idVaccinations;
    }

    public void setIdVaccinations(Integer idVaccinations) {
        this.idVaccinations = idVaccinations;
    }

    public String getNameVaccinations() {
        return nameVaccinations;
    }

    public void setNameVaccinations(String nameVaccinations) {
        this.nameVaccinations = nameVaccinations;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        hash += (idVaccinations != null ? idVaccinations.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vaccinations)) {
            return false;
        }
        Vaccinations other = (Vaccinations) object;
        if ((this.idVaccinations == null && other.idVaccinations != null) || (this.idVaccinations != null && !this.idVaccinations.equals(other.idVaccinations))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Vaccinations[ idVaccinations=" + idVaccinations + " ]";
    }
    
}
