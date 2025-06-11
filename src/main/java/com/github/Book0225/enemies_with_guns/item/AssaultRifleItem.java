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
import net.minecraft.world.phys.Vec3; // Vec3 をインポート

import com.github.Book0225.enemies_with_guns.Enemies_with_guns;
import com.github.Book0225.enemies_with_guns.entity.projectile.CustomBulletEntity;

public class AssaultRifleItem extends Item {
    public AssaultRifleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) { // サーバーサイドでのみ弾丸を生成・発射
            CustomBulletEntity bullet = new CustomBulletEntity(Enemies_with_guns.CUSTOM_BULLET.get(), level);
            bullet.setOwner(player); // 発射元をプレイヤーに設定

            // --- ★ここから修正★ ---
            // プレイヤーの目の位置から発射開始
            Vec3 eyePos = player.getEyePosition(); // プレイヤーの目の座標 (Vec3はx,y,zのベクトルを表すクラス)

            // プレイヤーの視線方向を取得（単位ベクトル）
            Vec3 lookVec = player.getLookAngle(); // プレイヤーが向いている方向のベクトル

            // 発射位置を少し手前に調整（銃身の先端あたり）
            // lookVecに0.5Dを掛けて、目の位置から0.5ブロック前方にずらす
            double spawnX = eyePos.x + lookVec.x * 0.5D;
            double spawnY = eyePos.y + lookVec.y * 0.5D;
            double spawnZ = eyePos.z + lookVec.z * 0.5D;

            // 弾丸の初期位置を設定
            bullet.setPos(spawnX, spawnY, spawnZ);

            // 弾丸を発射（視線方向のベクトルと速度を設定）
            // shoot(x, y, z, 速度, ばらつき)
            // x, y, z は弾丸が進む方向のベクトル (今回は視線方向のベクトルをそのまま使う)
            // 速度は3.0F (お好みで調整)
            // ばらつきは1.0F (お好みで調整。小さいほど真っ直ぐ飛ぶ)
            bullet.shoot(lookVec.x, lookVec.y, lookVec.z, 3.0F, 1.0F);
            // --- ★修正ここまで★ ---

            level.addFreshEntity(bullet); // ワールドに弾丸を追加（スポーン）

            // 銃声とパーティクル (サーバーサイドで音を鳴らす)
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

        } else { // クライアントサイドでのみ発砲エフェクト（煙のパーティクル）を出す
            // クライアント側でも発射位置を考慮してパーティクルを出すと良い
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            level.addParticle(ParticleTypes.POOF, eyePos.x + lookVec.x * 0.5, eyePos.y + lookVec.y * 0.5, eyePos.z + lookVec.z * 0.5, 0.0, 0.0, 0.0);
        }

        player.getCooldowns().addCooldown(this, 10);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
}