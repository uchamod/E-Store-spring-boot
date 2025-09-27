package com.uchamod.estore.Repo;

import com.uchamod.estore.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface Product_Repo extends JpaRepository<Product, UUID> {
  List<Product> findProductByProductCategory(String category);
  List<Product> findProductByProductBrand(String brand);
  @Query("SELECT p FROM Product p WHERE " +
          "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(p.productDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(p.productCategory) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
          "LOWER(p.productBrand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Product> searchProduct(String keyword);

  List<Product> findProductBySellerId(UUID sellerId);

  @Query("SELECT p.productPrice FROM Product p WHERE p.productId = :productId")
  Double findProductPriceByProductId(UUID productId);
}
