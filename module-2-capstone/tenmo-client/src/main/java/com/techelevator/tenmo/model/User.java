package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class User {

    private Long id;
    private String name;
    private BigDecimal balance = null;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User otherUser = (User) other;
            return otherUser.getId().equals(id)
                    && otherUser.getname().equals(name);
        } else {
            return false;
        }
    }
}
