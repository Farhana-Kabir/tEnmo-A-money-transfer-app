package com.techelevator.tenmo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GetQuerries {
    @Autowired
    JdbcOperations jdbcTemplate;

    public GetQuerries(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getUserNameFromAccountId(String accountId){
        String sql ="select username from tenmo_user " +
                "join account on account.user_id = tenmo_user.user_id" +
                " where account_id = ?";

        return jdbcTemplate.queryForObject(sql,String.class,Integer.parseInt(accountId));
    }

    public String getTransferStatus(String status){
        String sql ="select transfer_status_desc from transfer_status where transfer_status_id = ?";
        return jdbcTemplate.queryForObject(sql,String.class,Integer.parseInt(status));
    }

    public String getTransferType(String type){
        String sql ="select transfer_type_desc from transfer_type where transfer_type_id = ?";
        return jdbcTemplate.queryForObject(sql,String.class,Integer.parseInt(type));
    }

    public int getpendingReceiverId(int transferId) {
        String sql = "SELECT account_to FROM transfer WHERE transfer_id = ? ";
        int receiverAccountId = jdbcTemplate.queryForObject(sql,Integer.class,transferId);

        sql = "SELECT user_id FROM account WHERE account_id = ?";
        int receiverId = jdbcTemplate.queryForObject(sql,Integer.class,receiverAccountId);
        return  receiverId;
    }
    public int getAccountIdFromUserId(long userId){
        String sql = "SELECT account_id from account where user_id = ? ";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

}
