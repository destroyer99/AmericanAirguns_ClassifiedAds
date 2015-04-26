package net.americanairguns.classifiedads;

public class Ad {
    public String subject, primary, secondary;
    public Integer adId;

    public Ad() {
        super();
    }

    public Ad(String subject, String primary, String secondary, Integer adId) {
        super();
        this.subject = subject;
        this.primary = primary;
        this.secondary = secondary;
        this.adId = adId;
    }

    public Ad(String subject, String primary, String secondary) {
        super();
        this.subject = subject;
        this.primary = primary;
        this.secondary = secondary;
        this.adId = 0;
    }
}
