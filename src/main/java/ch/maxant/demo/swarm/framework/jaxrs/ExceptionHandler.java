package ch.maxant.demo.swarm.framework.jaxrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class ExceptionHandler implements ExceptionMapper<Exception> {

    ObjectMapper objectMapper = JacksonConfig.getMapper();

    @Override
    public Response toResponse(final Exception exception) {

        Throwable rootCause = getCause(exception);

        try {
            if(rootCause instanceof ConstraintViolationException){
                ObjectNode body = buildExceptionBody(rootCause);
                ArrayNode constraintViolations = objectMapper.createArrayNode();
                for(ConstraintViolation cv : ((ConstraintViolationException) rootCause).getConstraintViolations()){
                    constraintViolations.add(objectMapper.createObjectNode().put("violation", (cv.getPropertyPath() + " " + cv.getMessage())));
                }
                body.set("constraintViolations", constraintViolations);

                return Response
                            .status(Response.Status.PRECONDITION_FAILED)
                            .entity(objectMapper.writeValueAsString(body))
                            .build();
            }else{
                ObjectNode body = buildExceptionBody(exception);
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(objectMapper.writeValueAsString(body))
                        .build();
            }
        } catch (JsonProcessingException e) {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(e) //cant turn it into a nice thing => just spit it out any which way. should never happen really. if it does, lets fix the framework.
                        .build();
        }
    }

    private ObjectNode buildExceptionBody(Throwable t) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("class", t.getClass().getName());
        body.put("message", t.getMessage());
        body.put("stacktrace", ExceptionUtils.getStackTrace(t));
        return body;
    }

    private Throwable getCause(Throwable t) {
        Throwable cause = t.getCause();
        if(cause != null)
            return getCause(cause);
        return t;
    }
}