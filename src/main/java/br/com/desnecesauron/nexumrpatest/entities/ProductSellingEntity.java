package br.com.desnecesauron.nexumrpatest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
public class ProductSellingEntity implements Comparator<ProductSellingEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 1500)
    private String url;
    @Column(nullable = false, length = 1500)
    private String title;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private LocalDateTime timestampGenerated;

    @Override
    public int compare(ProductSellingEntity p1, ProductSellingEntity p2) {
        return Double.compare(p1.getPrice(), p2.getPrice());
    }
}
