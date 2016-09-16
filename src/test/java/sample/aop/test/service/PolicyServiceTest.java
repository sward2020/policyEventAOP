package sample.aop.test.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.AutoPolicyIssueEvent;
import sample.aop.event.PolicyEventPublisherImpl;
import sample.aop.event.PolicyIssueEvent;
import sample.aop.service.PolicyService;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.StringContains.*;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


/**
 * Created by n0292928 on 9/2/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyServiceTest {
    public final static String POLICY_TYPE_HOME = "Home";
    public final static String POLICY_TYPE_AUTO = "Auto";
    public final static String POLICY_TYPE_LIFE = "Life";

    public final static BasicPolicyDto HomePolicyDto = AbstractPolicy.policyDtoFactory("Home Policy 10", 10, "Customer Number 1", 1, 450000.00);
    public final static BasicPolicyDto AutoPolicyDto = AbstractPolicy.policyDtoFactory("Auto Policy 20", 20, "Customer Number 2", 2, 38000.00);
    public final static BasicPolicyDto LifePolicyDto = AbstractPolicy.policyDtoFactory("Life Policy 30", 30, "Customer Number 3", 3, 60);

    @InjectMocks
    private PolicyService               policyService;

    @Mock
    private PolicyEventPublisherImpl    mockPolicyEventPublisher;

    @Mock
    private PolicyDaoImpl               mockPolicyDao;

    @Mock
    private ApplicationEventPublisher   mockAppEventPublisher;

    //Test Policy Members
    private List<AbstractPolicy>        lstPolicies;
    private HomePolicy                  homePolicy;
    private AutoPolicy                  autoPolicy;
    private LifePolicy                  lifePolicy;

    // Test PolicyIssue Event Members
    private AutoPolicyIssueEvent        autoPolicyIssueEvent;
    private AutoPolicyIssueEvent        homePolicyIssueEvent;
    private AutoPolicyIssueEvent        lifePolicyIssueEvent;

    //***********************************************
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        this.lstPolicies = new ArrayList<AbstractPolicy>();

        // Create the Policy Objects for Auto,Home,Life
        homePolicy = new HomePolicy(HomePolicyDto);
        homePolicy.setPolicyPremium(homePolicy.calculatePremium());

        autoPolicy = new AutoPolicy(AutoPolicyDto);
        autoPolicy.setPolicyPremium(autoPolicy.calculatePremium());

        lifePolicy = new LifePolicy(LifePolicyDto);
        lifePolicy.setPolicyPremium(lifePolicy.calculatePremium());

        // Add to list
        lstPolicies.add(homePolicy);
        lstPolicies.add(autoPolicy);
        lstPolicies.add(lifePolicy);

        // Policy Events
        autoPolicyIssueEvent = new AutoPolicyIssueEvent(mockPolicyEventPublisher,autoPolicy);
        homePolicyIssueEvent = new AutoPolicyIssueEvent(mockPolicyEventPublisher,homePolicy);
        lifePolicyIssueEvent = new AutoPolicyIssueEvent(mockPolicyEventPublisher,lifePolicy);

    }

    @Test
    public void testCreatePolicies() {
        // Set expectation
        when(mockPolicyDao.createPolicy("Auto")).thenReturn(autoPolicy);
        when(mockPolicyDao.createPolicy("Home")).thenReturn(homePolicy);
        when(mockPolicyDao.createPolicy("Life")).thenReturn(lifePolicy);

        // Actuals
        AbstractPolicy actualAutoPolicy = policyService.createPolicy("Auto");
        AbstractPolicy actualHomePolicy = policyService.createPolicy("Home");
        AbstractPolicy actualLifePolicy = policyService.createPolicy("Life");

        Assert.assertTrue(actualAutoPolicy != null);
        Assert.assertTrue(actualHomePolicy != null);
        Assert.assertTrue(actualLifePolicy != null);

        Assert.assertTrue(actualAutoPolicy.getPolicyId() > 0);
        Assert.assertTrue(actualHomePolicy.getPolicyId() > 0);
        Assert.assertTrue(actualLifePolicy.getPolicyId() > 0);

        Assert.assertTrue("Error- AutoPolicy ID - Actual does not match mock instance",
                            (autoPolicy.getPolicyId() == actualAutoPolicy.getPolicyId()) );

        Assert.assertTrue("Error- AutoPolicy Name - Actual does not match mock instance",
                StringUtils.equals(autoPolicy.getPolicyName(),actualAutoPolicy.getPolicyName()));

        Assert.assertTrue("Error- HomePolicy ID - Actual does not match mock instance",
                (homePolicy.getPolicyId() == actualHomePolicy.getPolicyId()) );

        Assert.assertTrue("Error- HomePolicy Name - Actual does not match mock instance",
                StringUtils.equals(homePolicy.getPolicyName(),actualHomePolicy.getPolicyName()));

        Assert.assertTrue("Error- LifePolicy ID - Actual does not match mock instance",
                (autoPolicy.getPolicyId() == actualAutoPolicy.getPolicyId()) );

        Assert.assertTrue("Error- LifePolicy Name - Actual does not match mock instance",
                StringUtils.equals(lifePolicy.getPolicyName(),actualLifePolicy.getPolicyName()));
    }

    @Test
    public void testPublishPolicyIssueEvent() {
        when(mockPolicyEventPublisher.publishPolicyEvent(autoPolicy)).thenReturn(autoPolicyIssueEvent);
        when(mockPolicyEventPublisher.publishPolicyEvent(homePolicy)).thenReturn(homePolicyIssueEvent);
        when(mockPolicyEventPublisher.publishPolicyEvent(lifePolicy)).thenReturn(lifePolicyIssueEvent);

        // actual call
        PolicyIssueEvent actualAutoIssueEvent = policyService.publishPolicyIssueEvent(autoPolicy);
        PolicyIssueEvent actualHomeIssueEvent = policyService.publishPolicyIssueEvent(homePolicy);
        PolicyIssueEvent actualLifeIssueEvent = policyService.publishPolicyIssueEvent(lifePolicy);

        // No nulls please
        Assert.assertNotNull("Error - AutoPolicyIssueEvent is null",actualAutoIssueEvent);
        Assert.assertNotNull("Error - HomePolicyIssueEvent is null",actualHomeIssueEvent);
        Assert.assertNotNull("Error - LifePolicyIssueEvent is null",actualLifeIssueEvent);

        // Validate Policy ID was assigned
        assertThat(actualAutoIssueEvent.getPolicy().getPolicyId(),not(0));
        assertThat(actualHomeIssueEvent.getPolicy().getPolicyPremium(), not(0));
        assertThat(actualLifeIssueEvent.getPolicy().getPolicyPremium(), not(0));

        //Validate Type
        assertThat(actualAutoIssueEvent.getEventType(),is(autoPolicyIssueEvent.getEventType()));
        assertThat(actualHomeIssueEvent.getEventType(),is(homePolicyIssueEvent.getEventType()));
        assertThat(actualLifeIssueEvent.getEventType(),is(lifePolicyIssueEvent.getEventType()));

        // Validate Prem assignment
        assertThat(actualAutoIssueEvent.getPolicy().getPolicyPremium(), is(autoPolicyIssueEvent.getPolicy().getPolicyPremium()));
        assertThat(actualHomeIssueEvent.getPolicy().getPolicyPremium(), is(homePolicyIssueEvent.getPolicy().getPolicyPremium()));
        assertThat(actualLifeIssueEvent.getPolicy().getPolicyPremium(), is(lifePolicyIssueEvent.getPolicy().getPolicyPremium()));

        // Validate assigned Customer
        assertThat(actualAutoIssueEvent.getPolicy().getPolicyCustomerName(), is(autoPolicyIssueEvent.getPolicy().getPolicyCustomerName()));
        assertThat(actualHomeIssueEvent.getPolicy().getPolicyCustomerName(), is(homePolicyIssueEvent.getPolicy().getPolicyCustomerName()));
        assertThat(actualLifeIssueEvent.getPolicy().getPolicyCustomerName(), is(lifePolicyIssueEvent.getPolicy().getPolicyCustomerName()));

    }

}
