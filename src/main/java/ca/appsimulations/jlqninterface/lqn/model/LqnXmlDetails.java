package ca.appsimulations.jlqninterface.lqn.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@Data
@Accessors(fluent = true, chain = true)
public class LqnXmlDetails {
    private final String xmlnsXsi;
    private String comment;
    private String name;
    private String description;
    private String schemaLocation;
}
