package com.cw.dao;

import com.cw.models.RegistroJpa;

import javax.persistence.EntityManager;

public class RegistroJpaDAO {

    EntityManager em;

    public RegistroJpaDAO(EntityManager em) {
        this.em = em;
    }

    public void registrar(RegistroJpa registro) {
        this.em.persist(registro);
    }
}