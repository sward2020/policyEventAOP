package sample.aop.event;

import org.springframework.context.ApplicationEvent;
import sample.aop.domain.AbstractPolicy;

/**
 * Created by n0292928 on 8/31/16.
 */
public class PolicyIssueEvent extends ApplicationEvent {
    private static final long   serialId = 1L;

    private String              eventType;
    private AbstractPolicy      policy;

    public PolicyIssueEvent(Object source) {
        super(source);
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public AbstractPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(AbstractPolicy policy) {
        this.policy = policy;
    }

}
