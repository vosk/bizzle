package bizzle.space;

import java.util.List;
import java.util.stream.Stream;

public class Directions {

    public static final Vector3D LEFT = new Vector3D(-1, 0, 0);
    public static final Vector3D RIGHT = new Vector3D(1, 0, 0);
    public static final Vector3D FRONT = new Vector3D(0, -1, 0);
    public static final Vector3D BACK = new Vector3D(0, 1, 0);
    public static final Vector3D DOWN = new Vector3D(0, 0, -1);
    public static final Vector3D UP = new Vector3D(0, 0, 1);

    public static Stream<Coordinates3D> positiveNeighbours(Coordinates3D coordinates3D) {
        return Stream.of(RIGHT, BACK, UP)
                .map(coordinates3D::plus)
                .filter(Coordinates3D::isValid);
    }

    public static Stream<Coordinates3D> negativeNeighbours(Coordinates3D coordinates3D) {
        return Stream.of(LEFT, FRONT, DOWN)
                .map(coordinates3D::plus)
                .filter(Coordinates3D::isValid);
    }

    public static List<Vector3D> neighBours() {
        return List.of(RIGHT, BACK, UP, LEFT, FRONT, DOWN);
    }
}
