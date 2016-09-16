package sample.aop.domain;

import sample.aop.dto.BasicPolicyDto;

/**
 * Created by n0292928 on 8/31/16.
 */
public abstract class AbstractPolicy {

    public static final String[] VALID_POLICIES = {"Auto","Home", "Life"};

    private int         policyId;
    private String      policyName;
    private String      policyStatus;
    private String      policyCustomerName;
    private int         policyCustomerId;
    private double      policyAssetValue;
    private double      policyBasePremium;
    private double      policyPremium;

    public double getPolicyAssetValue() {
        return policyAssetValue;
    }

    public AbstractPolicy(BasicPolicyDto parmDto) {
        this.policyId = parmDto.getPolicyId();
        this.policyName = parmDto.getPolicyName();
        this.policyStatus = "Policy Created";
        this.policyCustomerName = parmDto.getCustomerName();
        this.policyCustomerId = parmDto.getCustomerId();
        this.policyAssetValue = parmDto.getAssetValue();
    }

    public abstract double calculatePremium();

    public double getPolicyPremium() { return policyPremium;  }
    public void setPolicyPremium(double parmPrem) { this.policyPremium= parmPrem; }

    public String getPolicyCustomerName() {
        return policyCustomerName;
    }
    public int getPolicyCustomerId() {
        return policyCustomerId;
    }
    public int getPolicyId() {
        return policyId;
    }
    public String getPolicyName() { return policyName; }

    public String getPolicyStatus() {  return policyStatus;  }
    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public double getPolicyBasePremium() {  return policyBasePremium; }
    public void setPolicyBasePremium(double policyBasePremium) {  this.policyBasePremium = policyBasePremium;   }

    //********************************

    public static BasicPolicyDto policyDtoFactory(final String parmPolicyName, int parmPolicyId, final String parmCustomerName,
                                                  int parmCustomerId, double parmAssetValue) {
        BasicPolicyDto policyDto = new BasicPolicyDto();
        policyDto.setPolicyId(parmPolicyId);
        policyDto.setPolicyName(parmPolicyName);
        policyDto.setCustomerName(parmCustomerName);
        policyDto.setCustomerId(parmCustomerId);
        policyDto.setAssetValue(parmAssetValue);

        return policyDto;
    }

}
