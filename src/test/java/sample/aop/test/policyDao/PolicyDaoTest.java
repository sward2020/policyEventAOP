package sample.aop.test.policyDao;

/**
 * Created by n0292928 on 9/5/16.
 */
import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;
import sample.aop.dao.IPolicyDao;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.*;
import java.util.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.StringContains.*;
import static org.junit.Assert.assertThat;


/**
 * Created by n0292928 on 9/2/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyDaoTest {

    @Autowired
    private PolicyDaoImpl               policyDao;

    @Mock
    private PolicyEventPublisherImpl    mockPolicyEventPublisher;


    public final static String POLICY_TYPE_HOME = "Home";
    public final static String POLICY_TYPE_AUTO = "Auto";
    public final static String POLICY_TYPE_LIFE = "Life";

    public final static List<String> POLICY_ISSUE_EVENT_TYPES = Arrays.asList("AutoPolicyIssued","HomePolicyIssued", "LifePolicyIssued");
    public final static BasicPolicyDto HomePolicyDto = AbstractPolicy.policyDtoFactory("Home Policy 10", 10, "Customer Number 1", 1, 450000.00);
    public final static BasicPolicyDto AutoPolicyDto = AbstractPolicy.policyDtoFactory("Auto Policy 20", 20, "Customer Number 2", 2, 38000.00);
    public final static BasicPolicyDto LifePolicyDto = AbstractPolicy.policyDtoFactory("Life Policy 30", 30, "Customer Number 3", 3, 60);


    //Test Policy Members
    private HomePolicy              homePolicy;
    private AutoPolicy              autoPolicy;
    private LifePolicy              lifePolicy;

    // Test PolicyIssue Event Members
    private AutoPolicyIssueEvent    autoPolicyIssueEvent;
    private HomePolicyIssueEvent    homePolicyIssueEvent;
    private LifePolicyIssueEvent    lifePolicyIssueEvent;

    //***********************************************
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        // Create the Policy Objects for Auto,Home,Life
        homePolicy = new HomePolicy(HomePolicyDto);
        homePolicy.setPolicyPremium(homePolicy.calculatePremium());

        autoPolicy = new AutoPolicy(AutoPolicyDto);
        autoPolicy.setPolicyPremium(autoPolicy.calculatePremium());

        lifePolicy = new LifePolicy(LifePolicyDto);
        lifePolicy.setPolicyPremium(lifePolicy.calculatePremium());

        // Policy Events
        autoPolicyIssueEvent = new AutoPolicyIssueEvent(mockPolicyEventPublisher, autoPolicy);
        homePolicyIssueEvent = new HomePolicyIssueEvent(mockPolicyEventPublisher, homePolicy);
        lifePolicyIssueEvent = new LifePolicyIssueEvent(mockPolicyEventPublisher, lifePolicy);

    }

    @Test
    public void testCreatePolicies() {
        // Create the 3 policy types
        AbstractPolicy localHomePolicy =  policyDao.createPolicy(this.POLICY_TYPE_HOME);
        AbstractPolicy localAutoPolicy =  policyDao.createPolicy(this.POLICY_TYPE_AUTO);
        AbstractPolicy localLifePolicy =  policyDao.createPolicy(this.POLICY_TYPE_LIFE);

        homePolicy.setPolicyPremium(homePolicy.calculatePremium());
        autoPolicy.setPolicyPremium(autoPolicy.calculatePremium());
        lifePolicy.setPolicyPremium(lifePolicy.calculatePremium());

        // Is assumes equal...local ( actual) and expected
        assertThat(localHomePolicy.getPolicyName(), is(homePolicy.getPolicyName()));
        assertThat(localAutoPolicy.getPolicyName(), is(autoPolicy.getPolicyName()));
        assertThat(localLifePolicy.getPolicyName(), is(lifePolicy.getPolicyName()));

        assertThat(localHomePolicy.getPolicyPremium(), is(homePolicy.getPolicyPremium()));
        assertThat(localAutoPolicy.getPolicyPremium(), is(autoPolicy.getPolicyPremium()));
        assertThat(localLifePolicy.getPolicyPremium(), is(lifePolicy.getPolicyPremium()));

    }
    @Test
    public void testPersistPolicyEvent() {
        policyDao.persistPolicyEvent(autoPolicyIssueEvent);
        policyDao.persistPolicyEvent(homePolicyIssueEvent);
        policyDao.persistPolicyEvent(lifePolicyIssueEvent);

        assertThat(policyDao.getRepoPolicyEvents().size(), is(3));
        assertThat(policyDao.getRepoPolicyEvents().isEmpty(), is(false));

        Iterator<PolicyIssueEvent> eventIterator = policyDao.getRepoPolicyEvents().iterator();

        while (eventIterator.hasNext()) {
            PolicyIssueEvent event = eventIterator.next();
            Assert.assertTrue(event instanceof ApplicationEvent);

            Assert.assertTrue("Error - Unexpected Policy Event Type = " + event.getEventType(),
                    POLICY_ISSUE_EVENT_TYPES.contains(event.getEventType()));

            Assert.assertNotNull(event.getPolicy());
            Assert.assertTrue(event.getPolicy().getPolicyPremium() > 0.0);

            if ( event instanceof AutoPolicyIssueEvent ) {
                Assert.assertTrue("Auto PolicyEvents do not match", autoPolicyIssueEvent.getEventType().equalsIgnoreCase(event.getEventType()));
                Assert.assertTrue(autoPolicyIssueEvent.getPolicy().getPolicyName().equalsIgnoreCase(event.getPolicy().getPolicyName()));
                Assert.assertTrue(autoPolicyIssueEvent.getPolicy().getPolicyId() == event.getPolicy().getPolicyId());
                Assert.assertTrue(autoPolicyIssueEvent.getPolicy().getPolicyPremium() == event.getPolicy().getPolicyPremium());
            } else if ( event instanceof HomePolicyIssueEvent ) {
                Assert.assertTrue("Home PolicyEvents do not match", homePolicyIssueEvent.getEventType().equalsIgnoreCase(event.getEventType()));
                Assert.assertTrue(homePolicyIssueEvent.getPolicy().getPolicyName().equalsIgnoreCase(event.getPolicy().getPolicyName()));
                Assert.assertTrue(homePolicyIssueEvent.getPolicy().getPolicyId() == event.getPolicy().getPolicyId());
                Assert.assertTrue(homePolicyIssueEvent.getPolicy().getPolicyPremium() == event.getPolicy().getPolicyPremium());
            } else if ( event instanceof LifePolicyIssueEvent ) {
                Assert.assertTrue("Life PolicyEvents do not match", lifePolicyIssueEvent.getEventType().equalsIgnoreCase(event.getEventType()));
                Assert.assertTrue(lifePolicyIssueEvent.getPolicy().getPolicyName().equalsIgnoreCase(event.getPolicy().getPolicyName()));
                Assert.assertTrue(lifePolicyIssueEvent.getPolicy().getPolicyId() == event.getPolicy().getPolicyId());
                Assert.assertTrue(lifePolicyIssueEvent.getPolicy().getPolicyPremium() == event.getPolicy().getPolicyPremium());
            }
        }
    }
}