package com.thelion.lytics.helpers;



import java.util.List;

public class ParametersHolder {
    List<Parameter> parameters;

    public ParametersHolder(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

}
