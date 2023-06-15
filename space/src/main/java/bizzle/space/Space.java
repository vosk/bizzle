package bizzle.space;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Getter
@EqualsAndHashCode(exclude =  "allPoints")
public class Space implements Locus<Collection<Coordinates3D>> {
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final List<Coordinates3D> allPoints;

    public Space(Space otherSpace) {
        this(otherSpace.getSizeX(), otherSpace.getSizeY(), otherSpace.getSizeZ());
    }

    public Space(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        allPoints = IntStream.range(0,getSize())
                .mapToObj(index -> Coordinates3D.expand(index, this))
                .toList();
    }


    public boolean contains(Coordinates3D coordinates3D) {
        return coordinates3D.getX() >=0 && coordinates3D.getX() < sizeX
                && coordinates3D.getY() >=0 && coordinates3D.getY() < sizeY
                && coordinates3D.getZ() >=0 && coordinates3D.getZ() < sizeZ;
    }

    public int getSize() {
        return sizeX*sizeY*sizeZ;
    }

    public List<Coordinates3D> all() {
        return allPoints;
    }
    public List<Coordinates3D> planeX(int x) {
        return filterCoordinates(coordinates3D -> coordinates3D.getX() == x);
    }
    public List<Coordinates3D> planeY(int y) {
        return filterCoordinates(coordinates3D -> coordinates3D.getX() == y);
    }
    public List<Coordinates3D> planeZ(int z) {
        return filterCoordinates(coordinates3D -> coordinates3D.getX() == z);
    }

    protected List<Coordinates3D> filterCoordinates(Predicate<Coordinates3D> predicate) {
        return allPoints.stream().filter(predicate).toList();
    }



}
