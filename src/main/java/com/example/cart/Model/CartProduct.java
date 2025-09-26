package com.example.cart.Model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;


@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProduct {



    private UUID productId;
    private Integer productCount;
    private Boolean isCheckout;


}
