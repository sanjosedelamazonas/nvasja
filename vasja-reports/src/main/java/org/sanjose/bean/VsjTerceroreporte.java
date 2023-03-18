package org.sanjose.bean;

import java.util.List;

public class VsjTerceroreporte {

    private String codTercero;
    private String txtNombre;
    private String fechaDesde;
    private String fechaHasta;
    private List<VsjOperaciontercero> operaciones;
    
    public VsjTerceroreporte(String codTercero, String txtNombre, String fechaDesde, String fechaHasta,
			List<VsjOperaciontercero> operaciones) {
		this.codTercero = codTercero;
		this.txtNombre = txtNombre;
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.operaciones = operaciones;
	}

	public String getCodTercero() {
        return codTercero;
    }

    public void setCodTercero(String codTercero) {
        this.codTercero = codTercero;
    }

    public String getTxtNombre() {
        return txtNombre;
    }

    public void setTxtNombre(String txtNombre) {
        this.txtNombre = txtNombre;
    }

    public String getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(String fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public String getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(String fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

	public List<VsjOperaciontercero> getOperaciones() {
		return operaciones;
	}

	public void setOperaciones(List<VsjOperaciontercero> operaciones) {
		this.operaciones = operaciones;
	}
    
    
}
