package nz.ac.auckland.cer.jobaudit.pojo;

public class AuditRecordFormData {

    private String user;
    private String orderBy;
    private String sortOrder;

    public String getUser() {

        return user;
    }

    public void setUser(
            String user) {

        this.user = user;
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
