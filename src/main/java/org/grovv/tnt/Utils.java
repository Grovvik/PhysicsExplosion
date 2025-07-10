package org.grovv.tnt;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public class Utils {
    private final NoiseGenerator perlin1;
    private final NoiseGenerator perlin2;
    private int rand;
    private int maxBlocks;
    private int minParticles;
    private int maxParticles;
    private double blocksPercent;
    private Long currNoise = 0L;


    public Utils (Settings settings) {
        this.perlin1 = new NoiseGenerator();
        this.perlin2 = new NoiseGenerator();

        this.initSettings(settings);
    }

    private void initSettings(Settings settings) {
        FileConfiguration storage = settings.getStorage();
        rand = storage.getInt("random_method") % 3;
        if (rand < 0) rand = 0;
        storage.set("random_method", rand);
        maxBlocks = Math.max(0, storage.getInt("max_falling_blocks"));
        storage.set("max_falling_blocks", maxBlocks);
        maxParticles = Math.max(0, Math.min(100_000, storage.getInt("max_particles")));
        minParticles = Math.max(0, Math.min(maxParticles, storage.getInt("min_particles")));
        storage.set("min_particles", minParticles);
        storage.set("max_particles", maxParticles);
        blocksPercent = Math.max(0, Math.min(100, storage.getDouble("falling_blocks_percent")));
        storage.set("falling_blocks_percent", blocksPercent);
        settings.saveStorage();
    }

    public void explode(EntityExplodeEvent event) {
        World world = event.getLocation().getWorld();
        if (world == null) return;
        particles(event);
        if (countFallingBlocks(event.getLocation().getWorld()) < maxBlocks) {
            destroyBlocksInSphere(event.getLocation(), Random(4, 6));
        }
    }

    public void particles(EntityExplodeEvent event) {
        event.getLocation().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, event.getLocation(),
                Random(minParticles, maxParticles), 0.2, 0.2, 0.2, 0.2);
    }
    public void destroyBlocksInSphere(Location center, int radius) {
        World world = center.getWorld();
        int blockX = center.getBlockX();
        int blockY = center.getBlockY();
        int blockZ = center.getBlockZ();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    Block currentBlock = world.getBlockAt(blockX + x, blockY + y, blockZ + z);

                    double distance = currentBlock.getLocation().distanceSquared(center);

                    if (distance < radius * radius && currentBlock.getType() != Material.AIR) {
                        if (currentBlock.getType() == Material.TNT)
                            continue;
                        if (currentBlock.getType() == Material.BEDROCK)
                            continue;
                        if (currentBlock.getType() == Material.OBSIDIAN)
                            continue;
                        if (currentBlock.getType() == Material.WATER)
                            continue;
                        if (currentBlock.getType() == Material.LAVA)
                            continue;
                        if (currentBlock.getType() == Material.COMMAND_BLOCK)
                            continue;
                        if (currentBlock.getType() == Material.REPEATING_COMMAND_BLOCK)
                            continue;
                        if (currentBlock.getType() == Material.CHAIN_COMMAND_BLOCK)
                            continue;
                        if (Random(1, 100) <= (int) blocksPercent) {
                            FallingBlock fallingBlock = world.spawnFallingBlock(
                                        currentBlock.getLocation().add(new Vector(0, 0.5, 0)),
                                        currentBlock.getType().createBlockData());
                            Vector motion;
                            if (rand == 0) {
                                motion = new Vector(
                                    ((float) Random2(-10, 10)) / 10f,
                                    ((float) Random2(5, 20)) / 15f,
                                    ((float) Random1(-10, 10)) / 10f);
                            } else if (rand == 1) {
                                motion = new Vector(
                                    ((float) Random(-7, 7)) / 25f,
                                    ((float) Random(5, 20)) / 15f,
                                    ((float) Random(-7, 7)) / 25f);
                            } else {
                                Location circle = currentBlock.getLocation().clone().subtract(center);
                                motion = new Vector(
                                    circle.getX()/radius/2,
                                    Math.abs(radius*2-distance(currentBlock.getLocation().getX(), currentBlock.getLocation().getZ(), center.getX(), center.getZ()))/radius/2,
                                    circle.getZ()/radius/2
                                );
                            }
                            fallingBlock.setVelocity(motion);
                            currentBlock.setType(Material.AIR);
                        }

                    }
                }
            }
        }
    }

    public int countFallingBlocks(World world) {
        int count = 0;
        for (Entity entity : world.getEntities()) {
            if (entity instanceof FallingBlock) {
                count++;
            }
        }
        return count;
    }

    private int Random1(int Min, int Max) {
        currNoise++;
        return (int) ((perlin1.noise(((double) currNoise) / 5.0) + 1) / 2 * (Max - Min) + Min);
    }

    private int Random2(int Min, int Max) {
        currNoise++;
        return (int) ((perlin2.noise(((double) currNoise) / 5.0) + 1) / 2 * (Max - Min) + Min);
    }

    public int Random(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
