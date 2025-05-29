package myshampooisdrunk.incantatium.util;


import net.minecraft.util.math.Box;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record BoxPolyhedron(Set<Box> boxes) {
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private final Set<Box> boxes;
        public Builder() {
            boxes = new HashSet<>();
        }
        public Builder(Collection<Box> boxes) {
            this();
            this.boxes.addAll(boxes);
        }
        public Builder(Box... boxes) {
            this.boxes = Set.of(boxes);
        }
        public Builder add(Box box) {
            boxes.add(box);
            return this;
        }
        public Builder add(Box... box) {
            boxes.addAll(Set.of(box));
            return this;
        }
        public Builder add(Collection<Box> boxes) {
            this.boxes.addAll(boxes);
            return this;
        }
        public BoxPolyhedron build() {
            return new BoxPolyhedron(boxes);
        }
    }
}
