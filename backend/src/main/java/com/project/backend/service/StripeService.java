package com.project.backend.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public Session createCheckoutSession(Double amount, Long orderId) throws Exception {

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:4200/customer/payment-success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("http://localhost:4200/customer/payment-failed")

                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("inr")
                                                        .setUnitAmount((long) (amount * 100))
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Order #" + orderId)
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        return Session.create(params);
    }

    public Refund refundPayment(String paymentIntentId) throws StripeException{
        RefundCreateParams params = RefundCreateParams.builder().setPaymentIntent(paymentIntentId).build();
        return  Refund.create(params);
    }

}
