package com.cw.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "registro")
@Getter
@Setter
public class RegistroJpa {

    @Id
    @Column(name = "id_registro")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "dt_hora")
    private String dtHora;
    @Column(name = "uso_cpu")
    private Double usoCpu;
    @Column(name = "uso_ram")
    private Long usoRam;
    @Column(name = "disponivel_ram")
    private Long disponivelRam;

    public RegistroJpa(String dtHora, Double usoCpu, Long usoRam, Long disponivelRam) {
        this.dtHora = dtHora;
        this.usoCpu = usoCpu;
        this.usoRam = usoRam;
        this.disponivelRam = disponivelRam;
    }
}
