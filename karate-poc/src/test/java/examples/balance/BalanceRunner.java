package examples.balance;

import com.intuit.karate.junit5.Karate;

class BalanceRunner {
    
    @Karate.Test
    Karate testUsers() {
        return Karate.run("balance").relativeTo(getClass());
    }    

}
