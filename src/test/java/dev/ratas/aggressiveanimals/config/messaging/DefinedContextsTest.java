package dev.ratas.aggressiveanimals.config.messaging;

import org.junit.Assert;
import org.junit.Test;

import dev.ratas.aggressiveanimals.config.messaging.Context.ContextBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.DelegateMultiContextBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.DelegatingContext;
import dev.ratas.aggressiveanimals.config.messaging.Context.HelperDelegateBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.StringReplacementContext;

public class DefinedContextsTest {

    @Test
    public void test_StringReplacementContext_replaces() {
        String placeholder = "%MySpecialPH%";
        String msg = "Message(SRC) that includes PH: " + placeholder + " and some other text";
        String replacement = "IMPORTANT DATA";
        StringReplacementContext context = new StringReplacementContext(placeholder, replacement);
        String output = context.fill(msg);
        Assert.assertFalse("Output cannot contain placholder", output.contains(placeholder));
        Assert.assertTrue("Output should contain replacement", output.contains(replacement));
    }

    @Test
    public void test_DelegatingContext_delegates() {
        String placeholder1 = "%MySpecialPH_ONE%";
        String placeholder2 = "%MySpecialPH_TWO%";
        String msg = "Message(DC) that includes PH: " + placeholder1 + " and some other text (PH" + placeholder2 + ")";
        String replacement1 = "First Data";
        String replacement2 = "Second Data";
        StringReplacementContext context1 = new StringReplacementContext(placeholder1, replacement1);
        StringReplacementContext context2 = new StringReplacementContext(placeholder2, replacement2);
        DelegatingContext context = new DelegatingContext(context1, context2);
        String output = context.fill(msg);
        Assert.assertFalse("Output cannot contain placholder onw", output.contains(placeholder1));
        Assert.assertFalse("Output cannot contain placholder two", output.contains(placeholder2));
        Assert.assertTrue("Output should contain replacement one", output.contains(replacement1));
        Assert.assertTrue("Output should contain replacement two", output.contains(replacement2));
    }

    @Test
    public void test_ContextBuilder_buildsWorking() {
        String placeholder = "%MySpecialPH%";
        String msg = "Message(CB) that includes PH: " + placeholder + " and some other text";
        String replacement = "IMPORTANT DATA";
        ContextBuilder<String> builder = new ContextBuilder<>(placeholder, s -> "ADD" + s);
        Context context = builder.context(replacement);
        String output = context.fill(msg);
        Assert.assertFalse("Output cannot contain placholder", output.contains(placeholder));
        Assert.assertTrue("Output should contain replacement", output.contains(replacement));
        Assert.assertTrue("Output should contain replacement (full)", output.contains("ADD" + replacement));
    }

    @Test
    public void test_DelegateMultiContextBuilder_buildsWorking() {
        String placeholder1 = "%MySpecialPH%";
        String placeholder2 = "%TheOtherPH%";
        String msg = "Message(DMCB) that includes PH: " + placeholder1 + " and some other text " + placeholder2 + " ++";
        String strReplacement = "lower_case stuff";
        int intRepalacement = 10;
        ContextBuilder<String> upperCaseBuilder = new ContextBuilder<>(placeholder1, s -> s.toUpperCase());
        ContextBuilder<Integer> intBuilder = new ContextBuilder<>(placeholder2, nr -> String.valueOf(nr));
        DelegateMultiContextBuilder<String, Integer> builder = new DelegateMultiContextBuilder<>(upperCaseBuilder,
                intBuilder);
        Context context = builder.context(strReplacement, intRepalacement);
        String output = context.fill(msg);
        Assert.assertFalse("Output cannot contain placholder one", output.contains(placeholder1));
        Assert.assertFalse("Output cannot contain placholder two", output.contains(placeholder2));
        Assert.assertTrue("Output should contain replacement one", output.contains(strReplacement.toUpperCase()));
        Assert.assertTrue("Output should contain replacement two", output.contains(String.valueOf(intRepalacement)));
    }

    @Test
    public void test_HelperDelegateBuilder_working() {
        String ph = "%PH1%";
        String msg = "Long MSG " + ph + " with placeholder";
        ContextBuilder<Integer> intBuilder = new ContextBuilder<>(ph,
                i -> String.valueOf(i) + (i == 0 ? "false" : "true"));
        class Converter {
            Integer convert(Boolean b) {
                return b ? 1 : 0;
            }
        }
        HelperDelegateBuilder<Integer, Boolean, Converter> helper = new HelperDelegateBuilder<>(intBuilder,
                (b, c) -> c.convert(b));
        Context cont = helper.context(true, new Converter());
        String output = cont.fill(msg);
        Assert.assertFalse("Output cannot contain placeholder - true", output.contains(ph));
        Assert.assertTrue("Output should contain converted replacement - true (1)", output.contains("1"));
        Assert.assertTrue("Output should contain converted replacement - true (2)", output.contains("true"));
        Assert.assertTrue("Output should contain converted replacement - true (3)", output.contains("1true"));
        cont = helper.context(false, new Converter());
        output = cont.fill(msg);
        Assert.assertFalse("Output cannot contain placeholder - false", output.contains(ph));
        Assert.assertTrue("Output should contain converted replacement - false (1)", output.contains("0"));
        Assert.assertTrue("Output should contain converted replacement - false (2)", output.contains("false"));
        Assert.assertTrue("Output should contain converted replacement - false (3)", output.contains("0false"));
    }

}
