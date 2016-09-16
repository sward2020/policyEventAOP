package sample.aop.dto;

import sample.aop.domain.AbstractPolicy;

/**
 * Created by n0292928 on 9/5/16.
 */
public class BasicPolicyDto     {

    public static final BasicPolicyDto HomePolicyDto = AbstractPolicy.policyDtoFactory("Home Policy 10",10,"Customer Number 1", 1, 450000.00);
    public static final BasicPolicyDto AutoPolicyDto = AbstractPolicy.policyDtoFactory("Auto Policy 20",20,"Customer Number 2", 2, 38000.00);
    public static final BasicPolicyDto LifePolicyDto = AbstractPolicy.policyDtoFactory("Life Policy 30",30,"Customer Number 3", 3, 60);

    private String      policyName;
    private String      customerName;
    private int         policyId;
    private int         customerId;
    private double      assetValue;

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(double assetValue) {
        this.assetValue = assetValue;
    }

}
