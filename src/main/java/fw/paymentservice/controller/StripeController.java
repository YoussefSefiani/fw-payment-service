package fw.paymentservice.controller;

import com.stripe.exception.StripeException;
import fw.paymentservice.model.CheckoutPayment;
import fw.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class StripeController {

    private final PaymentService paymentService;

    @Autowired
    public StripeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/payment")
    public String payPartnership(@RequestBody CheckoutPayment payment) throws StripeException {
       return paymentService.payPartnership(payment);
    }

    @PostMapping(value = "/stripe-events")
    public void postEventsWebhook(HttpServletRequest request, HttpServletResponse response) throws StripeException {
        paymentService.postEventsWebhook(request, response);
    }



}
