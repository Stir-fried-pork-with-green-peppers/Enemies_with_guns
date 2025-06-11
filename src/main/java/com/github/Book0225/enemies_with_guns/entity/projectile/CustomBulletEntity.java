package com.github.Book0225.enemies_with_guns.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener; // パケットの型定義用

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack; // getPickupItem() の戻り値用

import com.github.Book0225.enemies_with_guns.Enemies_with_guns;

public class CustomBulletEntity extends AbstractArrow {
    public CustomBulletEntity(EntityType<? extends CustomBulletEntity> type, Level level) {
        super(type, level);
        this.setBaseDamage(6.0); // 弾丸の基本ダメージ
        this.setNoGravity(true); // 重力の影響を受けないようにする
    }

    public CustomBulletEntity(Level level, LivingEntity shooter) {
        super(Enemies_with_guns.CUSTOM_BULLET.get(), shooter, level);
        this.setBaseDamage(6.0);
        this.setNoGravity(true);
    }

    // クライアントにエンティティをスポーンさせるためのパケットを返す
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() { // 戻り値の型を修正
        return new ClientboundAddEntityPacket(this);
    }

    // エンティティに命中したときの処理
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        // ダメージソースを作成する適切なメソッドを使用
        DamageSource damageSource = this.damageSources().arrow(this, this.getOwner()); // arrow() メソッドを使用
        result.getEntity().hurt(damageSource, (float)this.getBaseDamage());

        this.discard(); // 弾丸を消滅させる
    }

    // ブロックに命中したときの処理
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.discard(); // 弾丸を消滅させる
    }

    // AbstractArrow の抽象メソッド。弾丸が拾えるアイテムを定義する。
    // 今回は拾えない弾丸なので空のアイテムスタックを返す。
    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}