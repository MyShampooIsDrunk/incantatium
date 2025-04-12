package myshampooisdrunk.incantatium.component;

import org.ladysnake.cca.api.v3.component.Component;

public interface Toggle extends Component {
    boolean get();
    void set(boolean state);
}
