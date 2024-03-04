package ru.ani.scan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import ru.ani.scan.domain.enumeration.OperationType;
import ru.ani.scan.domain.enumeration.RobotType;

/**
 * A Robot.
 */
@Entity
@Table(name = "robot")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Robot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RobotType type;

    @NotNull
    @Column(name = "lots", nullable = false)
    private String lots;

    @NotNull
    @Column(name = "period", nullable = false)
    private Long period;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;

    @NotNull
    @Column(name = "operation_count", nullable = false)
    private Long operationCount;

    @NotNull
    @Column(name = "first_operation_dttm", nullable = false)
    private Instant firstOperationDttm;

    @NotNull
    @Column(name = "last_operation_dttm", nullable = false)
    private Instant lastOperationDttm;

    @Column(name = "last_price")
    private Double lastPrice;

    @Column(name = "volume_by_hour")
    private Long volumeByHour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "robots" }, allowSetters = true)
    private Instrument instrument;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Robot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RobotType getType() {
        return this.type;
    }

    public Robot type(RobotType type) {
        this.setType(type);
        return this;
    }

    public void setType(RobotType type) {
        this.type = type;
    }

    public String getLots() {
        return this.lots;
    }

    public Robot lots(String lots) {
        this.setLots(lots);
        return this;
    }

    public void setLots(String lots) {
        this.lots = lots;
    }

    public Long getPeriod() {
        return this.period;
    }

    public Robot period(Long period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public OperationType getOperationType() {
        return this.operationType;
    }

    public Robot operationType(OperationType operationType) {
        this.setOperationType(operationType);
        return this;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Long getOperationCount() {
        return this.operationCount;
    }

    public Robot operationCount(Long operationCount) {
        this.setOperationCount(operationCount);
        return this;
    }

    public void setOperationCount(Long operationCount) {
        this.operationCount = operationCount;
    }

    public Instant getFirstOperationDttm() {
        return this.firstOperationDttm;
    }

    public Robot firstOperationDttm(Instant firstOperationDttm) {
        this.setFirstOperationDttm(firstOperationDttm);
        return this;
    }

    public void setFirstOperationDttm(Instant firstOperationDttm) {
        this.firstOperationDttm = firstOperationDttm;
    }

    public Instant getLastOperationDttm() {
        return this.lastOperationDttm;
    }

    public Robot lastOperationDttm(Instant lastOperationDttm) {
        this.setLastOperationDttm(lastOperationDttm);
        return this;
    }

    public void setLastOperationDttm(Instant lastOperationDttm) {
        this.lastOperationDttm = lastOperationDttm;
    }

    public Double getLastPrice() {
        return this.lastPrice;
    }

    public Robot lastPrice(Double lastPrice) {
        this.setLastPrice(lastPrice);
        return this;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Long getVolumeByHour() {
        return this.volumeByHour;
    }

    public Robot volumeByHour(Long volumeByHour) {
        this.setVolumeByHour(volumeByHour);
        return this;
    }

    public void setVolumeByHour(Long volumeByHour) {
        this.volumeByHour = volumeByHour;
    }

    public Instrument getInstrument() {
        return this.instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Robot instrument(Instrument instrument) {
        this.setInstrument(instrument);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Robot)) {
            return false;
        }
        return getId() != null && getId().equals(((Robot) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Robot{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", lots='" + getLots() + "'" +
            ", period=" + getPeriod() +
            ", operationType='" + getOperationType() + "'" +
            ", operationCount=" + getOperationCount() +
            ", firstOperationDttm='" + getFirstOperationDttm() + "'" +
            ", lastOperationDttm='" + getLastOperationDttm() + "'" +
            ", lastPrice=" + getLastPrice() +
            ", volumeByHour=" + getVolumeByHour() +
            "}";
    }
}
