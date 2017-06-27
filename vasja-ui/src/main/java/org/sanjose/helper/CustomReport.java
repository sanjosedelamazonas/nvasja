package org.sanjose.helper;

/**
 * VASJA class
 * User: prubach
 * Date: 27.06.17
 */
public class CustomReport {

    private String fileName;

    private String name;

    private boolean isFecha;

    private boolean isProyTercero;

    private boolean isCategoria;

    public CustomReport(String fileName, String name, boolean isFecha, boolean isProyTercero, boolean isCategoria) {
        this.fileName = fileName;
        this.name = name;
        this.isFecha = isFecha;
        this.isProyTercero = isProyTercero;
        this.isCategoria = isCategoria;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFecha() {
        return isFecha;
    }

    public void setFecha(boolean fecha) {
        isFecha = fecha;
    }

    public boolean isProyTercero() {
        return isProyTercero;
    }

    public void setProyTercero(boolean proyTercero) {
        isProyTercero = proyTercero;
    }

    public boolean isCategoria() {
        return isCategoria;
    }

    public void setCategoria(boolean categoria) {
        isCategoria = categoria;
    }

    @Override
    public String toString() {
        return "CustomReport{" +
                "fileName='" + fileName + '\'' +
                ", name='" + name + '\'' +
                ", isFecha=" + isFecha +
                ", isProyTercero=" + isProyTercero +
                ", isCategoria=" + isCategoria +
                '}';
    }
}
