package bizzle.space;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, exclude = {"space"})
public class Coordinates3D extends Vector3D {
    private final Space space;

    public Coordinates3D(Space space, int x, int y, int z) {
        super(x, y, z);
        this.space = space;
    }

    public int index() {
        return getX() +
                getY() * getSpace().getSizeX() +
                getZ() * getSpace().getSizeX() * getSpace().getSizeY();
    }

    public Coordinates3D plus(Vector3D dir) {
        return new Coordinates3D(space, this.getX() + dir.getX(), this.getY() + dir.getY(), this.getZ() + dir.getZ());
    }

    public static Coordinates3D expand(int index, Space space) {
        int z = index / (space.getSizeX() * space.getSizeY());
        int xy = index % (space.getSizeX() * space.getSizeY());
        int y = xy / space.getSizeX();
        int x = xy % space.getSizeX();
        return Coordinates3D.builder()
                .space(space)
                .x(x)
                .y(y)
                .z(z)
                .build();
    }

    public boolean isValid() {
        return getSpace().contains(this);
    }

}
