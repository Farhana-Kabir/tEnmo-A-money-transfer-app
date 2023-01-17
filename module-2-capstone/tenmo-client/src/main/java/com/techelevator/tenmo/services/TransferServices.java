package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import java.math.BigDecimal;

public class TransferServices {
    private final String baseUrl;
    private ClientServices clientServices;
    private ConsoleService consoleService;


    public TransferServices(String baseUrl) {
        this.baseUrl = baseUrl;
        clientServices = new ClientServices(baseUrl);
        consoleService = new ConsoleService();
    }

    public void viewPendingRequest(AuthenticatedUser user) {
       User[] pendingUsers= clientServices.showAllUser(user,"pendingrequest",user.getToken());
        System.out.println("Please enter transfer ID to approve/reject (0 to cancel):  ");
        int transferID = consoleService.promptForInt("");
        if(transferID==0){
            return;
        }else{
            int approveId = consoleService.printApproveOrRejectPendingTransfer();
            if(approveId == 1){
                long senderId = user.getUser().getId();
                BigDecimal requestMoney = getRequestMoney(pendingUsers,transferID);
                Transfer tranferObject = clientServices.setTransfer(requestMoney,0,senderId,"Approved");
                clientServices.approvedPending(tranferObject,transferID, user.getToken());
            }else if(approveId == 2){
                clientServices.rejectPendingReq(transferID, user);
            }else{
                return;
            }
        }
    }

    private BigDecimal getRequestMoney(User[] pendingUsers, int transferID) {
        for(User pendingUser: pendingUsers){
            if(pendingUser.getId() == transferID)
            {return pendingUser.getBalance();}
        }
        return null;
    }

    public void sendBucks(AuthenticatedUser currentUser) {
        clientServices.showBalance(currentUser);
        User[] us = clientServices.showAllUser(null,"getAllUser", currentUser.getToken());
        long receiverId = clientServices.getId(us,currentUser);
        if(receiverId==0)
            return;
        BigDecimal transferAmount = null;
        boolean isContinue = true;

        while(isContinue){
            transferAmount = consoleService.promptForBigDecimal("Enter amount (press 0 to cancel) : ");
            if(transferAmount.compareTo(BigDecimal.ZERO)==0){
                System.out.println("Transaction is canceled. ");
                return;
            }else if(transferAmount.compareTo(clientServices.transfer.getSenderBalance())==1){
                System.out.println("Transfer Money is greater than you balance");
            }else{
                isContinue = false;
            }
        }
        clientServices.sendTransfer(clientServices.setTransfer(transferAmount, receiverId,currentUser.getUser().getId(),"Approved"),"transfer", currentUser.getToken());
    }


    public void requestBucks(AuthenticatedUser currentUser) {
        User[] users = clientServices.showAllUser(null,"getAllUser", currentUser.getToken());
        long senderId = clientServices.getId(users,currentUser);
        BigDecimal requestAmount = null;
        if(senderId==0) {
            System.out.println("Transaction has been canceled...");
            return;
        }else{
            boolean isContinue = true;
            while(isContinue){
                requestAmount = consoleService.promptForBigDecimal("Enter amount greater than 0:");
                if(requestAmount.compareTo(BigDecimal.ZERO)==1){
                    isContinue = false;
                }else{
                    System.out.println("Please request money greater than 0...");
                }
            }
        }
        clientServices.sendTransfer(clientServices.setTransfer(requestAmount, currentUser.getUser().getId(),senderId,"Pending"),
                "requestbucks", currentUser.getToken());
    }

    public void viewTransferHistory(AuthenticatedUser currentUser) {
        //System.out.println(currentUser.getUser().getId());
        clientServices.showAllUser(currentUser,"transferhistory", currentUser.getToken());

        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel):");
        if(transferId==0){
            return;
        }else {
            clientServices.veiwTransferHistoryOfId(currentUser,transferId);
        }
    }
}
