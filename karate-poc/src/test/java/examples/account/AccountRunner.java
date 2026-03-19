package examples.account;

import com.intuit.karate.junit5.Karate;

class AccountRunner {
    
    @Karate.Test
    Karate testUsers() {
        return Karate.run("account").relativeTo(getClass());
    }    

}
