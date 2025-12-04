package proyecto.modelo;

import com.google.gson.*;
import java.lang.reflect.Type;

public class MedicamentoTypeAdapter implements JsonSerializer<Medicamento>, JsonDeserializer<Medicamento> {
    @Override
    public JsonElement serialize(Medicamento src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = (JsonObject) new Gson().toJsonTree(src);
        obj.addProperty("tipo", src instanceof Insulina ? "Insulina" : "Medicamento");
        return obj;
    }

    @Override
    public Medicamento deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();
        String tipo = obj.get("tipo").getAsString();

        if (tipo.equals("Insulina")) {
            return new Gson().fromJson(json, Insulina.class);
        }
        return new Gson().fromJson(json, Medicamento.class);
    }
}
