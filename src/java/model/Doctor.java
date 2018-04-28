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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
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
    @NamedQuery(name = "Doctor.findAll", query = "SELECT d FROM Doctor d")
    , @NamedQuery(name = "Doctor.findByIdUser", query = "SELECT d FROM Doctor d WHERE d.idUser = :idUser")
    , @NamedQuery(name = "Doctor.findByExperience", query = "SELECT d FROM Doctor d WHERE d.experience = :experience")
    , @NamedQuery(name = "Doctor.findByQualification", query = "SELECT d FROM Doctor d WHERE d.qualification = :qualification")
    , @NamedQuery(name = "Doctor.findBySpecialization", query = "SELECT d FROM Doctor d WHERE d.specialization = :specialization")
    , @NamedQuery(name = "Doctor.findByNote", query = "SELECT d FROM Doctor d WHERE d.note = :note")})
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_user")
    private Integer idUser;
    private Integer experience;
    @Size(max = 50)
    private String qualification;
    @Size(max = 50)
    private String specialization;
    @Size(max = 500)
    private String note;
    @OneToMany(mappedBy = "idDoctor")
    private List<Ticket> ticketList;
    @JoinColumn(name = "id_office", referencedColumnName = "number_office")
    @ManyToOne
    private Office idOffice;
    @JoinColumn(name = "id_post", referencedColumnName = "id_post")
    @ManyToOne
    private Post idPost;
    @JoinColumn(name = "id_timetable", referencedColumnName = "id_timetable")
    @ManyToOne
    private Timetable idTimetable;
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    public Doctor() {
    }

    public Doctor(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @XmlTransient
    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    public Office getIdOffice() {
        return idOffice;
    }

    public void setIdOffice(Office idOffice) {
        this.idOffice = idOffice;
    }

    public Post getIdPost() {
        return idPost;
    }

    public void setIdPost(Post idPost) {
        this.idPost = idPost;
    }

    public Timetable getIdTimetable() {
        return idTimetable;
    }

    public void setIdTimetable(Timetable idTimetable) {
        this.idTimetable = idTimetable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUser != null ? idUser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) object;
        if ((this.idUser == null && other.idUser != null) || (this.idUser != null && !this.idUser.equals(other.idUser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Doctor[ idUser=" + idUser + " ]";
    }
    
}
