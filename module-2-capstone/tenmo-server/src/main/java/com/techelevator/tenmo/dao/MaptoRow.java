package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dto.UserNameAndId;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class MaptoRow {
@Autowired
    JdbcOperations jdbcTemplate;

    public MaptoRow(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate  = jdbcTemplate;
    }

    public UserNameAndId mapRowToHistory(SqlRowSet result, int accountIdOfUser) {
        UserNameAndId user = new UserNameAndId();
        String preFix = "";
        long id;
        if(result.getInt("account_from")== accountIdOfUser){
            id = result.getLong("account_to");
            preFix ="To: ";
        }else{
            id = result.getLong("account_from");
            preFix ="From: ";
        }

        String username = "";
        try {
            String sql = "select username from tenmo_user " +
                    "join account on tenmo_user.user_id= account.user_id " +
                    "where account_id = ?";
            username = jdbcTemplate.queryForObject(sql, String.class,id);

        }catch(DataAccessException e){
            System.out.println(e.getMessage());
        }
        user.setName(preFix+ username);
        user.setId(result.getLong("transfer_id"));
        user.setBalance(result.getBigDecimal("amount"));
        return user;

    }

    public UserNameAndId mapRowToPending(SqlRowSet result) {
        UserNameAndId p = new UserNameAndId();
        String sql ="select username from tenmo_user " +
                "join account on tenmo_user.user_id= account.user_id " +
                "where account_id = ?";

        String username= jdbcTemplate.queryForObject(sql,String.class,result.getLong("account_to"));
        p.setName(username);
        p.setId(result.getLong("transfer_id"));
        p.setBalance(result.getBigDecimal("amount"));
        return p;
    }

    public UserNameAndId mapRowToUserNameandId(SqlRowSet rs) {
        UserNameAndId user = new UserNameAndId();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("username"));
        return user;
    }

    public User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
