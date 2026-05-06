package com.github.zipcodewilmington;

import java.util.ArrayList;
import java.util.List;

import com.github.zipcodewilmington.casino.CasinoAccount;

public class CasinoAccountManager {

    private List<CasinoAccount> casinoAccountList = new ArrayList<>();

    public CasinoAccount getAccount(String accountName, String accountPassword) {
        for (CasinoAccount account : casinoAccountList) {
            if (account.getPlayerName().equals(accountName)) {
                return account;
            }
        }
        return null;
    }

    public CasinoAccount createAccount(String accountName, String accountPassword) {
        return new CasinoAccount(accountName, 100.0);
    }

    public void registerAccount(CasinoAccount casinoAccount) {
        casinoAccountList.add(casinoAccount);
    }

    public List<CasinoAccount> getCasinoAccountList() {
        return casinoAccountList;
    }
}