package nz.ac.auckland.cer.jobaudit.pojo;

public class AuditRecordFormData {

    private String accountName;
    private String orderBy;
    private String sortOrder;

    public String getAccountName() {

        return accountName;
    }

    public void setAccountName(
            String accountName) {

        this.accountName = accountName;
    }

    public String getOrderBy() {

        return orderBy;
    }

    public void setOrderBy(
            String orderBy) {

        this.orderBy = orderBy;
    }

    public String getSortOrder() {

        return sortOrder;
    }

    public void setSortOrder(
            String sortOrder) {

        this.sortOrder = sortOrder;
    }

}
