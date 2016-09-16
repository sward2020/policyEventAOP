package sample.aop.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.dto.BasicPolicyDto;

/**
 * Created by n0292928 on 8/31/16.
 */
public class LifePolicy extends AbstractPolicy {

    static final Logger logger = LoggerFactory.getLogger(LifePolicy.class);

    public LifePolicy(final BasicPolicyDto parmDto)  {
        super(parmDto);
        super.setPolicyBasePremium(589.99);
    }

    @Override
    public double calculatePremium() {
        double   dblAgeVar = 2.5;

        if ((int)super.getPolicyAssetValue() > 55 ) {
            dblAgeVar = 3.5;
        }
        return super.getPolicyBasePremium() * dblAgeVar;
    }
}