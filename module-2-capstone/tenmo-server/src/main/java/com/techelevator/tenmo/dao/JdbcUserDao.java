package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.dto.TransferHistoryOfId;
import com.techelevator.tenmo.dto.UserNameAndId;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.GetQuerries;
import com.techelevator.tenmo.model.TransferMoney;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private MaptoRow maptoRow ;

    @Autowired
    GetQuerries getQuerries;


    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        maptoRow = new MaptoRow(jdbcTemplate);
        getQuerries = new GetQuerries(jdbcTemplate);
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = maptoRow.mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return maptoRow.mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }


    public List<UserNameAndId> findAllUserNameandId() {
        List<UserNameAndId> users = new ArrayList<>();
        String sql = "SELECT user_id, username FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            UserNameAndId user = maptoRow.mapRowToUserNameandId(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public boolean transfer(TransferMoney transfer) throws TransferNotFoundException {
        int senderId = transfer.getSenderId();
        int receiverId = transfer.getReceiverId();
        boolean transferStatus = false;
        BigDecimal transferMoney = transfer.getTransferMoney();
        try {
            String sqlUpdateBalance = "UPDATE account SET balance = ? WHERE user_id = ? ;";
            deposit(senderId, receiverId, transferMoney,sqlUpdateBalance);
            withdraw(senderId, receiverId, transferMoney,sqlUpdateBalance);
            updateTransferTable(2,2,senderId ,receiverId,transferMoney);
            transferStatus = true;

        } catch (DataAccessException e) {
            throw new TransferNotFoundException();
        }
        return transferStatus;
    }

    private void withdraw(int senderId, int receiverId, BigDecimal transferMoney, String sqlUpdateBalance) {
        BigDecimal balance = getBalance(senderId);
        int a =jdbcTemplate.update(sqlUpdateBalance,balance.subtract(transferMoney),senderId);
        if(a>0){
            System.out.println("$"+transferMoney+" has been transfer from account "+senderId);
        }
    }

    private void deposit(int senderId, int receiverId, BigDecimal transferMoney, String sqlUpdateBalance) {
       BigDecimal balance = getBalance(receiverId);
        int a = jdbcTemplate.update(sqlUpdateBalance,balance.add(transferMoney),receiverId);
        if(a>0){
            System.out.println("$"+transferMoney+" has been added to account "+receiverId);
        }
    }


