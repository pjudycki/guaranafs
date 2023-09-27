package com.guarana.entity;


import javax.persistence.*;

@Entity
@Table(name = "currency")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String currencyName;

    @Column
    private Double rateOfExchange;

    @ManyToOne
    @JoinColumn(name = "retrieval_id")
    private RetrievalEntity retrievalId;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setRateOfExchange(Double rateOfExchange) {
        this.rateOfExchange = rateOfExchange;
    }

    public void setRetrievalId(RetrievalEntity retrievalId) {
        this.retrievalId = retrievalId;
    }

    public RetrievalEntity getRetrievalId() {
        return retrievalId;
    }

    public Double getRateOfExchange() {
        return rateOfExchange;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
