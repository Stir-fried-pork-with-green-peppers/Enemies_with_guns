package com.github.Book0225.enemies_with_guns.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob; // RangedAttackMob インターフェースをインポート
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import com.github.Book0225.enemies_with_guns.Enemies_with_guns;
import com.github.Book0225.enemies_with_guns.entity.projectile.CustomBulletEntity;

// RangedAttackMob インターフェースを実装することで、RangedAttackGoalが使用可能になる
public class GuerrillaSoldierEntity extends Monster implements RangedAttackMob {
    public GuerrillaSoldierEntity(EntityType<? extends GuerrillaSoldierEntity> type, Level level) {
        super(type, level);
    }

    // MobのAI（行動）を登録するメソッド
    @Override
    protected void registerGoals() {
        // 最優先でプレイヤーをターゲットする
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        // 遠距離攻撃（銃撃）のAIを設定。RangedAttackMob を実装しているので、このクラスの performRangedAttack が呼ばれる
        // RangedAttackGoal(このMob, 移動速度, 攻撃間隔ティック, 攻撃範囲)
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.25D, 20, 15.0F));

        // その他のAI行動
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D)); // 水を避けて歩く
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // RangedAttackMob インターフェースの実装（RangedAttackGoal から呼び出される）
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        // Mobが手持ちにAssaultRifleを持っているかチェックしてから攻撃
        if (!this.isHolding(Enemies_with_guns.ASSAULT_RIFLE.get())) { // LivingEntityの isHolding() を使う
            return; // 銃を持っていなければ攻撃しない
        }

        CustomBulletEntity bullet = new CustomBulletEntity(this.level(), this);

        // ターゲットに向かって弾丸を撃つように調整
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - bullet.getY();
        double d2 = target.getZ() - this.getZ();

        // shoot(x, y, z, 速度, ばらつき)
        bullet.shoot(d0, d1, d2, 3.0F, 1.0F); // 速度3.0F、ばらつき1.0F

        this.level().addFreshEntity(bullet); // ワールドに弾丸を追加

        // 銃声と発砲エフェクト (Mobの位置で音を鳴らす)
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.HOSTILE, 1.0F, 1.0F / (this.level().getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }

    // Mobの属性（体力、移動速度、攻撃力など）を設定するメソッド
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D) // 体力30（ゾンビの1.5倍）
                .add(Attributes.MOVEMENT_SPEED, 0.25D) // 移動速度（ゾンビと同じくらい）
                .add(Attributes.ATTACK_DAMAGE, 0.0D) // 近接攻撃はしないので0（銃撃でダメージを与えるため）
                .add(Attributes.FOLLOW_RANGE, 48.0D); // プレイヤーを追跡する範囲を48ブロックに設定（スナイパーのように）
    }

    // Mobが最初にスポーンしたときに呼び出されるメソッド
    // finalizeSpawn の戻り値の型が SpawnGroupData に変更されている点に注意
    @Override
    @Nullable // nullを返しても良いことを示す
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable net.minecraft.nbt.CompoundTag dataTag) {
        super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
        // Mobがスポーンしたときに銃を持たせる
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Enemies_with_guns.ASSAULT_RIFLE.get()));
        // 必要ならアーマーも装備させる
        // this.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(net.minecraft.world.item.Items.IRON_HELMET));
        return spawnDataIn; // 呼び出し元が期待する値を返す
    }
}