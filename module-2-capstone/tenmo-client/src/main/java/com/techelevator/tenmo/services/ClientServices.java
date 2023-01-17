package com.techelevator.tenmo.services;

import com.techelevator.dto.TransferHistoryOfId;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.math.BigDecimal;

public class ClientServices {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ConsoleService consoleService = new ConsoleService();
    public ClientServices(String url) {
        this.baseUrl = url;
    }
    BigDecimal currentBalance;
    Transfer transfer = new Transfer();


    public boolean showBalance(AuthenticatedUser user){
        HttpEntity<Object> entity = createAuthenticatedUserEntity(user,user.getToken());
        boolean success = false;

        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(baseUrl + "currentbalance", HttpMethod.POST, entity,BigDecimal.class);
            success = true;
            transfer.setSenderBalance(response.getBody());
            System.out.println("Your current account balance is: "+response.getBody());
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    private HttpEntity<Object> createAuthenticatedUserEntity(Object user,String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(user, headers);
    }

    public Transfer setTransfer(BigDecimal transferAmount, long receiverId, Long senderId, String approved) {
        transfer.setTransferMoney(transferAmount);
        transfer.setReceiverId(receiverId);
        transfer.setSenderId(senderId);
        transfer.setTransferStatus(approved);
        return transfer;
    }

    public User[] showAllUser(Object ob, String address,String token) {
        HttpEntity<Object> entity = createAuthenticatedUserEntity(ob,token);
        ResponseEntity<User[]> response =
                restTemplate.exchange(baseUrl + address, HttpMethod.POST, entity, User[].class);

        User[] us = response.getBody();
        consoleService.printAllUser(us);
        return us;
    }

    public long getId(User[] us, AuthenticatedUser currentUser) {
        boolean isContinue = true;
        int id = -1;
        while (isContinue) {
            id = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel):");
            if (id == 0) {
                isContinue = false;
            } else {
                if (id != currentUser.getUser().getId()) {
                    for (int i = 0; i < us.length; i++) {
                        if (us[i].getId() == id) {
                            isContinue = false;
                            break;
                        }
                    }
                }else{
                    System.out.println("you can have selected your current account. Please select another..");
                }
            }
        }
        return  id;
    }

    public void sendTransfer(Object transfer, String s,String token) {
        HttpEntity<Object> entity = createAuthenticatedUserEntity(transfer,token);

        try{
            ResponseEntity<String> response =
            restTemplate.exchange(baseUrl + s, HttpMethod.POST, entity, String.class);
            System.out.println(response.getBody());
            }catch (HttpClientErrorException e){
                System.out.println("Transaction is not successful");
        }
    }

    public void rejectPendingReq(int transferID, AuthenticatedUser user) {
        HttpEntity<Object> entity = createAuthenticatedUserEntity(user,user.getToken());
        try{

            ResponseEntity<String> response =
                    restTemplate.exchange(baseUrl+"rejectPendingReq/" + transferID, HttpMethod.POST,
                            entity, String.class);
            System.out.println(response.getBody());
        }catch(ResourceAccessException e ) {
            // Handle network I/O errors
            System.out.println(e.getMessage());
        }
        catch (RestClientResponseException e) {
            // Handle response status codes 4xx and 5xx
            System.out.println(e.getRawStatusCode());
        }catch (RestClientException e){
            System.out.println(e.getMessage());
        }

    }

    public void approvedPending(Transfer user, int transferID,String token) {
       sendTransfer(user,"approvedpendingrequest/"+transferID,token);

    }

    public void veiwTransferHistoryOfId(AuthenticatedUser currentUser, int transferId) {
       // sendTransfer(currentUser,"veiwTransferHistoryOfId/"+transferId);
        HttpEntity<Object> entity = createAuthenticatedUserEntity(currentUser, currentUser.getToken());
        try{
            ResponseEntity<TransferHistoryOfId> response =
                    restTemplate.exchange(baseUrl + "veiwTransferHistoryOfId/"+transferId, HttpMethod.POST, entity,TransferHistoryOfId.class);

            consoleService.printTransferHistoryOfId(response.getBody(),currentUser.getUser().getname());
        }catch (HttpClientErrorException e){
            System.out.println("Something went wrong..");
        }
    }
}
