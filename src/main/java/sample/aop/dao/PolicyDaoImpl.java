package sample.aop.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sample.aop.domain.AbstractPolicy;
import sample.aop.domain.AutoPolicy;
import sample.aop.domain.HomePolicy;
import sample.aop.domain.LifePolicy;
import sample.aop.dto.BasicPolicyDto;
import sample.aop.event.PolicyIssueEvent;

import java.util.*;

@Repository("policyDao")
public class PolicyDaoImpl implements IPolicyDao {

        static final Logger logger = LoggerFactory.getLogger(PolicyDaoImpl.class);

        private Set<PolicyIssueEvent> repoPolicyEvents;
        public static final String DROP_POLICY_TABLE 	= "DROP TABLE Policy IF EXISTS";
        public static final String	CREATE_POLICY_TABLE = "CREATE TABLE policy(policyId SMALLINT, policyName VARCHAR(128), policyStatus VARCHAR(128)," +
                                                  " policyCustomerName VARCHAR(128), policyCustomerId SMALLINT, policyAssetValue DECIMAL(10,2)," +
                                                  " policyBasePremium DECIMAL(10,2), policyPremium DECIMAL(10,2) )";

        static final String	INSERT_INTO_POLICY_TABLE = "INSERT INTO policy(policyId,policyName,policyStatus,policyCustomerName," +
                                                        " policyCustomerId, policyAssetValue, policyBasePremium, policyPremium ) " +
                                                        " VALUES (?,?,?,?,?,?,?,?) ";

        @Autowired
        private JdbcTemplate            jdbcTemplate;

        public PolicyDaoImpl() {
            this.repoPolicyEvents = new HashSet<PolicyIssueEvent>();
         }
        public Set<PolicyIssueEvent> getRepoPolicyEvents() { return repoPolicyEvents;  }

        public AbstractPolicy createPolicy(final String parmType)  {
            AbstractPolicy  objPolicy=null;

            logger.info("In PolicyDaoImpl.createPolicy..Input Arg Type = [{}]",parmType);

            if ( parmType.equalsIgnoreCase("Home") )
                objPolicy = this.createHomePolicy();
            else if (parmType.equalsIgnoreCase("Auto"))
                objPolicy = this.createAutoPolicy();
            else if (parmType.equalsIgnoreCase("Life"))
                objPolicy = this.createLifePolicy();

            logger.info("Policy [{}] Created.",parmType);
            return objPolicy;
        }

        @Transactional( propagation = Propagation.REQUIRES_NEW, rollbackForClassName = {"Exception"})
        public void persistPolicyEvent(final Object parmPolicyEvent) throws IllegalArgumentException {
            PolicyIssueEvent   event;

            if ( parmPolicyEvent != null && parmPolicyEvent instanceof PolicyIssueEvent) {
                event = (PolicyIssueEvent)parmPolicyEvent;
                repoPolicyEvents.add(event);

                //Insert Policy Rec
                jdbcTemplate.update(INSERT_INTO_POLICY_TABLE,new Object[]{	event.getPolicy().getPolicyId(),
                        event.getPolicy().getPolicyName(),
                        event.getPolicy().getPolicyStatus(),
                        event.getPolicy().getPolicyCustomerName(),
                        event.getPolicy().getPolicyCustomerId(),
                        event.getPolicy().getPolicyAssetValue(),
                        event.getPolicy().getPolicyBasePremium(),
                        event.getPolicy().getPolicyPremium()
                });

                // Get Record Count
                if ( this.getRecordCount() > 0 ) {
                    this.logRecordDetail();
                }

            } else {
                logger.error("Received Policy Event is of wrong type. [{}] ", parmPolicyEvent.getClass().getName());

            }
            throw new IllegalArgumentException("Received Policy Event is of wrong type. Type = " + parmPolicyEvent.getClass().getName());
            // debug what is in event
            //logger.info("Success. Houston the policy event has been queued. Policy Event = [{}] ", event);
            //logger.info("Policy Name = [{}] ", event.getPolicy().getPolicyName());

        }


        private int getRecordCount() {
            logger.info("Querying for policy records");
            int nCount = jdbcTemplate.queryForObject("Select count(*) from policy", Integer.class);
            logger.info("Count of Inserted Policy Records = [{}]",nCount);

            return nCount;
        }

        private void logRecordDetail() {
            List<Map<String,Object>> lstResults = jdbcTemplate.queryForList("SELECT policyId,policyName,policyStatus,policyCustomerName,policyCustomerId, " +
                                                                           " policyAssetValue, policyBasePremium, policyPremium FROM policy");

            for ( Map<String,Object> policyRec : lstResults) {
                for ( String key : policyRec.keySet() ) {
                    logger.info("Column Name = [{}]. Column Value = [{}] ", key, policyRec.get(key));
                }
            }
        }

        // utility methods
        private HomePolicy createHomePolicy()  {
            HomePolicy homePolicy = new HomePolicy(BasicPolicyDto.HomePolicyDto);
            homePolicy.setPolicyPremium(homePolicy.calculatePremium());
            return homePolicy;
        }

        private AutoPolicy createAutoPolicy() {
            AutoPolicy autoPolicy = new AutoPolicy(BasicPolicyDto.AutoPolicyDto);
            autoPolicy.setPolicyPremium(autoPolicy.calculatePremium());
            return autoPolicy;
        }

        private LifePolicy createLifePolicy() {
            LifePolicy lifePolicy = new LifePolicy(BasicPolicyDto.LifePolicyDto);
            lifePolicy.setPolicyPremium(lifePolicy.calculatePremium());
            return lifePolicy;

        }

}
