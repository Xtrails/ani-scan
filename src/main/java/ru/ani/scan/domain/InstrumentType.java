package ru.ani.scan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A InstrumentType.
 */
@Entity
@Table(name = "instrument_type")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InstrumentType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "type")
    @JsonIgnoreProperties(value = { "robots", "type" }, allowSetters = true)
    private Set<Instrument> instruments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InstrumentType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public InstrumentType name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Instrument> getInstruments() {
        return this.instruments;
    }

    public void setInstruments(Set<Instrument> instruments) {
        if (this.instruments != null) {
            this.instruments.forEach(i -> i.setType(null));
        }
        if (instruments != null) {
            instruments.forEach(i -> i.setType(this));
        }
        this.instruments = instruments;
    }

    public InstrumentType instruments(Set<Instrument> instruments) {
        this.setInstruments(instruments);
        return this;
    }

    public InstrumentType addInstrument(Instrument instrument) {
        this.instruments.add(instrument);
        instrument.setType(this);
        return this;
    }

    public InstrumentType removeInstrument(Instrument instrument) {
        this.instruments.remove(instrument);
        instrument.setType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstrumentType)) {
            return false;
        }
        return getId() != null && getId().equals(((InstrumentType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstrumentType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
