package proyecto.modelo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Recordatorio {
    private LocalTime hora;
    private int frecuenciaHoras;
    private Medicamento medicamentoAsociado;
    private LocalDateTime ultimaToma;

    public Recordatorio(LocalTime hora, int frecuenciaHoras, Medicamento medicamentoAsociado) {
        if (hora == null) {
            throw new IllegalArgumentException("La hora no puede ser nula");
        }
        if (frecuenciaHoras <= 0) {
            throw new IllegalArgumentException("La frecuencia debe ser mayor a 0 horas");
        }
        if (medicamentoAsociado == null) {
            throw new IllegalArgumentException("El medicamento asociado no puede ser nulo");
        }

        this.hora = hora;
        this.frecuenciaHoras = frecuenciaHoras;
        this.medicamentoAsociado = medicamentoAsociado;
        this.ultimaToma = null;
    }

    public boolean esHoraDeTomar() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime horaActual = LocalDateTime.of(ahora.toLocalDate(), this.hora);

        if (ultimaToma == null) {
            return ahora.toLocalTime().equals(this.hora) || (ahora.isAfter(horaActual) && ahora.toLocalDate().isEqual(horaActual.toLocalDate()));
        }

        LocalDateTime proximo = ultimaToma.plusHours(frecuenciaHoras);
        return ahora.isAfter(proximo) || ahora.equals(proximo);
    }

    public void registrarTomado() {
        this.ultimaToma = LocalDateTime.now();
    }

    //CORRECCION: nombre descriptivo
    public String formatearInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recordatorio para ").append(medicamentoAsociado.getNombre())
                .append(" a las ").append(hora.format(DateTimeFormatter.ofPattern("HH:mm")))
                .append(" (cada ").append(frecuenciaHoras).append(" horas)");
        if (ultimaToma != null) {
            sb.append(" - Ultima toma: ").append(ultimaToma.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        return sb.toString();
    }

    //GETTERS
    public LocalTime getHora() {
        return hora;
    }
    public int getFrecuenciaHoras() {
        return frecuenciaHoras;
    }
    public Medicamento getMedicamentoAsociado() {
        return medicamentoAsociado;
    }
    public LocalDateTime getUltimaToma() {
        return ultimaToma;
    }

    @Override
    public String toString() {
        return "Recordatorio{" + "hora=" + hora + ", frecuenciaHoras=" + frecuenciaHoras + ", medicamento='" + medicamentoAsociado.getNombre() + "'}";
    }

    public String toCSV() {
        return hora.format(DateTimeFormatter.ofPattern("HH:mm")) + ";" + frecuenciaHoras + ";" + medicamentoAsociado.getNombre();
    }

    public static Recordatorio fromCSV(String lineaCSV, List<Medicamento> medicamentosPaciente) {
        String[] parte = lineaCSV.split(";");
        if (parte.length != 3) {
            throw new IllegalArgumentException("Error");
        }

        LocalTime hora = LocalTime.parse(parte[0], DateTimeFormatter.ofPattern("HH:mm"));
        int frecuencia = Integer.parseInt(parte[1]);
        String nombreMed= parte[2];

        Optional<Medicamento> medAsociado = medicamentosPaciente.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombreMed))
                .findFirst();

        if (medAsociado.isEmpty()) {
            throw new IllegalArgumentException("No se encontro el medicamento '" + nombreMed);
        }

        return new Recordatorio(hora, frecuencia, medAsociado.get());
    }
}
