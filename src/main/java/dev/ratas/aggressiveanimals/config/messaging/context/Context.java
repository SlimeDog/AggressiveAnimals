package dev.ratas.aggressiveanimals.config.messaging.context;

public interface Context {
    public static final VoidContext NULL = new VoidContext();

    String fill(String msg);

    public class VoidContext implements Context {

        private VoidContext() {
            // only local instsance
        }

        @Override
        public String fill(String msg) {
            return msg;
        }

    }

}
