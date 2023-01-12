//package com.mimi.w2m.backend.converter.json;
//
//import com.fasterxml.jackson.core.JacksonException;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.*;
//import com.mimi.w2m.backend.config.exception.InvalidValueException;
//import com.mimi.w2m.backend.domain.type.TimeRange;
//import org.springframework.boot.jackson.JsonComponent;
//
//import java.io.IOException;
//import java.util.Objects;
//
///**
// * TimeRangeConverter
// *
// * @author teddy
// * @version 1.0.0
// * @since 2023/01/11
// **/
//@JsonComponent
//public class TimeRangeConverter {
//    public static class Serializer extends JsonSerializer<TimeRange> {
//
//        @Override
//        public void serialize(TimeRange value, JsonGenerator gen, SerializerProvider serializers) throws InvalidValueException {
//            try {
//                final var str = Objects.isNull(value) ? null : value.toString();
//                gen.writeString(str);
//            } catch (IOException e) {
//                throw new InvalidValueException("Serializer Failed");
//            }
//        }
//    }
//
//    public static class Deserializer extends JsonDeserializer<TimeRange> {
//
//        @Override
//        public TimeRange deserialize(JsonParser p, DeserializationContext ctxt) throws InvalidValueException {
//            try {
//                final var str = ((JsonNode) p.getCodec().readTree(p)).asText();
//                return Objects.isNull(str) || str.isEmpty() ? null : TimeRange.of(str);
//
//            } catch (IOException e) {
//                throw new InvalidValueException("Deserializer Failed");
//            }
//        }
//    }
//}