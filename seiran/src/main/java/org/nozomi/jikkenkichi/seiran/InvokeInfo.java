package org.nozomi.jikkenkichi.seiran;

public class InvokeInfo {
    public InvokeInfo(String server, String path, Class<?> returnType) {
        this.server = server;
        this.path = path;
        this.returnType = returnType;
    }


    private String server;

    private String path;

    private Class<?> returnType;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }
}
