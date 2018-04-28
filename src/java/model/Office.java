/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ника
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Office.findAll", query = "SELECT o FROM Office o")
    , @NamedQuery(name = "Office.findByNumberOffice", query = "SELECT o FROM Office o WHERE o.numberOffice = :numberOffice")
    , @NamedQuery(name = "Office.findByLevel", query = "SELECT o FROM Office o WHERE o.level = :level")})
public class Office implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "number_office")
    private Integer numberOffice;
    private Integer level;
    @OneToMany(mappedBy = "idOffice")
    private List<Doctor> doctorList;

    public Office() {
    }

    public Office(Integer numberOffice) {
        this.numberOffice = numberOffice;
    }

    public Integer getNumberOffice() {
        return numberOffice;
    }

    public void setNumberOffice(Integer numberOffice) {
        this.numberOffice = numberOffice;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @XmlTransient
    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public void setDoctorList(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numberOffice != null ? numberOffice.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Office)) {
            return false;
        }
        Office other = (Office) object;
        if ((this.numberOffice == null && other.numberOffice != null) || (this.numberOffice != null && !this.numberOffice.equals(other.numberOffice))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Office[ numberOffice=" + numberOffice + " ]";
    }
    
}
