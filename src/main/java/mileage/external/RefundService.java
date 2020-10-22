
package mileage.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="refund", url="${api.refund.url}")
public interface RefundService {

    @RequestMapping(method= RequestMethod.POST, path="/refunds")
    public void forfeit(@RequestBody Refund refund);

}