package sample.aop.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.dto.BasicPolicyDto;

/**
 * Created by n0292928 on 8/31/16.
 */
public class HomePolicy extends AbstractPolicy {

    static final Logger logger = LoggerFactory.getLogger(HomePolicy.class);

    public HomePolicy(final BasicPolicyDto parmDto) {
        super(parmDto);
        super.setPolicyBasePremium(2500.00);
    }

    @Override
    public double calculatePremium() {
        double   dblPriceVar = 1.5;

        if (super.getPolicyAssetValue() > 1000000.00 ) {
            dblPriceVar = 3.5;
        }
        return super.getPolicyBasePremium() * dblPriceVar;
    }
}
