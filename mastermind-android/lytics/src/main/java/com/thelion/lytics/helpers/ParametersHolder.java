package com.thelion.lytics.helpers;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParametersHolder {
    List<Parameter> parameters;

    public ParametersHolder() {
        this.parameters = new ArrayList<>();
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public void addParameters(Parameter... parameters) {
        this.parameters.addAll(Arrays.asList(parameters));
    }


}
