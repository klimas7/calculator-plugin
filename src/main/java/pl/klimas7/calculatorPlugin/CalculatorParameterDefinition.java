package pl.klimas7.calculatorPlugin;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class CalculatorParameterDefinition extends ParameterDefinition {

    @DataBoundConstructor
    public CalculatorParameterDefinition(String name, String description) {
        super(name, description);
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        String name = getName();
        CalculatorParameterValue calculatorParameterValue = new CalculatorParameterValue(name, getDescription());
        calculatorParameterValue.setFirst(req.getParameter(name + "_first"));
        calculatorParameterValue.setSecond(req.getParameter(name + "_second"));
        calculatorParameterValue.setOperation(Operation.valueOf(req.getParameter(name + "_operation")));
        return calculatorParameterValue;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        CalculatorParameterValue calculatorParameterValue = new CalculatorParameterValue(getName(), getDescription(), "0", "0", Operation.ADD);
        return calculatorParameterValue;
    }


    public Operation[] getOperations() {
        return Operation.values();
    }

    public String getRootUrl() {
        return Jenkins.getInstance().getRootUrl();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
        public static final class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Calculator parameter";
        }

        /**
         * Called to validate the passed user entered value against the configured regular expression.
         */
        public FormValidation doValidate(@QueryParameter("value") final String value) {
            try {
                if (Pattern.matches("\\d*", value)) {
                    return FormValidation.ok();
                } else {
                    return FormValidation.error("Należy podać liczbę całkowitą");
                }
            } catch (PatternSyntaxException pse) {
                return FormValidation.error("Unexpected error: " + pse.getDescription());
            }
        }
    }
}
