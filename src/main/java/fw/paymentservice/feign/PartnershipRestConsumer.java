package fw.paymentservice.feign;

import org.springframework.web.bind.annotation.PostMapping;

public interface PartnershipRestConsumer {
    @PostMapping("/api/partnership/validate")
    void validatePartnership(Long partnershipId);
}
