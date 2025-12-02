package proyecto.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Medicamento {
    private String nombre;
    private int dosis; //mg o unidades
    private int cantidad; //stock disponible
    private String fechaVencimiento; //formato: dd/MM/yyyy

    //constructor con validaciones
    public Medicamento(String nombre, int dosis, int cantidad, String fechaVencimiento) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del medicamento no puede ser nulo o vacio");
        }
        if (dosis <= 0) {
            throw new IllegalArgumentException("La dosis debe ser mayor a 0");
        }
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser nula o vacia");
        }

        this.nombre = nombre.trim();
        this.dosis = dosis;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento.trim();
    }

    //verifica si esta vencido
    public boolean estaVencido() {
        try {
            LocalDate hoy = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate vencimiento = LocalDate.parse(fechaVencimiento, formatter);
            return vencimiento.isBefore(hoy); //vencido si fecha < hoy
        } catch (DateTimeParseException e) {
            System.err.println("Formato de fecha invalido para " + nombre + ": " + fechaVencimiento);
            return true;
        }
    }

    public void disminuirCantidad() {
        if (cantidad > 0) {
            cantidad--;
        } else {
            throw new IllegalStateException("No hay stock disponible para " + nombre);
        }
    }

    //CAMBIO: renombrar metodo por buena practica, paso de obtenerTexto a formatearInformacion
    public String formatearInformacion() {
        return nombre + " - Dosis: " + dosis + " mg, Stock: " + cantidad + ", Vence: " + fechaVencimiento + (estaVencido() ? " (VENCIDO)" : "");
    }

    //GETTERS Y SETTERS
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio");
        }
        this.nombre = nombre.trim();
    }

    public int getDosis() {
        return dosis;
    }
    public void setDosis(int dosis) {
        if (dosis <= 0) {
            throw new IllegalArgumentException("La dosis debe ser mayor a 0");
        }
        this.dosis = dosis;
    }

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.cantidad = cantidad;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(String fechaVencimiento) {
        if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser nula o vacia");
        }
        this.fechaVencimiento = fechaVencimiento.trim();
    }

    @Override
    public String toString() {
        return "Medicamento{" + "nombre='" + nombre + "', dosis=" + dosis + ", cantidad=" + cantidad + ", fechaVencimiento='" + fechaVencimiento + "'}";
    }

    public String toCSV() {
        return nombre + ";" + dosis + ";" + cantidad + ";" + fechaVencimiento;
    }

    public static Medicamento fromCSV(String linea) {
        try {
            String[] parts = linea.split(";");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Linea CSV invalida para Medicamento");
            }
            String nombre = parts[0].trim();
            int dosis = Integer.parseInt(parts[1].trim());
            int cantidad = Integer.parseInt(parts[2].trim());
            String fecha = parts[3].trim();
            return new Medicamento(nombre, dosis, cantidad, fecha);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al cargar Medicamento desde historial: " + e.getMessage(), e);
        }
    }
}