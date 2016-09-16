package sample.aop.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import sample.aop.domain.AbstractPolicy;

public class HomePolicyIssueEvent extends PolicyIssueEvent implements IPolicyEvent {

    static final Logger logger = LoggerFactory.getLogger(HomePolicyIssueEvent.class);

    public HomePolicyIssueEvent(final Object parmSource, final AbstractPolicy parmPolicy)
    {
        super(parmSource);
        super.setEventType("HomePolicyIssued");
        super.setPolicy(parmPolicy);
    }

    public String getEventType() {  return super.getEventType();  }
    public AbstractPolicy getPolicy() { return super.getPolicy(); }
}
