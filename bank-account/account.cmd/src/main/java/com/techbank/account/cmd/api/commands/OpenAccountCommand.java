package com.techbank.account.cmd.api.commands;

import com.techbank.account.common.dto.AccountType;
import com.techbank.cqrs.core.commands.BaseCommand;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenAccountCommand extends BaseCommand {

    private String accountHolder;
    private AccountType accountType;
    private BigDecimal openingBalance;
}
