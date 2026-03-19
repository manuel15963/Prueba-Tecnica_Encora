package examples.transfer;

import com.intuit.karate.junit5.Karate;

class TransferRunner {
    
    @Karate.Test
    Karate testUsers() {
        return Karate.run("transfer").relativeTo(getClass());
    }    

}
