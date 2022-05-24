package fw.paymentservice.model;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutPayment {
    private String name;
    private String currency;

    private String successUrl;
    private String cancelUrl;

    private long amount;
    private long quantity;
}
