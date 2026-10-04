package com.amin.aggar.frontend.form;

public class StateForm {
    private String name;
    private String nameAr;
    private String code;

    public StateForm() {}

    public StateForm(String name, String nameAr, String code) {
        this.name = name;
        this.nameAr = nameAr;
        this.code = code;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
