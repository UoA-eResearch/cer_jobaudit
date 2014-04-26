package nz.ac.auckland.cer.jobaudit.dao;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

public class IBatisProjectDatabaseDao extends SqlSessionDaoSupport implements ProjectDatabaseDao {

    public Boolean isCurrentUserAdviser(
            String sharedToken) throws Exception {

        Integer count = (Integer) getSqlSession().selectOne("isCurrentUserAdviser", sharedToken);
        return (count > 0) ? true : false;
    }

    public List<String> getResearcherAccountNamesForSharedToken(
            String sharedToken) throws Exception {

        return getSqlSession().selectList("getResearcherAccountNamesForSharedToken", sharedToken);
    }

    public List<String> getResearcherOrAdviserAccountNamesForSharedToken(
            String sharedToken) throws Exception {

        return getSqlSession().selectList("getResearcherOrAdviserAccountNamesForSharedToken", sharedToken);
    }

    public List<String> getProjectCodesForSharedToken(
            String sharedToken) throws Exception {

        return getSqlSession().selectList("getProjectCodesForSharedToken", sharedToken);
    }

    public List<String> getProjectCodes() throws Exception {

        return getSqlSession().selectList("getProjectCodes");
    }

}
