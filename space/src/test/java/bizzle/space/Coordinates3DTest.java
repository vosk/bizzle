package bizzle.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Coordinates3DTest {

    @Test
    public void collapseWorks() {
        Space space = new Space(2,3,4);
        int index=0;

        for (int z=0;z<space.getSizeZ();z++) {
            for (int y=0;y<space.getSizeY();y++) {
                for (int x=0;x<space.getSizeX();x++) {
                    Coordinates3D coordinates3D = Coordinates3D.builder()
                            .space(space)
                            .x(x)
                            .y(y)
                            .z(z)
                            .build();
                    assertEquals(index++, coordinates3D.index());
                }
            }
        }
    }
    @Test
    public void expandWorks() {
        Space space = new Space(2,3,4);
        int index=0;

        for (int z=0;z<space.getSizeZ();z++) {
            for (int y=0;y<space.getSizeY();y++) {
                for (int x=0;x<space.getSizeX();x++) {
                    Coordinates3D expanded = Coordinates3D.expand(index++,space);
                    assertEquals(x, expanded.getX());
                    assertEquals(y, expanded.getY());
                    assertEquals(z, expanded.getZ());
                }
            }
        }
    }
}