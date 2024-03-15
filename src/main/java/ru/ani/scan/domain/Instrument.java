package ru.ani.scan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Instrument.
 */
@Entity
@Table(name = "instrument")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Instrument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "sec_code")
    private String secCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instrument")
    @JsonIgnoreProperties(value = { "instrument" }, allowSetters = true)
    private Set<Robot> robots = new HashSet<>();

    public Instrument(Long id, String secCode) {
        this.id = id;
        this.secCode = secCode;
    }

    public Instrument() {}

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Instrument id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecCode() {
        return this.secCode;
    }

    public Instrument secCode(String secCode) {
        this.setSecCode(secCode);
        return this;
    }

    public void setSecCode(String secCode) {
        this.secCode = secCode;
    }

    public Set<Robot> getRobots() {
        return this.robots;
    }

    public void setRobots(Set<Robot> robots) {
        if (this.robots != null) {
            this.robots.forEach(i -> i.setInstrument(null));
        }
        if (robots != null) {
            robots.forEach(i -> i.setInstrument(this));
        }
        this.robots = robots;
    }

    public Instrument robots(Set<Robot> robots) {
        this.setRobots(robots);
        return this;
    }

    public Instrument addRobot(Robot robot) {
        this.robots.add(robot);
        robot.setInstrument(this);
        return this;
    }

    public Instrument removeRobot(Robot robot) {
        this.robots.remove(robot);
        robot.setInstrument(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Instrument)) {
            return false;
        }
        return getId() != null && getId().equals(((Instrument) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Instrument{" +
            "id=" + getId() +
            ", secCode='" + getSecCode() + "'" +
            "}";
    }
}
