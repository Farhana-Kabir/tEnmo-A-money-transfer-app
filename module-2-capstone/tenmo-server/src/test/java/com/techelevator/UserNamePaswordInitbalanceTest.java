package com.techelevator;

import com.techelevator.tenmo.dao.JdbcUserDao;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;
@JdbcTest
@Component
public class UserNamePaswordInitbalanceTest {
    private static SingleConnectionDataSource dataSource;
    @Autowired
    private static JdbcTemplate jdbcTemplate;
    private static ValidatorFactory validatorFactory;
    private static Validator validator;
    @Autowired
    private static JdbcUserDao jdbcUserDao;

    @BeforeClass
    public static void setupDataSource() throws SQLException, IOException, FileNotFoundException, ClassNotFoundException {
        dataSource = createDatasource("tenmo");
        dataSource.setAutoCommit(false);
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcUserDao = new JdbcUserDao(jdbcTemplate);

    }
    /* After all tests have finished running, this method will close the DataSource */
    @AfterClass
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    public UserNamePaswordInitbalanceTest() {    }

    @Test
    public void userNameShouldNotBeBlank() {
        int rowNumberOfEmptyUser = findEmptyUserName();
        assertEquals(0,rowNumberOfEmptyUser);
    }

    @Test
    public void passwordShouldNotBeBlank() {
        int rowNumberOfEmptyPassword = findEmptyPassword();
        assertEquals(0,rowNumberOfEmptyPassword);
    }

    @Test
    public void initialBalancevalidation(){
        boolean isRegister = jdbcUserDao.create("test", "test");
        System.out.println(isRegister);
        String sql ="Select user_id from tenmo_user where username ='test' ";
        int id = jdbcTemplate.queryForObject(sql,Integer.class);

        sql = "Select balance From account where user_id = ? ";
        int balance = jdbcTemplate.queryForObject(sql,Integer.class,id);
        assertEquals(1000,balance);
    }

    @Test
    public void registrationTest(){

        boolean isRegister = jdbcUserDao.create("test1", "test1");
        System.out.println(isRegister);
        int currentTotalUser = getAllUserCount();

        jdbcUserDao.create("test2", "test");
        int a =getAllUserCount();

        assertEquals(currentTotalUser+1,a);

    }



    private int findEmptyPassword() {
        String sql = "SELECT * FROM tenmo_user WHERE password_hash IS NULL OR password_hash = ' '; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        return results.getRow();
    }

    public int findEmptyUserName() {
        String sql = "SELECT * FROM tenmo_user WHERE username IS NULL OR username = ' '; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        return results.getRow();
    }
    public int getAllUserCount(){
        String sqlAlluser ="SELECT count (*) FROM tenmo_user  ";
       return jdbcTemplate.queryForObject(sqlAlluser,Integer.class);

}

    private static SingleConnectionDataSource createDatasource(String defaultDbName) {
        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
        String port = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5432";
        String dbName = System.getenv("DB_DATABASE") != null ? System.getenv("DB_DATABASE") : defaultDbName;
        String username = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "postgres";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "postgres1";

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}