package bizzle.space;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
public class Vector3D {
    public final static Vector3D ZERO = new Vector3D(0, 0, 0);
    private final int x;
    private final int y;
    private final int z;

    public Vector3D plus(Vector3D dir) {
        return new Vector3D(this.x + dir.x, this.y + dir.y, this.z + dir.z);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Vector3D> T plusBuilder(Vector3D dir) {
        return (T) this.toBuilder()
                .x(this.x + dir.x)
                .y(this.y + dir.y)
                .z(this.z + dir.z)
                .build();
    }

    public int manhattanMagnitude() {
        return Math.abs(this.x) + Math.abs(this.y) + Math.abs(this.z);
    }

    public int manhattanDistance(Coordinates3D b) {
        return Math.abs(this.getX() - b.getX()) +
                Math.abs(this.getY() - b.getY()) +
                Math.abs(this.getZ() - b.getZ());
    }
}
