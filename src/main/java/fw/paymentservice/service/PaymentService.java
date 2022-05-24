package fw.paymentservice.service;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import fw.paymentservice.model.CheckoutPayment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Value("${stripe.secretKey")
    String stripeApiKey;

    @Value("${stripe.secretWebHook}")
    String stripeWebhookSecret;


    public String payPartnership(CheckoutPayment payment) throws StripeException {
        Gson gson = new Gson();
        // We initialize stripe object with the api key
        init();
        // We create a  stripe session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                // We will use the credit card payment method
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
        // create a stripe session
        Session session = Session.create(params);
        Map<String, String> responseData = new HashMap<>();
        // We get the sessionId and we put inside the response data you can get more info from the session object
        responseData.put("id", session.getId());
        // We can return only the sessionId as a String
        return gson.toJson(responseData);

    }

    public void postEventsWebhook(HttpServletRequest request, HttpServletResponse response) {

        Stripe.apiKey = stripeApiKey;

        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (!StringUtils.isEmpty(payload)) {
                String sigHeader = request.getHeader("Stripe-Signature");
                String endpointSecret = stripeWebhookSecret;

                Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

                if ("charge.refunded".equalsIgnoreCase(event.getType())) {

                    Gson gson = new Gson();
                    Charge charge = gson.fromJson(event.getData().getObject().toJson(), Charge.class);
                    // Call your service here with your Charge object.
                    System.out.println(charge);
                }
                response.setStatus(200);
                System.out.println(response);
            }
        } catch (Exception e) {
            response.setStatus(500);
            System.out.println(response);
            System.out.println(e.getMessage());
        }
    }

    private static void init() {
        Stripe.apiKey = "sk_test_51L2waTAKK7UhTIYhWZ0gap86miBgGOg7GX8NTKVum9cSyUdceoKrjFgrWhvAjBwVDQXz1wVq5aw6vCkyOOwSpBAS003FVAjo7e";
    }
}
