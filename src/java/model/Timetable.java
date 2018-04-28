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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Timetable.findAll", query = "SELECT t FROM Timetable t")
    , @NamedQuery(name = "Timetable.findByIdTimetable", query = "SELECT t FROM Timetable t WHERE t.idTimetable = :idTimetable")
    , @NamedQuery(name = "Timetable.findByMonstart", query = "SELECT t FROM Timetable t WHERE t.monstart = :monstart")
    , @NamedQuery(name = "Timetable.findByMonfinish", query = "SELECT t FROM Timetable t WHERE t.monfinish = :monfinish")
    , @NamedQuery(name = "Timetable.findByTuestart", query = "SELECT t FROM Timetable t WHERE t.tuestart = :tuestart")
    , @NamedQuery(name = "Timetable.findByTuefinish", query = "SELECT t FROM Timetable t WHERE t.tuefinish = :tuefinish")
    , @NamedQuery(name = "Timetable.findByWedstart", query = "SELECT t FROM Timetable t WHERE t.wedstart = :wedstart")
    , @NamedQuery(name = "Timetable.findByWedfinish", query = "SELECT t FROM Timetable t WHERE t.wedfinish = :wedfinish")
    , @NamedQuery(name = "Timetable.findByThustart", query = "SELECT t FROM Timetable t WHERE t.thustart = :thustart")
    , @NamedQuery(name = "Timetable.findByThufinish", query = "SELECT t FROM Timetable t WHERE t.thufinish = :thufinish")
    , @NamedQuery(name = "Timetable.findByFri1start", query = "SELECT t FROM Timetable t WHERE t.fri1start = :fri1start")
    , @NamedQuery(name = "Timetable.findByFri2finish", query = "SELECT t FROM Timetable t WHERE t.fri2finish = :fri2finish")
    , @NamedQuery(name = "Timetable.findByNote", query = "SELECT t FROM Timetable t WHERE t.note = :note")})
public class Timetable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_timetable")
    private Integer idTimetable;
    @Column(name = "Mon_start")
    @Temporal(TemporalType.TIME)
    private Date monstart;
    @Column(name = "Mon_finish")
    @Temporal(TemporalType.TIME)
    private Date monfinish;
    @Column(name = "Tue_start")
    @Temporal(TemporalType.TIME)
    private Date tuestart;
    @Column(name = "Tue_finish")
    @Temporal(TemporalType.TIME)
    private Date tuefinish;
    @Column(name = "Wed_start")
    @Temporal(TemporalType.TIME)
    private Date wedstart;
    @Column(name = "Wed_finish")
    @Temporal(TemporalType.TIME)
    private Date wedfinish;
    @Column(name = "Thu_start")
    @Temporal(TemporalType.TIME)
    private Date thustart;
    @Column(name = "Thu_finish")
    @Temporal(TemporalType.TIME)
    private Date thufinish;
    @Column(name = "Fri1_start")
    @Temporal(TemporalType.TIME)
    private Date fri1start;
    @Column(name = "Fri2_finish")
    @Temporal(TemporalType.TIME)
    private Date fri2finish;
    @Size(max = 50)
    private String note;
    @OneToMany(mappedBy = "idTimetable")
    private List<Doctor> doctorList;

    public Timetable() {
    }

    public Timetable(Integer idTimetable) {
        this.idTimetable = idTimetable;
    }

    public Integer getIdTimetable() {
        return idTimetable;
    }

    public void setIdTimetable(Integer idTimetable) {
        this.idTimetable = idTimetable;
    }

    public Date getMonstart() {
        return monstart;
    }

    public void setMonstart(Date monstart) {
        this.monstart = monstart;
    }

    public Date getMonfinish() {
        return monfinish;
    }

    public void setMonfinish(Date monfinish) {
        this.monfinish = monfinish;
    }

    public Date getTuestart() {
        return tuestart;
    }

    public void setTuestart(Date tuestart) {
        this.tuestart = tuestart;
    }

    public Date getTuefinish() {
        return tuefinish;
    }

    public void setTuefinish(Date tuefinish) {
        this.tuefinish = tuefinish;
    }

    public Date getWedstart() {
        return wedstart;
    }

    public void setWedstart(Date wedstart) {
        this.wedstart = wedstart;
    }

    public Date getWedfinish() {
        return wedfinish;
    }

    public void setWedfinish(Date wedfinish) {
        this.wedfinish = wedfinish;
    }

    public Date getThustart() {
        return thustart;
    }

    public void setThustart(Date thustart) {
        this.thustart = thustart;
    }

    public Date getThufinish() {
        return thufinish;
    }

    public void setThufinish(Date thufinish) {
        this.thufinish = thufinish;
    }

    public Date getFri1start() {
        return fri1start;
    }

    public void setFri1start(Date fri1start) {
        this.fri1start = fri1start;
    }

    public Date getFri2finish() {
        return fri2finish;
    }

    public void setFri2finish(Date fri2finish) {
        this.fri2finish = fri2finish;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        hash += (idTimetable != null ? idTimetable.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Timetable)) {
            return false;
        }
        Timetable other = (Timetable) object;
        if ((this.idTimetable == null && other.idTimetable != null) || (this.idTimetable != null && !this.idTimetable.equals(other.idTimetable))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Timetable[ idTimetable=" + idTimetable + " ]";
    }
    
}
