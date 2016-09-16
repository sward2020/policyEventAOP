package sample.aop.test.domain;

/**
 * Created by n0292928 on 9/5/16.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.AutoPolicyIssueEvent;
import sample.aop.event.HomePolicyIssueEvent;
import sample.aop.event.LifePolicyIssueEvent;
import sample.aop.event.PolicyEventPublisherImpl;

import java.util.Arrays;
import java.util.List;

/**
 * Created by n0292928 on 9/2/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyTest {


    public final static String POLICY_TYPE_HOME = "Home";
    public final static String POLICY_TYPE_AUTO = "Auto";
    public final static String POLICY_TYPE_LIFE = "Life";

    public final static BasicPolicyDto AutoPolicyStandardDto = AbstractPolicy.policyDtoFactory("Auto Policy 20", 20, "Customer Number 2", 2, 38000.00);
    public final static BasicPolicyDto AutoPolicyExpensiveDto = AbstractPolicy.policyDtoFactory("Auto Policy 40", 40, "Customer Number 4", 4, 75000.00);

    public final static BasicPolicyDto HomePolicyStandardDto = AbstractPolicy.policyDtoFactory("Home Policy 10", 10, "Customer Number 1", 1, 450000.00);
    public final static BasicPolicyDto HomePolicyExpensiveDto = AbstractPolicy.policyDtoFactory("Home Policy 50", 50, "Customer Number 5", 5, 1450000.00);

    public final static BasicPolicyDto LifePolicySeniorDto = AbstractPolicy.policyDtoFactory("Life Policy 30", 30, "Customer Number 3", 3, 60);
    public final static BasicPolicyDto LifePolicyStandardDto = AbstractPolicy.policyDtoFactory("Life Policy 60", 60, "Customer Number 6", 6, 42);

    //***********************************************
    @Before
    public void init() {

    }

    @Test
    public void testCalculateAutoPremiumExpensive() {
        AutoPolicy autoPolicyExpensive  = new AutoPolicy(AutoPolicyExpensiveDto);
        autoPolicyExpensive.setPolicyPremium(autoPolicyExpensive.calculatePremium());

        // Base Prem 1000.00
        // Car prices <= 40000 - Premimum = Base * 1.5
        // Car price > 40000 - Premimum = Base * 3.5

        Assert.assertTrue(autoPolicyExpensive.getPolicyPremium() == (autoPolicyExpensive.getPolicyBasePremium() * 3.5));
    }

    @Test
    public void testCalculateAutoPremiumStandard() {
        AutoPolicy autoPolicyStandard  = new AutoPolicy(AutoPolicyStandardDto);
        autoPolicyStandard.setPolicyPremium(autoPolicyStandard.calculatePremium());

        // Base Prem 1000.00
        // Car prices <= 40000 - Premimum = Base * 1.5
        // Car price > 40000 - Premimum = Base * 3.5

        Assert.assertTrue(autoPolicyStandard.getPolicyPremium() == (autoPolicyStandard.getPolicyBasePremium() * 1.5));
    }

    @Test
    public void testCalculateHomePremiumExpensive() {
        HomePolicy homePolicyExpensive  = new HomePolicy(HomePolicyExpensiveDto);
        homePolicyExpensive.setPolicyPremium(homePolicyExpensive.calculatePremium());

        // Base Prem 2500.00
        // Home Price  <= 1000000 - Premimum = Base * 1.5
        // Home price > 1000000 - Premimum = Base * 3.5

        Assert.assertTrue(homePolicyExpensive.getPolicyPremium() == (homePolicyExpensive.getPolicyBasePremium() * 3.5));
    }

    @Test
    public void testCalculateHomePremiumStandard() {
        HomePolicy homePolicyStandard  = new HomePolicy(HomePolicyStandardDto);
        homePolicyStandard.setPolicyPremium(homePolicyStandard.calculatePremium());

        // Base Prem 2500.00
        // Home Price  <= 1000000 - Premimum = Base * 1.5
        // Home price > 1000000 - Premimum = Base * 3.5

        Assert.assertTrue(homePolicyStandard.getPolicyPremium() == (homePolicyStandard.getPolicyBasePremium() * 1.5));
    }

    @Test
    public void testCalculateLifePremiumSenior() {
        LifePolicy lifePolicySenior  = new LifePolicy(LifePolicySeniorDto);
        lifePolicySenior.setPolicyPremium(lifePolicySenior.calculatePremium());

        // Base Prem 589.99
        // Age > 55 - Premimum = Base * 2.5
        // Age <= 55  - Premimum = Base * 3.5

        Assert.assertTrue(lifePolicySenior.getPolicyPremium() == (lifePolicySenior.getPolicyBasePremium() * 3.5));
    }

    @Test
    public void testCalculateLifePremiumStandard() {
        LifePolicy lifePolicyStandard  = new LifePolicy(LifePolicyStandardDto);
        lifePolicyStandard.setPolicyPremium(lifePolicyStandard.calculatePremium());

        // Base Prem 589.99
        // Age > 55 - Premimum = Base * 2.5
        // Age <= 55  - Premimum = Base * 3.5

        Assert.assertTrue(lifePolicyStandard.getPolicyPremium() == (lifePolicyStandard.getPolicyBasePremium() * 2.5));
    }

}
