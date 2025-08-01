package myshampooisdrunk.incantatium.util;

import com.google.common.math.IntMath;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.math.BigDecimal;
import java.util.*;

public class BoxPolyhedronDecomposer {
    private final double resolution;
    private final BoxPolyhedron polygon;
    private Vec3d offset;
    private final Set<Vec3i> voxels;
    private Vec3d dims;

    public BoxPolyhedronDecomposer(BoxPolyhedron polygon) {
        this.polygon = polygon;
        this.resolution = calculateResolution();
        this.voxels = this.toVoxels();
    }

    private Set<Vec3i> toVoxels() {
        Set<Vec3d> tempCoords = new HashSet<>();
        polygon.boxes().forEach(b -> {
            tempCoords.add(b.getMinPos()); tempCoords.add(b.getMaxPos());
        });
//        List<Vec3d> coords = new ArrayList<>(tempCoords);
//        coords.sort((v1,v2) -> {
//            if(v1.equals(v2)) return 0;
//            // v1.x - v2.x + v1.y - v2.y + v1.z - v2.z
//            double d = v1.add(v2.multiply(-1)).dotProduct(new Vec3d(1,1,1));
//            return (int)(d/Math.abs(d));
//        });
        Pair<Vec3d, Vec3d> d = dimsDouble(tempCoords);
        Vec3d min = this.offset = d.getLeft();
        Vec3d max = d.getRight();
        this.dims = max.subtract(min).multiply(1/resolution).floorAlongAxes(EnumSet.of(Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z));
        if(dims.x < 0 || dims.y < 0 || dims.z < 0) return new HashSet<>();
        this.dims = new Vec3d(Math.max(1,dims.x), Math.max(1,dims.y), Math.max(1,dims.z));
        Set<Vec3i> voxels = new HashSet<>();
        for (Box box : polygon.boxes()) {
            Vec3d minVox = box.getMinPos().subtract(this.offset).multiply(1 / resolution).floorAlongAxes(EnumSet.of(Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z));
            Vec3d maxVox = box.getMaxPos().subtract(this.offset).multiply(1 / resolution);
            maxVox = new Vec3d(Math.min(Math.ceil(maxVox.x), dims.x), Math.min(Math.ceil(maxVox.y), dims.y), Math.min(Math.ceil(maxVox.z), dims.z));
            minVox = new Vec3d(Math.max(minVox.x, 0), Math.max(minVox.y, 0), Math.max(minVox.z, 0));
            if (minVox.x >= maxVox.x || minVox.y >= maxVox.y || minVox.z >= maxVox.z) continue;
            for (int x = (int)minVox.x; x < maxVox.x; x++)
                for (int y = (int)minVox.y; y < maxVox.y; y++)
                    for (int z = (int)minVox.z; z < maxVox.z; z++)
                        voxels.add(new Vec3i(x, y, z));
        }
        return voxels;
    }

    private Cube largestCube(Set<Vec3i> voxelSpace) {
        if(voxelSpace.isEmpty()) return null;
        Vec3i dims = new Vec3i((int)this.dims.getX(), (int)this.dims.getY(), (int)this.dims.getZ());
        Vec3i bestCorner = Vec3i.ZERO;
        int maxLength = 0;
        for (Vec3i v : voxelSpace) {
            int x = v.getX(), y = v.getY(), z = v.getZ();
            int max = Math.min(Math.min(dims.getX() - x, dims.getY() - y), dims.getZ() - z);
            if(max == 0) continue;
            for(int s = max; s > 0; s--) {
                if(s <= maxLength) break;
                boolean solid = true;
                if(x + s > dims.getX() || y + s > dims.getY() || z + s > dims.getZ()) solid = false;
                else {
                    for (int i = x; i < x + s; i++) {
                        for (int j = y; j < y + s; j++) {
                            for (int k = z; k < z + s; k++) {
                                if(!voxelSpace.contains(new Vec3i(i,j,k))) {
                                    solid = false;
                                    break;
                                }
                            }
                            if(!solid) break;
                        }
                        if(!solid) break;
                    }
                }
                if(solid) {
                    maxLength = s;
                    bestCorner = new Vec3i(x,y,z);
                    break;
                }
            }
        }
        if(maxLength != 0) {
            return new Cube(bestCorner, maxLength);
        }

        return null;
    }

