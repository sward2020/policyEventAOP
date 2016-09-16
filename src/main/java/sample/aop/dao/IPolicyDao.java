package sample.aop.dao;

import com.sun.xml.internal.rngom.parse.host.Base;
import sample.aop.domain.AbstractPolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.PolicyIssueEvent;

import java.util.Set;

/**
 * Created by n0292928 on 8/31/16.
 */
public interface IPolicyDao {

    public AbstractPolicy createPolicy(final String parmType);
    public void persistPolicyEvent(final Object parmPolicyEvent);
    public Set<PolicyIssueEvent> getRepoPolicyEvents();
}