/**
 * transfer_id	Unique identifier of the transfer
 * transfer_type_id	Foreign key to the transfer_types table; identifies type of transfer
 * transfer_status_id	Foreign key to the transfer_statuses table; identifies status of transfer
 * account_from	Foreign key to the accounts table; identifies the account that the funds are being taken from
 * account_to	Foreign key to the accounts table; identifies the account that the funds are going to
 * amount
 * @param transferStatus
 */
    private void updateTransferTable(int transferTypeId,int transferStatus,
                                     int  accountFrom,int accountTo,BigDecimal balance ) {
        String sql = "SELECT account_id from account where user_id = ? ";
        int accountIdOfSender = jdbcTemplate.queryForObject(sql,Integer.class,accountFrom);
        int accountIdOfReceiver = jdbcTemplate.queryForObject(sql,Integer.class,accountTo);

        sql = "INSERT INTO transfer (transfer_type_id,transfer_status_id,account_from,account_to," +
                    "amount) VALUES(?,?,?,?,?) RETURNING transfer_id; ";
        jdbcTemplate.queryForObject(sql, Integer.class, transferTypeId, transferStatus, accountIdOfSender, accountIdOfReceiver, balance);

        }

    public boolean requestBucks(TransferMoney transfer){
        int senderId = transfer.getSenderId();
        try{
            updateTransferTable(1,1,transfer.getSenderId(),transfer.getReceiverId(),transfer.getTransferMoney());
            return true;
        }catch (Exception e){
            e.getMessage();
            return false;
        }
    }

    @Override
    public List<UserNameAndId> pendingRequest(AuthenticatedUser user) {

        try {
            int accountIdOfSender = getQuerries.getAccountIdFromUserId( user.getUser().getId());
            String sql = "SELECT transfer_id, account_to,amount from transfer where account_from = "+accountIdOfSender+" and transfer_status_id = 1 ";
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            List<UserNameAndId> pendingList = new ArrayList<>();
            while(result.next()) {//System.out.println(result);
                UserNameAndId pendingRequest = maptoRow.mapRowToPending(result);
                pendingList.add(pendingRequest);
            }
            return pendingList;
        }catch (Exception e){
            System.out.println("Pending is not working");
            return null;
        }
    }

    @Override
    public String reject(int id) {
           // String sql ="DELETE FROM transfer WHERE transfer_id = ? ";
        String sql ="update  transfer set transfer_Status_id = 3 where transfer_id= ?";
             int a= jdbcTemplate.update(sql, id);
             if(a==1)
                 return "Rejection has been successful.";
             else
                 return "ID not found";
    }
    @Override
    public boolean approvePendingRequest(int transferId, TransferMoney pendingRequest) throws TransferNotFoundException {
        int pendingreceiverId = getQuerries.getpendingReceiverId(transferId);
        //System.out.println("SenderId: "+pendingRequest.getSenderId()+"receiverId: "+pendingreceiverId+"Balance: "+pendingRequest.getTransferMoney());
        pendingRequest.setReceiverId(pendingreceiverId);
        BigDecimal senderBalance =getBalance(pendingRequest.getSenderId());
        if(senderBalance.compareTo(pendingRequest.getTransferMoney())!=-1) {
            try {
                transfer(pendingRequest);
                String sql = "DELETE FROM transfer WHERE transfer_id = ? ";
                jdbcTemplate.update(sql, transferId);
                return true;
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public List<UserNameAndId> transferHistory(AuthenticatedUser user) {
        int accountIdofUser = getQuerries.getAccountIdFromUserId(user.getUser().getId());
        System.out.println("accountIdofUser: "+accountIdofUser);
        String sql ="select transfer_id, account_from, account_to, amount from transfer where account_to =? or account_from = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,accountIdofUser,accountIdofUser);
        List<UserNameAndId> transferHistoryList = new ArrayList<>();
        MaptoRow mapHistory = new MaptoRow(jdbcTemplate);
        while(results.next()){
            //transferHistoryList.add(mapRowToPending(results));
            transferHistoryList.add(mapHistory.mapRowToHistory(results,accountIdofUser));
        }
        return transferHistoryList;
    }

    @Override
    public TransferHistoryOfId veiwTransferHistoryOfId(int transferId) {
        TransferHistoryOfId transferHistory = new TransferHistoryOfId();
        MaptoRow mapHistoryOfId = new MaptoRow(jdbcTemplate);
        System.out.println("transferId: "+transferId);
        String sql ="select * from transfer where transfer_id =?";
        TransferHistoryOfId transferHistoryOfId = new TransferHistoryOfId();
        try {
            transferHistoryOfId = (TransferHistoryOfId) jdbcTemplate.queryForObject(sql,new Object[]{transferId},
                    new BeanPropertyRowMapper(TransferHistoryOfId.class));
            transferHistoryOfId.setAccountTo(getQuerries.getUserNameFromAccountId(transferHistoryOfId.getAccountTo()));
            transferHistoryOfId.setAccountFrom(getQuerries.getUserNameFromAccountId(transferHistoryOfId.getAccountFrom()));
            transferHistoryOfId.setTransferStatusId(getQuerries.getTransferStatus(transferHistoryOfId.getTransferStatusId()));
            transferHistoryOfId.setTransferTypeId(getQuerries.getTransferType(transferHistoryOfId.getTransferTypeId()));
        }
        catch (DataAccessException e){
            System.out.println(e.getMessage());
        }
        return  transferHistoryOfId;
    }

//    private int getpendingReceiverId(int transferId) {
//        String sql = "SELECT account_to FROM transfer WHERE transfer_id = ? ";
//        int receiverAccountId = jdbcTemplate.queryForObject(sql,Integer.class,transferId);
//
//        sql = "SELECT user_id FROM account WHERE account_id = ?";
//        int receiverId = jdbcTemplate.queryForObject(sql,Integer.class,receiverAccountId);
//        return  receiverId;
//    }

//    private int getAccountIdFromUserId(long userId){
//        String sql = "SELECT account_id from account where user_id = ? ";
//        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
//    }

    public BigDecimal getBalance(long id) throws UsernameNotFoundException{
        String sql= "SELECT balance FROM account WHERE user_id ="+id+" ;";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }
}
