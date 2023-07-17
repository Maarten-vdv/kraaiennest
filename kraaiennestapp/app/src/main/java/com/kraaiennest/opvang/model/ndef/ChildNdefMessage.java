package com.kraaiennest.opvang.model.ndef;

public class ChildNdefMessage {

    private String source;

    private String qrId;
    private String name;
    private Integer id;

    public ChildNdefMessage(String source) {
        this.source = source;

        String[] parts = source.split("/");
        qrId = parts[0];
        name = parts[1];
        id = Integer.parseInt(parts[2]);
    }

    public ChildNdefMessage(String qrId, String name, Integer id) {
        this.qrId = qrId;
        this.name = name;
        this.id = id;

        source = String.format("%s/%s/%s", qrId, name, id);
    }

    public String getSource() {
        return source;
    }

    public String getQrId() {
        return qrId;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }
}
