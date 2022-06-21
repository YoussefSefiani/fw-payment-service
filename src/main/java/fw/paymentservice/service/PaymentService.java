package fw.paymentservice.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import fw.paymentservice.feign.PartnershipRestConsumer;
import fw.paymentservice.model.CheckoutPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Value("${stripe.secretKey}")
    String stripeApiKey;

    @Value("${stripe.secretWebHook}")
    String stripeWebhookSecret;

    Long partnershipId;

    private final PartnershipRestConsumer consumer;

    @Autowired
    public PaymentService(PartnershipRestConsumer consumer) {
        this.consumer = consumer;
    }


    public String payPartnership(CheckoutPayment payment) throws StripeException {
        System.out.println("payment is: " + payment);
        this.partnershipId = payment.getPartnershipId();

        Gson gson = new Gson();
        init();
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl(payment.getSuccessUrl())
                .setCancelUrl(
                        payment.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setQuantity(payment.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(payment.getCurrency()).setUnitAmount(payment.getAmount())
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                        .builder().setName(payment.getName()).build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        Map<String, String> responseData = new HashMap<>();
        responseData.put("id", session.getId());
        return gson.toJson(responseData);

    }

        public void postEventsWebhook(HttpServletRequest request, HttpServletResponse response) throws SignatureVerificationException, IOException {

        Stripe.apiKey = stripeApiKey;

        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (!StringUtils.isEmpty(payload)) {
                String sigHeader = request.getHeader("Stripe-Signature");
                String endpointSecret = stripeWebhookSecret;

                Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

                if("checkout.session.completed".equalsIgnoreCase(event.getType())) {
                    System.out.println("partnership id is: " + partnershipId);
                    consumer.validatePartnership(partnershipId);
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Error during payment");
                }
            }
        } catch(Exception e) {
            response.setStatus(500);
        }
        }

    private static void init() {
        Stripe.apiKey = "sk_test_51L2waTAKK7UhTIYhWZ0gap86miBgGOg7GX8NTKVum9cSyUdceoKrjFgrWhvAjBwVDQXz1wVq5aw6vCkyOOwSpBAS003FVAjo7e";
    }
}
