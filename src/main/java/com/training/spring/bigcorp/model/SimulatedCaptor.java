package com.training.spring.bigcorp.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * Représente un capteur avec des valeurs simulées
 */
@Entity
@DiscriminatorValue("SIMULATED")
public class SimulatedCaptor  extends Captor{

    /**
     * Minimum d'énergie en Watt
     */
    @NotNull
    private Integer minPowerInWatt;

    /**
     * Maximum d'énergie en Watt
     */
    @NotNull
    private Integer maxPowerInWatt;

    @Deprecated
    public SimulatedCaptor(){
        super();
    }

    /*public SimulatedCaptor(String name, Site site){
        super(name,site);
    }*/

    public SimulatedCaptor(String name, Site site, Integer minPowerInWatt, Integer maxPowerInWatt){
        super(name,site, PowerSource.SIMULATED);
        this.maxPowerInWatt = maxPowerInWatt;
        this.minPowerInWatt = minPowerInWatt;
    }

    @AssertTrue(message="minPowerInWatt should be less than maxPowerInWatt")
    public boolean isValid(){
        return this.minPowerInWatt <= this.maxPowerInWatt;
    }

    public Integer getMinPowerInWatt() {
        return minPowerInWatt;
    }

    public void setMinPowerInWatt(Integer minPowerInWatt) {
        this.minPowerInWatt = minPowerInWatt;
    }

    public Integer getMaxPowerInWatt() {
        return maxPowerInWatt;
    }

    public void setMaxPowerInWatt(Integer maxPowerInWatt) {
        this.maxPowerInWatt = maxPowerInWatt;
    }
}
