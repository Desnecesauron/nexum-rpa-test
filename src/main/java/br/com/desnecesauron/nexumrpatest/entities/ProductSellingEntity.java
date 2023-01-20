package br.com.desnecesauron.nexumrpatest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
public class ProductSellingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private LocalDateTime timestampGenerated;

}
