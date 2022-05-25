package fw.paymentservice.model;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutPayment {

    private long partnershipId;

    private String name;
    private String currency;

    private String successUrl;
    private String cancelUrl;

    private Long amount;
    private Long quantity;
}
