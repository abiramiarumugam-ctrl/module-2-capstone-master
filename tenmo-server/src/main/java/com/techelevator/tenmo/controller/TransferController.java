package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for transactions
 */


@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {

    private TransferDao transferDao;

    public TransferController(TransferDao transferDao) {this.transferDao = transferDao;}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int createTransfer(@RequestBody Transfer transfer){
        return transferDao.createTransfer(transfer);
    }

    @PutMapping(path = "/{id}")
    public void doTransfer(@PathVariable int id, @RequestBody Transfer transfer){
        transferDao.doTransfer(transfer, id);
    }
    @PutMapping(path = "/update/{transferId}/{transferStatusId}")
    public ResponseEntity<String> updateTransferStatus(@PathVariable Integer transferStatusId, @PathVariable int transferId){
        try {
            transferDao.updateTransferStatus(transferStatusId, transferId);
            return ResponseEntity.ok("Transfer status updated successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid transfer status ID");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating transfer status");
        }
    }

    @GetMapping(path = "/history/{id}")
    public List<Transfer> transferHistory(@PathVariable int id){return transferDao.transferHistory(id);}

    @GetMapping(path = "/pending/{accountFrom}")
    public List<Transfer> pendingRequests(@PathVariable int accountFrom){
        return transferDao.pendingRequests(accountFrom);
    }
    @GetMapping(path = "/{transferId}")
    public Transfer getTransferById(@PathVariable int transferId){
        return transferDao.getTransferById(transferId);
    }

    @GetMapping(path = "/all/{userAccountId}")
    public List<Transfer> getAllUserTransfers(@PathVariable int userAccountId){return transferDao.getAllUserTransfers(userAccountId);}

}
