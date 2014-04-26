package nz.ac.auckland.cer.jobaudit.pojo;

public class BarDiagramStatistics {

    private Integer bottom;
    private Integer top;
    private Integer serial_jobs;
    private Integer parallel_jobs;
    private Float serial_core_hours;
    private Float parallel_core_hours;
    private Float avg_waiting_hours;

    public Integer getBottom() {

        return bottom;
    }

    public void setBottom(
            Integer bottom) {

        this.bottom = bottom;
    }

    public Float getSerial_core_hours() {

        return serial_core_hours;
    }

    public void setSerial_core_hours(
            Float serial_core_hours) {

        this.serial_core_hours = serial_core_hours;
    }

    public Float getParallel_core_hours() {

        return parallel_core_hours;
    }

    public void setParallel_core_hours(
            Float parallel_core_hours) {

        this.parallel_core_hours = parallel_core_hours;
    }

    public Integer getTop() {

        return top;
    }

    public void setTop(
            Integer top) {

        this.top = top;
    }

    public Integer getSerial_jobs() {

        return serial_jobs;
    }

    public void setSerial_jobs(
            Integer serial_jobs) {

        this.serial_jobs = serial_jobs;
    }

    public Integer getParallel_jobs() {

        return parallel_jobs;
    }

    public void setParallel_jobs(
            Integer parallel_jobs) {

        this.parallel_jobs = parallel_jobs;
    }

    public Float getAvg_waiting_hours() {

        return avg_waiting_hours;
    }

    public void setAvg_waiting_hours(
            Float avg_waiting_hours) {

        this.avg_waiting_hours = avg_waiting_hours;
    }
}
