package com.example.doantest.Model;

public class FontModel {
    int id;
    String name, nameFile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FontModel(String nameFile, int id, String name) {
        this.id = id;
        this.name = name;
        this.nameFile = nameFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public FontModel(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
