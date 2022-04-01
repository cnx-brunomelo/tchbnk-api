package com.techbank.account.cmd.domain;

import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.cqrs.core.domain.AggregateRoot;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
public class AccountAggregate extends AggregateRoot {

    private Boolean active;
    private BigDecimal balance;

    public AccountAggregate(OpenAccountCommand command) {
        raiseEvent(AccountOpenedEvent.builder()
                .id(command.getId())
                .accountHolder(command.getAccountHolder())
                .createdDate(LocalDateTime.now())
                .accountType(command.getAccountType())
                .openingBalance(command.getOpeningBalance())
                .build());
    }

    public void apply(AccountOpenedEvent event) {
        this.setId(event.getId());
        this.active = true;
        this.balance = event.getOpeningBalance();
    }

    public void deposityFunds(BigDecimal amount) {

        if(!this.active) {
            throw  new IllegalStateException("Funds cannot be deposited into a closed account!");
        }
        if(amount == null || BigDecimal.ZERO.compareTo(amount) != -1) {
            throw new IllegalStateException("The deposit amount must be greater than 0!");
        }

        raiseEvent(FundsDepositedEvent.builder()
                .id(this.getId())
                .amount(amount)
                .build());
    }

    public void apply(FundsDepositedEvent event) {
        this.setId(event.getId());
        this.active = true;
        this.balance = this.balance.add(event.getAmount());
    }

    public void withdrawFunds(BigDecimal amount) {

        if(!this.active) {
            throw  new IllegalStateException("Funds cannot be withdrawn into a closed account!");
        }
        if(amount == null || BigDecimal.ZERO.compareTo(amount) != -1) {
            throw new IllegalStateException("The withdraw amount must be greater than 0!");
        }

        raiseEvent(FundsWithdrawnEvent.builder()
                .id(this.getId())
                .amount(amount)
                .build());
    }

    public void apply(FundsWithdrawnEvent event) {
        this.setId(event.getId());
        this.active = true;
        this.balance = this.balance.subtract(event.getAmount());
    }

    public void closeAccount() {
        if(!this.active) {
            throw  new IllegalStateException("The bank account has already been closed!");
        }
        raiseEvent(AccountClosedEvent.builder()
                .id(this.getId())
                .build());
    }

    public void apply(AccountClosedEvent event) {
        this.setId(event.getId());
        this.active = false;
    }


}
