package synergy.cs530.ccsu.mobileauthentication.utils;

/**
 * Created by ejwint on 10/15/15.
 */
public class FileStatus {

    private String name;
    private boolean pass;

    public FileStatus() {
    }

    public FileStatus(String name, boolean pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

}
