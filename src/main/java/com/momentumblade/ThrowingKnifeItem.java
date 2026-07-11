package com.momentumblade;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

/**
 * A throwing knife. Rather than spawning a custom entity (not allowed for this tier),
 * the knife performs an instant raycast when thrown. Damage scales with how far the
 * ray travels before hitting an entity — the farther the target, the greater the hit.
 */
public class ThrowingKnifeItem extends Item {

    /** Maximum distance the knife can travel, in blocks. */
    private static final double MAX_RANGE = 32.0D;
    /** Base damage when hitting a target at point-blank range. */
    private static final float BASE_DAMAGE = 3.0F;
    /** Additional damage added per block travelled up to MAX_RANGE. */
    private static final float DAMAGE_PER_BLOCK = 0.5F;

    public ThrowingKnifeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Cooldown to prevent spamming.
        player.getItemCooldownManager().set(this, 20);

        if (!world.isClient) {
            Vec3d start = player.getEyePos();
            Vec3d direction = player.getRotationVec(1.0F);
            Vec3d end = start.add(direction.multiply(MAX_RANGE));

            // First, find where a block would stop the knife.
            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    start, end,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player));

            Vec3d limit = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getPos();
            double maxTravel = start.distanceTo(limit);

            // Now search for the closest entity along the ray up to the block/limit.
            EntityHitResult entityHit = raycastEntities(world, player, start, limit, maxTravel);

            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entityHit.getEntity();
                double travelled = start.distanceTo(entityHit.getPos());
                float damage = computeDamage(travelled);

                DamageSource source = world.getDamageSources().thrown(player, player);
                target.damage(source, damage);

                world.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            // Throw sound at the player.
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.6F, 1.4F);

            // Consume one knife unless in creative.
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        player.swingHand(hand, true);
        return TypedActionResult.success(stack, world.isClient());
    }

    /**
     * Damage scales linearly with travel distance, clamped to MAX_RANGE.
     */
    private float computeDamage(double travelled) {
        double clamped = Math.min(travelled, MAX_RANGE);
        return BASE_DAMAGE + (float) (clamped * DAMAGE_PER_BLOCK);
    }

    /**
     * Finds the closest living entity intersected by the ray between start and limit.
     */
    private EntityHitResult raycastEntities(World world, PlayerEntity shooter,
                                            Vec3d start, Vec3d limit, double maxTravel) {
        Box searchBox = new Box(start, limit).expand(1.0D);
        List<Entity> candidates = world.getOtherEntities(shooter, searchBox,
                e -> e instanceof LivingEntity && e.isAlive() && e.canHit());

        Entity closest = null;
        Vec3d closestPos = null;
        double closestDist = maxTravel;

        for (Entity entity : candidates) {
            Box bounds = entity.getBoundingBox().expand(0.3D);
            var optional = bounds.raycast(start, limit);
            if (optional.isPresent()) {
                Vec3d hitPos = optional.get();
                double dist = start.distanceTo(hitPos);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                    closestPos = hitPos;
                }
            }
        }

        if (closest == null) {
            return null;
        }
        return new EntityHitResult(closest, closestPos);
    }
}