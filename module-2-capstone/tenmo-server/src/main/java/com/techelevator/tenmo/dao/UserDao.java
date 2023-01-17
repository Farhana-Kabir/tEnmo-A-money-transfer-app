package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.dto.TransferHistoryOfId;
import com.techelevator.tenmo.dto.UserNameAndId;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferMoney;
import com.techelevator.tenmo.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    BigDecimal getBalance(long id);

    List<UserNameAndId> findAllUserNameandId();
@Transactional
    boolean transfer(TransferMoney t) throws TransferNotFoundException;

    boolean requestBucks(TransferMoney transfer);

    List<UserNameAndId> pendingRequest(AuthenticatedUser user);

    String reject(int id);

@Transactional
    boolean approvePendingRequest(int id, TransferMoney pendingRequest) throws TransferNotFoundException;

    List<UserNameAndId> transferHistory(AuthenticatedUser user);

    TransferHistoryOfId veiwTransferHistoryOfId(int transferId);
}
