package io.cuber.vdcraft.terrain;

import io.cuber.vdcraft.display.Section;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TerrainBuilder {
  private static final int WATER_LEVEL = 32;
  private static final int TERRAIN_MAX_HEIGHT = 80;
  private static final int TERRAIN_MIN_HEIGHT = 10;
  private static final int NOISE_OCTAVES = 5;
  private static final double NOISE_AMPLITUDE_FACTOR = 0.5;
  private static final double NOISE_FREQUENCY_FACTOR = 2.0;
  private static final double NOISE_SCALE = 0.01;
  private static final double ORE_NOISE_SCALE = 0.1;

  private static class OreRule {
    final CubeTypes cubeType;
    final int maxDepth;
    final double noiseThreshold;

    OreRule(CubeTypes cubeType, int maxDepth, double noiseThreshold) {
        this.cubeType = cubeType;
        this.maxDepth = maxDepth;
        this.noiseThreshold = noiseThreshold;
    }
  }

  private static final List<OreRule> ORE_RULES = Arrays.asList(
      new OreRule(CubeTypes.DIAMOND_ORE, 16, 1.7),
      new OreRule(CubeTypes.GOLD_ORE, 32, 1.5),
      new OreRule(CubeTypes.REDSTONE_ORE, 20, 1.4),
      new OreRule(CubeTypes.IRON_ORE, 64, 1.3),
      new OreRule(CubeTypes.COAL_ORE, 128, 1.2)
  );
  private final long seed;
  private final Random random;

  public TerrainBuilder(long seed) {
    this.seed = seed;
    this.random = new Random(seed);
  }

  public TerrainBuilder() {
    this(System.currentTimeMillis());
  }

  public void buildSection(Section section) {
    int sectionX = (int) (section.getOrigin().x / GameMap.CUBE_SIZE);
    int sectionZ = (int) (section.getOrigin().z / GameMap.CUBE_SIZE);

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        int worldX = sectionX + x;
        int worldZ = sectionZ + z;

        double height = calcHeight(worldX, worldZ);
        int terrainHeight = (int) height;

        for (int y = 0; y < terrainHeight && y < 128; y++) {
          CubeTypes cubeType;

          if (y == terrainHeight - 1) {
            cubeType = terrainHeight > 60 ? CubeTypes.STONE : CubeTypes.GRASS;
          } else if (y >= terrainHeight - 4) {
            cubeType = terrainHeight > 60 ? CubeTypes.STONE : CubeTypes.DIRT;
          } else if (y == 0) {
            cubeType = CubeTypes.BEDROCK;
          } else {
            cubeType = pickOre(worldX, y, worldZ);
          }

          section.set(x, y, z, cubeType.makeCube(worldX, y, worldZ));
        }
      }
    }
  }

  private double calcHeight(int x, int z) {
    double height = 0;
    double amplitude = 1.0;
    double frequency = 1.0;
    double maxValue = 0;

    for (int octave = 0; octave < NOISE_OCTAVES; octave++) {
      height += noise(x * frequency * NOISE_SCALE, z * frequency * NOISE_SCALE) * amplitude;
      maxValue += amplitude;
      amplitude *= NOISE_AMPLITUDE_FACTOR;
      frequency *= NOISE_FREQUENCY_FACTOR;
    }

    height = height / maxValue;
    height = (height + 1) / 2;
    return TERRAIN_MIN_HEIGHT + height * (TERRAIN_MAX_HEIGHT - TERRAIN_MIN_HEIGHT);
  }

  private double noise(double x, double z) {
    int xi = (int) Math.floor(x) & 255;
    int zi = (int) Math.floor(z) & 255;
    double xf = x - Math.floor(x);
    double zf = z - Math.floor(z);

    double u = smooth(xf);
    double v = smooth(zf);

    int aa = scramble(xi) + zi;
    int ab = scramble(xi) + zi + 1;
    int ba = scramble(xi + 1) + zi;
    int bb = scramble(xi + 1) + zi + 1;

    double x1 = mix(gradient(scramble(aa), xf, zf), gradient(scramble(ba), xf - 1, zf), u);
    double x2 = mix(gradient(scramble(ab), xf, zf - 1), gradient(scramble(bb), xf - 1, zf - 1), u);

    return mix(x1, x2, v);
  }

  private CubeTypes pickOre(int x, int y, int z) {
    double oreNoise = noise(x * ORE_NOISE_SCALE, z * ORE_NOISE_SCALE) + noise(y * ORE_NOISE_SCALE, x * ORE_NOISE_SCALE);

    for (OreRule rule : ORE_RULES) {
        if (y < rule.maxDepth && oreNoise > rule.noiseThreshold) {
            return rule.cubeType;
        }
    }

    return CubeTypes.STONE;
  }

  private double smooth(double t) {
    return t * t * t * (t * (t * 6 - 15) + 10);
  }

  private double mix(double a, double b, double t) {
    return a + t * (b - a);
  }

  private double gradient(int hash, double x, double z) {
    int h = hash & 3;
    double u = h < 2 ? x : z;
    double v = h < 2 ? z : x;
    return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
  }

  private int scramble(int i) {
    i = i * 393241 + (int) (seed & 0xFFFFFFFF);
    i = (i << 17) ^ i;
    return (i * (i * i * 1013904223 + 1664117991) + 1402024253) & 0x7fffffff;
  }
}
