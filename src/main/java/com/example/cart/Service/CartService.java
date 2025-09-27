package com.example.cart.Service;

import com.example.cart.DTO.CountUpdater;
import com.example.cart.Feign.Feign_Client;
import com.example.cart.Model.Cart;
import com.example.cart.Model.CartProduct;
import com.example.cart.Model.ProductResponse;
import com.example.cart.Model.ProductWrapper;
import com.example.cart.Repo.CartRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            feignClient.updateAvailableCount(new CountUpdater(productId,1,false));
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
            ResponseEntity<Double> total=  feignClient.getTotalAmount(productId);

            for(CartProduct cartProduct : cart.getCartProductList()){
                if(cartProduct.getProductId().equals(productId)){
                    Integer existingCount=cartProduct.getProductCount();
                    Double finalAmount= (cart.getTotalAmount()-existingCount*total.getBody())+count*total.getBody();
                    cart.setTotalAmount(finalAmount);
                    Integer finalCount=existingCount-count;
                    feignClient.updateAvailableCount(new CountUpdater(productId,Math.abs(finalCount),existingCount > count));
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
            ResponseEntity<Double> total=  feignClient.getTotalAmount(productId);

            CartProduct cartProduct= cart.getCartProductList().remove(productId.compareTo(productId));
            feignClient.updateAvailableCount(new CountUpdater(productId,cartProduct.getProductCount(),true));
              cart.setTotalAmount(cart.getTotalAmount()-(total.getBody()*cartProduct.getProductCount()));
            cartRepo.save(cart);

            return ResponseEntity.ok("product is deleted successfully");
        }catch (Exception e){
            System.out.println("internal server error"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
//get all user product from cart
    public ResponseEntity<ProductResponse> getProductFromCart(UUID uuid) {
        try{
            if(uuid == null){
                return ResponseEntity.badRequest().build();
            }
           Cart cart= cartRepo.findCartByCustomerId(uuid);
            if(cart == null){
                return ResponseEntity.notFound().build();
            }
            List<ProductWrapper> productWrappers=new ArrayList<>();
            ProductWrapper productWrapper=new ProductWrapper();
            for(CartProduct cartProduct : cart.getCartProductList()){
                 productWrapper= feignClient.getProductById(cartProduct.getProductId()).getBody();
                 productWrapper.setPurchaseCount(cartProduct.getProductCount());
                 productWrappers.add(productWrapper);
            }
            ProductResponse productResponse=new ProductResponse(cart.getTotalAmount(),productWrappers);
            return ResponseEntity.ok(productResponse);
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
           if(uuid == null){
               return ResponseEntity.badRequest().build();
           }
           Cart cart=cartRepo.findCartByCustomerId(uuid);
           if(cart == null | cart.getCartProductList().isEmpty()){
               return ResponseEntity.ok(0.0);
           }

           return ResponseEntity.ok(cart.getTotalAmount());
       }catch (Exception e){
           return ResponseEntity.internalServerError().build();
       }
    }
}
