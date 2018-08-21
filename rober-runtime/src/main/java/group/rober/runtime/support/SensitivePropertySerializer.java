package group.rober.runtime.support;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SensitivePropertySerializer extends StdSerializer<Object> {

    private String mask = "*****";

    public SensitivePropertySerializer() {
        super(Object.class);
    }

    public SensitivePropertySerializer(Class<?> handlerClass) {
        super(handlerClass, false);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(mask);
    }
}
