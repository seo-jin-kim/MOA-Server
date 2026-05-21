package com.moa.server.entity.inventory;

import com.moa.server.common.BaseEntity;
import com.moa.server.entity.inventory.dto.OrderDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orderer")
@Getter
@Setter // DTO 역할을 위해 세터를 열어줍니다
@NoArgsConstructor // JPA와 DTO 처리를 위한 기본 생성자
@AllArgsConstructor
@Builder
public class OrdererEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderer_id")
    private Integer ordererId;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "orderform_id")
    private Integer orderformId;

    @Column(name = "order_sno")
    private Integer orderSno;

    @Column(name = "unit_price")
    private Integer unitPrice;

    //product 와 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private ProductEntity product;

    //orderform 와 join
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderform_id", insertable = false, updatable = false)
    private OrderformEntity orderform;

    public OrderDTO toDTO() {

        Integer calculatedPrice = (this.orderSno != null && this.unitPrice != null)
                ? this.orderSno * this.unitPrice
                : 0;

        Integer calculatedSno = (this.orderSno != null)
                ? this.orderSno * this.unitPrice
                : 0;

        Integer calculatedUnitPrice = (this.unitPrice != null)
                ? this.orderSno * this.unitPrice
                : 0;

        return OrderDTO.builder()
                .ordererId(this.ordererId)
                .productId(this.productId)
                .orderformId(this.orderformId)
                .productName(this.product != null ? this.product.getProductName() : null)
                .orderformDate(this.orderform != null ? this.orderform.getOrderformDate() : null)
                .stockInDate(this.orderform != null ? this.orderform.getStockInDate() : null)
                .orderStatus(this.getOrderform() != null ? this.orderform.getOrderStatus() : null)
                .vendorId(this.getOrderform() != null ? this.orderform.getVendorId() : null)
                .vendorCord(this.getOrderform() != null && this.getOrderform().getVendor() != null
                        ? this.getOrderform().getVendor().getVendorCord() : null)
                .vendorName(this.getOrderform() != null && this.getOrderform().getVendor() != null
                        ? this.getOrderform().getVendor().getVendorName() : null)
                .totalPrice(calculatedPrice)
                .build();
    }

    public OrderDTO toinfoDTO() {

        Integer calculatedPrice = (this.orderSno != null && this.unitPrice != null)
                ? this.orderSno * this.unitPrice
                : 0;

        return OrderDTO.builder()
                .ordererId(this.ordererId)
                .productId(this.productId)
                .orderformId(this.orderformId)
                .orderSno(this.orderSno)
                .unitPrice(this.unitPrice)
                .productCord(this.product != null ? this.product.getProductCord() : null)
                .productName(this.product != null ? this.product.getProductName() : null)
                .productPrice(this.product != null ? this.product.getProductPrice() : null)
                .orderformDate(this.orderform != null ? this.orderform.getOrderformDate() : null)
                .stockInDate(this.orderform != null ? this.orderform.getStockInDate() : null)
                .orderStatus(this.getOrderform() != null ? this.orderform.getOrderStatus() : null)
                .vendorId(this.getOrderform() != null ? this.orderform.getVendorId() : null)
                .vendorCord(this.getOrderform() != null && this.getOrderform().getVendor() != null
                        ? this.getOrderform().getVendor().getVendorCord() : null)
                .vendorName(this.getOrderform() != null && this.getOrderform().getVendor() != null
                        ? this.getOrderform().getVendor().getVendorName() : null)
                .totalPrice(calculatedPrice)
                .build();
    }

}
