package group.rober.office.autoconfigure;

public class PageOfficeProperties {

    private String licensePath = "/data/rober/pageoffice";
    private String staticResourceUrl = "/office/pageoffice/static";

    public static final String DIFF_PATH_SEGMENT = "/real";

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public String getStaticResourceUrl() {
        return staticResourceUrl;
    }

    public void setStaticResourceUrl(String staticResourceUrl) {
        this.staticResourceUrl = staticResourceUrl;
    }
}
