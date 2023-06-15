package bizzle.solid;

import bizzle.solid.bitgrid.BitGrid;
import bizzle.solid.bitgrid.BitGridFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Getter
public class ComponentGraph {
    @EqualsAndHashCode.Exclude
    private final BitGridFactory factory;
    @EqualsAndHashCode.Exclude
    private BitGrid componentsUnion; //Cache Union
    private final HashSet<BitGrid> components;
    private final List<BitGrid> ignored;


    public ComponentGraph(BitGridFactory factory, List<BitGrid> components) {
        this.factory = factory;
        this.components = new HashSet<>();
        this.components.addAll(components);
        this.componentsUnion = components.stream().reduce(factory.empty(), BitGrid::union);
        ignored = new ArrayList<>(components.size());
        assert (isValid());
    }

    private ComponentGraph(ComponentGraph other) {
        factory = other.factory;
        components = new HashSet<>(other.components);
        ignored = new ArrayList<>(other.ignored);
        componentsUnion = other.componentsUnion.copy();
        assert (isValid());
    }


    public ComponentGraph forgetAbout(BitGrid piece) {
        if (components.contains(piece)) {
            ComponentGraph copy = new ComponentGraph(this);
            boolean removed = copy.components.remove(piece);
            assert (removed);
            copy.componentsUnion = copy.componentsUnion.difference(piece);
            copy.ignored.add(piece);
            assert (copy.isValid());
            return copy;
        }
        return null;
    }

    public boolean isDone() {
        return componentsUnion.cardinality() <= 0;
    }

    public ComponentGraph mergeComponents(BitGrid component1, BitGrid component2) {
        ComponentGraph copy = new ComponentGraph(this);
        boolean remove = copy.components.remove(component1);
        boolean remove1 = copy.components.remove(component2);
        assert (remove && remove1);
        copy.components.add(component1.union(component2));
        assert (copy.isValid());
        return copy;
    }

    public boolean isValid() {
        Optional<BitGrid> ignoredIntersection = Optional.empty();
        if (ignored.size() > 1) {
            ignoredIntersection = ignored.stream().reduce(BitGrid::intersection);
        }
        Optional<BitGrid> componentsIntersection = Optional.empty();
        if (components.size() > 1) {
            componentsIntersection = components.stream().reduce(BitGrid::intersection);
        }
        if (ignoredIntersection.isPresent() && ignoredIntersection.get().cardinality() > 0) {
            System.out.println(this);
            return false;
        }
        if (componentsIntersection.isPresent() && componentsIntersection.get().cardinality() > 0) {
            System.out.println(this);
            return false;
        }
        Optional<BitGrid> ignoredUnion = ignored.stream().reduce(BitGrid::union);

        if (ignoredUnion.isPresent()) {
            Optional<BitGrid> any = components.stream().filter(component -> component.intersection(ignoredUnion.get()).cardinality() > 0).findAny();
            if (any.isPresent()) {
                System.out.println(this);
                return false;
            }
        }
        return true;

    }
}
