/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.db.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name = "albums")
public class Album extends AbstractEntity implements Serializable, Comparable<Album> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @Column(name = "release_year")
    private Integer releaseYear;
    @Column
    private Integer checked;
    @ManyToOne
    @JoinColumn(name = "band_name", nullable = false)
    private Band band;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(name);
        hash = 53 * hash + Objects.hashCode(releaseYear);
        hash = 53 * hash + Objects.hashCode(band.getName());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Album other = (Album) obj;
        if (!name.equalsIgnoreCase(other.name)) {
            return false;
        }
        if (!Objects.equals(releaseYear, other.releaseYear)) {
            return false;
        }
        if (!band.getName().equalsIgnoreCase(other.band.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toDetailedString() {
        return "id=" + id + ", name=" + name + ", year=" + releaseYear + ", checked=" + checked + ", band="
                + band.getName();
    }

    @Override
    public int compareTo(Album o) {
        int retVal = releaseYear.compareTo(o.releaseYear);
        if (retVal == 0) {
            retVal = name.compareToIgnoreCase(o.name);
        }
        return retVal;
    }

    /**
     * Updates this name and checked status from album 'a'
     *
     * @param a
     */
    public void update(Album a) {
        checked = a.checked;
        name = a.name;
    }
}
