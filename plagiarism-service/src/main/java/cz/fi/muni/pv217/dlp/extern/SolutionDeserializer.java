package cz.fi.muni.pv217.dlp.extern;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class SolutionDeserializer extends JsonbDeserializer<SolutionDTO> {

    public SolutionDeserializer() {
        super(SolutionDTO.class);
    }
}
