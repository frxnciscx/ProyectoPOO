package proyecto.modelo;

public class Insulina extends Medicamento {
    private double glucosaMinima; //mg/dL, umbral para inyectar

    public Insulina(String nombre, int dosis, int cantidad, String fechaVencimiento, double glucosaMinima) {
        super(nombre, dosis, cantidad, fechaVencimiento);
        setGlucosaMinima(glucosaMinima);
    }

    //Verificar si se puede tomar (glucosa >= minima)
    public boolean sePuedeTomar(double glucosaActual) {
        return glucosaActual >= glucosaMinima && !estaVencido() && getCantidad() > 0;
    }

    @Override
    public String toFormattedString() {
        return super.toFormattedString() + " [Insulina - Glucosa minima: " + glucosaMinima + " mg/dL]";
    }

    //getter y setter para glucosa
    public double getGlucosaMinima() {
        return glucosaMinima;
    }

    public void setGlucosaMinima(double glucosaMinima) {
        if (glucosaMinima <= 0) {
            throw new IllegalArgumentException("La glucosa minima debe ser mayor a 0 mg/dL");
        }
        this.glucosaMinima = glucosaMinima;
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + glucosaMinima;
    }

    public static Insulina fromCSV(String linea) {
        try {
            String[] parts = linea.split(";");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Linea CSV invalida para Insulina");
            }
            String nombre = parts[0].trim();
            int dosis = Integer.parseInt(parts[1].trim());
            int cantidad = Integer.parseInt(parts[2].trim());
            String fecha = parts[3].trim();
            double glucosa = Double.parseDouble(parts[4].trim());
            return new Insulina(nombre, dosis, cantidad, fecha, glucosa);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al cargar Insulina desde Historial: " + e.getMessage(), e);
        }
    }
}