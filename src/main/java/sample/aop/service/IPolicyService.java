package sample.aop.service;

import sample.aop.domain.AbstractPolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.PolicyIssueEvent;

import java.util.Set;

/**
 * Created by n0292928 on 9/2/16.
 */
public interface  IPolicyService {

    public static final String STARS = "************************************";

    public AbstractPolicy createPolicy(final String parmPolicyType);
    public PolicyIssueEvent publishPolicyIssueEvent(final AbstractPolicy parmPolicy);
    public Set<PolicyIssueEvent> getPolicyIssueEvents();
}
