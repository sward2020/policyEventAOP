/*
 * Copyright 2012-2015 the original author or authors.
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.PolicyIssueEvent;
import sample.aop.service.IPolicyService;
import sample.aop.service.PolicyService;

import java.util.*;

@EnableTransactionManagement
@SpringBootApplication
public class PolicyAopApplication implements CommandLineRunner {
	static final Logger logger = LoggerFactory.getLogger(PolicyAopApplication.class);

	@Autowired
	private JdbcTemplate		jdbcTemplate;

	@Autowired
	private PolicyService policyService;

	public PolicyService getPolicyService() {
		return policyService;
	}


	public List<AbstractPolicy>  createPolicies( String[] parmPolicyType) {
		List<AbstractPolicy> lstPolicy = new ArrayList<AbstractPolicy>();

		// Load em up...and create the POLICY objects....now!
		for ( String policyType : parmPolicyType ) {
			lstPolicy.add(policyService.createPolicy(policyType));
		}
		return lstPolicy;
	}

	public void publishPolicy(List<AbstractPolicy> parmLstPolicy) {
		for (AbstractPolicy policyEntry : parmLstPolicy) {
			try {
				logger.info("Couple secs delay between Policy Pub Event");
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				logger.error("Someone interrupted my sleep...{[]}", ex.getMessage());
			}
			policyService.publishPolicyIssueEvent(policyEntry);
		}
	}

	@Override
	public void run(String[] args) {

		logger.info("Received cmd line args (policy types) [{}]",args);
		// Execute H2 DDL
		this.executePolicyTableDDL();

		List<AbstractPolicy> lstPolicies = this.createPolicies(args);
		logger.info("Created auto, home, life policy objects...Publish each next..");

		publishPolicy(lstPolicies);

		logger.info("START - List of All Published Policy Issue Events ");

		// List out the objects in the "queue"

		if (this.policyService.getPolicyIssueEvents().size() > 0) {
			for(Object  entry  : this.policyService.getPolicyIssueEvents().toArray() ) {
				logger.info(IPolicyService.STARS);
				logger.info("Policy Event Type [{}] ", ((PolicyIssueEvent)entry).getEventType());
				logger.info("Policy Event Source [{}] ", ((PolicyIssueEvent)entry).getSource());
				logger.info("Policy Name [{}] ", ((PolicyIssueEvent)entry).getPolicy().getPolicyName());
				logger.info("Policy ID [{}] ", ((PolicyIssueEvent)entry).getPolicy().getPolicyId());
				logger.info("Policy Premimum [{}] ", ((PolicyIssueEvent)entry).getPolicy().getPolicyPremium());

				logger.info("Policy Status [{}] ", ((PolicyIssueEvent)entry).getPolicy().getPolicyStatus());
				logger.info("Policy Customer Name [{}] ", ((PolicyIssueEvent)entry).getPolicy().getPolicyCustomerName());
				logger.info("Policy Customer ID [{}] ", ((PolicyIssueEvent)entry).getPolicy().getPolicyCustomerId());
				logger.info(IPolicyService.STARS);
			}
		}

		logger.info("END - List of All Published Policy Issue Events ");
		logger.info("Policy Event Pub Complete");
	}

	public static void main(String[] args) throws Exception {

		logger.info("Start PolicyAopApp");
		// Input Args Validation
		PolicyAopApplication.validateCmdLineArgs(args);
		SpringApplication.run(PolicyAopApplication.class, args);
		logger.info("End PolicyAopApp");

		System.exit(0);
	}

	//Utility Methods
	private void executePolicyTableDDL() {
		logger.info("Start CREATE Policy Table");
		jdbcTemplate.execute(PolicyDaoImpl.DROP_POLICY_TABLE);
		jdbcTemplate.execute(PolicyDaoImpl.CREATE_POLICY_TABLE);
		logger.info("END CREATE Policy Table");
	}

	private static void validateCmdLineArgs(String[] parmArgs) {
		if ( parmArgs == null || parmArgs.length == 0 ) {
			logger.error("No Required Policy Type Command Line Args...");
			logger.error("Usage Command Line : java -jar sb-policyEvent-aop-1.4.1.BUILD-SNAPSHOT.jar Auto Home Life");
			System.exit(-1);
		}
		// Now Validate
		boolean bValid=true;
		for ( String type : parmArgs ) {
			logger.debug("Policy Type Array Join string [{}] ", StringUtils.join(AbstractPolicy.VALID_POLICIES,StringUtils.SPACE));
			logger.debug("Type parm = [{}]", type);

			if ( !StringUtils.containsIgnoreCase(StringUtils.join(AbstractPolicy.VALID_POLICIES,StringUtils.SPACE), type)) { bValid=false; }
		}

		if (!bValid) {
			logger.error("Illegal Policy Type command line arg provided ...");
			logger.error("Valid Values are [{}] ", String.join(",",AbstractPolicy.VALID_POLICIES));
			System.exit(-2);
		}
	}
}
