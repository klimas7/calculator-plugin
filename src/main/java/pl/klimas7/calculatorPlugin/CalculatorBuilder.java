package pl.klimas7.calculatorPlugin;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nonnull;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.ParametersAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class CalculatorBuilder extends Builder implements SimpleBuildStep {

    private final String parameterName;

    @DataBoundConstructor
    public CalculatorBuilder(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {

        NumericalSystem numericalSystem = getDescriptor().getNumericalSystem();
        CalculatorParameterValue calculatorParameterValue = (CalculatorParameterValue) run.getAction(ParametersAction.class).getParameter(parameterName);
        if (calculatorParameterValue == null) {
            listener.fatalError("Bad parameter name: " + parameterName);
            run.setResult(Result.FAILURE);
            return;
        }
        BigInteger first = new BigInteger(calculatorParameterValue.getFirst());
        BigInteger second = new BigInteger(calculatorParameterValue.getSecond());
        Operation operation = calculatorParameterValue.getOperation();

        String answer = "nan";
        if (operation != Operation.DIV || second.intValue() != 0) {
            answer = calculate(first, second, operation, numericalSystem);
        }

        listener.getLogger().println("Numerical system is: " + numericalSystem);
        listener.getLogger().println("Calculation result:");
        listener.getLogger().println(first + " " + operation.getSymbol() + " " + second + " = " + answer);
    }

    private String calculate(BigInteger first, BigInteger second, Operation operation, NumericalSystem numericalSystem) {
        BigInteger answer = null;
        switch (operation) {
            case ADD:
                answer = first.add(second);
                break;
            case SUB:
                answer = first.subtract(second);
                break;
            case MUL:
                answer = first.multiply(second);
                break;
            case DIV:
                answer = first.divide(second);
        }

        return answer.toString(numericalSystem.getBase());
    }

    public String getParameterName() {
        return parameterName;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private NumericalSystem numericalSystem = NumericalSystem.HEX;

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }


        @Override
        public String getDisplayName() {
            return "Calculate";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            numericalSystem = NumericalSystem.valueOf((String) json.get("numericalSystem"));
            save();
            return super.configure(req, json);
        }

        public NumericalSystem getNumericalSystem() {
            return numericalSystem;
        }
    }
}
