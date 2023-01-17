package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferHistoryOfId;
import com.techelevator.tenmo.dto.UserNameAndId;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferMoney;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class ServiceController {
    private UserDao userDao;

    public ServiceController(UserDao userDao) {
        this.userDao = userDao;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/currentbalance", method = RequestMethod.POST)
    public BigDecimal currentBalance(@RequestBody AuthenticatedUser currentUser){
        return userDao.getBalance(currentUser.getUser().getId());
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/getAllUser")
    public List<UserNameAndId> getAllUserByNameandId() {
       return userDao.findAllUserNameandId();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/transfer")
    public String transfer(@RequestBody TransferMoney transfer) throws TransferNotFoundException {
        boolean isSuccess = userDao.transfer(transfer);
        return isSuccess? "You have successfully made the transfer":"Transfer is not successful";
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/requestbucks")
    public String requestBucks(@RequestBody TransferMoney transfer) throws TransferNotFoundException {
        boolean isSuccess = userDao.requestBucks(transfer);
        return isSuccess? "You have successfully send the request of bucks":"Does not send the request of bucks";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/pendingrequest")
    public List<UserNameAndId> pendingRequest(@RequestBody AuthenticatedUser user) throws TransferNotFoundException {
        return userDao.pendingRequest(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "rejectPendingReq/{id}", method = RequestMethod.POST)
    public  String delete(@PathVariable int id) throws TransferNotFoundException  {
        String str =userDao.reject(id);
        return str;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "approvedpendingrequest/{transferId}", method = RequestMethod.POST)
    public  String approvedpendingrequest(@PathVariable int transferId, @RequestBody TransferMoney pendingRequest) throws TransferNotFoundException  {
       boolean isSuccess = userDao.approvePendingRequest(transferId,pendingRequest);
        return isSuccess? "Apprved pending request":"Does not approved pending request";
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/transferhistory")
    public List<UserNameAndId> getTransferHistory(@RequestBody AuthenticatedUser user) {
        return userDao.transferHistory(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "veiwTransferHistoryOfId//{transferId}", method = RequestMethod.POST)
    public TransferHistoryOfId veiwTransferHistoryOfId(@PathVariable int transferId) throws TransferNotFoundException  {
        return userDao.veiwTransferHistoryOfId(transferId);
    }
}
