package ru.practicum.ewm.stats.serialization.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.PrincipalDeserializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final DatumReader<T> reader;

    public BaseAvroDeserializer(Schema schema) {
        this.reader = new SpecificDatumReader<>(schema);
        this.decoderFactory = DecoderFactory.get();
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.reader = new SpecificDatumReader<>(schema);
        this.decoderFactory = decoderFactory;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
                return this.reader.read(null, decoder);
            }
            return null;
        } catch (Exception e) {
            throw new PrincipalDeserializationException("Deserialization error on topic [" + topic + "]", e);
        }
    }
}