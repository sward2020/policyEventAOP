/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.aop;

import org.junit.*;

import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.AutoPolicyIssueEvent;
import sample.aop.event.PolicyEventPublisherImpl;
import sample.aop.service.PolicyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyAopApplicationTest {

	public static final String[] VALID_POLICY_TYPES = new String[] { "Auto", "Home", "Life" };
	public static final String[] INVALID_POLICY_TYPES = new String[] { "Auto", "Home", "DINO" };

	public final static BasicPolicyDto HomePolicyDto = AbstractPolicy.policyDtoFactory("Home Policy 10", 10, "Customer Number 1", 1, 450000.00);
	public final static BasicPolicyDto AutoPolicyDto = AbstractPolicy.policyDtoFactory("Auto Policy 20", 20, "Customer Number 2", 2, 38000.00);
	public final static BasicPolicyDto LifePolicyDto = AbstractPolicy.policyDtoFactory("Life Policy 30", 30, "Customer Number 3", 3, 60);

	@InjectMocks
	private PolicyAopApplication		app;

	@Mock
	private PolicyService 				mockPolicyService;

	@Mock
	private PolicyEventPublisherImpl 	mockPolicyEventPublisher;

	@Autowired
	private ApplicationContext 			context;

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	private List<AbstractPolicy> 		lstPolicies;
	private HomePolicy					homePolicy;
	private AutoPolicy					autoPolicy;
	private LifePolicy					lifePolicy;

	// Test PolicyIssue Event Members
	private AutoPolicyIssueEvent 		autoPolicyIssueEvent;
	private AutoPolicyIssueEvent        homePolicyIssueEvent;
	private AutoPolicyIssueEvent        lifePolicyIssueEvent;

	//***********************************************
	@Before
	public void init() {
		app = context.getBean(PolicyAopApplication.class);
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
		when(mockPolicyService.createPolicy("Auto")).thenReturn(autoPolicy);
		when(mockPolicyService.createPolicy("Home")).thenReturn(homePolicy);
		when(mockPolicyService.createPolicy("Life")).thenReturn(lifePolicy);

		List<AbstractPolicy> lstActual = app.createPolicies(VALID_POLICY_TYPES);

		Assert.assertTrue(lstActual != null);
		Assert.assertFalse(lstActual.isEmpty());
		Assert.assertEquals(3, lstActual.size());

		for (AbstractPolicy entry : lstActual) {
			Assert.assertTrue("Error-Expected that premimum attribute not 0", entry.getPolicyPremium() > 0.0);
			if (entry instanceof AutoPolicy)
				assertThat(entry.getPolicyId() == autoPolicy.getPolicyId());
			else if (entry instanceof HomePolicy)
				assertThat(entry.getPolicyId() == homePolicy.getPolicyId());
			else
				assertThat(entry.getPolicyId() == lifePolicy.getPolicyId());
		}
	}

	@Test
	public void testPublishPolicy() {
		when(mockPolicyService.publishPolicyIssueEvent(autoPolicy)).thenReturn(autoPolicyIssueEvent);
		when(mockPolicyService.publishPolicyIssueEvent(homePolicy)).thenReturn(homePolicyIssueEvent);
		when(mockPolicyService.publishPolicyIssueEvent(lifePolicy)).thenReturn(lifePolicyIssueEvent);

		// actual call
		app.publishPolicy(lstPolicies);

		//Assert.assertTrue(app.getPolicyService().getPolicyIssueEvents().size() == 3);
	}


	@Test
	public void testNoCommandLineArgs() throws Exception {
		exit.expectSystemExitWithStatus(-1);
		PolicyAopApplication.main(new String[0]);
	}

	@Test
	public void testCommandLineIllegalValue() throws Exception {
		exit.expectSystemExitWithStatus(-2);
		PolicyAopApplication.main(INVALID_POLICY_TYPES);
	}

	@Test
	public void testCommandLineLegalValues() throws Exception {
		exit.expectSystemExitWithStatus(0);
		PolicyAopApplication.main(VALID_POLICY_TYPES);
	}



}
