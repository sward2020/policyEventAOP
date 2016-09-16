/*
 * Copyright 2012-2013 the original author or authors.
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

package sample.aop.eventMonitor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sample.aop.dao.IPolicyDao;
import sample.aop.event.PolicyIssueEvent;

@Aspect
@Component
public class PolicyEventMonitor {

	private static final Logger logger = LoggerFactory.getLogger(PolicyEventMonitor.class);

	@Autowired
	private IPolicyDao		 		policyDao;


	@AfterReturning("execution(* sample.aop.event.PolicyEventListenerImpl.onApplicationEvent(..))")
	public void policyEventProcessed(JoinPoint parmJoinPoint) {

		logger.info("************************");
		logger.info("In AOP Intercept - After exec of the onApplicationEvent");
		logger.info("onApplicationEvent in PolicyEventListener just executed!");
		logger.info("Intercepted Method [{}] : ", parmJoinPoint.getSignature().getName());
		logger.info("Policy Issue Event Type [{}] : ", parmJoinPoint.getArgs()[0]);

		PolicyIssueEvent policyEvent  = (PolicyIssueEvent) parmJoinPoint.getArgs()[0];
		logger.info("Sending PolicyIssueEvent with Policy [{}] to queue...", policyEvent.getPolicy().getPolicyName());
		logger.info("************************");

		try{
			policyDao.persistPolicyEvent(policyEvent);
		} catch(Exception ex ) {
			logger.error("Exception Caught from DAO layer");
		}
	}

}
