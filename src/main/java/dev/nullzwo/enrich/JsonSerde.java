package dev.nullzwo.enrich;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class JsonSerde<T> implements Serde<T> {
    public static JsonSerde<Object> INST = new JsonSerde<>();

    public static class JsonSerializer<T> implements Serializer<T> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, T pojo) {
            try {
                var tree = mapper.valueToTree(pojo);
                ObjectNode obj;
                if (tree.isObject()) {
                    obj = (ObjectNode) tree;
                } else {
                    obj = new ObjectNode(new JsonNodeFactory(false));
                    obj.set("$$value", tree);
                }
                obj.set("$$clazz", new TextNode(pojo.getClass().getName()));
                return mapper.writeValueAsBytes(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Serializer<T> serializer() {
        return new JsonSerializer<>();
    }

    public static class JsonDeserializer<T> implements Deserializer<T> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public T deserialize(String topic, byte[] data) {
            try {
                var tree = mapper.readTree(data);
                if (tree.isObject()) {
                    ObjectNode obj = (ObjectNode) tree;
                    var clazz = Class.forName(obj.remove("$$clazz").asText());
                    if (obj.has("$$value"))
                        return (T) mapper.convertValue(obj.get("$$value"), clazz);
                    else
                        return (T) mapper.convertValue(tree, clazz);
                } else {
                    throw new RuntimeException("unsupported datatype: " + tree.getNodeType());
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Deserializer<T> deserializer() {
        return new JsonDeserializer<>();
    }
}