    private double calculateResolution() {
        Set<Double> coordValues = new HashSet<>();
        polygon.boxes().forEach(b -> coordValues.addAll(List.of(b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ)));
        double max = 0;
        for (Double d : coordValues)
            if(Math.abs(d-Math.round(d)) > 0)
                max = Math.max(max, decimalPlaces(d));
        double factor = Math.pow(10,max);
        Set<Integer> scaled = new HashSet<>();
        for (Double d : coordValues)
            scaled.add((int)Math.abs(Math.round(d * factor)));

        int current = 0;
        for (Integer i : scaled)
            current = IntMath.gcd(i, current);

        if(current < 0.01 * factor) return 0.01;
        return current/factor;
    }

    private static Pair<Vec3d,Vec3d> dimsDouble(Set<Vec3d> voxels) {
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double zMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;
        double zMax = Double.MIN_VALUE;
        for (Vec3d voxel : voxels) {
            xMin = Math.min(xMin, voxel.getX());
            yMin = Math.min(yMin, voxel.getY());
            zMin = Math.min(zMin, voxel.getZ());
            xMax = Math.max(xMax, voxel.getX());
            yMax = Math.max(yMax, voxel.getY());
            zMax = Math.max(zMax, voxel.getZ());
        }
        return new Pair<>(new Vec3d(xMin, yMin, zMin), new Vec3d(xMax, yMax, zMax));
    }

    public Map<Vec3d, Double> decompose() {
        Map<Vec3d, Double> ret = new HashMap<>();
        if(voxels.isEmpty()) return new HashMap<>();
        Set<Vec3i> working = new HashSet<>(voxels);
        int decomposed = 0;
        while (decomposed < voxels.size()) {
            if(working.isEmpty())
                break;
            Cube largest = largestCube(working);
            if(largest == null) { // no largest -> search for base sized voxels
                if(working.isEmpty() && decomposed < voxels.size()) break;
                Set<Vec3i> rem = new HashSet<>();
                for (Vec3i v : working) {
                    int vx = v.getX(), vy = v.getY(), vz = v.getZ();
                    Pair<Vec3d, Double> worldCoords = getWorldCoords(new Vec3i(vx, vy, vz), 1);
                    ret.put(worldCoords.getLeft(), worldCoords.getRight());
                    rem.add(v);
                    decomposed++;
                }
                working.removeAll(rem);
                break;
            }

            Vec3i v = largest.corner;
            int vSize = largest.sideLength;
            int vx = v.getX(), vy = v.getY(), vz = v.getZ();
            Pair<Vec3d, Double> worldCoords = getWorldCoords(new Vec3i(vx, vy, vz), vSize);
            ret.put(worldCoords.getLeft(), worldCoords.getRight());
            Set<Vec3i> rem = new HashSet<>();
            for (int i = vx; i < vx + vSize; i++) {
                for (int j = vy; j < vy + vSize; j++) {
                    for (int k = vz; k < vz + vSize; k++) {
                        Vec3i temp;
                        if(working.contains(temp = new Vec3i(i,j,k))){
                            rem.add(temp);
                            decomposed++;
                        }
                    }
                }
            }
            if(!rem.isEmpty()) working.removeAll(rem);
            if(decomposed > voxels.size()) break;
        }
//        ret.forEach((v,d) -> {
//            System.out.println("polyhedron with corner " + v + " and length " + d);
//        });

        return ret;
    }

    public Pair<Vec3d, Double> getWorldCoords(Vec3i v, double length) {
        Vec3d origin = offset.add(new Vec3d(v.getX(), v.getY(), v.getZ()).multiply(resolution));
        length *= resolution;
        return new Pair<>(origin,length);
    }

    public static BoxPolyhedronDecomposer decomposer(Set<Box> boxes) {
        BoxPolyhedron shape = BoxPolyhedron.builder().add(boxes).build();
        return new BoxPolyhedronDecomposer(shape);
    }

    private static int decimalPlaces(double d) {
        return Math.max(0,new BigDecimal(""+d).stripTrailingZeros().scale());
    }

    private record Cube(Vec3i corner, int sideLength) {}

}
