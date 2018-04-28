/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Post.findAll", query = "SELECT p FROM Post p")
    , @NamedQuery(name = "Post.findByIdPost", query = "SELECT p FROM Post p WHERE p.idPost = :idPost")
    , @NamedQuery(name = "Post.findByLevelAccess", query = "SELECT p FROM Post p WHERE p.levelAccess = :levelAccess")
    , @NamedQuery(name = "Post.findByNamePost", query = "SELECT p FROM Post p WHERE p.namePost = :namePost")
    , @NamedQuery(name = "Post.findBySalary", query = "SELECT p FROM Post p WHERE p.salary = :salary")})
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_post")
    private Integer idPost;
    @Column(name = "level_access")
    private Integer levelAccess;
    @Size(max = 50)
    @Column(name = "name_post")
    private String namePost;
    private Integer salary;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPost")
    private List<Personal> personalList;
    @OneToMany(mappedBy = "idPost")
    private List<Doctor> doctorList;

    public Post() {
    }

    public Post(Integer idPost) {
        this.idPost = idPost;
    }

    public Integer getIdPost() {
        return idPost;
    }

    public void setIdPost(Integer idPost) {
        this.idPost = idPost;
    }

    public Integer getLevelAccess() {
        return levelAccess;
    }

    public void setLevelAccess(Integer levelAccess) {
        this.levelAccess = levelAccess;
    }

    public String getNamePost() {
        return namePost;
    }

    public void setNamePost(String namePost) {
        this.namePost = namePost;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @XmlTransient
    public List<Personal> getPersonalList() {
        return personalList;
    }

    public void setPersonalList(List<Personal> personalList) {
        this.personalList = personalList;
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
        hash += (idPost != null ? idPost.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Post)) {
            return false;
        }
        Post other = (Post) object;
        if ((this.idPost == null && other.idPost != null) || (this.idPost != null && !this.idPost.equals(other.idPost))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Post[ idPost=" + idPost + " ]";
    }
    
}
