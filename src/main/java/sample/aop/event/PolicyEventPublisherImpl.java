package sample.aop.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sample.aop.dao.PolicyDaoImpl;
import sample.aop.domain.AbstractPolicy;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;

/**
 * Created by n0292928 on 8/31/16.
 */
@Component("policyEventPublisher")
public class PolicyEventPublisherImpl  {
    static final Logger logger = LoggerFactory.getLogger(PolicyEventPublisherImpl.class);

    @Autowired
    private ApplicationEventPublisher   appEventPublisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        appEventPublisher = publisher;
    }


    // Create Event and Policy Object and Published to all registered event listeners who care
    public PolicyIssueEvent publishPolicyEvent(final AbstractPolicy parmPolicy)
    {
        PolicyIssueEvent event=null;
        logger.info("In PolicyEventPublisherImpl:publishPolicyEvent. Policy Name [{}] ..Status [{}} ",
                                                                     parmPolicy.getPolicyName(),parmPolicy.getPolicyStatus());

        event = this.createEvent(parmPolicy);
        logger.info("Event Created");

        // Publish
        appEventPublisher.publishEvent(event);

        logger.info("Event Published....done");
        return event;
    }

    private PolicyIssueEvent createEvent(final AbstractPolicy parmPolicy) {
        PolicyIssueEvent   event = null;

        logger.info("Creating Policy Issue Event for [{}]",parmPolicy.getPolicyCustomerName());

        if ( parmPolicy instanceof AutoPolicy) {
            event = new AutoPolicyIssueEvent(this,parmPolicy);
        } else if ( parmPolicy instanceof HomePolicy) {
            event = new HomePolicyIssueEvent(this,parmPolicy);
        } else if ( parmPolicy instanceof LifePolicy) {
            event = new LifePolicyIssueEvent(this,parmPolicy);
        }
        return event;
    }
}

