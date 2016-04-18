package pl.klimas7.calculatorPlugin;

import java.io.IOException;
import java.util.regex.Pattern;

import hudson.AbortException;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.ParameterValue;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;

public class CalculatorParameterValue extends ParameterValue {

    private String first;
    private String second;
    private Operation operation;

    @DataBoundConstructor
    public CalculatorParameterValue(String name, String description) {
        this(name, description, null, null, Operation.ADD);
    }

    public CalculatorParameterValue(String name, String description, String first, String second, Operation operation) {
        super(name, description);
        this.first = first;
        this.second = second;
        this.operation = operation;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String toString() {
        return "(CalculatorParameterValue) " + this.getName() + ": First: " + this.first + " Second: " + this.second;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?, ?> build) {
        Pattern pattern = Pattern.compile("\\d*");
        if (!pattern.matcher(first).matches() || !pattern.matcher(second).matches()) {
            // abort the build within BuildWrapper
            return new BuildWrapper() {
                @Override
                public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                    throw new AbortException("Invalid value type! Vale must be integer");
                }
            };
        } else {
            return null;
        }
    }
}
