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
    @NamedQuery(name = "Service.findAll", query = "SELECT s FROM Service s")
    , @NamedQuery(name = "Service.findByIdService", query = "SELECT s FROM Service s WHERE s.idService = :idService")
    , @NamedQuery(name = "Service.findByNameService", query = "SELECT s FROM Service s WHERE s.nameService = :nameService")
    , @NamedQuery(name = "Service.findByCategory", query = "SELECT s FROM Service s WHERE s.category = :category")
    , @NamedQuery(name = "Service.findByPrice", query = "SELECT s FROM Service s WHERE s.price = :price")
    , @NamedQuery(name = "Service.findByNote", query = "SELECT s FROM Service s WHERE s.note = :note")})
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_service")
    private Integer idService;
    @Size(max = 50)
    @Column(name = "name_service")
    private String nameService;
    @Size(max = 50)
    private String category;
    private Integer price;
    @Size(max = 500)
    private String note;
    @OneToMany(mappedBy = "idService")
    private List<Ticket> ticketList;

    public Service() {
    }

    public Service(Integer idService) {
        this.idService = idService;
    }

    public Integer getIdService() {
        return idService;
    }

    public void setIdService(Integer idService) {
        this.idService = idService;
    }

    public String getNameService() {
        return nameService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idService != null ? idService.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.idService == null && other.idService != null) || (this.idService != null && !this.idService.equals(other.idService))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Service[ idService=" + idService + " ]";
    }
    
}
