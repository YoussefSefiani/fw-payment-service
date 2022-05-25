package fw.paymentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="fw-partnership-service", url="${app.services.partnership.url}")
public interface PartnershipRestConsumer {

    @PostMapping("/api/partnership/validate/{partnershipId}")
    void validatePartnership(@PathVariable("partnershipId") Long partnershipId);

}
