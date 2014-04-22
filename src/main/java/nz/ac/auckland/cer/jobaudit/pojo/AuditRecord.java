package nz.ac.auckland.cer.jobaudit.pojo;

public class AuditRecord {

	private Long id;
	private Integer appstatus;
	private Integer status;
	private Integer cores;
	private Integer nodes;
	private String processors;
	private Integer efficacy;
	private Long coretime;
	private Long mem;
	private Long vmem;
	private Long memrequested;
	private Long walltime;
	private String account;
	private String jobid;
	private String queue;
	private String user;
	private String jobname;
	private String jobgroup;
	private String qtime;
	private String start;
	private String done;
	private String jobtype;
	private String executable;
	private String workingdir;
	private String shared;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getJobid() {
		return jobid;
	}
	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	public String getJobgroup() {
		return jobgroup;
	}
	public void setJobgroup(String jobgroup) {
		this.jobgroup = jobgroup;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getCores() {
		return cores;
	}
	public void setCores(Integer cores) {
		this.cores = cores;
	}
	public Long getMemrequested() {
		return memrequested;
	}
	public void setMemrequested(Long memrequested) {
		this.memrequested = memrequested;
	}
	public void setWalltime(Long walltime) {
		this.walltime = walltime;
	}
	public Long getWalltime() {
		return this.walltime;
	}
	public String getQtime() {
		return qtime;
	}
	public void setQtime(String qtime) {
		this.qtime = qtime;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getDone() {
		return done;
	}
	public void setDone(String done) {
		this.done = done;
	}
	public Long getCoretime() {
		return coretime;
	}
	public void setCoretime(Long coretime) {
		this.coretime = coretime;
	}
	public String getJobtype() {
		return jobtype;
	}
	public void setJobtype(String jobtype) {
		this.jobtype = jobtype;
	}
	public String getExecutable() {
		return executable;
	}
	public void setExecutable(String executable) {
		this.executable = executable;
	}
	public Long getMem() {
		return mem;
	}
	public void setMem(Long mem) {
		this.mem = mem;
	}
	public Long getVmem() {
		return vmem;
	}
	public void setVmem(Long vmem) {
		this.vmem = vmem;
	}
	public Integer getEfficacy() {
		return efficacy;
	}
	public void setEfficacy(Integer efficacy) {
		this.efficacy = efficacy;
	}
	public Integer getAppstatus() {
		return appstatus;
	}
	public void setAppstatus(Integer appstatus) {
		this.appstatus = appstatus;
	}
	public Integer getNodes() {
		return nodes;
	}
	public void setNodes(Integer nodes) {
		this.nodes = nodes;
	}
	public String getWorkingdir() {
		return workingdir;
	}
	public void setWorkingdir(String workingdir) {
		this.workingdir = workingdir;
	}
	public String getProcessors() {
		return processors;
	}
	public void setProcessors(String processors) {
		this.processors = processors;
	}
	public String getShared() {
		return shared;
	}
	public void setShared(String shared) {
		this.shared = shared;
	}
}
