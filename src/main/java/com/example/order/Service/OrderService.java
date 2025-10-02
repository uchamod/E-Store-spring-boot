package com.example.order.Service;
import com.example.order.DTO.SellerDTO;
import com.example.order.DTO.UserWrapper;
import com.example.order.Feign.OrderFeignClient;
import com.example.order.Feign.UserFeignClient;
import com.example.order.Model.Order;
import com.example.order.Repostory.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderFeignClient orderFeignClient;
    private final EmailService emailService;
    private final UserFeignClient userFeignClient;
    //place new order
    public ResponseEntity<Order> placeOrder(Order order) {
        try{
            if(order == null || order.getOrderProductModelList().isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            Order order1= orderRepo.save(order);
             //send acknowledgement
             ResponseEntity<UserWrapper> userDTO= userFeignClient.getUserDTO(order.getCustomerId());
            String subject = "New Order Received - Order #" + order.getOrderId().toString().substring(0, 8);
            emailService.sendSimpleEmail(userDTO.getBody().getUserEmail(),subject,"email is recive succsussfuly");
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
            if(uuid == null || !role.equals("CUSTOMER")){
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
    public ResponseEntity<String> purcheForOrder(UUID orderId,String status) {
        try{
            if(orderId == null || status.isEmpty()){
                return ResponseEntity.badRequest().build();
            }
           Optional<Order> order= orderRepo.findById(orderId);
            if(order.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            order.get().setOrderStatus(status);
            //when paid clear from cart
            if(status.equals("PAID")){
                ResponseEntity<String> result= orderFeignClient.checkoutFromCart(order.get().getCustomerId());
                if(result.getStatusCode().isError()){
                    return ResponseEntity.internalServerError().build();
                }
                System.out.println(result.getBody());
            }
            orderRepo.save(order.get());




              /*

               seller acknowledgement

               */
            return ResponseEntity.ok("status updated "+status);
        }catch (Exception e){
            System.out.println("cannot get orders by seller id");
            return ResponseEntity.internalServerError().build();
        }
    }
}
