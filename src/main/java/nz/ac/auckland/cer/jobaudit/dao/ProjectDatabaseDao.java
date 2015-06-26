package nz.ac.auckland.cer.jobaudit.dao;

import java.util.List;

public interface ProjectDatabaseDao {

    public List<String> getResearcherAccountNamesForSharedToken(
            String sharedToken) throws Exception;

    public List<String> getResearcherOrAdviserAccountNamesForSharedToken(
            String sharedToken) throws Exception;

    public List<String> getProjectCodesForSharedToken(
            String sharedToken) throws Exception;

    public List<String> getProjectCodesForEppn(
            String eppn) throws Exception;

    public List<String> getProjectCodes() throws Exception;

    public Boolean isCurrentUserAdviser(
            String sharedToken) throws Exception;
}
