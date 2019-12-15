package be.sgerard.test.i18n.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * Holds the result of an MVC response.
 *
 * @author Sebastien Gerard
 */
public class JsonHolderResultHandler<T> implements ResultHandler {

    private final ObjectMapper objectMapper;
    private final TypeReference<T> typeReference;
    private final Class<T> classReference;

    private T value;

    public JsonHolderResultHandler(ObjectMapper objectMapper, Class<T> responseClazz) {
        this.objectMapper = objectMapper;
        this.classReference = responseClazz;
        this.typeReference = null;
    }

    public JsonHolderResultHandler(ObjectMapper objectMapper, TypeReference<T> typeReference) {
        this.objectMapper = objectMapper;
        this.typeReference = typeReference;
        this.classReference = null;
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        this.value = (this.typeReference == null)
                ? objectMapper.readValue(result.getResponse().getContentAsString(), this.classReference)
                : objectMapper.readValue(result.getResponse().getContentAsString(), this.typeReference);
    }

    /**
     * Returns the value.
     */
    public T getValue() {
        return value;
    }
}
