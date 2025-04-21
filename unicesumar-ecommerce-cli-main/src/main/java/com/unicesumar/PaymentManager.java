package com.unicesumar;
import com.unicesumar.paymentMethods.PaymentMethod;

public class PaymentManager {

    private PaymentMethod paymentMethod;

    public PaymentManager(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void pay(double amount) {
        paymentMethod.pay(amount);
    }
}
