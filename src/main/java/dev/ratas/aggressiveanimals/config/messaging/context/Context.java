package dev.ratas.aggressiveanimals.config.messaging.context;

public interface Context {
    VoidContext NULL = new VoidContext();

    String fill(String msg);

    class VoidContext implements Context {

        private VoidContext() {
            // only local instsance
        }

        @Override
        public String fill(String msg) {
            return msg;
        }

    }

}
