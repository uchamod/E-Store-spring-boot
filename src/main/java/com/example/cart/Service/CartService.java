package com.example.cart.Service;

import com.example.cart.Feign.Feign_Client;
import com.example.cart.Model.Cart;
import com.example.cart.Model.CartProduct;
import com.example.cart.Repo.CartRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Array;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final Feign_Client feignClient;

    //add item to cart
    public ResponseEntity<String> addToCart(UUID userId, UUID productId) {
        try{
            if(userId==null || productId == null){
                System.out.println("Empty Credentials");
                return ResponseEntity.badRequest().build();
            }
            CartProduct product=new CartProduct(productId,1,false);

            List<CartProduct> cartProduct=new ArrayList<>();
            cartProduct.add(product);
            //get product price
            ResponseEntity<Double> total=  feignClient.getTotalAmount(productId);
            Cart cart=new Cart();
            Cart existingCart= cartRepo.findCartByCustomerId(userId);
            if(existingCart == null){
                cart.setCustomerId(userId);
                cart.setCartProductList(cartProduct);
                cart.setTotalAmount(total.getBody());
                cartRepo.save(cart);
                return ResponseEntity.ok("product is add to cart successfully");
            }
            existingCart.getCartProductList().add(product);
            //update cart total
            existingCart.setTotalAmount(existingCart.getTotalAmount()+total.getBody());

            cartRepo.save(existingCart);
            return ResponseEntity.ok("product is add to cart successfully");
        }catch (Exception e){
            System.out.println("internal server error : "+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
   //update cart item(increse item count)
    public ResponseEntity<List<CartProduct>> updateCartItem(UUID uuid, UUID productId,Integer count) {
        try{
            if(uuid == null | productId == null){
                return ResponseEntity.badRequest().build();
            }

            Cart cart= cartRepo.findCartByCustomerId(uuid);
            if(cart == null | cart.getCartProductList().isEmpty()){
                return ResponseEntity.notFound().build();
            }

            for(CartProduct cartProduct : cart.getCartProductList()){
                if(cartProduct.getProductId().equals(productId)){
                    cartProduct.setProductCount(count);
                    cartRepo.save(cart);
                    return ResponseEntity.ok(cart.getCartProductList());
                }
            }

            return ResponseEntity.notFound().build();


        }catch (Exception e){
            System.out.println("internal server error"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
//delete cart item
    public ResponseEntity<String> deleteCartItem(UUID uuid, UUID productId) {
        try{
            if(uuid == null | productId == null){
                return ResponseEntity.badRequest().build();
            }
           Cart cart= cartRepo.findCartByCustomerId(uuid);
            if(cart == null | cart.getCartProductList().isEmpty()){
                return ResponseEntity.notFound().build();
            }

            cart.getCartProductList().remove(productId.compareTo(productId));
            cartRepo.save(cart);

            return ResponseEntity.ok("product is deleted successfully");
        }catch (Exception e){
            System.out.println("internal server error"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
//get all user product from cart
    public ResponseEntity<List<CartProduct>> getProductFromCart(UUID uuid) {
        try{
            if(uuid == null){
                return ResponseEntity.badRequest().build();
            }
           Cart cart= cartRepo.findCartByCustomerId(uuid);
            if(cart == null){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(cart.getCartProductList());
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
//check out the cart
    public ResponseEntity<String> toggleCheckOut(UUID uuid, List<UUID> productIds) {
       try{
           if(uuid == null | productIds.isEmpty()){
               return ResponseEntity.badRequest().build();
           }
           Cart cart=cartRepo.findCartByCustomerId(uuid);
           if(cart == null | cart.getCartProductList().isEmpty()){
               return ResponseEntity.notFound().build();
           }
           cart.getCartProductList().forEach(((p)->p.setIsCheckout(true)));
           cartRepo.save(cart);
           return ResponseEntity.ok("checkout the items");
       }catch (Exception e){
           return ResponseEntity.internalServerError().build();
       }
    }
//get total amount of items
    public ResponseEntity<Double> getTotal(UUID uuid) {
       try{
           return ResponseEntity.ok(1000.0);
       }catch (Exception e){
           return ResponseEntity.internalServerError().build();
       }
    }
}
