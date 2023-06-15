package bizzle.solver;

import bizzle.solid.ComponentGraph;
import bizzle.solid.bitgrid.BitGrid;
import bizzle.space.Coordinates3D;
import com.perunlabs.jsolid.d3.Axis;
import com.perunlabs.jsolid.d3.Solid;
import com.perunlabs.jsolid.d3.Vector3;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static com.perunlabs.jsolid.JSolid.cuboid;

public class SolidExport {

    public static final double SIZE = 0.99;
    public static final double RADIUS = 0.1;

    public static void save(ComponentGraph graph) {
        String prefix = String.valueOf(graph.getIgnored().hashCode());
        graph.getIgnored()
                .forEach(piece -> {
                            savePiece(prefix, piece);

                        }
                );
    }

    private static void savePiece(String prefix, BitGrid piece) {
        List<Coordinates3D> coordinates = piece.toCoordinates().stream().toList();
        Optional<Solid> reduce = coordinates
                .stream()
                .map(
                        coordinates3D ->
                                rCube()
                                        .moveBy(Vector3.vector3(coordinates3D.getX(), coordinates3D.getY(), coordinates3D.getZ()))
                ).reduce(Solid::add);
        Solid solid = reduce.get();

        for (int i=0;i<coordinates.size();i++) {
            for (int j=i+1;j<coordinates.size();j++) {
                Coordinates3D a = coordinates.get(i);
                Coordinates3D b = coordinates.get(j);
                 if (a.manhattanDistance(b)==1){
                     Coordinates3D sum = a.plus(b);

                     solid = solid.add(
                             joiner().moveBy(Vector3.vector3(sum.getX()/2.0, sum.getY()/2.0, sum.getZ()/2.0))
                     );
                 }
            }
        }




        try {
            com.perunlabs.jsolid.d3.Stl.toStl(solid, Paths.get(prefix + "-" + piece.hashCode() + ".stl"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Solid rCube() {
        return Stream.of(Axis.X, Axis.Y, Axis.Z)
                .map(axis -> (Solid) cuboid(SIZE, SIZE, SIZE).cornerR(axis, RADIUS))
                .reduce(Solid::intersect).get();

    }

    public static Solid joiner() {
        return cuboid(SIZE - RADIUS, SIZE - RADIUS, SIZE - RADIUS);
    }


}
