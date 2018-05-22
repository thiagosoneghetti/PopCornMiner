package popcornminer.thiagosoneghetti.com.br.popcornminer.Model;

import java.io.Serializable;

public class Saldo implements Serializable {
    private Long id;
    private String balance;

    public Saldo(Long id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    public Saldo (String balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

}
