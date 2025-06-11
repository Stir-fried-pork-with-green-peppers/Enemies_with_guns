package com.github.Book0225.enemies_with_guns.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import com.github.Book0225.enemies_with_guns.Enemies_with_guns;
import com.github.Book0225.enemies_with_guns.entity.projectile.CustomBulletEntity;
import org.jetbrains.annotations.NotNull;

// ★遅延実行のために追加のインポート
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AssaultRifleItem extends Item {
    private static final ScheduledExecutorService BURST_FIRE_SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public AssaultRifleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStackInHand = player.getItemInHand(hand);
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            fireBullet(serverLevel, player, 0.8F);
            BURST_FIRE_SCHEDULER.schedule(() -> serverLevel.getServer().execute(() -> {
                if (player.isAlive() && player.getItemInHand(hand).equals(itemStackInHand)) {
                    fireBullet(serverLevel, player, 1.2F);
                }
            }), 100, TimeUnit.MILLISECONDS);
            BURST_FIRE_SCHEDULER.schedule(() -> serverLevel.getServer().execute(() -> {
                if (player.isAlive() && player.getItemInHand(hand).equals(itemStackInHand)) {
                    fireBullet(serverLevel, player, 1.2F);
                }
            }), 200, TimeUnit.MILLISECONDS);

        } else {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            level.addParticle(ParticleTypes.POOF, eyePos.x + lookVec.x * 0.5, eyePos.y + lookVec.y * 0.5, eyePos.z + lookVec.z * 0.5, 0.0, 0.0, 0.0);
        }
        player.getCooldowns().addCooldown(this, 20);

        return InteractionResultHolder.consume(itemStackInHand);
    }

    /**
     * 弾丸を1発発射する処理をまとめたヘルパーメソッド
     * @param level ワールド
     * @param player 発射主のプレイヤー
     * @param inaccuracy 弾のばらつき度合い (0でまっすぐ)
     */
    private void fireBullet(Level level, Player player, float inaccuracy) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.8F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        CustomBulletEntity bullet = new CustomBulletEntity(Enemies_with_guns.CUSTOM_BULLET.get(), level);
        bullet.setOwner(player);
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        double spawnX = eyePos.x + lookVec.x * 0.5D;
        double spawnY = eyePos.y + lookVec.y * 0.5D;
        double spawnZ = eyePos.z + lookVec.z * 0.5D;
        bullet.setPos(spawnX, spawnY, spawnZ);
        bullet.shoot(lookVec.x, lookVec.y, lookVec.z, 3.0F, inaccuracy);
        level.addFreshEntity(bullet);
    }
}