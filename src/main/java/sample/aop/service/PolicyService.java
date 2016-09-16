package sample.aop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.aop.dao.IPolicyDao;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.PolicyEventPublisherImpl;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import sample.aop.event.PolicyIssueEvent;

import java.util.Set;

/**
 * Created by n0292928 on 9/1/16.
 */
@Service("policyService")
public class PolicyService implements  IPolicyService {
    static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private PolicyEventPublisherImpl    policyEventPublisher;
    @Autowired
    private IPolicyDao                  policyDao;

    // Create the Policy
    public AbstractPolicy createPolicy(final String parmPolicyType) {
        logger.info("Creating Policy for type [{}] ", parmPolicyType);
        return policyDao.createPolicy(parmPolicyType);
    }

    // Generate PolicyIssueEvent and Publish for the given Policy
    public PolicyIssueEvent publishPolicyIssueEvent(final AbstractPolicy parmPolicy) {
        logger.info(IPolicyService.STARS);
        logger.info("Creating and Publish Event for PolicyName [{}] , ID [{}] ", parmPolicy.getPolicyName(),parmPolicy.getPolicyId());
        return policyEventPublisher.publishPolicyEvent(parmPolicy);
    }

    public Set<PolicyIssueEvent> getPolicyIssueEvents() {
        return policyDao.getRepoPolicyEvents();
    }
}
