package pv217.entities.extern;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class PlagiarismDeserializer extends JsonbDeserializer<PlagiarismDTO> {

    public PlagiarismDeserializer() {
        super(PlagiarismDTO.class);
    }
}
