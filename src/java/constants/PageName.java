package constants;

public enum PageName {
    INDEX("/admin/main.jsp"),ERROR("/error.jsp"),SIGN_IN("/sign_in.jsp");

    private String path;

    PageName(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
