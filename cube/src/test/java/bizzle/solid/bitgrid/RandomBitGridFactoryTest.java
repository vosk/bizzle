package bizzle.solid.bitgrid;

import org.junit.jupiter.api.Test;
import bizzle.space.Coordinates3D;
import bizzle.space.Space;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class RandomBitGridFactoryTest {
    public BitGridFactory factory = new BitGridFactory(new Space(6,6,6));
    public Random random = new Random(0);

    @Test
    public void testBitUniformity() {
        HashMap<Coordinates3D, Integer> buckets= new HashMap<>();
        RandomBitGridFactory randomBitGridFactory = new RandomBitGridFactory(random, factory);
        final int trials= 2000000;
        for (int i=0;i<trials;i++){
            Collection<Coordinates3D> coordinates = randomBitGridFactory.generateNext().toCoordinates();
            coordinates
                    .forEach( coordinates3D -> {
                        buckets.compute(coordinates3D, (key, old) -> {
                            if (old == null) {
                                return 1;
                            }
                            else return old+1;
                        });
                    });

        }
        buckets.entrySet().forEach(
                entry-> {
                    Integer value = entry.getValue();
                    double sigma=Math.sqrt(trials*0.5*0.5);
                    assertTrue( value>trials/2.0-3*sigma
                            && value<trials/2.0+3*sigma,
                            ()-> "bit "+entry.getKey().index()+" is " + value
                    );
                }

        );
        System.out.println(buckets.entrySet().stream().collect(Collectors.toMap(k -> k.getKey().index(), e -> String.valueOf(e.getValue()))));

    }

}