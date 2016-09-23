package riders.gumjung.smart.smartridingservice.dragonSns;

/**
 * Created by HackerAnderson on 2016. 7. 1..
 */

public class SnsInfo {

    private String id;
    private String snsMessage;
    private boolean status;

    public SnsInfo() {

    }


    public void setStatus(boolean temp_status) {
        this.status = temp_status;
    }
    public void setUserId(String temp_id) {
        this.id = temp_id;
    }
    public void setSnsMessage(String temp_message) {
        this.snsMessage = temp_message;
    }

    public String getUserId() {
        return this.id;
    }
    public boolean getStatus() {
        return this.status;
    }
    public String getSnsMessage() {
        return this.snsMessage;
    }


}
