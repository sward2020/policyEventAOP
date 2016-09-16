package sample.aop.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import sample.aop.domain.AbstractPolicy;

/**
 * Created by n0292928 on 8/31/16.
 */
public class LifePolicyIssueEvent extends PolicyIssueEvent implements IPolicyEvent {

    static final Logger logger = LoggerFactory.getLogger(LifePolicyIssueEvent.class);

    public LifePolicyIssueEvent(final Object parmSource,final AbstractPolicy parmPolicy)
    {
        super(parmSource);
        super.setEventType("LifePolicyIssued");
        super.setPolicy(parmPolicy);
    }

    public String getEventType() {  return super.getEventType();  }
    public AbstractPolicy getPolicy() { return super.getPolicy(); }
}
