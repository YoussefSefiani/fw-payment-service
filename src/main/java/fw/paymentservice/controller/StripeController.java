package fw.paymentservice.controller;

import com.stripe.exception.StripeException;
import fw.paymentservice.model.CheckoutPayment;
import fw.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/api")
public class StripeController {

    private final PaymentService paymentService;

    @Autowired
    public StripeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(path = "payment/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping(value = "/payment")
    public String payPartnership(@RequestBody CheckoutPayment payment) throws StripeException {
       return paymentService.payPartnership(payment);
    }

    @PostMapping(value = "/stripe-events")
    public void postEventsWebhook(HttpServletRequest request, HttpServletResponse response) throws StripeException, IOException {
       paymentService.postEventsWebhook(request, response);
    }



}
