package sample.aop.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sample.aop.service.IPolicyService;

/**
 * Created by n0292928 on 8/31/16.
 */
@Component("policyEventListener")
public class PolicyEventListenerImpl implements ApplicationListener<PolicyIssueEvent>
{
    static final Logger logger = LoggerFactory.getLogger(PolicyEventListenerImpl.class);

    // Listen for all PolicyIssue Events
    @Transactional(rollbackForClassName = {"Exception"})
    @Override
    public void onApplicationEvent(PolicyIssueEvent parmPolicyEvent)  {
        logger.info(IPolicyService.STARS);
        logger.info("In onApplication Event for PolicyIssueEvent Listener.");
        logger.info("Policy Event Received = " + parmPolicyEvent.getEventType());
        logger.info("Policy Received = " + parmPolicyEvent.getPolicy().getPolicyName() );
        logger.info("Policy Status = [{}] ", parmPolicyEvent.getPolicy().getPolicyStatus());
        logger.info(IPolicyService.STARS);

        parmPolicyEvent.getPolicy().setPolicyStatus("Policy Issued");
        this.processPolicyIssueEvent(parmPolicyEvent);
    }

    // Just makes App Event handler cleaner
    private void processPolicyIssueEvent(final PolicyIssueEvent parmPolicyEvent) {

        if ( parmPolicyEvent instanceof AutoPolicyIssueEvent) {
            this.processAutoPolicyIssueEventHandler(parmPolicyEvent);
        } else if ( parmPolicyEvent instanceof HomePolicyIssueEvent) {
            this.processHomePolicyIssueEventHandler(parmPolicyEvent);
        } else if ( parmPolicyEvent instanceof LifePolicyIssueEvent ) {
            this.processLifePolicyIssueEventHandler(parmPolicyEvent);
        }
     }

    // Utility Methods
    private void processAutoPolicyIssueEventHandler(final PolicyIssueEvent parmPolicyEvent) {
        AutoPolicyIssueEvent autoPolicyEvent = (AutoPolicyIssueEvent) parmPolicyEvent;
        // Do something Auto Policy Specific..or not

     }

    private void processHomePolicyIssueEventHandler(final PolicyIssueEvent parmPolicyEvent) {
        HomePolicyIssueEvent homePolicyEvent = (HomePolicyIssueEvent) parmPolicyEvent;
        // Do something Home Policy Specific..or not
    }

    private void processLifePolicyIssueEventHandler(final PolicyIssueEvent parmPolicyEvent) {
        LifePolicyIssueEvent lifePolicyEvent = (LifePolicyIssueEvent) parmPolicyEvent;
        // Do something Life Policy Specific..or not
    }

}

