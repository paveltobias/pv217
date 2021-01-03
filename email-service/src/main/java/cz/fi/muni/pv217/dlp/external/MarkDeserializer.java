package cz.fi.muni.pv217.dlp.external;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class MarkDeserializer extends JsonbDeserializer<MarkDTO> {

    public MarkDeserializer() {
        super(MarkDTO.class);
    }
}
