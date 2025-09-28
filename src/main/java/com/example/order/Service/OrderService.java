package com.example.order.Service;

import com.example.order.DTO.SellerDTO;
import com.example.order.Model.Order;
import com.example.order.Repostory.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class OrderService {

    private final OrderRepo orderRepo;

    //place new order
    public ResponseEntity<Order> placeOrder(Order order) {
        try{
            if(order == null || order.getOrderProductModelList().isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            Order order1= orderRepo.save(order);
               /*

               seller acknowledgement

               */
            return ResponseEntity.ok(order1);
        }catch (Exception e){
            System.out.println("error while creating order"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
//get all orders (Admin)
    public ResponseEntity<List<Order>> getAllOrders() {
        try{
            List<Order> orders=  orderRepo.findAll();
            return ResponseEntity.ok(orders);
        }catch (Exception e){
            System.out.println("cannot get all orders"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    //get orders by seller id
    public ResponseEntity<List<SellerDTO>> getOrdersBySellerId(UUID uuid,String role) {
        try{
            System.out.println(role);
            if(uuid == null || !role.equals("SELLER")){
                System.out.println("role or id is empty");
                return ResponseEntity.badRequest().build();
            }
           List<SellerDTO> sellerDTOList= orderRepo.findSellerOrdersBySellerId(uuid);
            if(sellerDTOList.isEmpty()){
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(sellerDTOList);
        }catch (Exception e){
            System.out.println("cannot get orders by seller id");
            return ResponseEntity.internalServerError().build();
        }
    }
//get orders by customer id
    public ResponseEntity<List<Order>> getOrdersByCustomerId(UUID uuid, String role) {
        try{
            if(uuid == null){
                return ResponseEntity.badRequest().build();
            }
           List<Order> orders= orderRepo.findOrdersByCustomerId(uuid);
            if(orders.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(orders);
        }catch (Exception e){
            System.out.println("cannot get orders by seller id");
            return ResponseEntity.internalServerError().build();
        }
    }
//update order state to paid
    public ResponseEntity<String> purcheForOrder(UUID orderId) {
        try{
            if(orderId == null){
                return ResponseEntity.badRequest().build();
            }
           Optional<Order> order= orderRepo.findById(orderId);
            if(order.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            order.get().setOrderStatus("PAID");
            orderRepo.save(order.get());
            return ResponseEntity.ok("status update successfully");
        }catch (Exception e){
            System.out.println("cannot get orders by seller id");
            return ResponseEntity.internalServerError().build();
        }
    }
}
