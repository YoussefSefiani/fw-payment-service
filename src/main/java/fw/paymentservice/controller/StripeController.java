package fw.paymentservice.controller;

import com.stripe.exception.StripeException;
import fw.paymentservice.model.CheckoutPayment;
import fw.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class StripeController {

    private final PaymentService paymentService;

    @Autowired
    public StripeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public String payPartnership(@RequestBody CheckoutPayment payment) throws StripeException {
       return paymentService.payPartnership(payment);
    }

}
