package nz.ac.auckland.cer.jobaudit.pojo;

public class StatisticsFormData {

    String category;
    String categoryChoice;
    Integer firstMonth;
    Integer lastMonth;
    Integer firstYear;
    Integer lastYear;

    public String getCategory() {

        return category;
    }

    public void setCategory(
            String category) {

        this.category = category;
    }

    public String getCategoryChoice() {

        return categoryChoice;
    }

    public void setCategoryChoice(
            String categoryChoice) {

        this.categoryChoice = categoryChoice;
    }

    public Integer getFirstMonth() {

        return firstMonth;
    }

    public void setFirstMonth(
            Integer firstMonth) {

        this.firstMonth = firstMonth;
    }

    public Integer getLastMonth() {

        return lastMonth;
    }

    public void setLastMonth(
            Integer lastMonth) {

        this.lastMonth = lastMonth;
    }

    public Integer getFirstYear() {

        return firstYear;
    }

    public void setFirstYear(
            Integer firstYear) {

        this.firstYear = firstYear;
    }

    public Integer getLastYear() {

        return lastYear;
    }

    public void setLastYear(
            Integer lastYear) {

        this.lastYear = lastYear;
    }

}
