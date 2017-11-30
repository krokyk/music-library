/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kroky.musiclib.db.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name = "recent_dirs")
public class RecentDir extends AbstractEntity implements Serializable, Comparable<RecentDir> {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "dir_path")
    private String dirPath;
    @Column(name = "last_date")
    private Timestamp lastDate;

    public RecentDir() {
    }

    public RecentDir(String path, Timestamp timestamp) {
        dirPath = path;
        lastDate = timestamp;
    }

    @Override
    public String toDetailedString() {
        return "RecentDir{" + "path=" + dirPath + ", lastDate=" + lastDate + '}';
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public Timestamp getLastDate() {
        return lastDate;
    }

    public void setLastDate(Timestamp lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public String toString() {
        return dirPath;
    }

    @Override
    public int compareTo(RecentDir o) {
        if (o == null || o.lastDate == null) {
            return 1;
        }
        return -1 * lastDate.compareTo(o.lastDate); //to ensure Z-A ordering
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.dirPath);
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
        final RecentDir other = (RecentDir) obj;
        if (!Objects.equals(this.dirPath, other.dirPath)) {
            return false;
        }
        return true;
    }

}
